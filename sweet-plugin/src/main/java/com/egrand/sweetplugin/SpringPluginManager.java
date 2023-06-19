package com.egrand.sweetplugin;

import cn.hutool.core.io.FileUtil;
import com.egrand.sweetplugin.action.impl.extension.ExtensionsInjector;
import com.egrand.sweetplugin.action.impl.extension.SpringExtensionFactory;
import com.egrand.sweetplugin.bean.DefaultPluginBeanManager;
import com.egrand.sweetplugin.bean.PluginBeanManager;
import com.egrand.sweetplugin.bootstrap.PluginSpringApplication;
import com.egrand.sweetplugin.bootstrap.processor.DefaultProcessorContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.common.utils.FilesUtils;
import com.egrand.sweetplugin.common.utils.ObjectUtils;
import com.egrand.sweetplugin.core.descriptor.*;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class SpringPluginManager extends DefaultPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ExtensionsInjector extensionsInjector;

    private PluginBeanManager pluginBeanManager;

    public SpringPluginManager() {
        super();
    }

    public SpringPluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    public SpringPluginManager(List<Path> pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected List<Path> createPluginsRoot() {
        String pluginsDir = "/plugins";
        if (pluginsDir != null && !pluginsDir.isEmpty()) {
            List<Path> pathList = new ArrayList<>();
            Arrays.stream(pluginsDir.split(",")).map(String::trim).forEach((dir) -> {
                try {
                    if (null == getClass().getResource(dir)) {
                        String classPath = getClass().getResource("/").getPath();
                        String pluginPath = classPath + "plugins";
                        FileUtil.mkdir(pluginPath);
                    }
                    pathList.add(Paths.get(getClass().getResource(dir).toURI()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
            return pathList;
        }
        pluginsDir = isDevelopment() ? DEVELOPMENT_PLUGINS_DIR : DEFAULT_PLUGINS_DIR;
        return Collections.singletonList(Paths.get(pluginsDir));
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private PluginBeanManager createPluginBeanManager() {
        return new DefaultPluginBeanManager();
    }

    @Override
    protected void initialize() {
        super.initialize();
        this.pluginBeanManager = this.createPluginBeanManager();
    }

    /**
     * 复写createPluginDescriptorFinder,实现自定义PluginDescriptor查询器，扩展PluginDescriptor信息
     * @return
     */
    @Override
    protected PluginDescriptorFinder createPluginDescriptorFinder() {
        return (new CompoundPluginDescriptorFinder()).add(new PropertiesSpringPluginDescriptorFinder()).add(new ManifestSpringPluginDescriptorFinder());
    }

    /**
     * 复写createPluginWrapper，实现自定义PluginDescriptor注入
     * @param pluginDescriptor
     * @param pluginPath
     * @param pluginClassLoader
     * @return
     */
    @Override
    protected PluginWrapper createPluginWrapper(PluginDescriptor pluginDescriptor, Path pluginPath, ClassLoader pluginClassLoader) {
        // create the plugin wrapper
        log.debug("Creating wrapper for plugin '{}'", pluginPath);

        PluginResourcesConfig resourcesConfig = getPluginResourcesConfig(pluginPath, pluginDescriptor);
        ((DefaultInsidePluginDescriptor)pluginDescriptor).setIncludeMainResourcePatterns(resourcesConfig.getLoadMainResourceIncludes());
        ((DefaultInsidePluginDescriptor)pluginDescriptor).setExcludeMainResourcePatterns(resourcesConfig.getLoadMainResourceExcludes());
        PluginWrapper pluginWrapper = new PluginWrapper(this, pluginDescriptor, pluginPath, pluginClassLoader);
        pluginWrapper.setPluginFactory(getPluginFactory());
        return pluginWrapper;
    }

    protected PluginResourcesConfig getPluginResourcesConfig(Path path, PluginDescriptor pluginDescriptor) {
        String libIndex = ((DefaultInsidePluginDescriptor)pluginDescriptor).getPluginResourcesConfig();
        if(ObjectUtils.isEmpty(libIndex)){
            return new PluginResourcesConfig();
        }
        File file = new File(libIndex);
        if(!file.exists()){
            // 如果绝对路径不存在, 则判断相对路径
            String pluginPath = ((DefaultInsidePluginDescriptor)pluginDescriptor).getPluginClassPath();
            file = new File(FilesUtils.joiningFilePath(pluginPath, libIndex));
        }
        if(!file.exists()){
            // 都不存在, 则返回为空
            return new PluginResourcesConfig();
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return PluginResourcesConfig.parse(lines);
        } catch (IOException e) {
            throw new PluginRuntimeException("Load plugin lib index path failure. " + libIndex, e);
        }
    }

    /**
     * 复写startPlugins，启动时向插件传递应用父ApplicationContext和动态注册Extensions的Bean
     * @return
     */
    @Override
    public void startPlugins() {
        for (PluginWrapper pluginWrapper : resolvedPlugins) {
            PluginState pluginState = pluginWrapper.getPluginState();
            if ((PluginState.DISABLED != pluginState) && (PluginState.STARTED != pluginState)) {
                try {
                    log.info("Start plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()));
                    this.startPlugin(pluginWrapper);
                    pluginWrapper.setPluginState(PluginState.STARTED);
                    pluginWrapper.setFailedException(null);
                    startedPlugins.add(pluginWrapper);
                } catch (Exception | LinkageError e) {
                    pluginWrapper.setPluginState(PluginState.FAILED);
                    pluginWrapper.setFailedException(e);
                    log.error("Unable to start plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()), e);
                } finally {
                    firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
                }
            }
        }
    }

    /**
     * 复写startPlugin，启动时向插件传递应用父ApplicationContext和动态注册Extensions的Bean
     * @param pluginId
     * @return
     */
    @Override
    public PluginState startPlugin(String pluginId) {
        checkPluginId(pluginId);

        PluginWrapper pluginWrapper = getPlugin(pluginId);
        PluginDescriptor pluginDescriptor = pluginWrapper.getDescriptor();
        PluginState pluginState = pluginWrapper.getPluginState();
        if (PluginState.STARTED == pluginState) {
            log.debug("Already started plugin '{}'", getPluginLabel(pluginDescriptor));
            return PluginState.STARTED;
        }

        if (!resolvedPlugins.contains(pluginWrapper)) {
            log.warn("Cannot start an unresolved plugin '{}'", getPluginLabel(pluginDescriptor));
            return pluginState;
        }

        if (PluginState.DISABLED == pluginState) {
            // automatically enable plugin on manual plugin start
            if (!enablePlugin(pluginId)) {
                return pluginState;
            }
        }

        for (PluginDependency dependency : pluginDescriptor.getDependencies()) {
            // start dependency only if it marked as required (non optional) or if it optional and loaded
            if (!dependency.isOptional() || plugins.containsKey(dependency.getPluginId())) {
                startPlugin(dependency.getPluginId());
            }
        }

        log.info("Start plugin '{}'", getPluginLabel(pluginDescriptor));
        try {
            this.startPlugin(pluginWrapper);
            pluginWrapper.setPluginState(PluginState.STARTED);
            startedPlugins.add(pluginWrapper);
        } catch (Exception | LinkageError e) {
            pluginWrapper.setPluginState(PluginState.FAILED);
            pluginWrapper.setFailedException(e);
            log.error("Unable to start plugin '{}'", getPluginLabel(pluginWrapper.getDescriptor()), e);
        } finally {
            firePluginStateEvent(new PluginStateEvent(this, pluginWrapper, pluginState));
        }

        return pluginWrapper.getPluginState();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 查找指定插件的ApplicationContext
     * @param pluginId
     * @return
     */
    public ApplicationContext getPluginApplicationContext(String pluginId) {
        List<PluginWrapper> startedPluginWrapperList = this.getStartedPlugins();
        if (null == startedPluginWrapperList || startedPluginWrapperList.size() == 0)
            return null;
        for (PluginWrapper pluginWrapper : startedPluginWrapperList) {
            if (pluginWrapper.getPluginId().equals(pluginId)) {
                return ((SpringPlugin) pluginWrapper.getPlugin()).getApplicationContext();
            }
        }
        return null;
    }

    /**
     * This method load, start plugins and inject extensions in Spring
     */
    @PostConstruct
    public void init() {
        createExtensionsInject();
        loadPlugins();
    }

    public ExtensionsInjector getExtensionsInjector() {
        return this.extensionsInjector;
    }

    /**
     * 根据插件服务类查找所属的插件信息
     * @param pluginServiceClazz 插件服务类
     * @return 插件信息
     */
    public PluginWrapper findPluginWrapper(Class pluginServiceClazz) {
        List<PluginWrapper> pluginWrapperList = this.getPlugins();
        if (null == pluginWrapperList || pluginWrapperList.size() == 0)
            return null;
        for (PluginWrapper pluginWrapper : pluginWrapperList) {
            List<ExtensionWrapper> extensionWrapperList = this.extensionFinder.find(pluginWrapper.getPluginId());
            if(null == extensionWrapperList || extensionWrapperList.size() == 0)
                continue;
            for (ExtensionWrapper extensionWrapper : extensionWrapperList) {
                if ((extensionWrapper.getDescriptor().extensionClass.getName()).equals(pluginServiceClazz.getName())) {
                    return pluginWrapper;
                }
            }
        }
        return null;
    }

    public String upload(MultipartFile file) {
        String classPath = getClass().getResource("/").getPath();
        String pluginPath = classPath + "plugins";
        String fileName = file.getOriginalFilename();
        String pluginName = fileName.substring(0, fileName.lastIndexOf("."));
        String pluginFilePath = FilesUtils.saveFile(file, pluginPath, pluginName);
        if (null == pluginFilePath)
            return "上传插件失败！";
        String pluginId = this.loadPlugin(new File(pluginFilePath).toPath());
        this.startPlugin(pluginId);
        return pluginFilePath;
    }

    private void createExtensionsInject() {
        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) this.applicationContext.getAutowireCapableBeanFactory();
        this.extensionsInjector = new ExtensionsInjector(this, beanFactory);
    }

    /**
     * 加载自定义Jar并调用自定义插件启动方法
     * @param pluginWrapper
     * @throws IOException
     */
    private void startPlugin(PluginWrapper pluginWrapper) {
        DefaultProcessorContext defaultProcessorContext = new DefaultProcessorContext(ProcessorContext.RunMode.PLUGIN, this.applicationContext,
                super.getPluginLoader(), pluginWrapper, pluginBeanManager,this.getExtensionsInjector());
        PluginSpringApplication pluginSpringApplication = new PluginSpringApplication(defaultProcessorContext);
        pluginSpringApplication.run();
    }

}

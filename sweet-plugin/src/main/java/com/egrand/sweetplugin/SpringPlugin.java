package com.egrand.sweetplugin;

import com.egrand.sweetplugin.action.PluginAction;
import com.egrand.sweetplugin.bootstrap.PluginWebApplicationContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.realize.AutowiredTypeDefiner;
import com.egrand.sweetplugin.common.utils.DestroyUtils;
import com.egrand.sweetplugin.common.utils.JarUtils;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Spring插件
 */
public abstract class SpringPlugin extends Plugin {

    /**
     * 插件ApplicationContext
     */
    private ApplicationContext applicationContext;

    /**
     * 处理者上下文
     */
    private ProcessorContext processorContext;

    /**
     * 插件Action管理
     */
    private PluginAction pluginActionManager;

    public SpringPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    public final ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    /**
     * 启动
     * @param processorContext 处理上下文
     * @param pluginActionManager 插件Action管理
     */
    public PluginWebApplicationContext start(ProcessorContext processorContext, PluginAction pluginActionManager) {
        this.processorContext = processorContext;
        this.pluginActionManager = pluginActionManager;
        this.applicationContext = createApplicationContext(processorContext);
        this.start();
        return (PluginWebApplicationContext) this.applicationContext;
    }

    @Override
    public void stop() {
        if (null != this.pluginActionManager)
            this.pluginActionManager.close(this.processorContext);
        if (null != this.processorContext.getApplicationContext())
            this.processorContext.getApplicationContext().close();
        this.processorContext.clearRegistryInfo();
        DestroyUtils.destroyAll(null, SpringFactoriesLoader.class, "cache", Map.class);
        this.applicationContext = null;
    }

    /**
     * 加载插件指定类
     * @param parentClass 需要满足的父类条件
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws ClassNotFoundException
     */
    public Map<String, Class<?>> loadClasses(List<Class<?>> parentClass) throws IOException,
            URISyntaxException, ClassNotFoundException {
        return JarUtils.loadClasses(this.wrapper, parentClass, "");
    }

    /**
     * 加载插件指定类
     * @param parentClass 需要满足的父类条件
     * @param packagePrefix 包前缀
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws ClassNotFoundException
     */
    public Map<String, Class<?>> loadClasses(List<Class<?>> parentClass, String packagePrefix) throws IOException,
            URISyntaxException, ClassNotFoundException {
        return JarUtils.loadClasses(this.wrapper, parentClass, packagePrefix);
    }

    /**
     * 设置 AutowiredTypeDefiner
     */
    public AutowiredTypeDefiner autowiredTypeDefiner(){
        return null;
    }

    /**
     * 创建插件ApplicationContext
     * @param processorContext
     * @return
     */
    protected ApplicationContext createApplicationContext(ProcessorContext processorContext) {
        PluginWebApplicationContext applicationContext = new PluginWebApplicationContext(processorContext);
        return applicationContext;
    }
}

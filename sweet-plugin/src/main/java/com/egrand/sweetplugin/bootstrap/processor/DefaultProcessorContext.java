package com.egrand.sweetplugin.bootstrap.processor;

import com.egrand.sweetplugin.action.impl.extension.ExtensionsInjector;
import com.egrand.sweetplugin.SpringPlugin;
import com.egrand.sweetplugin.bean.PluginBeanManager;
import com.egrand.sweetplugin.bootstrap.PluginListableBeanFactory;
import com.egrand.sweetplugin.core.descriptor.InsidePluginDescriptor;
import com.egrand.sweetplugin.core.launcher.plugin.CacheRegistryInfo;
import com.egrand.sweetplugin.spring.*;
import org.pf4j.PluginLoader;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * 默认的处理者上下文
 */
public class DefaultProcessorContext extends CacheRegistryInfo implements ProcessorContext {

    private final RunMode runMode;

    private final SpringPlugin springPluginBootstrap;
    private final PluginWrapper pluginWrapper;
    private final PluginBeanManager pluginBeanManager;
    private final ExtensionsInjector extensionsInjector;
    private final Class<? extends SpringPlugin> runnerClass;
    private final MainApplicationContext mainApplicationContext;
    private final PluginLoader pluginLoader;
    private final ClassLoader classLoader;
    private final ResourceLoader resourceLoader;
    private final WebConfig webConfig;

    private GenericApplicationContext applicationContext;
    private PluginListableBeanFactory beanFactory;

    public DefaultProcessorContext(RunMode runMode, ApplicationContext applicationContext, PluginLoader pluginLoader,
                                   PluginWrapper pluginWrapper, PluginBeanManager pluginBeanManager, ExtensionsInjector extensionsInjector) {
        this.runMode = runMode;
        this.pluginWrapper = pluginWrapper;
        this.pluginBeanManager = pluginBeanManager;
        this.extensionsInjector = extensionsInjector;
        this.springPluginBootstrap = (SpringPlugin) pluginWrapper.getPlugin();
        this.runnerClass = springPluginBootstrap.getClass();
        this.mainApplicationContext = new MainApplicationContextProxy((GenericApplicationContext)applicationContext,
                (AutoCloseable)applicationContext);
        this.pluginLoader = pluginLoader;
        this.classLoader = pluginWrapper.getPluginClassLoader();
        this.resourceLoader = new DefaultResourceLoader(this.classLoader);
        this.webConfig = new WebConfig();
    }

    @Override
    public RunMode runMode() {
        return runMode;
    }

    @Override
    public PluginBeanManager getPluginBeanManager() {
        return this.pluginBeanManager;
    }

    @Override
    public PluginWrapper getPluginWrapper() {
        return this.pluginWrapper;
    }

    @Override
    public SpringPlugin getSpringPluginBootstrap() {
        return this.springPluginBootstrap;
    }

    @Override
    public ExtensionsInjector getExtensionsInjector() {
        return this.extensionsInjector;
    }

    @Override
    public InsidePluginDescriptor getPluginDescriptor() {
        return (InsidePluginDescriptor) pluginWrapper.getDescriptor();
    }

    @Override
    public Class<? extends SpringPlugin> getRunnerClass() {
        return runnerClass;
    }

    @Override
    public String getRunnerPackage() {
        if (null == this.getRunnerClass())
            return null;
        String className = this.getRunnerClass().getName();
        return className.substring(0, className.lastIndexOf("."));
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    public SpringBeanFactory getMainBeanFactory() {
        return mainApplicationContext.getSpringBeanFactory();
    }

    @Override
    public DefaultListableBeanFactory getPluginBeanFactory() {
        if (null == this.beanFactory)
            return new PluginListableBeanFactory(this);
        else
            return this.beanFactory;
    }

    @Override
    public GenericApplicationContext getApplicationContext() {
        if(applicationContext == null){
            return null;
        }
        return applicationContext;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public WebConfig getWebConfig() {
        return webConfig;
    }

    @Override
    public void setApplicationContext(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}

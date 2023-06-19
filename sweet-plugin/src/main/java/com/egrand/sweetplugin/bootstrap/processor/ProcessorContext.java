package com.egrand.sweetplugin.bootstrap.processor;

import com.egrand.sweetplugin.action.impl.extension.ExtensionsInjector;
import com.egrand.sweetplugin.SpringPlugin;
import com.egrand.sweetplugin.bean.PluginBeanManager;
import com.egrand.sweetplugin.core.descriptor.InsidePluginDescriptor;
import com.egrand.sweetplugin.core.launcher.plugin.RegistryInfo;
import com.egrand.sweetplugin.spring.MainApplicationContext;
import com.egrand.sweetplugin.spring.SpringBeanFactory;
import com.egrand.sweetplugin.spring.WebConfig;
import org.pf4j.PluginLoader;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * 处理者上下文
 */
public interface ProcessorContext extends RegistryInfo {

    /**
     * 当前运行模式
     * @return RunMode
     */
    RunMode runMode();

    /**
     * 得到入口类对象-SpringPluginBootstrap
     * @return SpringPluginBootstrap
     */
    SpringPlugin getSpringPluginBootstrap();

    /**
     * 得到插件Bean管理
     * @return PluginBeanManager
     */
    PluginBeanManager getPluginBeanManager();

    /**
     * 得到当前插件 ExtensionsInjector
     * @return ExtensionsInjector
     */
    ExtensionsInjector getExtensionsInjector();

    /**
     * 得到插件 PluginWrapper
     * @return PluginWrapper
     */
    PluginWrapper getPluginWrapper();

    /**
     * 得到插件信息 PluginDescriptor
     * @return PluginDescriptor
     */
    InsidePluginDescriptor getPluginDescriptor();

    /**
     * 得到启动的class类
     * @return Class
     */
    Class<? extends SpringPlugin> getRunnerClass();

    /**
     * 得到启动class的包名
     * @return
     */
    String getRunnerPackage();

    /**
     * 得到 PluginInteractive
     * @return PluginInteractive
     */
//    PluginInteractive getPluginInteractive();

    /**
     * 得到主程序的 ApplicationContext
     * @return MainApplicationContext
     */
    MainApplicationContext getMainApplicationContext();

    /**
     * 得到主程序的 SpringBeanFactory
     * @return SpringBeanFactory
     */
    SpringBeanFactory getMainBeanFactory();

    /**
     * 得到当前框架的集成配置
     * @return IntegrationConfiguration
     */
//    IntegrationConfiguration getConfiguration();

    /**
     * 获取插件BeanFactory
     * @return 插件BeanFactory
     */
    DefaultListableBeanFactory getPluginBeanFactory();

    /**
     * 得到当前插件的 ApplicationContext
     * @return GenericApplicationContext
     */
    GenericApplicationContext getApplicationContext();

    /**
     * 得当当前插件的 PluginLoader
     * @return PluginLoader
     */
    PluginLoader getPluginLoader();

    /**
     * 得到当前插件的 ClassLoader
     * @return ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * 得到插件的资源loader
     * @return ResourceLoader
     */
    ResourceLoader getResourceLoader();

    /**
     * 获取 WebConfig
     * @return WebConfig
     */
    WebConfig getWebConfig();

    /**
     * set 当前插件的 ApplicationContext
     * @param applicationContext GenericApplicationContext
     */
    void setApplicationContext(GenericApplicationContext applicationContext);

    /**
     * 运行模式
     */
    enum RunMode{
        /**
         * 全部运行
         */
        ALL,

        /**
         * 插件环境运行
         */
        PLUGIN,

        /**
         * 插件独立运行
         */
        ONESELF
    }


}

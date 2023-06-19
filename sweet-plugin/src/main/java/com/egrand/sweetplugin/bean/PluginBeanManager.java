package com.egrand.sweetplugin.bean;

import org.pf4j.PluginWrapper;

import java.util.Map;

/**
 * 插件Bean接口
 */
public interface PluginBeanManager {

    /**
     * 注册Bean
     * @param pluginWrapper 插件
     * @param clazzName 类名称
     * @param clazz 类
     * @param propertyValues 属性值
     * @return
     */
    Map<String, Object> registerBeanDefinition(PluginWrapper pluginWrapper, String clazzName, Class<?> clazz, Map<String, Object> propertyValues);

    /**
     * 注册Bean
     * @param pluginWrapper 插件
     * @param classMap 类集合
     * @return
     */
    Map<String, Object> registerBeanDefinition(PluginWrapper pluginWrapper, Map<String, Class<?>> classMap);

    /**
     * 移除所有Bean
     * @param pluginWrapper 插件
     */
    void removeBeanDefinition(PluginWrapper pluginWrapper, Map<String, Object> beanMap);
}

package com.egrand.sweetplugin.bean;

import com.egrand.sweetplugin.SpringPlugin;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件Bean管理
 */
@Slf4j
public class DefaultPluginBeanManager implements PluginBeanManager {

    public DefaultPluginBeanManager() {

    }

    @Override
    public Map<String, Object> registerBeanDefinition(PluginWrapper pluginWrapper, String clazzName, Class<?> clazz, Map<String, Object> propertyValues) {
        ApplicationContext pluginApplicationContext = ((SpringPlugin)pluginWrapper.getPlugin()).getApplicationContext();
        // 如果存在则返回Bean
        if (pluginApplicationContext.containsBean(clazzName)) {
            Map<String, Object> beanMap = new HashMap<>();
            beanMap.put(clazzName, pluginApplicationContext.getBean(clazzName));
            return beanMap;
        }
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory)pluginApplicationContext.getAutowireCapableBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        if (null != propertyValues && propertyValues.size() != 0) {
            propertyValues.forEach((propertyName, value) -> beanDefinitionBuilder.addPropertyValue(propertyName, value));
        }
        // 注册bean
        factory.registerBeanDefinition(clazzName, beanDefinitionBuilder.getRawBeanDefinition());
        log.info("Register Bean = '{}'", clazzName);
        Object bean = pluginApplicationContext.getBean(clazzName);
        Map<String, Object> beanMap = new HashMap<>();
        beanMap.put(clazzName, bean);
        return beanMap;
    }

    @Override
    public Map<String, Object> registerBeanDefinition(PluginWrapper pluginWrapper, Map<String, Class<?>> classMap) {
        Map<String, Object> beanMap = new HashMap<>();
        if (null == classMap || classMap.size() == 0)
            return beanMap;
        classMap.forEach((className, clazz) -> beanMap.putAll(this.registerBeanDefinition(pluginWrapper, className, clazz, null)));
        return beanMap;
    }

    @Override
    public void removeBeanDefinition(PluginWrapper pluginWrapper, Map<String, Object> beanMap) {
        ApplicationContext pluginApplicationContext = ((SpringPlugin)pluginWrapper.getPlugin()).getApplicationContext();
        List<String> removeList = new ArrayList();
        beanMap.keySet().forEach(clazzName -> {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory)pluginApplicationContext.getAutowireCapableBeanFactory();
            factory.removeBeanDefinition(clazzName);
            log.info("Remove Bean = '{}'", clazzName);
            removeList.add(clazzName);
        });
    }
}

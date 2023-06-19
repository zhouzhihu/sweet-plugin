package com.egrand.sweetplugin.spring;

/**
 * 自定义ApplicationContext
 */
public interface ApplicationContext extends AutoCloseable {

    /**
     * 得到 SpringBeanFactory
     * @return SpringBeanFactory
     */
    SpringBeanFactory getSpringBeanFactory();

}

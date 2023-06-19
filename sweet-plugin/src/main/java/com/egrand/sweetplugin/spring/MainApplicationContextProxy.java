package com.egrand.sweetplugin.spring;

import com.egrand.sweetplugin.spring.environment.EnvironmentProvider;
import com.egrand.sweetplugin.spring.environment.MainSpringBootEnvironmentProvider;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主程序 ApplicationContext 的实现
 */
public class MainApplicationContextProxy extends ApplicationContextProxy implements MainApplicationContext{

    private final GenericApplicationContext applicationContext;
    private final boolean isWebEnvironment;

    public MainApplicationContextProxy(GenericApplicationContext applicationContext) {
        super(applicationContext.getBeanFactory());
        this.applicationContext = applicationContext;
        this.isWebEnvironment = getIsWebEnvironment(applicationContext);
    }

    public MainApplicationContextProxy(GenericApplicationContext applicationContext,
                                       AutoCloseable autoCloseable) {
        super(applicationContext.getBeanFactory(), autoCloseable);
        this.applicationContext = applicationContext;
        this.isWebEnvironment = getIsWebEnvironment(applicationContext);
    }

    @Override
    public Map<String, Map<String, Object>> getConfigurableEnvironment() {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Map<String, Object>> environmentMap = new LinkedHashMap<>(propertySources.size());
        for (PropertySource<?> propertySource : propertySources) {
            if (!(propertySource instanceof EnumerablePropertySource)) {
                continue;
            }
            EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
            String[] propertyNames = enumerablePropertySource.getPropertyNames();
            Map<String, Object> values = new HashMap<>(propertyNames.length);
            for (String propertyName : propertyNames) {
                values.put(propertyName, enumerablePropertySource.getProperty(propertyName));
            }
            if (!values.isEmpty()) {
                environmentMap.put(propertySource.getName(), values);
            }
        }
        return environmentMap;
    }

    @Override
    public EnvironmentProvider getEnvironmentProvider() {
        return new MainSpringBootEnvironmentProvider(applicationContext.getEnvironment());
    }

    @Override
    public Object resolveDependency(String requestingBeanName, Class<?> dependencyType) {
        try {
            return applicationContext.getBean(dependencyType);
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean isWebEnvironment() {
        return isWebEnvironment;
    }

    private boolean getIsWebEnvironment(GenericApplicationContext applicationContext){
        return applicationContext instanceof AnnotationConfigServletWebServerApplicationContext
                || applicationContext instanceof AnnotationConfigReactiveWebServerApplicationContext;
    }

    @Override
    public GenericApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

}

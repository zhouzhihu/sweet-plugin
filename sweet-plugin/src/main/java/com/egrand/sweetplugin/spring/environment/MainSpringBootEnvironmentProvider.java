package com.egrand.sweetplugin.spring.environment;

import com.egrand.sweetplugin.common.utils.ObjectUtils;
import com.egrand.sweetplugin.common.utils.ObjectValueUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 主程序配置信息提供者实现
 */
public class MainSpringBootEnvironmentProvider implements EnvironmentProvider {

    private final ConfigurableEnvironment environment;

    public MainSpringBootEnvironmentProvider(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Object getValue(String name) {
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            Object property = propertySource.getProperty(name);
            if (property != null) {
                return property;
            }
        }
        return null;
    }

    @Override
    public String getString(String name) {
        return ObjectValueUtils.getString(getValue(name));
    }

    @Override
    public Integer getInteger(String name) {
        return ObjectValueUtils.getInteger(getValue(name));
    }

    @Override
    public Long getLong(String name) {
        return ObjectValueUtils.getLong(getValue(name));
    }

    @Override
    public Double getDouble(String name) {
        return ObjectValueUtils.getDouble(getValue(name));
    }

    @Override
    public Float getFloat(String name) {
        return ObjectValueUtils.getFloat(getValue(name));
    }

    @Override
    public Boolean getBoolean(String name) {
        return ObjectValueUtils.getBoolean(getValue(name));
    }

    @Override
    public EnvironmentProvider getByPrefix(String prefix) {
        if(ObjectUtils.isEmpty(prefix)){
            return new EmptyEnvironmentProvider();
        }
        Map<String, Object> collect = new LinkedHashMap<>();
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            String name = propertySource.getName();
            if(name.startsWith(prefix)){
                collect.put(MapEnvironmentProvider.resolveKey(prefix, name), propertySource.getSource());
            }
        }
        return new MapEnvironmentProvider(collect);
    }

    @Override
    public void forEach(BiConsumer<String, Object> action) {
        MutablePropertySources propertySources = environment.getPropertySources();
        for (PropertySource<?> propertySource : propertySources) {
            action.accept(propertySource.getName(), propertySource.getSource());
        }
    }
}

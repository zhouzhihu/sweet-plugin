package com.egrand.sweetplugin.spring.environment;

import java.util.function.BiConsumer;

/**
 * 空的配置信息提供者
 */
public class EmptyEnvironmentProvider implements EnvironmentProvider{


    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public String getString(String name) {
        return null;
    }

    @Override
    public Integer getInteger(String name) {
        return null;
    }

    @Override
    public Long getLong(String name) {
        return null;
    }

    @Override
    public Double getDouble(String name) {
        return null;
    }

    @Override
    public Float getFloat(String name) {
        return null;
    }

    @Override
    public Boolean getBoolean(String name) {
        return null;
    }

    @Override
    public EnvironmentProvider getByPrefix(String prefix) {
        return new EmptyEnvironmentProvider();
    }

    @Override
    public void forEach(BiConsumer<String, Object> action) {

    }
}

package com.egrand.sweetplugin.spring.environment;

import java.util.function.BiConsumer;

/**
 * 配置信息提供者接口
 */
public interface EnvironmentProvider {

    /**
     * 根据名称获取配置值
     * @param name 配置名称
     * @return 配置值
     */
    Object getValue(String name);

    /**
     * 根据名称获取 String 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    String getString(String name);

    /**
     * 根据名称获取 Integer 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Integer getInteger(String name);

    /**
     * 根据名称获取 Long 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Long getLong(String name);

    /**
     * 根据名称获取 Double 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Double getDouble(String name);

    /**
     * 根据名称获取 Float 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Float getFloat(String name);

    /**
     * 根据名称获取 Boolean 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Boolean getBoolean(String name);

    /**
     * 根据前缀名称批量获取配置值
     * @param prefix 前缀
     * @return 环境
     */
    EnvironmentProvider getByPrefix(String prefix);

    /**
     * 获取所有配置集合
     * @param action 每个条目执行的操作
     */
    void forEach(BiConsumer<String, Object> action);


}

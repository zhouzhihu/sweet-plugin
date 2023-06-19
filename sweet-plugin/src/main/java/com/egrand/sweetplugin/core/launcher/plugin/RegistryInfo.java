
package com.egrand.sweetplugin.core.launcher.plugin;
import java.util.function.Supplier;

/**
 * 注册的信息接口
 */
public interface RegistryInfo {


    /**
     * 添加注册的信息
     * @param key 注册信息key
     * @param value 注册信息值
     */
    void addRegistryInfo(String key, Object value);

    /**
     * 得到注册信息
     * @param key 注册信息key
     * @param <T> 返回类型泛型
     * @return 注册信息的值
     */
    <T> T getRegistryInfo(String key);

    /**
     * 得到注册信息
     * @param key 注册信息key
     * @param notExistCreate 不存在的话, 进行创建操作
     * @param <T> 返回类型泛型
     * @return 注册信息的值
     */
    <T> T getRegistryInfo(String key, Supplier<T> notExistCreate);


    /**
     * 移除注册信息
     * @param key 注册信息key
     */
    void removeRegistryInfo(String key);

    /**
     * 清除全部的注册信息
     */
    void clearRegistryInfo();

}

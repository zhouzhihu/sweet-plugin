package com.egrand.sweetplugin.common.utils;

import java.util.Properties;

/**
 * 操作 Manifest 工具类
 */
public abstract class PropertiesUtils {

    private PropertiesUtils(){}

    /**
     * 获取值
     *
     * @param properties properties
     * @param key 获取的key
     * @return 获取的值或者null
     */
    public static String getValue(Properties properties, String key){
        return getValue(properties, key, true);
    }

    /**
     * 获取值
     *
     * @param properties properties
     * @param key 获取的key
     * @param notExitsThrowException 如果不存在是否抛出异常
     * @return 获取的值
     */
    public static String getValue(Properties properties, String key, boolean notExitsThrowException){
        boolean throwException = false;
        String value = null;
        try {
            value = properties.getProperty(key);
            if(value == null && notExitsThrowException){
                throwException = true;
            }
        } catch (Throwable e){
            // 忽略
            throwException = true;
        }
        if(throwException){
            throw new IllegalStateException("Not found '" + key + "' config!");
        }
        return value;
    }

}

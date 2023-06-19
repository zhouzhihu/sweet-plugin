package com.egrand.sweetplugin.common.utils;

import java.lang.reflect.Field;

/**
 * 文过滤接口
 */
@FunctionalInterface
public interface FieldFilter {

    /**
     * 过滤
     *
     * @param field 当前字段
     * @return true 允许, false 不允许
     */
    boolean filter(Field field);


}

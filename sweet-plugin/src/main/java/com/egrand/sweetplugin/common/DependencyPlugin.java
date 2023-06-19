package com.egrand.sweetplugin.common;

/**
 * 依赖的插件
 */
public interface DependencyPlugin {

    /**
     * 依赖插件id
     *
     * @return String
     */
    String getId();

    /**
     * 依赖插件版本. 如果设置为: 0.0.0 表示支持任意版本依赖
     *
     * @return String
     */
    String getVersion();

    /**
     * 是否为必须依赖. 默认: false
     *
     * @return boolean
     */
    Boolean getOptional();

}

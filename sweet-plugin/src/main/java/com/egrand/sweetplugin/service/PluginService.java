package com.egrand.sweetplugin.service;

import org.pf4j.ExtensionPoint;

/**
 * 基础插件Service接口
 */
public interface PluginService extends ExtensionPoint {


    /**
     * Service类型
     * @return
     */
    String getType();

    /**
     * 设置关键字（一般设置为租户ID，为空时代表不区分）
     * @return
     */
    default String getPrimaryKey() {
        return "";
    }

    /**
     * 排序号
     * @return
     */
    default int orderNo() {
        return 0;
    }

}

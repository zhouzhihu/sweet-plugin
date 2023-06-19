package com.egrand.sweetplugin.aop;

import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;

import java.util.List;

/**
 * AOP桥接接口，用于绑定应用和AOP插件服务
 */
public interface PluginAopAdapter {

    /**
     * 获取目标Bean
     * @return
     */
    List<Object> getTargetBean(ProcessorContext processorContext);

    /**
     * 获取AOP表达式
     * @return
     */
    String getExpression();
}

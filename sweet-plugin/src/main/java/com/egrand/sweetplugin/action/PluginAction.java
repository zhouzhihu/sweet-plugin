package com.egrand.sweetplugin.action;

import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorException;
import com.egrand.sweetplugin.common.utils.Order;
import com.egrand.sweetplugin.common.utils.OrderPriority;

/**
 * 插件动作接口
 */
public interface PluginAction extends Order {
    /**
     * 初始化时
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void initialize(ProcessorContext context) throws ProcessorException {

    }

    /**
     * 刷新上下文前
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void refreshBefore(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 刷新上下文后
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void refreshAfter(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 启动失败
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void failure(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 关闭容器时
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void close(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 执行顺序
     * @return OrderPriority
     */
    @Override
    default OrderPriority order(){
        return OrderPriority.getLowPriority();
    }

    /**
     * 处理器运行模式
     * @return RunMode
     */
    ProcessorContext.RunMode runMode();

    /**
     * 获取动作名称
     * @return
     */
    String getName();
}

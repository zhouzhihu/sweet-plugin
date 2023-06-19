package com.egrand.sweetplugin.aop;

import com.egrand.sweetplugin.service.PluginService;
import org.aopalliance.intercept.MethodInvocation;

/**
 * AOP插件服务
 */
public interface PluginAopService extends PluginService {

    /**
     * 执行前
     * @param invocation 方法Invocation
     */
    Object before(MethodInvocation invocation);

    /**
     * 执行后
     * @param invocation 方法Invocation
     */
    Object after(MethodInvocation invocation, Object ret, Object obj);

    /**
     * 异常
     * @param invocation 方法Invocation
     */
    void throwing(MethodInvocation invocation, Throwable e, Object obj);
}

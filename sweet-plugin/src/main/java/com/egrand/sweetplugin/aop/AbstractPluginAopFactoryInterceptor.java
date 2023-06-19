package com.egrand.sweetplugin.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 插件AOP工厂MethodInterceptor公共实现
 */
public abstract class AbstractPluginAopFactoryInterceptor implements MethodInterceptor {

    @Autowired
    protected PluginAopFactory pluginAopFactory;

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        PluginAopService pluginAopService = pluginAopFactory.getPluginAopService(this.getType());
        if (null == pluginAopService) {
            return methodInvocation.proceed();
        }
        Object obj = pluginAopService.before(methodInvocation);
        try {
            Object ret = methodInvocation.proceed();
            return pluginAopService.after(methodInvocation, ret, obj);
        } catch (Throwable e) {
            pluginAopService.throwing(methodInvocation, e, obj);
            throw e;
        }
    }

    /**
     * 获取需要应用的AOP插件类型
     * @return
     */
    public abstract String getType();
}

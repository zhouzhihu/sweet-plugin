package com.egrand.sweetplugin.aop;

import com.egrand.sweetplugin.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * AOP服务抽象类
 */
public abstract class AbstractPluginAopServiceImpl {

    @Autowired
    protected SpringPluginManager springPluginManager;

    /**
     * 获取指定参数名称的值
     * @param methodParameters 方法参数
     * @param methodParameterValues 方法参数值
     * @param parameterName 参数名称
     * @return 参数值
     */
    protected Object getMethodParameterValue(Parameter[] methodParameters, Object[] methodParameterValues, String parameterName) {
        if (null != methodParameters && methodParameters.length != 0) {
            for (int i = 0; i < methodParameters.length; i++) {
                if (methodParameters[i].getName().equals(parameterName)) {
                    return methodParameterValues[i];
                }
            }
        }
        return null;
    }

    /**
     * 获取方法参数Map
     * @param methodParameters 方法参数
     * @param methodParameterValues 方法参数值
     * @return
     */
    protected Map<String, Object> getMethodParameter(Parameter[] methodParameters, Object[] methodParameterValues) {
        Map<String, Object> parameterMap = new HashMap<>();
        if (null != methodParameters && methodParameters.length != 0) {
            for (int i = 0; i < methodParameters.length; i++) {
                parameterMap.put(methodParameters[i].getName(), methodParameterValues[i]);
            }
        }
        return parameterMap;
    }

    protected ApplicationContext getPluginApplicationContext(String pluginId) {
        return this.springPluginManager.getPluginApplicationContext(pluginId);
    }

}

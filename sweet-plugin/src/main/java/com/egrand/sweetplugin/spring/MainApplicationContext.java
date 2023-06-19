package com.egrand.sweetplugin.spring;


import com.egrand.sweetplugin.spring.environment.EnvironmentProvider;
import org.springframework.context.support.GenericApplicationContext;
import java.util.Map;

/**
 * 主程序 ApplicationContext 接口
 */
public interface MainApplicationContext extends ApplicationContext {

    /**
     * 得到主程序所有配置的 env
     *
     * @return 主程序配置的 env 集合
     */
    Map<String, Map<String, Object>> getConfigurableEnvironment();


    /**
     * 得到主程序配置的 Provider
     * @return EnvironmentProvider
     */
    EnvironmentProvider getEnvironmentProvider();

    /**
     * 从主程序获取依赖
     *
     * @param requestingBeanName 依赖Bean名称
     * @param dependencyType 依赖类型
     * @return boolean
     */
    Object resolveDependency(String requestingBeanName, Class<?> dependencyType);

    /**
     * 是否为web环境
     * @return boolean
     */
    boolean isWebEnvironment();

    /**
     * 获取主程序的ApplicationContext
     * @return
     */
    GenericApplicationContext getApplicationContext();

}

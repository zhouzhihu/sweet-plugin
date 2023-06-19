package com.egrand.sweetplugin.action.impl;

import com.egrand.sweetplugin.SpringPlugin;
import com.egrand.sweetplugin.aop.PluginAopAdapter;
import com.egrand.sweetplugin.action.PluginAction;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorException;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.Advised;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 插件AOP动作
 */
@Slf4j
public class PluginAopAction implements PluginAction {

    @Override
    public void refreshAfter(ProcessorContext processorContext) throws ProcessorException {
        Map<String, Class<?>> classMap;
        try {
            classMap = this.getActionClass(processorContext);
        } catch (IOException e) {
            throw new ProcessorException("获取操作类错误！", e);
        } catch (ClassNotFoundException e) {
            throw new ProcessorException("获取操作类错误！", e);
        }
        if (null == classMap || classMap.size() == 0)
            return;
        Map<String, Object> beanMap = processorContext.getRegistryInfo(this.getName());
        if (beanMap == null) {
            beanMap = new HashMap<>();
            processorContext.addRegistryInfo(this.getName(), beanMap);
        }
        Map<String, Object> adviceBeanMap = processorContext.getPluginBeanManager().registerBeanDefinition(processorContext.getPluginWrapper(), classMap);
        if (null == adviceBeanMap || adviceBeanMap.size() == 0)
            return;
        List<Object> adviceBeanList = new ArrayList<>();
        for (String clazzName : adviceBeanMap.keySet()) {
            beanMap.put(clazzName, adviceBeanMap.get(clazzName));
            adviceBeanList.add(adviceBeanMap.get(clazzName));
        }
        this.addAdvice(processorContext, adviceBeanList);
    }

    @Override
    public void close(ProcessorContext processorContext) throws ProcessorException {
        Map<String, Object> adviceBeanMap = processorContext.getRegistryInfo(this.getName());
        if (null != adviceBeanMap && adviceBeanMap.size() != 0) {
            adviceBeanMap.keySet().forEach(key -> {
                Object adviceBean = adviceBeanMap.get(key);
                if (PluginAopAdapter.class.isAssignableFrom(adviceBean.getClass())) {
                    List<Object> targetBeanList = ((PluginAopAdapter)adviceBean).getTargetBean(processorContext);
                    if (null != targetBeanList && targetBeanList.size() != 0) {
                        targetBeanList.forEach(targetBean -> {
                            ((Advised) targetBean).removeAdvice((Advice) adviceBean);
                        });
                    }
                }
            });
        }
        processorContext.getPluginBeanManager().removeBeanDefinition(processorContext.getPluginWrapper(), adviceBeanMap);
        adviceBeanMap.clear();
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }

    @Override
    public String getName() {
        return "AOP-ACTION";
    }

    private Map<String, Class<?>> getActionClass(ProcessorContext processorContext) throws IOException,
            ClassNotFoundException {
        Map<String, Class<?>> actionClazzes = new HashMap<>();
        List<Class<?>> parentClass = new ArrayList<>();
        parentClass.add(PluginAopAdapter.class);
        parentClass.add(Advice.class);
        try {
            Map<String, Class<?>> clazzMap = ((SpringPlugin)processorContext.getPluginWrapper().getPlugin()).loadClasses(parentClass, "com.egrand");
            if (null != clazzMap && clazzMap.size() != 0) {
                actionClazzes.putAll(clazzMap);
            }
        } catch (URISyntaxException e) {
            log.error("加载插件本身包含的Action类失败，跳过错误！", e);
        }
        return actionClazzes;
    }

    private void addAdvice(ProcessorContext processorContext, List<Object> adviceBeanList) {
        if (null == adviceBeanList || adviceBeanList.size() == 0)
            return;
        // 构建advisorBean
        Map<Object, List<Object>> advisorToBeanMap = new HashMap<>();
        Map<Object, Object> advisorToAdvice = new HashMap<>();
        adviceBeanList.forEach(adviceBean -> {
            Map<String, Object> propertyValueMap = new HashMap<>();
            PluginAopAdapter pluginAopAdapter = (PluginAopAdapter)adviceBean;
            propertyValueMap.put("expression", pluginAopAdapter.getExpression());
            propertyValueMap.put("advice", adviceBean);
            String clazzName = adviceBean.getClass().getName() + "-aop";
            Map<String, Object> advisorBeanMap = processorContext.getPluginBeanManager().registerBeanDefinition(processorContext.getPluginWrapper(), clazzName, AspectJExpressionPointcutAdvisor.class, propertyValueMap);
            if (null != advisorBeanMap && advisorBeanMap.size() != 0) {
                Map<String, Object> beanMap = processorContext.getRegistryInfo(this.getName());
                beanMap.putAll(advisorBeanMap);
                Object advisor = advisorBeanMap.get(clazzName);
                advisorToBeanMap.put(advisor, pluginAopAdapter.getTargetBean(processorContext));
                advisorToAdvice.put(advisor, adviceBean);
            }
        });
        // 绑定advisor
        if (advisorToBeanMap.size() == 0)
            return;
        advisorToBeanMap.forEach((advisor, beanList) -> {
            if (null != beanList && beanList.size() != 0) {
                Object adviceBean = advisorToAdvice.get(advisor);
                for (Object bean : beanList) {
                    if (bean == this)
                        continue;
                    // 如果bean不是Advised类型则跳过
                    if (!(bean instanceof Advised))
                        continue;
                    // 如果bean已经注册了Advised则跳过
                    if (findAdvice(adviceBean.getClass().getName(), (Advised) bean) != null)
                        continue;
                    // 将advisor绑定到bean上
                    ((Advised) bean).addAdvisor((Advisor) advisor);
                }
            }
        });

    }

    /**
     * 查找指定切面
     * @param className
     * @param advised
     * @return
     */
    private Advice findAdvice(String className, Advised advised) {
        for (Advisor a : advised.getAdvisors()) {
            if (a.getAdvice().getClass().getName().equals(className)) {
                return a.getAdvice();
            }
        }
        return null;
    }
}

package com.egrand.sweetplugin.action.impl;

import com.egrand.sweetplugin.action.PluginAction;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorException;
import com.egrand.sweetplugin.common.utils.AnnotationUtils;
import com.egrand.sweetplugin.common.utils.DestroyUtils;
import com.egrand.sweetplugin.common.utils.ObjectUtils;
import com.egrand.sweetplugin.common.utils.SpringBeanCustomUtils;
import com.egrand.sweetplugin.spring.SpringBeanFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Controller动作
 */
@Slf4j
public class PluginControllerAction implements PluginAction {

    static final String PROCESS_CONTROLLERS = "PLUGIN_CONTROLLER";

    @Override
    public void refreshBefore(ProcessorContext processorContext) throws ProcessorException {
        GenericApplicationContext applicationContext = processorContext.getApplicationContext();
        applicationContext.registerBean("pluginControllerPostProcessor",
                ControllerPostProcessor.class, ()-> new ControllerPostProcessor(processorContext));
    }

    @Override
    public void refreshAfter(ProcessorContext processorContext) throws ProcessorException {
        GenericApplicationContext applicationContext = processorContext.getApplicationContext();
        SpringBeanFactory mainBeanFactory = processorContext.getMainBeanFactory();
        RequestMappingHandlerMapping requestMappingHandlerMapping = mainBeanFactory.getBean(RequestMappingHandlerMapping.class);

        String pluginId = processorContext.getPluginDescriptor().getPluginId();
        List<ControllerWrapper> controllerWrappers = processorContext.getRegistryInfo(PROCESS_CONTROLLERS);
        if(ObjectUtils.isEmpty(controllerWrappers)){
            return;
        }

        Iterator<ControllerWrapper> iterator = controllerWrappers.iterator();
        String pathPrefix = pluginId;
        PluginRequestMappingHandlerMapping pluginHandlerMapping = new PluginRequestMappingHandlerMapping(pathPrefix);

        while (iterator.hasNext()){
            ControllerWrapper controllerWrapper = iterator.next();
            if(!applicationContext.containsBean(controllerWrapper.getBeanName())){
                iterator.remove();
            }
            Object controllerBean = applicationContext.getBean(controllerWrapper.getBeanName());
            pluginHandlerMapping.registerHandler(controllerBean);
            List<RegisterMappingInfo> registerMappingInfo = pluginHandlerMapping.getAndClear();

            Set<RequestMappingInfo> requestMappingInfoSet = new HashSet<>(registerMappingInfo.size());
            for (RegisterMappingInfo mappingInfo : registerMappingInfo) {
                RequestMappingInfo requestMappingInfo = mappingInfo.getRequestMappingInfo();
                requestMappingHandlerMapping.registerMapping(
                        requestMappingInfo,
                        mappingInfo.getHandler(),
                        mappingInfo.getMethod()
                );
                log.info("插件[{}]注册接口: {}", pluginId, requestMappingInfo);
                requestMappingInfoSet.add(requestMappingInfo);
            }
            controllerWrapper.setRequestMappingInfo(requestMappingInfoSet);
        }
    }

    @Override
    public void close(ProcessorContext processorContext) throws ProcessorException {
        List<ControllerWrapper> controllerWrappers = processorContext.getRegistryInfo(PROCESS_CONTROLLERS);
        if(ObjectUtils.isEmpty(controllerWrappers)){
            return;
        }
        for (ControllerWrapper controllerWrapper : controllerWrappers) {
            unregister(processorContext, controllerWrapper);
        }
        controllerWrappers.clear();
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }

    @Override
    public String getName() {
        return PROCESS_CONTROLLERS;
    }

    /**
     * 卸载具体的Controller操作
     * @param controllerBeanWrapper controllerBean包装
     */
    private void unregister(ProcessorContext processorContext, ControllerWrapper controllerBeanWrapper) {
        SpringBeanFactory mainBeanFactory = processorContext.getMainBeanFactory();
        RequestMappingHandlerMapping requestMappingHandlerMapping = mainBeanFactory.getBean(RequestMappingHandlerMapping.class);
        Set<RequestMappingInfo> requestMappingInfoSet = controllerBeanWrapper.getRequestMappingInfo();
        if(requestMappingInfoSet != null && !requestMappingInfoSet.isEmpty()){
            for (RequestMappingInfo requestMappingInfo : requestMappingInfoSet) {
                requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                log.info("反注册接口: {}", requestMappingInfo);
            }
        }
        RequestMappingHandlerAdapter handlerAdapter = SpringBeanCustomUtils.getExistBean(processorContext.getMainApplicationContext(),
                RequestMappingHandlerAdapter.class);
        if(handlerAdapter != null){
            Class<?> beanClass = controllerBeanWrapper.getBeanClass();
            DestroyUtils.destroyValue(handlerAdapter, "sessionAttributesHandlerCache", beanClass);
            DestroyUtils.destroyValue(handlerAdapter, "initBinderCache", beanClass);
            DestroyUtils.destroyValue(handlerAdapter, "modelAttributeCache", beanClass);
        }
    }

    private static class ControllerPostProcessor implements BeanPostProcessor {

        private final static Logger LOG = LoggerFactory.getLogger(ControllerPostProcessor.class);

        private final ProcessorContext processorContext;

        private ControllerPostProcessor(ProcessorContext processorContext) {
            this.processorContext = processorContext;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            Class<?> aClass = bean.getClass();
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(aClass, RequestMapping.class);
            boolean isController = AnnotationUtils.existOr(aClass, new Class[]{
                    Controller.class, RestController.class
            });
            if(requestMapping != null && isController){
                addControllerWrapper(beanName, aClass);
            }
            return bean;
        }

        private void addControllerWrapper(String beanName, Class<?> aClass){
            List<ControllerWrapper> controllerWrappers = processorContext.getRegistryInfo(PROCESS_CONTROLLERS);
            if(controllerWrappers == null){
                controllerWrappers = new ArrayList<>();
                processorContext.addRegistryInfo(PROCESS_CONTROLLERS, controllerWrappers);
            }
            ControllerWrapper controllerWrapper = new ControllerWrapper();
            controllerWrapper.setBeanName(beanName);
            controllerWrapper.setBeanClass(aClass);
            controllerWrappers.add(controllerWrapper);
        }

    }

    @Data
    static class ControllerWrapper{

        /**
         * controller bean 名称
         */
        private String beanName;

        /**
         * controller bean 类型
         */
        private Class<?> beanClass;

        /**
         * controller 的 RequestMappingInfo 集合
         */
        private Set<RequestMappingInfo> requestMappingInfo;
    }

    private static class PluginRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

        private final List<RegisterMappingInfo> registerMappingInfo = new ArrayList<>();

        public PluginRequestMappingHandlerMapping(){
            this(null);
        }

        public PluginRequestMappingHandlerMapping(String pathPrefix){
            if(!ObjectUtils.isEmpty(pathPrefix)){
                Map<String, Predicate<Class<?>>> prefixes = new HashMap<>();
                prefixes.put(pathPrefix, c->true);
                setPathPrefixes(prefixes);
            }
        }

        public void registerHandler(Object handler){
            detectHandlerMethods(handler);
        }

        @Override
        protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
            super.registerHandlerMethod(handler, method, mapping);
            registerMappingInfo.add(new RegisterMappingInfo(handler, method, mapping));
        }

        public List<RegisterMappingInfo> getAndClear(){
            List<RegisterMappingInfo> registerMappingInfo = new ArrayList<>(this.registerMappingInfo);
            this.registerMappingInfo.clear();
            return registerMappingInfo;
        }

    }

    @AllArgsConstructor
    @Getter
    private static class RegisterMappingInfo{
        private final Object handler;
        private final Method method;
        private final RequestMappingInfo requestMappingInfo;
    }
}

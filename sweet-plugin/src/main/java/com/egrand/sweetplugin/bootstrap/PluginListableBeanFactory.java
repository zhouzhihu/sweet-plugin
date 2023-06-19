package com.egrand.sweetplugin.bootstrap;

import com.egrand.sweetplugin.bootstrap.annotation.AutowiredType;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.common.utils.DestroyUtils;
import com.egrand.sweetplugin.common.utils.ReflectionUtils;
import com.egrand.sweetplugin.spring.MainApplicationContext;
import com.egrand.sweetplugin.spring.SpringBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 插件BeanFactory实现
 */
public class PluginListableBeanFactory extends DefaultListableBeanFactory {

    private final MainApplicationContext applicationContext;
    private final AutowiredTypeResolver autowiredTypeResolver;

    public PluginListableBeanFactory(ProcessorContext processorContext) {
        this.applicationContext = processorContext.getMainApplicationContext();
        this.autowiredTypeResolver = new AutowiredTypeResolver(processorContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveDependency(DependencyDescriptor descriptor,
                                    @Nullable String requestingBeanName,
                                    @Nullable Set<String> autowiredBeanNames,
                                    @Nullable TypeConverter typeConverter) throws BeansException {
        AutowiredType.Type autowiredType = getAutowiredType(descriptor);
        Class<?> dependencyType = descriptor.getDependencyType();
        if (dependencyType == ObjectFactory.class || dependencyType == ObjectProvider.class) {
            Object dependencyObj = super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames,
                    typeConverter);
            ObjectProvider<Object> provider = (ObjectProvider<Object>) dependencyObj;
            return new PluginObjectProviderWrapper(provider, requestingBeanName, descriptor, autowiredType);
        }

        if(autowiredType == AutowiredType.Type.MAIN){
            Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
            if(dependencyObj != null){
                return dependencyObj;
            }
            throw new NoSuchBeanDefinitionException(descriptor.getDependencyType());
        } else if(autowiredType == AutowiredType.Type.PLUGIN){
            return super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
        } else if(autowiredType == AutowiredType.Type.PLUGIN_MAIN){
            try {
                return super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames,
                        typeConverter);
            } catch (BeansException e){
                if(e instanceof NoSuchBeanDefinitionException){
                    Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                    if(dependencyObj != null){
                        return dependencyObj;
                    }
                }
                throw e;
            }
        } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
            Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
            if(dependencyObj instanceof ObjectProvider){
                ObjectProvider<Object> provider = (ObjectProvider<Object>) dependencyObj;
                return new PluginObjectProviderWrapper(provider, requestingBeanName, descriptor, autowiredType);
            }
            if(dependencyObj != null){
                return dependencyObj;
            }
            return super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames,
                    typeConverter);
        }
        throw new NoSuchBeanDefinitionException(descriptor.getDependencyType());
    }

    @Override
    public void destroySingletons() {
        String[] beanDefinitionNames = getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            destroyBean(beanDefinitionName);
        }
        super.destroySingletons();
        destroyAll();
    }

//    @Override
//    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
//        return super.getBeanProvider(requiredType, allowEagerInit);
//    }

    protected AutowiredType.Type getAutowiredType(DependencyDescriptor descriptor){
        return autowiredTypeResolver.resolve(descriptor);
    }

    protected Object resolveDependencyFromMain(String requestingBeanName, DependencyDescriptor descriptor){
        Object dependencyObj = null;
        try {
            dependencyObj = applicationContext.resolveDependency(requestingBeanName,
                    descriptor.getDependencyType());
        } catch (Exception e){
            return null;
        }
        return dependencyObj;
    }

    private void destroyAll(){
        ReflectionUtils.findField(this.getClass(), field -> {
            field.setAccessible(true);
            try {
                Object o = field.get(this);
                DestroyUtils.destroyAll(o);
            } catch (IllegalAccessException e) {
                // 忽略
            }
            return false;
        });
    }


    private class PluginObjectProviderWrapper implements ObjectProvider<Object> {

        private final ObjectProvider<Object> pluginObjectProvider;

        private final String requestingBeanName;
        private final DependencyDescriptor descriptor;
        private final AutowiredType.Type autowiredType;

        private PluginObjectProviderWrapper(ObjectProvider<Object> pluginObjectProvider,
                                            String requestingBeanName,
                                            DependencyDescriptor descriptor,
                                            AutowiredType.Type autowiredType) {
            this.pluginObjectProvider = pluginObjectProvider;
            this.requestingBeanName = requestingBeanName;
            this.descriptor = new NestedDependencyDescriptor(descriptor);
            this.autowiredType = autowiredType;
        }

        @Override
        public Object getObject() throws BeansException {
            if(autowiredType == AutowiredType.Type.PLUGIN) {
                return pluginObjectProvider.getObject();
            } else if(autowiredType == AutowiredType.Type.MAIN){
                Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                if(dependencyObj != null){
                    return dependencyObj;
                }
            } else if(autowiredType == AutowiredType.Type.PLUGIN_MAIN) {
                try {
                    return pluginObjectProvider.getObject();
                } catch (Exception e) {
                    Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                    if (dependencyObj != null) {
                        return dependencyObj;
                    }
                    throw e;
                }
            } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
                Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                if(dependencyObj != null){
                    return dependencyObj;
                }
                return pluginObjectProvider.getObject();
            }
            throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
        }

        @Override
        public Object getObject(final Object... args) throws BeansException {
            if(autowiredType == AutowiredType.Type.PLUGIN){
                return pluginObjectProvider.getObject(args);
            } else if(autowiredType == AutowiredType.Type.MAIN){
                Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                if(dependencyObj != null){
                    return dependencyObj;
                }
            } else if(autowiredType == AutowiredType.Type.PLUGIN_MAIN){
                try {
                    return pluginObjectProvider.getObject();
                } catch (Exception e){
                    try {
                        return applicationContext.getSpringBeanFactory().getBean(requestingBeanName, args);
                    } catch (Exception e2){
                        // 忽略
                    }
                    throw e;
                }
            } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
                Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                if(dependencyObj != null){
                    return dependencyObj;
                }
                return pluginObjectProvider.getObject(args);
            }
            throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
        }

        @Override
        @Nullable
        public Object getIfAvailable() throws BeansException {
            if(autowiredType == AutowiredType.Type.PLUGIN){
                return pluginObjectProvider.getIfAvailable();
            } else if(autowiredType == AutowiredType.Type.MAIN){
                return resolveDependencyFromMain(requestingBeanName, descriptor);
            } else if(autowiredType == AutowiredType.Type.PLUGIN_MAIN){
                Object dependencyObj = pluginObjectProvider.getIfAvailable();
                if(dependencyObj == null){
                    dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                }
                return dependencyObj;
            } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
                Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                if(dependencyObj != null){
                    return dependencyObj;
                }
                return pluginObjectProvider.getIfAvailable();
            }
            return null;
        }

        @Override
        public void ifAvailable(Consumer<Object> dependencyConsumer) throws BeansException {
            Object ifAvailable = getIfAvailable();
            if(ifAvailable != null){
                dependencyConsumer.accept(ifAvailable);
            }
        }

        @Override
        @Nullable
        public Object getIfUnique() throws BeansException {
            if(autowiredType == AutowiredType.Type.PLUGIN){
                return pluginObjectProvider.getIfUnique();
            } else if(autowiredType == AutowiredType.Type.MAIN){
                return resolveDependencyFromMain(requestingBeanName, descriptor);
            } else if(autowiredType == AutowiredType.Type.PLUGIN_MAIN){
                Object dependencyObj = pluginObjectProvider.getIfUnique();
                if(dependencyObj == null){
                    dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                }
                return dependencyObj;
            } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
                Object dependencyObj = resolveDependencyFromMain(requestingBeanName, descriptor);
                if(dependencyObj != null){
                    return dependencyObj;
                }
                return pluginObjectProvider.getIfUnique();
            }
            return null;
        }

        @Override
        public void ifUnique(Consumer<Object> dependencyConsumer) throws BeansException {
            Object ifUnique = getIfUnique();
            if(ifUnique != null){
                dependencyConsumer.accept(ifUnique);
            }
        }

        @Override
        public Stream<Object> stream() {
            if(autowiredType == AutowiredType.Type.PLUGIN){
                return pluginObjectProvider.stream();
            } else if(autowiredType == AutowiredType.Type.MAIN){
                return getStreamOfMain().stream();
            } else if (autowiredType == AutowiredType.Type.PLUGIN_MAIN){
                Stream<Object> stream = pluginObjectProvider.stream();
                List<Object> collect = stream.collect(Collectors.toList());
                if(!collect.isEmpty()){
                    return collect.stream();
                }
                return getStreamOfMain().stream();
            } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
                Set<Object> collection = getStreamOfMain();
                if(!collection.isEmpty()){
                    return collection.stream();
                }
                return pluginObjectProvider.stream();
            }
            return Stream.empty();
        }

        @Override
        public Stream<Object> orderedStream() {
            if(autowiredType == AutowiredType.Type.PLUGIN){
                return pluginObjectProvider.orderedStream();
            } else if(autowiredType == AutowiredType.Type.MAIN){
                return getStreamOfMain().stream().sorted();
            } else if(autowiredType == AutowiredType.Type.PLUGIN_MAIN){
                Stream<Object> stream = pluginObjectProvider.stream();
                List<Object> collect = stream.collect(Collectors.toList());
                if(!collect.isEmpty()){
                    return collect.stream();
                }
                return getStreamOfMain().stream().sorted();
            } else if(autowiredType == AutowiredType.Type.MAIN_PLUGIN){
                Set<Object> collection = getStreamOfMain();
                if(!collection.isEmpty()){
                    return collection.stream().sorted();
                }
                return pluginObjectProvider.stream();
            }
            return Stream.empty();
        }

        @SuppressWarnings("unchecked")
        private Set<Object> getStreamOfMain(){
            SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
            Map<String, ?> beansOfType = springBeanFactory.getBeansOfType(descriptor.getDependencyType());
            return new HashSet<>(beansOfType.values());
        }
    }


    private static class NestedDependencyDescriptor extends DependencyDescriptor {

        public NestedDependencyDescriptor(DependencyDescriptor original) {
            super(original);
            increaseNestingLevel();
        }
    }

}

package com.egrand.sweetplugin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetplugin.SpringPluginManager;
import com.egrand.sweetplugin.service.IPluginServiceFactory;
import com.egrand.sweetplugin.service.PluginService;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

/**
 * 基础插件Service工厂
 */
public abstract class PluginServiceFactory<T extends PluginService> implements ApplicationContextAware, IPluginServiceFactory<T> {

    protected ApplicationContext applicationContext;

    @Autowired
    protected SpringPluginManager springPluginManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public List<T> getPluginService(String type) {
        Map<String, T> map = this.applicationContext.getBeansOfType(this.getClazz());
        String primaryKey = this.getPrimaryKey();
        List<T> serviceList = new ArrayList<>();
        map.forEach((k, v) -> {
            String serviceType = v.getType();
            String servicePrimaryKey = v.getPrimaryKey();
            if (type.equals(serviceType)) {
                if (StrUtil.isEmpty(servicePrimaryKey)) {
                    serviceList.add(v);
                } else {
                    if (servicePrimaryKey.equals(primaryKey)) {
                        serviceList.add(v);
                    }
                }
//                if (StrUtil.isEmpty(primaryKey)) {
//                    if (StrUtil.isEmpty(servicePrimaryKey)) {
//                        serviceList.add(v);
//                    }
//                } else {
//                    if (StrUtil.isNotEmpty(servicePrimaryKey) && primaryKey.equals(servicePrimaryKey)) {
//                        serviceList.add(v);
//                    }
//                }
            }
        });
        return serviceList;
    }

    @Override
    public Map<String, T> listPluginService() {
        Map<String, T> map = this.applicationContext.getBeansOfType(this.getClazz());
        String primaryKey = this.getPrimaryKey();
        Map<String, T> pluginServiceMap = new HashMap<>();
        map.forEach((k, v) -> {
            String servicePrimaryKey = v.getPrimaryKey();
            if (StrUtil.isEmpty(servicePrimaryKey)) {
                pluginServiceMap.put(k, v);
            } else {
                if (servicePrimaryKey.equals(primaryKey)) {
                    pluginServiceMap.put(k, v);
                }
            }
        });
        List<Integer> orderNoList = new ArrayList<>();
        for (String key : pluginServiceMap.keySet()) {
            orderNoList.add(pluginServiceMap.get(key).orderNo());
        }
        Collections.sort(orderNoList);
        Map<String, T> newPluginServiceMap = new LinkedHashMap<>();
        if(orderNoList.size() != 0) {
            for (Integer orderNo : orderNoList) {
                for (String key : pluginServiceMap.keySet()) {
                    if(pluginServiceMap.get(key).orderNo() == orderNo) {
                        newPluginServiceMap.put(key, pluginServiceMap.get(key));
                    }
                }
            }
        }
        return newPluginServiceMap;
    }

    @Override
    public PluginWrapper findPluginWrapper(Class pluginServiceClazz) {
        return this.springPluginManager.findPluginWrapper(pluginServiceClazz);
    }

    @Override
    public void started(PluginStateEvent pluginStateEvent, Set<String> extensionClassNames) {
        this.onPluginStarted(pluginStateEvent, this.getBean(pluginStateEvent.getPlugin()),
                this.getIncludeClassNames(pluginStateEvent.getPlugin(), extensionClassNames));
    }

    @Override
    public void stopped(PluginStateEvent pluginStateEvent, Set<String> extensionClassNames) {
        this.onPluginStopped(pluginStateEvent, this.getBean(pluginStateEvent.getPlugin()),
                this.getIncludeClassNames(pluginStateEvent.getPlugin(), extensionClassNames));
    }

    public void onPluginStarted(PluginStateEvent pluginStateEvent, List<T> beanList, List<Class<T>> extensionClasses) {
        // DoNothing
    }

    public void onPluginStopped(PluginStateEvent pluginStateEvent, List<T> beanList, List<Class<T>> extensionClasses) {
        // DoNothing
    }

    /**
     * 获取接口类型
     * @return
     */
    protected abstract Class getClazz();

    /**
     * 获取包含T类型的Bean集合
     * @param plugin
     * @return
     */
    protected List<T> getBean(PluginWrapper plugin) {
        ApplicationContext pluginApplicationContext = this.springPluginManager.getPluginApplicationContext(plugin.getPluginId());
        List<T> beanList = new ArrayList<>();
        if (null == pluginApplicationContext)
            return beanList;
        String[] beanNames = pluginApplicationContext.getBeanDefinitionNames();
        if (null != beanNames && beanNames.length != 0) {
            for (String beanName : beanNames) {
                if(getClazz().isInstance(pluginApplicationContext.getBean(beanName))) {
                    beanList.add((T) pluginApplicationContext.getBean(beanName));
                }
            }
        }
        return beanList;
    }

    /**
     * 获取包含T类型的类名称集合
     * @param extensionClassNames
     * @return
     */
    private List<Class<T>> getIncludeClassNames(PluginWrapper plugin, Set<String> extensionClassNames) {
        if(null == extensionClassNames || extensionClassNames.size() == 0)
            return null;
        List<Class<T>> clazzList = new ArrayList<>();
        extensionClassNames.forEach(className -> {
            try {
                Class<?> clazz = plugin.getPluginClassLoader().loadClass(className);
                if (this.getClazz().isAssignableFrom(clazz)) {
                    clazzList.add((Class<T>) clazz);
                }
            } catch (ClassNotFoundException e) {

            }
        });
        return clazzList;
    }

}

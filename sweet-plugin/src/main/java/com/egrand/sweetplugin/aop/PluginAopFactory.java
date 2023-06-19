package com.egrand.sweetplugin.aop;

import cn.hutool.core.util.StrUtil;
import com.egrand.sweetplugin.service.impl.PluginServiceFactory;
import org.pf4j.PluginStateEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AOP工厂，用于获取指定的AOP插件服务
 */
public class PluginAopFactory extends PluginServiceFactory<PluginAopService> {

    /**
     * 插件包含的接口实例，由插件启动和停止来维护
     */
    private Map<String, List<PluginAopService>> pluginBeanList = new HashMap<>();

    @Override
    public void onPluginStarted(PluginStateEvent pluginStateEvent, List<PluginAopService> beanList,
                                List<Class<PluginAopService>> extensionClasses) {
        if (null == beanList || beanList.size() == 0)
            return;
        this.pluginBeanList.put(pluginStateEvent.getPlugin().getPluginId(), beanList);
    }

    @Override
    public void onPluginStopped(PluginStateEvent pluginStateEvent, List<PluginAopService> beanList,
                                List<Class<PluginAopService>> extensionClasses) {
        this.pluginBeanList.remove(pluginStateEvent.getPlugin().getPluginId());
    }

    private PluginAopService getServiceFromPlugin(String type) {
        List<Object> beanList = new ArrayList<>();
        this.pluginBeanList.values().forEach(connectionAdapteServiceList -> beanList.addAll(connectionAdapteServiceList));
        if (null != beanList && beanList.size() != 0) {
            for (Object bean : beanList) {
                PluginAopService pluginAopService = (PluginAopService) bean;
                if (pluginAopService.getType().equals(type.toUpperCase()))
                    return pluginAopService;
            }
        }
        return null;
    }

    @Override
    protected Class getClazz() {
        return PluginAopService.class;
    }

    public PluginAopService getPluginAopService(String type) {
        if (StrUtil.isEmpty(type))
            return null;
        PluginAopService pluginAopService = this.filter(this.getPluginService(type), type);
        if (null == pluginAopService)
            pluginAopService = this.getServiceFromPlugin(type);
        return pluginAopService;
    }

    @Override
    public String getPrimaryKey() {
        // TODO 待修复
        // return TenantContextHolder.getTenant();
        return "";
    }

    private PluginAopService filter(List<PluginAopService> pluginAopServiceList, String type) {
        if (null == pluginAopServiceList || pluginAopServiceList.size() == 0)
            return null;
        if (pluginAopServiceList.size() == 1)
            return pluginAopServiceList.get(0);
        List<PluginAopService> priTmp = new ArrayList<>();
        List<PluginAopService> tmp = new ArrayList<>();
        for (PluginAopService pluginAopService : pluginAopServiceList) {
            if (pluginAopService.getPrimaryKey().equals(this.getPrimaryKey())) {
                priTmp.add(pluginAopService);
            } else {
                tmp.add(pluginAopService);
            }
        }
        if (priTmp.size() == 1)
            return priTmp.get(0);

        if (priTmp.size() > 1 || tmp.size() > 1) {
            throw new RuntimeException("存在多个[" + type + "]类型的NotifySender，请检查！");
        }
        return null;
    }
}

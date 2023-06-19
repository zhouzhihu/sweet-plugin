package com.egrand.sweetplugin.service;

import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPluginServiceFactory<T> {

    /**
     * 根据类型获取插件服务
     * @param type 类型
     * @return 插件服务
     */
    List<T> getPluginService(String type);

    /**
     * 获取所有插件接口实现
     * @return
     */
    Map<String, T> listPluginService();

    /**
     * 根据插件服务类查找所属的插件信息
     * @param pluginServiceClazz 插件服务类
     * @return 插件信息
     */
    PluginWrapper findPluginWrapper(Class pluginServiceClazz);

    /**
     * 提供关键字，用于在出现多个PluginService时，可以通过该值对比Service中的primaryKey来过滤，通常设置为当前租户ID
     * @return
     */
    String getPrimaryKey();

    /**
     * 启动插件监听事件
     * @param pluginStateEvent 插件事件体
     * @param extensionClassNames 插件类全名
     */
    void started(PluginStateEvent pluginStateEvent, Set<String> extensionClassNames);

    /**
     * 停止插件监听事件
     * @param pluginStateEvent 插件事件体
     * @param extensionClassNames 插件类全名
     */
    void stopped(PluginStateEvent pluginStateEvent, Set<String> extensionClassNames);
}

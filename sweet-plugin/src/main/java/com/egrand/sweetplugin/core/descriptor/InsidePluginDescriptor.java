package com.egrand.sweetplugin.core.descriptor;

import org.pf4j.PluginDescriptor;

import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

/**
 * 内部的PluginDescriptor
 */
public interface InsidePluginDescriptor extends PluginDescriptor {

    /**
     * 得到插件的 Properties 配置
     * @return Properties
     */
    Properties getProperties();

    /**
     * 获取插件配置文件名称。
     * 和 getConfigFileLocation 配置二选一, 如果都有值则默认使用 getConfigFileName
     * @return String
     */
    String getConfigFileName();

    /**
     * 获取插件配置文件路径。
     * 和 getConfigFileName 配置二选一, 如果都有值则默认使用 getConfigFileName
     * @return String
     */
    String getConfigFileLocation();

    /**
     * 得到插件启动时参数
     * @return String
     */
    String getArgs();

    /**
     * 得到内部的插件路径
     * @return Path
     */
    Path getInsidePluginPath();

    /**
     * 获取插件文件名称
     * @return String
     */
    String getPluginFileName();


    /**
     * 获取插件classes path路径
     * @return Path
     */
    String getPluginClassPath();

    /**
     * 设置当前插件包含主程序加载资源的匹配
     * @return Set
     */
    Set<String> getIncludeMainResourcePatterns();

    /**
     * 设置当前插件排除从主程序加载资源的匹配
     * @return Set
     */
    Set<String> getExcludeMainResourcePatterns();

    /**
     * 转换为 PluginDescriptor
     * @return PluginDescriptor
     */
    PluginDescriptor toPluginDescriptor();

}

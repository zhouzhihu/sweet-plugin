package com.egrand.sweetplugin.core.descriptor;

import org.pf4j.PluginDescriptor;
import org.pf4j.PropertiesPluginDescriptorFinder;

import java.util.Properties;

import static com.egrand.sweetplugin.common.utils.PropertiesUtils.getValue;

/**
 * Spring属性插件信息查找器，用于扩展PluginDescriptor信息
 */
public class PropertiesSpringPluginDescriptorFinder extends PropertiesPluginDescriptorFinder {

    public static final String PLUGIN_CONFIG_FILE_NAME = "plugin.configFileName";
    public static final String PLUGIN_CONFIG_FILE_LOCATION = "plugin.configFileLocation";
    public static final String PLUGIN_ARGS = "plugin.args";
    /** System create prop **/
    public static final String PLUGIN_RESOURCES_CONFIG = "plugin.system.resourcesConfig";

    @Override
    protected PluginDescriptor createPluginDescriptor(Properties properties) {
        PluginDescriptor pluginDescriptor = super.createPluginDescriptor(properties);
        DefaultInsidePluginDescriptor descriptor = new DefaultInsidePluginDescriptor(pluginDescriptor);

        descriptor.setProperties(properties);
        descriptor.setConfigFileName(getValue(properties, PLUGIN_CONFIG_FILE_NAME, false));
        descriptor.setConfigFileLocation(getValue(properties, PLUGIN_CONFIG_FILE_LOCATION, false));
        descriptor.setPluginResourcesConfig(getValue(properties, PLUGIN_RESOURCES_CONFIG, false));
        descriptor.setArgs(getValue(properties, PLUGIN_ARGS, false));
        return descriptor;
    }
}

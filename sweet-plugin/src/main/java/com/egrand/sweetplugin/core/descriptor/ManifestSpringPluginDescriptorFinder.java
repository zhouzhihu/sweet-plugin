package com.egrand.sweetplugin.core.descriptor;

import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptor;
import org.pf4j.util.StringUtils;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Spring Manifest插件信息查找器，用于扩展PluginDescriptor信息
 */
public class ManifestSpringPluginDescriptorFinder extends ManifestPluginDescriptorFinder {

    public static final String PLUGIN_CONFIG_FILE_NAME = "Plugin-ConfigFileName";
    public static final String PLUGIN_CONFIG_FILE_LOCATION = "Plugin-ConfigFileLocation";
    public static final String PLUGIN_ARGS = "Plugin-Args";
    /** System create prop **/
    public static final String PLUGIN_RESOURCES_CONFIG = "Plugin-System-ResourcesConfig";

    @Override
    protected PluginDescriptor createPluginDescriptor(Manifest manifest) {
        PluginDescriptor pluginDescriptor = super.createPluginDescriptor(manifest);
        DefaultInsidePluginDescriptor descriptor = new DefaultInsidePluginDescriptor(pluginDescriptor);
        Attributes attributes = manifest.getMainAttributes();

        String pluginConfigFileName = attributes.getValue(PLUGIN_CONFIG_FILE_NAME);
        if (StringUtils.isNotNullOrEmpty(pluginConfigFileName)) {
            descriptor.setConfigFileName(pluginConfigFileName);
        }

        String pluginConfigFileLocation = attributes.getValue(PLUGIN_CONFIG_FILE_LOCATION);
        if (StringUtils.isNotNullOrEmpty(pluginConfigFileLocation)) {
            descriptor.setConfigFileLocation(pluginConfigFileLocation);
        }

        String pluginResourcesConfig = attributes.getValue(PLUGIN_RESOURCES_CONFIG);
        if (StringUtils.isNotNullOrEmpty(pluginResourcesConfig)) {
            descriptor.setPluginResourcesConfig(pluginResourcesConfig);
        }

        String pluginArgs = attributes.getValue(PLUGIN_ARGS);
        if (StringUtils.isNotNullOrEmpty(pluginArgs)) {
            descriptor.setArgs(pluginArgs);
        }
        return descriptor;
    }
}

package com.egrand.sweetplugin.core.descriptor;

import lombok.Getter;
import lombok.Setter;
import org.pf4j.DefaultPluginDescriptor;
import org.pf4j.PluginDescriptor;

import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

/**
 * 内部的默认插件描述者
 */
public class DefaultInsidePluginDescriptor extends DefaultPluginDescriptor implements InsidePluginDescriptor {

    private final Path pluginPath;
    private final String pluginFileName;

    @Setter
    private String pluginClassPath;
    @Setter
    private Properties properties;
    @Setter
    private String configFileName;
    @Setter
    private String configFileLocation;
    @Setter
    private String args;
    @Setter
    private Set<String> includeMainResourcePatterns;
    @Setter
    private Set<String> excludeMainResourcePatterns;
    @Setter
    @Getter
    private String pluginResourcesConfig;

    public DefaultInsidePluginDescriptor(PluginDescriptor pluginDescriptor) {
        super(pluginDescriptor.getPluginId(),
                pluginDescriptor.getPluginDescription(), pluginDescriptor.getPluginClass(), pluginDescriptor.getVersion(),
                pluginDescriptor.getRequires(), pluginDescriptor.getProvider(), pluginDescriptor.getLicense());
        this.pluginPath = null;
        this.pluginFileName = "";
    }

    public DefaultInsidePluginDescriptor(String pluginId, String pluginDescription, String pluginClass,
                                         String pluginVersion, String pluginRequires, String pluginProvider,
                                         String pluginLicense, Path pluginPath) {
        super(pluginId, pluginDescription, pluginClass, pluginVersion, pluginRequires, pluginProvider, pluginLicense);
        this.pluginPath = pluginPath;
        this.pluginFileName = pluginPath.toFile().getName();
    }


    @Override
    public String getPluginClassPath() {
        return pluginClassPath;
    }

    @Override
    public Set<String> getIncludeMainResourcePatterns() {
        return includeMainResourcePatterns;
    }

    @Override
    public Set<String> getExcludeMainResourcePatterns() {
        return excludeMainResourcePatterns;
    }

    @Override
    public String getConfigFileName() {
        return configFileName;
    }

    @Override
    public String getConfigFileLocation() {
        return configFileLocation;
    }

    @Override
    public String getArgs() {
        return args;
    }

    @Override
    public Path getInsidePluginPath() {
        return pluginPath;
    }

    @Override
    public String getPluginFileName() {
        return pluginFileName;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public PluginDescriptor toPluginDescriptor() {
        DefaultPluginDescriptor descriptor = new DefaultPluginDescriptor(
                getPluginId(), getPluginDescription(), getPluginClass(), getVersion(), getRequires(), getProvider(), getLicense()
        );
        return descriptor;
    }

}

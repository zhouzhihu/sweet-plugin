package com.egrand.sweetplugin.bootstrap;

import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.common.utils.Assert;
import com.egrand.sweetplugin.common.utils.FilesUtils;
import com.egrand.sweetplugin.common.utils.ObjectUtils;
import com.egrand.sweetplugin.common.utils.PluginFileUtils;
import com.egrand.sweetplugin.core.descriptor.InsidePluginDescriptor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件环境配置
 */
class ConfigurePluginEnvironment {

    private final static String PLUGIN_PROPERTY_NAME = "pluginPropertySources";

    private final static String SPRING_CONFIG_NAME = "spring.config.name";
    private final static String SPRING_CONFIG_LOCATION = "spring.config.location";

    private final static String SPRING_JMX_UNIQUE_NAMES = "spring.jmx.unique-names";
    private final static String SPRING_ADMIN_JMX_NAME = "spring.application.admin.jmx-name";
    private final static String SPRING_ADMIN_JMX_VALUE = "org.springframework.boot:type=Admin,name=";

    public static final String REGISTER_SHUTDOWN_HOOK_PROPERTY = "logging.register-shutdown-hook";
    public static final String MBEAN_DOMAIN_PROPERTY_NAME = "spring.liveBeansView.mbeanDomain";

    private final ProcessorContext processorContext;
    private final InsidePluginDescriptor pluginDescriptor;

    ConfigurePluginEnvironment(ProcessorContext processorContext) {
        this.processorContext = Assert.isNotNull(processorContext, "processorContext 不能为空");
        this.pluginDescriptor = Assert.isNotNull(processorContext.getPluginDescriptor(),
                "pluginDescriptor 不能为空");
    }

    void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        Map<String, Object> env = new HashMap<>();
        String pluginId = pluginDescriptor.getPluginId();
        String configFileName = pluginDescriptor.getConfigFileName();
        if(!ObjectUtils.isEmpty(configFileName)){
            env.put(SPRING_CONFIG_NAME, PluginFileUtils.getFileName(configFileName));
        }
        String configFileLocation = pluginDescriptor.getConfigFileLocation();
        if(!ObjectUtils.isEmpty(configFileLocation)){
            env.put(SPRING_CONFIG_LOCATION, getConfigFileLocation(configFileLocation));
        }
//        env.put(AutoIntegrationConfiguration.ENABLE_STARTER_KEY, false);
        env.put(SPRING_JMX_UNIQUE_NAMES, true);
        env.put(SPRING_ADMIN_JMX_NAME, SPRING_ADMIN_JMX_VALUE + pluginId);
        env.put(REGISTER_SHUTDOWN_HOOK_PROPERTY, false);
        env.put(MBEAN_DOMAIN_PROPERTY_NAME, pluginId);
        environment.getPropertySources().addFirst(new MapPropertySource(PLUGIN_PROPERTY_NAME, env));

//        if(processorContext.runMode() == ProcessorContext.RunMode.ONESELF){
//            ConfigureMainPluginEnvironment configureMainPluginEnvironment =
//                    new ConfigureMainPluginEnvironment(processorContext);
//            configureMainPluginEnvironment.configureEnvironment(environment, args);
//        }
    }

    private String getConfigFileLocation(String configFileLocation){
        String s = FilesUtils.resolveRelativePath(new File("").getAbsolutePath(), configFileLocation);
        if(s.endsWith("/") || s.endsWith(File.separator)){
            return s;
        } else {
            return s + File.separator;
        }
    }

}

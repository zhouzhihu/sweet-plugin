package com.egrand.sweetplugin.bootstrap;

import com.egrand.sweetplugin.SpringPlugin;
import com.egrand.sweetplugin.action.DefaultPluginActionManager;
import com.egrand.sweetplugin.action.PluginAction;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;

/**
 * 插件SpringApplication实现
 */
public class PluginSpringApplication extends SpringApplication {

    private final Logger logger = LoggerFactory.getLogger(PluginSpringApplication.class);

    private final ProcessorContext.RunMode runMode;
    private final GenericApplicationContext applicationContext;

    private final PluginAction pluginActionManager;
    private final ProcessorContext processorContext;
    private final ResourceLoader resourceLoader;
    private final ConfigurePluginEnvironment configurePluginEnvironment;

    public PluginSpringApplication(ProcessorContext processorContext,
                                   Class<?>... primarySources) {
        super(primarySources);
        this.runMode = processorContext.runMode();
        this.pluginActionManager = new DefaultPluginActionManager(processorContext.runMode(), null);
        this.processorContext = processorContext;
        this.resourceLoader = processorContext.getResourceLoader();
        this.configurePluginEnvironment = new ConfigurePluginEnvironment(processorContext);
        this.applicationContext = getApplicationContext();
        setDefaultPluginConfig();
    }

    protected GenericApplicationContext getApplicationContext(){
        return ((SpringPlugin)processorContext.getPluginWrapper().getPlugin()).start(this.processorContext, this.pluginActionManager);
    }

    public void setDefaultPluginConfig(){
        if(runMode == ProcessorContext.RunMode.PLUGIN){
            setResourceLoader(resourceLoader);
            setBannerMode(Banner.Mode.OFF);
            setEnvironment(new StandardEnvironment());
            setWebApplicationType(WebApplicationType.NONE);
            setRegisterShutdownHook(false);
            setLogStartupInfo(false);
        }
    }

    @Override
    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        super.configureEnvironment(environment, args);
        configurePluginEnvironment.configureEnvironment(environment, args);
    }

    @Override
    protected ConfigurableApplicationContext createApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public ConfigurableApplicationContext run(String... args) {
        try {
            processorContext.setApplicationContext(this.applicationContext);
            PluginContextHolder.initialize(processorContext);
            pluginActionManager.initialize(processorContext);
            return super.run(args);
        } catch (Exception e) {
            pluginActionManager.failure(processorContext);
            logger.debug("启动插件[{}]失败. {}",
                    processorContext.getPluginDescriptor().getPluginId(),
                    e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void refresh(ApplicationContext applicationContext) {
        pluginActionManager.refreshBefore(processorContext);
        super.refresh(applicationContext);
        pluginActionManager.refreshAfter(processorContext);
    }

}

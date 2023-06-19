package com.egrand.sweetplugin.spring.boot.starter;

import com.egrand.sweetplugin.SpringPluginManager;
import com.egrand.sweetplugin.aop.PluginAopFactory;
import com.egrand.sweetplugin.service.IPluginServiceFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginStateEvent;
import org.pf4j.PluginWrapper;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@AllArgsConstructor
public class PluginAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public PluginAopFactory pluginAopFactory() {
        return new PluginAopFactory();
    }

    // TODO 这里去掉Feign
//    @Bean
//    public FeignClientBuilder feignClientBuilder() {
//        return new FeignClientBuilder(this.applicationContext);
//    }

    @Bean
    @ConditionalOnMissingBean
    public SpringPluginManager createPluginManager() {
        SpringPluginManager springPluginManager = new SpringPluginManager();
        springPluginManager.addPluginStateListener(pluginStateEvent -> {
            if (pluginStateEvent.getPluginState().toString().equals(pluginStateEvent.getOldState().toString())) {
                return;
            }
            PluginWrapper plugin = pluginStateEvent.getPlugin();
            Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
            switch (pluginStateEvent.getPluginState()) {
                case STARTED:
                case STOPPED:
                    this.on(pluginStateEvent, extensionClassNames);
                    return;
                default:
                    break;
            }
        });
        return springPluginManager;
    }

    private void on(PluginStateEvent pluginStateEvent, Set<String> extensionClassNames) {
        try {
            Map<String, IPluginServiceFactory> pluginServiceFactoryMap = this.applicationContext.
                    getBeansOfType(IPluginServiceFactory.class);
            if (null != pluginServiceFactoryMap && pluginServiceFactoryMap.size() != 0) {
                switch (pluginStateEvent.getPluginState()) {
                    case STARTED:
                        pluginServiceFactoryMap.keySet().forEach(key -> pluginServiceFactoryMap.get(key).
                                started(pluginStateEvent, extensionClassNames));
                        break;
                    case STOPPED:
                        pluginServiceFactoryMap.keySet().forEach(key -> pluginServiceFactoryMap.get(key).
                                stopped(pluginStateEvent, extensionClassNames));
                        break;
                    default:
                        break;
                }

            }
        } catch (IllegalStateException e) {
            log.warn("PluginStateEvent Warn");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

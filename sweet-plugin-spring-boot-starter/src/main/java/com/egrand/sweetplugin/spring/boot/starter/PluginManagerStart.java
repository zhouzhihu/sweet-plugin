package com.egrand.sweetplugin.spring.boot.starter;

import com.egrand.sweetplugin.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class PluginManagerStart implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private SpringPluginManager springPluginManager;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationReadyEvent) {
            this.springPluginManager.startPlugins();
        }
    }
}

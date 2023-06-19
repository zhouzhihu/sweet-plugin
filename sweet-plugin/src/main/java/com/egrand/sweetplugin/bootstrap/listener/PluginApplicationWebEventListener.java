package com.egrand.sweetplugin.bootstrap.listener;

import com.egrand.sweetplugin.bootstrap.PluginWebApplicationContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;

/**
 * 插件监听器扩展
 *
 * @author starBlues
 * @version 3.0.3
 */
public class PluginApplicationWebEventListener implements ApplicationListener<ApplicationEvent> {


    private final PluginWebApplicationContext applicationContext;

    public PluginApplicationWebEventListener(PluginWebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            callWebServerInitializedEvent();
        }
    }

    @SuppressWarnings("all")
    protected void callWebServerInitializedEvent(){
        String[] beanNamesForType = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(
                ApplicationListener.class, WebServerInitializedEvent.class
        ));
        PluginWebServerInitializedEvent pluginWebServerInitializedEvent =
                new PluginWebServerInitializedEvent(applicationContext);
        for (String beanName : beanNamesForType) {
            try {
                ApplicationListener<WebServerInitializedEvent> applicationListener =
                        (ApplicationListener<WebServerInitializedEvent>) applicationContext.getBean(beanName);
                applicationListener.onApplicationEvent(pluginWebServerInitializedEvent);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class PluginWebServerInitializedEvent extends WebServerInitializedEvent{

        private final PluginWebApplicationContext pluginWebApplicationContext;

        protected PluginWebServerInitializedEvent(PluginWebApplicationContext pluginWebApplicationContext) {
            super(pluginWebApplicationContext.getWebServer());
            this.pluginWebApplicationContext = pluginWebApplicationContext;
        }

        @Override
        public WebServerApplicationContext getApplicationContext() {
            return pluginWebApplicationContext;
        }
    }

}

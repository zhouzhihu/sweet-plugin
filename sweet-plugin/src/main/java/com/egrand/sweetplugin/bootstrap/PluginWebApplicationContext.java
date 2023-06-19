package com.egrand.sweetplugin.bootstrap;

import com.egrand.sweetplugin.bootstrap.listener.PluginApplicationWebEventListener;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.spring.environment.EnvironmentProvider;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

/**
 * 主程序为 web 类型时创建的插件 ApplicationContext
 */
public class PluginWebApplicationContext extends PluginApplicationContext implements WebServerApplicationContext {

    private final WebServer webServer;
    private final String serverNamespace;

    public PluginWebApplicationContext(ProcessorContext processorContext) {
        super(processorContext.getPluginBeanFactory(), processorContext);
        this.scan(processorContext.getRunnerPackage());
        this.webServer = new PluginSimulationWebServer(processorContext);
        this.serverNamespace = processorContext.getPluginDescriptor().getPluginId();
        addApplicationListener(new PluginApplicationWebEventListener(this));
    }

    @Override
    public WebServer getWebServer() {
        return webServer;
    }

    @Override
    public String getServerNamespace() {
        return serverNamespace;
    }


    public static class PluginSimulationWebServer implements WebServer {

        private final int port;

        public PluginSimulationWebServer(ProcessorContext processorContext) {
            EnvironmentProvider provider = processorContext.getMainApplicationContext().getEnvironmentProvider();
            Integer port = provider.getInteger("server.port");
            if(port == null){
                this.port = -1;
            } else {
                this.port = port;
            }
        }

        @Override
        public void start() throws WebServerException {
            throw new InvalidWebServerException();
        }

        @Override
        public void stop() throws WebServerException {
            throw new InvalidWebServerException();
        }

        @Override
        public int getPort() {
            return port;
        }

    }

    public static class InvalidWebServerException extends WebServerException{

        public InvalidWebServerException() {
            super("Invalid operation", null);
        }
    }

}

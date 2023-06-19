/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.egrand.sweetplugin.action.impl.extension;

import com.egrand.sweetplugin.SpringPluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Decebal Suiu
 */
public class ExtensionsInjector {

    private static final Logger log = LoggerFactory.getLogger(ExtensionsInjector.class);

    protected final SpringPluginManager springPluginManager;
    protected final AbstractAutowireCapableBeanFactory beanFactory;

    public ExtensionsInjector(SpringPluginManager springPluginManager, AbstractAutowireCapableBeanFactory beanFactory) {
        this.springPluginManager = springPluginManager;
        this.beanFactory = beanFactory;
    }

    public void injectExtensions() {
        // add extensions from classpath (non plugin)
        Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(null);
        for (String extensionClassName : extensionClassNames) {
            try {
                log.debug("Register extension '{}' as bean", extensionClassName);
                Class<?> extensionClass = getClass().getClassLoader().loadClass(extensionClassName);
                registerExtension(extensionClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }

        // add extensions for each started plugin
        List<PluginWrapper> startedPlugins = springPluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            if (plugin.getPluginState() != PluginState.STARTED)
                continue;
            log.debug("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
            extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
            for (String extensionClassName : extensionClassNames) {
                try {
                    log.debug("Register extension '{}' as bean", extensionClassName);
                    Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
                    registerExtension(extensionClass);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public void injectExtension(PluginWrapper plugin) {
        if (plugin.getPluginState() == PluginState.STARTED)
            return;
        log.info("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
        Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
        for (String extensionClassName : extensionClassNames) {
            try {
                log.info("Register extension '{}' as bean", extensionClassName);
                Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
                registerExtension(extensionClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void rejectExtension(PluginWrapper plugin) {
        Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
        for (String extensionClassName : extensionClassNames) {
            try {
                log.info("Destroy extension '{}' as bean", extensionClassName);
                Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
                destroyExtension(extensionClass);
            } catch (Exception e) {

            }
        }
    }

    public void removeExtensions(PluginWrapper plugin) {
        Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
        for (String extensionClassName : extensionClassNames) {
            try {
                Class<?> extensionClass = plugin.getPluginClassLoader().loadClass(extensionClassName);
                springPluginManager.getExtensionsInjector().destroyExtension(extensionClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Register an extension as bean.
     * Current implementation register extension as singleton using {@code beanFactory.registerSingleton()}.
     * The extension instance is created using {@code pluginManager.getExtensionFactory().create(extensionClass)}.
     * The bean name is the extension class name.
     * Override this method if you wish other register strategy.
     */
    protected void registerExtension(Class<?> extensionClass) {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(extensionClass);
        if (extensionBeanMap.isEmpty()) {
            Object extension = springPluginManager.getExtensionFactory().create(extensionClass);
            if (null != beanFactory.getSingleton(extensionClass.getName())) {
                this.destroyExtension(extensionClass);
            }
            beanFactory.registerSingleton(extensionClass.getName(), extension);
        } else {
            log.debug("Bean registeration aborted! Extension '{}' already existed as bean!", extensionClass.getName());
        }
    }

    /**
     * Destroy an extension as bean.
     * Current implementation destroy extension as singleton using {@code beanFactory.destroySingleton()}.
     * The bean name is the extension class name.
     * Override this method if you wish other destroy strategy.
     */
    protected void destroyExtension(Class<?> extensionClass) {
        beanFactory.destroySingleton(extensionClass.getName());
    }

}

package com.egrand.sweetplugin.action.impl;

import com.egrand.sweetplugin.action.PluginAction;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorException;

public class PluginExtensionsAction implements PluginAction {

    @Override
    public void refreshAfter(ProcessorContext processorContext) throws ProcessorException {
        processorContext.getExtensionsInjector().injectExtension(processorContext.getPluginWrapper());
    }

    @Override
    public void close(ProcessorContext processorContext) throws ProcessorException {
        processorContext.getExtensionsInjector().rejectExtension(processorContext.getPluginWrapper());
    }

    @Override
    public void failure(ProcessorContext processorContext) throws ProcessorException {
        processorContext.getExtensionsInjector().rejectExtension(processorContext.getPluginWrapper());
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }

    @Override
    public String getName() {
        return "PLUGIN_EXTENSIONS";
    }
}

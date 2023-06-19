package com.egrand.sweetplugin.common.utils;

import com.egrand.sweetplugin.action.PluginAction;
import java.util.List;
import java.util.function.Supplier;

/**
 * ProcessorUtils
 */
public class ProcessorUtils {

    public static void add(List<PluginAction> pluginActions, Supplier<PluginAction> supplier){
        try {
            PluginAction pluginProcessor = supplier.get();
            pluginActions.add(pluginProcessor);
        } catch (Throwable e){
            // 忽略
        }
    }


}

package com.egrand.sweetplugin.action;

import com.egrand.sweetplugin.action.impl.PluginAopAction;
import com.egrand.sweetplugin.action.impl.PluginControllerAction;
import com.egrand.sweetplugin.action.impl.PluginExtensionsAction;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorException;
import com.egrand.sweetplugin.common.utils.ObjectUtils;
import com.egrand.sweetplugin.common.utils.OrderPriority;
import com.egrand.sweetplugin.common.utils.OrderUtils;
import com.egrand.sweetplugin.common.utils.ProcessorUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件动作管理
 */
@Slf4j
public class DefaultPluginActionManager implements PluginAction {

    private List<PluginAction> pluginActionList;

    private final ProcessorContext.RunMode runMode;

    public DefaultPluginActionManager(ProcessorContext.RunMode runMode, List<PluginAction> pluginActionList) {
        this.runMode = runMode;
        if(!ObjectUtils.isEmpty(pluginActionList)){
            this.pluginActionList = pluginActionList;
        } else {
            this.pluginActionList = new ArrayList<>();
        }
    }

    @Override
    public void initialize(ProcessorContext context) throws ProcessorException {
        List<PluginAction> pluginActions = new ArrayList<>();
        addDefaultProcessors(context, pluginActions);
        addDefaultWebEnvProcessors(context, pluginActions);
        pluginActions.addAll(this.pluginActionList);
        this.pluginActionList = pluginActions.stream()
                .filter(p->{
                    ProcessorContext.RunMode runMode = p.runMode();
                    return runMode == ProcessorContext.RunMode.ALL || runMode == this.runMode;
                })
                .sorted(OrderUtils.orderPriority(PluginAction::order))
                .collect(Collectors.toList());

        for (PluginAction pluginAction : pluginActionList) {
            try {
                pluginAction.initialize(context);
            } catch (Throwable e){
                log.error("插件动作 " + pluginAction.getName() + " 错误！", e);
                processException(pluginAction, "initialize", e, true);
            }
        }
    }

    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        for (PluginAction processor : pluginActionList) {
            try {
                processor.refreshBefore(context);
            } catch (Throwable e){
                processException(processor, "refreshBefore", e, true);
            }
        }
    }

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        for (PluginAction processor : pluginActionList) {
            try {
                processor.refreshAfter(context);
            } catch (Throwable e){
                processException(processor, "refreshAfter", e, true);
            }
        }
    }

    @Override
    public void failure(ProcessorContext context) throws ProcessorException {
        for (PluginAction processor : pluginActionList) {
            try {
                processor.failure(context);
            } catch (Throwable e){
                processException(processor, "failure", e, false);
            }
        }
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        for (PluginAction processor : pluginActionList) {
            try {
                processor.close(context);
            } catch (Throwable e){
                processException(processor, "close", e, false);
            }
        }
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.ALL;
    }


    @Override
    public String getName() {
        return null;
    }

    /**
     * 获取默认的处理者
     * @param context ProcessorContext
     * @param pluginActions 处理者容器集合
     */
    protected void addDefaultProcessors(ProcessorContext context, List<PluginAction> pluginActions){
        pluginActions.add(new PluginAopAction());
    }

    /**
     * 添加默认web环境处理者
     * @param context ProcessorContext
     * @param processors 处理者容器集合
     */
    protected void addDefaultWebEnvProcessors(ProcessorContext context, List<PluginAction> processors){
        if(!context.getMainApplicationContext().isWebEnvironment()){
            // 主程序不是web类型, 则不进行注册
            return;
        }
        context.getWebConfig().setEnable(true);
        processors.add(new PluginControllerAction());
        processors.add(new PluginExtensionsAction());
//        processors.add(new PluginInterceptorsProcessor());
//        processors.add(new PluginStaticResourceProcessor());
//        processors.add(new PluginThymeleafProcessor());
//        ProcessorUtils.add(processors, PluginSpringDocControllerProcessor::new);
    }

    public void addPluginAction(PluginAction pluginAction) {
        this.pluginActionList.add(pluginAction);
    }

    private void processException(PluginAction pluginAction, String executeType,
                                  Throwable e, boolean isThrow) throws ProcessorException{
        String error =  "Processor[" + pluginAction.getClass().getName() + "] execute[" + executeType + "] failure : "
                + e.getMessage();
        if(isThrow){
            throw new ProcessorException(error, e);
        }
    }
}

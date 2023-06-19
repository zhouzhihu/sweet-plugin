package com.egrand.sweetplugin.bootstrap;

import com.egrand.sweetplugin.bootstrap.annotation.AutowiredType;
import com.egrand.sweetplugin.bootstrap.processor.ProcessorContext;
import com.egrand.sweetplugin.bootstrap.realize.AutowiredTypeDefiner;
import com.egrand.sweetplugin.common.utils.UrlUtils;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collections;
import java.util.Set;

public class AutowiredTypeResolver {

    private final Set<AutowiredTypeDefiner.ClassDefiner> classDefiners;
    private final PathMatcher pathMatcher = new AntPathMatcher();


    public AutowiredTypeResolver(ProcessorContext processorContext) {
        AutowiredTypeDefiner autowiredTypeDefiner = processorContext.getSpringPluginBootstrap().autowiredTypeDefiner();
        if(autowiredTypeDefiner != null){
            AutowiredTypeDefinerConfig definerConfig = new AutowiredTypeDefinerConfig();
            autowiredTypeDefiner.config(definerConfig);
            classDefiners = definerConfig.getClassDefiners();
        } else {
            classDefiners = Collections.emptySet();
        }
    }

    public AutowiredType.Type resolve(DependencyDescriptor descriptor){
        String name = descriptor.getDependencyType().getName();
        String classNamePath = UrlUtils.formatMatchUrl(name);
        for (AutowiredTypeDefiner.ClassDefiner classDefiner : classDefiners) {
            Set<String> classNamePatterns = classDefiner.getClassNamePatterns();
            for (String classNamePattern : classNamePatterns) {
                if(pathMatcher.match(classNamePattern, classNamePath)){
                    return classDefiner.getAutowiredType();
                }
            }
        }
        AutowiredType autowiredType = descriptor.getAnnotation(AutowiredType.class);
        if(autowiredType != null){
            return autowiredType.value();
        } else {
            return AutowiredType.Type.PLUGIN_MAIN;
        }
    }


}

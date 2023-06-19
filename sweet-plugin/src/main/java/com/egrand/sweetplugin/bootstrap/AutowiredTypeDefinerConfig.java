package com.egrand.sweetplugin.bootstrap;


import com.egrand.sweetplugin.bootstrap.annotation.AutowiredType;
import com.egrand.sweetplugin.bootstrap.realize.AutowiredTypeDefiner;

import java.util.HashSet;
import java.util.Set;

/**
 * 配置 ClassDefiner
 */
public class AutowiredTypeDefinerConfig {

    private final Set<AutowiredTypeDefiner.ClassDefiner> classDefiners;

    public AutowiredTypeDefinerConfig(){
        this.classDefiners = new HashSet<>();
    }

    Set<AutowiredTypeDefiner.ClassDefiner> getClassDefiners(){
        return classDefiners;
    }

    public AutowiredTypeDefinerConfig add(AutowiredType.Type type, String... classNamePatterns){
        if(type != null && classNamePatterns != null && classNamePatterns.length > 0){
            classDefiners.add(AutowiredTypeDefiner.ClassDefiner.config(type, classNamePatterns));
        }
        return this;
    }

    public AutowiredTypeDefinerConfig add(AutowiredType.Type type, Class<?>... classes){
        if(type != null && classes != null && classes.length > 0){
            classDefiners.add(AutowiredTypeDefiner.ClassDefiner.config(type, classes));
        }
        return this;
    }

}

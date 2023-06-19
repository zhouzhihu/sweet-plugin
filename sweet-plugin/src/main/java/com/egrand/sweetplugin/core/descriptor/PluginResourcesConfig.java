package com.egrand.sweetplugin.core.descriptor;

import com.egrand.sweetplugin.common.PackageStructure;
import com.egrand.sweetplugin.common.utils.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 插件 ResourcesDefine 文件定义
 */
public class PluginResourcesConfig {

    private Set<String> dependenciesIndex;
    private Set<String> loadMainResourceIncludes;
    private Set<String> loadMainResourceExcludes;

    public static PluginResourcesConfig parse(List<String> fileLines){
        final PluginResourcesConfig pluginResourcesConfig = new PluginResourcesConfig();
        if(ObjectUtils.isEmpty(fileLines)){
            return pluginResourcesConfig;
        }

        Set<String> dependenciesIndex = new HashSet<>();
        Set<String> loadMainResourceIncludes = new HashSet<>();
        Set<String> loadMainResourceExcludes = new HashSet<>();

        int i = 0;

        for (String fileLine : fileLines) {
            if(ObjectUtils.isEmpty(fileLine)){
                continue;
            }
            if(Objects.equals(fileLine, PackageStructure.RESOURCES_DEFINE_DEPENDENCIES)){
                i = 1;
                continue;
            } else if(Objects.equals(fileLine, PackageStructure.RESOURCES_DEFINE_LOAD_MAIN_INCLUDES)){
                i = 2;
                continue;
            } else if(Objects.equals(fileLine, PackageStructure.RESOURCES_DEFINE_LOAD_MAIN_EXCLUDES)){
                i = 3;
                continue;
            }
            if(i == 1){
                dependenciesIndex.add(fileLine);
            } else if(i == 2){
                loadMainResourceIncludes.add(fileLine);
            } else if(i == 3){
                loadMainResourceExcludes.add(fileLine);
            }
        }
        pluginResourcesConfig.setDependenciesIndex(dependenciesIndex);
        pluginResourcesConfig.setLoadMainResourceIncludes(loadMainResourceIncludes);
        pluginResourcesConfig.setLoadMainResourceExcludes(loadMainResourceExcludes);
        return pluginResourcesConfig;
    }


    public Set<String> getDependenciesIndex() {
        return dependenciesIndex;
    }

    public void setDependenciesIndex(Set<String> dependenciesIndex) {
        this.dependenciesIndex = dependenciesIndex;
    }

    public Set<String> getLoadMainResourceIncludes() {
        return loadMainResourceIncludes;
    }

    public void setLoadMainResourceIncludes(Set<String> loadMainResourceIncludes) {
        this.loadMainResourceIncludes = loadMainResourceIncludes;
    }

    public Set<String> getLoadMainResourceExcludes() {
        return loadMainResourceExcludes;
    }

    public void setLoadMainResourceExcludes(Set<String> loadMainResourceExcludes) {
        this.loadMainResourceExcludes = loadMainResourceExcludes;
    }



}

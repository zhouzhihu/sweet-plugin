package com.egrand.sweetplugin.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 依赖的插件
 */
public abstract class AbstractDependencyPlugin implements DependencyPlugin {

    public static final String SPLIT_ALL = ",";
    public static final String SPLIT_ONE = "@";


    /**
     * set依赖插件id
     *
     * @param id 插件id
     */
    public abstract void setId(String id);

    /**
     * set依赖插件版本
     *
     * @param version 插件版本
     */
    public abstract void setVersion(String version);

    /**
     * set optional
     *
     * @param optional 是否可选
     */
    public abstract void setOptional(Boolean optional);


    public static String toStr(List<? extends AbstractDependencyPlugin> dependencyPlugins){
        if(dependencyPlugins == null || dependencyPlugins.isEmpty()){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        final int size = dependencyPlugins.size();
        for (int i = 0; i < size; i++) {
            AbstractDependencyPlugin dependencyPlugin = dependencyPlugins.get(i);
            Boolean optional = dependencyPlugin.getOptional();
            if(optional == null){
                optional = false;
            }
            stringBuilder.append(dependencyPlugin.getId())
                    .append(SPLIT_ONE).append(dependencyPlugin.getVersion())
                    .append(SPLIT_ONE).append(optional);
            if(i <= size - 2){
                stringBuilder.append(SPLIT_ALL);
            }
        }
        return stringBuilder.toString();
    }


    public static List<DependencyPlugin> toList(String str, Supplier<? extends AbstractDependencyPlugin> supplier){
        if(str == null || "".equals(str)){
            return Collections.emptyList();
        }
        String[] all = str.split(SPLIT_ALL);
        if(all.length == 0){
            return Collections.emptyList();
        }
        List<DependencyPlugin> list = new ArrayList<>(all.length);
        for (String s : all) {
            String[] one = s.split(SPLIT_ONE);
            if(one.length == 0){
                continue;
            }
            if(one.length != 3){
                continue;
            }
            AbstractDependencyPlugin dependencyPlugin = supplier.get();
            dependencyPlugin.setId(one[0]);
            dependencyPlugin.setVersion(one[1]);
            dependencyPlugin.setOptional("true".equalsIgnoreCase(one[2]));
            list.add(dependencyPlugin);
        }
        return list;
    }

}

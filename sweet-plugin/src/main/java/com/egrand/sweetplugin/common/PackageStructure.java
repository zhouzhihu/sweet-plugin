package com.egrand.sweetplugin.common;


import java.io.File;
import java.util.regex.Matcher;

/**
 * 插件打包结构
 */
public abstract class PackageStructure {

    private PackageStructure(){}

    public static final String[] ILLEGAL_FORMAT = new String[]{
            AbstractDependencyPlugin.SPLIT_ALL, AbstractDependencyPlugin.SPLIT_ONE
    };


    public static final String CHARSET_NAME = "utf-8";

    public static final String SEPARATOR = "/";

    public static final String CLASSES_NAME = "classes";

    public static final String LIB_NAME = "lib";

    public static final String META_INF_NAME = "META-INF";

    public static final String MANIFEST = "MANIFEST.MF";

    public static final String PLUGIN_META_NAME = "PLUGIN.META";

    public static final String RESOURCES_DEFINE_NAME = "RESOURCES.CONF";

    public static final String RESOURCES_DEFINE_DEPENDENCIES = "dependencies.index";
    public static final String RESOURCES_DEFINE_LOAD_MAIN_INCLUDES = "load.main.resources.includes";
    public static final String RESOURCES_DEFINE_LOAD_MAIN_EXCLUDES = "load.main.resources.excludes";

    public static final String PROD_MANIFEST_PATH = META_INF_NAME + SEPARATOR + MANIFEST;

    public static final String PROD_RESOURCES_DEFINE_PATH = META_INF_NAME + SEPARATOR + RESOURCES_DEFINE_NAME;

    public static final String PROD_PLUGIN_META_PATH = META_INF_NAME + SEPARATOR + PLUGIN_META_NAME;

    public static final String PROD_CLASSES_PATH = CLASSES_NAME + SEPARATOR;

    public static final String PROD_LIB_PATH = LIB_NAME + SEPARATOR;


    public static String resolvePath(String path){
        if(path == null || "".equals(path)){
            return path;
        }
        if(path.contains(SEPARATOR)){
            return path.replaceAll(SEPARATOR, Matcher.quoteReplacement(File.separator));
        }
        return path;
    }

    public static String getIllegal(String str){
        for (String s : ILLEGAL_FORMAT) {
            if(str.contains(s)){
                return s;
            }
        }
        return null;
    }

}
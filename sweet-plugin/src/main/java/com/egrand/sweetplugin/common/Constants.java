package com.egrand.sweetplugin.common;

/**
 * 静态常量
 */
public abstract class Constants {

    private Constants(){}


    /**
     * 禁用所有插件标志
     */
    public final static String DISABLED_ALL_PLUGIN = "*";

    /**
     * 允许所有版本的标志
     */
    public final static String ALLOW_VERSION = "0.0.0";

    /**
     * 加载到主程序依赖的标志
     */
    public final static String LOAD_TO_MAIN_SIGN = "@LOAD_TO_MAIN";

    /**
     * 相对路径符号标志
     */
    public final static String RELATIVE_SIGN = "~";

}

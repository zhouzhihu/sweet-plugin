package com.egrand.sweetplugin.common.utils;

/**
 * object value convert utils
 */
public abstract class ObjectValueUtils {

    private ObjectValueUtils(){
    }

    public static String getString(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof CharSequence){
            return ((CharSequence) value).toString();
        }
        return String.valueOf(value);
    }

    public static Integer getInteger(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Integer){
            return (Integer) value;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    public static Long getLong(Object value){
        if(value == null){
            return null;
        }
        if(value instanceof Long){
            return (Long) value;
        }
        return Long.parseLong(String.valueOf(value));
    }

    public static Double getDouble(Object value) {
        if(value == null){
            return null;
        }
        if(value instanceof Double){
            return (Double) value;
        }
        return Double.parseDouble(String.valueOf(value));
    }

    public static Float getFloat(Object value) {
        if(value == null){
            return null;
        }
        if(value instanceof Float){
            return (Float) value;
        }
        return Float.parseFloat(String.valueOf(value));
    }

    public static Boolean getBoolean(Object value) {
        if(value == null){
            return null;
        }
        if(value instanceof Boolean){
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

}

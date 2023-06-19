package com.egrand.sweetplugin.common.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注解工具类
 */
public class AnnotationUtils {


    private AnnotationUtils(){}

    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(method, annotationType);
    }


    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(clazz, annotationType);
    }

    public static <A extends Annotation> boolean existOr(Class<?> clazz, Class<A>[] annotationTypes) {
        for (Class<A> annotationType : annotationTypes) {
            A annotation = findAnnotation(clazz, annotationType);
            if(annotation != null){
                return true;
            }
        }
        return false;
    }

    public static <A extends Annotation> boolean existAnd(Class<?> clazz, Class<A>[] annotationTypes) {
        for (Class<A> annotationType : annotationTypes) {
            A annotation = findAnnotation(clazz, annotationType);
            if(annotation == null){
                return false;
            }
        }
        return true;
    }

}

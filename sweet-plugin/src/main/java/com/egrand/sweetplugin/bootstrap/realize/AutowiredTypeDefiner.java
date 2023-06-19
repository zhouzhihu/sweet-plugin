package com.egrand.sweetplugin.bootstrap.realize;

import com.egrand.sweetplugin.bootstrap.AutowiredTypeDefinerConfig;
import com.egrand.sweetplugin.bootstrap.annotation.AutowiredType;
import com.egrand.sweetplugin.common.utils.UrlUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * autowiredType 批量定义接口
 */
public interface AutowiredTypeDefiner {

    /**
     * 定义 ClassDefiner
     * @param config 往config中进行配置 ClassDefiner
     */
    void config(AutowiredTypeDefinerConfig config);


    @Getter
    @EqualsAndHashCode
    class ClassDefiner{
        /**
         * 注入类型
         */
        private final AutowiredType.Type autowiredType;

        /**
         * 类名称匹配
         */
        private final Set<String> classNamePatterns;

        private ClassDefiner(AutowiredType.Type autowiredType, Set<String> classNamePatterns){
            this.autowiredType = autowiredType;
            this.classNamePatterns = classNamePatterns;
        }

        public static ClassDefiner config(AutowiredType.Type autowiredType, Class<?>... classes){
            if(autowiredType == null){
                throw new IllegalArgumentException("autowiredType 参数不能为空");
            }
            int length = classes.length;
            if(length == 0){
                throw new IllegalArgumentException("classes 参数不能为空");
            }
            Set<String> classNamePatterns = new HashSet<>(length);
            for (Class<?> aClass : classes) {
                classNamePatterns.add(UrlUtils.formatMatchUrl(aClass.getName()));
            }
            return new ClassDefiner(autowiredType, classNamePatterns);
        }

        public static ClassDefiner config(AutowiredType.Type autowiredType, String... classNamePatterns){
            if(autowiredType == null){
                throw new IllegalArgumentException("autowiredType 参数不能为空");
            }
            int length = classNamePatterns.length;
            if(length == 0){
                throw new IllegalArgumentException("classNamePatterns 参数不能为空");
            }
            Set<String> classNamePatternsSet = new HashSet<>(length);
            for (String classNamePattern : classNamePatterns) {
                classNamePatternsSet.add(UrlUtils.formatMatchUrl(classNamePattern));
            }
            return new ClassDefiner(autowiredType, classNamePatternsSet);
        }

    }


}

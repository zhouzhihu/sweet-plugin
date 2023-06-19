package com.egrand.sweetplugin.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.pf4j.PluginWrapper;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar帮助类
 */
public class JarUtils {

    /**
     * 加载插件指定类
     * @param pluginWrapper 插件
     * @param parentClass 条件
     * @param packagePrefix 包前缀
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws ClassNotFoundException
     */
    public static Map<String, Class<?>> loadClasses(PluginWrapper pluginWrapper, List<Class<?>> parentClass, String packagePrefix) throws IOException,
            URISyntaxException, ClassNotFoundException {
        Map<String, Class<?>> classMap = new HashMap<>();
        for (String className : listClassNames(pluginWrapper)) {
            if (StrUtil.isNotEmpty(packagePrefix) && !className.startsWith(packagePrefix)) {
                continue;
            }
            Class<?> loadClass = pluginWrapper.getPluginClassLoader().loadClass(className);
            boolean isAssignable = true;
            for (Class<?> aClass : parentClass) {
                if (!aClass.isAssignableFrom(loadClass)) {
                    isAssignable = false;
                    break;
                }
            }
            if (isAssignable) {
                classMap.put(className, loadClass);
            }
        }
        return classMap;
    }

    /**
     * 获取插件所有类名称
     * @param pluginWrapper 插件
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static List<String> listClassNames(PluginWrapper pluginWrapper) throws IOException, URISyntaxException {
        String filePath = ((URLClassLoader)pluginWrapper.getPluginClassLoader()).getURLs()[0].toURI().getPath();
        if (StrUtil.isEmpty(filePath))
            return new ArrayList<>();
        if(FileUtil.isFile(filePath) && filePath.endsWith(".jar"))
            return listClassNamesFromJar(filePath);
        if(FileUtil.isDirectory(filePath) && filePath.endsWith("classes/")) {
            List<String> classNameList = new ArrayList<>();
            classNameList.addAll(listClassNamesFromFile(filePath, null , true));
            // 查找lib下所有jar
            classNameList.addAll(listClassNamesFromLib(pluginWrapper.getPluginPath().toUri().getPath() + "lib/"));
            return classNameList;
        }
        return new ArrayList<>();
    }

    private static List<String> listClassNamesFromLib(String pluginLibPath) throws IOException {
        if (!FileUtil.isDirectory(pluginLibPath))
            return new ArrayList<>();
        File file = new File(pluginLibPath);
        File[] childFiles = file.listFiles();
        List<String> classNameList = new ArrayList<>();
        for (File childFile : childFiles) {
            if (!childFile.isDirectory() && childFile.getPath().endsWith(".jar")) {
                classNameList.addAll(listClassNamesFromJar(childFile.getPath()));
            }
        }
        return classNameList;
    }

    /**
     * 从Jar文件中读取所有类名称
     * @param filePath JAR文件路径
     * @return
     * @throws IOException
     */
    private static List<String> listClassNamesFromJar(String filePath) throws IOException {
        List<String> classNameList = new ArrayList<>();
        String jarPath = "jar:file:" + filePath + "!/";
        URL jarUrl = new URL(jarPath);
        URLConnection uc = jarUrl.openConnection();
        if (uc instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection)uc;
            JarFile jarFile = jarURLConnection.getJarFile();
            if (jarFile != null) {
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry entry = jarEntryEnumeration.nextElement();
                    String jarEntryName = entry.getName();
                    if (jarEntryName.contains(".class")) {
                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                        classNameList.add(className);
                    }
                }
            }
            jarURLConnection.getJarFile().close();
        }
        return classNameList;
    }

    /**
     * 从classes文件夹中读取所有类名称
     * @param filePath 文件夹路径
     * @param className 类名
     * @param childPackage 是否包含下级
     * @return
     */
    private static List<String> listClassNamesFromFile(String filePath, List<String> className, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(listClassNamesFromFile(childFile.getPath(), myClassName, childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.lastIndexOf("classes") + 8,
                            childFilePath.lastIndexOf("."));
                    if (childFilePath.indexOf("\\") != -1)
                        childFilePath = childFilePath.replace("\\", ".");
                    if (childFilePath.indexOf("/") != -1)
                        childFilePath = childFilePath.replace("/", ".");
                    myClassName.add(childFilePath);
                }
            }
        }

        return myClassName;
    }
}

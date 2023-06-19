package com.egrand.sweetplugin.common.utils;

import com.egrand.sweetplugin.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 */
@Slf4j
public class FilesUtils {

    /**
     * 获取存在的文件
     *
     * @param pathStr 文件路径
     * @return File
     */
    public static File getExistFile(String pathStr){
        File file = new File(pathStr);
        if(file.exists()){
            return file;
        }
        return null;
    }


    /**
     * 拼接file路径
     *
     * @param paths 拼接的路径
     * @return 拼接的路径
     */
    public static String joiningFilePath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(ObjectUtils.isEmpty(path)) {
                continue;
            }
            if(i > 0){
                if(path.startsWith(File.separator) || path.startsWith("/") ||
                        path.startsWith("\\") || path.startsWith("//")){
                    stringBuilder.append(path);
                } else {
                    stringBuilder.append(File.separator).append(path);
                }
            } else {
                stringBuilder.append(path);
            }
        }

        return stringBuilder.toString();
    }

    public static File createFile(String path) throws IOException {
        try {
            File file = new File(path);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                if(!parentFile.mkdirs()){
                    throw new IOException("Create " + parentFile + " dir error");
                }
            }
            if(file.createNewFile()){
                return file;
            }
            throw new IOException("Create " + path + " file error");
        } catch (Exception e){
            throw new IOException("Create " + path + " file error");
        }
    }


    /**
     * 解决相对路径
     * @param rootPath 根路径
     * @param relativePath 以 ~ 开头的相对路径
     * @return 处理后的路径
     */
    public static String resolveRelativePath(String rootPath, String relativePath){
        if(ObjectUtils.isEmpty(relativePath)){
            return relativePath;
        }
        if(isRelativePath(relativePath)){
            return joiningFilePath(rootPath, relativePath.replaceFirst(Constants.RELATIVE_SIGN, ""));
        } else {
            return relativePath;
        }
    }

    /**
     * 是否是相对路径
     * @param path 路径
     * @return true 为相对路径, false 未非相对路径
     */
    public static boolean isRelativePath(String path){
        if(ObjectUtils.isEmpty(path)){
            return false;
        }
        return path.startsWith(Constants.RELATIVE_SIGN);
    }

    public static String saveFile(MultipartFile file, String path, String fileName) {
        try {
            String oldFileName = file.getOriginalFilename();
            String fileType = oldFileName.substring(oldFileName.lastIndexOf(".") + 1);
            File targetFile = new File(new File(path).getAbsolutePath() + File.separator + fileName + "." + fileType);
            if (targetFile.exists() && targetFile.isFile()) {
                return targetFile.getCanonicalPath();
            }
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            file.transferTo(targetFile);
            return targetFile.getCanonicalPath();
        } catch (Exception e) {
            log.error("FileUtil->saveFile:{}" ,e.getMessage());
        }
        return null;
    }

}

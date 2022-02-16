package com.ecnu.adsmls.utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 文件处理工具类
 */
public class FileSystem {
    /**
     * 文件后缀
     */
    public enum Suffix {
        TREE(".tree"),
        MODEL(".model"),
        JSON(".json");

        public String value;

        Suffix(String value) {
            this.value = value;
        }
    }

    /**
     * 创建文件
     * @param directory 目录路径名
     * @param filename 文件名
     * @return 是否创建成功
     */
    public static boolean createFile(String directory, String filename) {
        String pathName = directory + "/" + filename;
        File file = new File(pathName);
        if(!file.getParentFile().exists()) {
            if(!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            if(file.createNewFile()) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createFile(File directory, String filename) {
        return createFile(directory.getAbsolutePath(), filename);
    }

    /**
     * 创建目录
     * @param directory 目录路径名
     * @return 是否创建成功
     */
    public static boolean createDir(String directory) {
        if(!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        File dir = new File(directory);
        if(dir.mkdirs()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 获取文件的后缀
     * @param file File 对象
     * @return 后缀字符串，含 .
     */
    public static String getSuffix(File file) {
        assert file.isFile();
        String filename = file.getName();
        int lastIndexOf = filename.lastIndexOf(".");
        return filename.substring(lastIndexOf);
    }

    /**
     * 打开 Project 时，获取 Project 名称
     * @param projectPath Project 路径
     * @return Project 名称
     */
    public static String getProjectName(String projectPath) {
        File project = new File(projectPath);
        String name = project.getName();
        if(Objects.equals(name, "")) {
            name = project.getPath();
        }
        return name;
    }

    /**
     * 打开 Project 时，获取 Project 所在文件夹路径
     * @param projectPath Project 路径
     * @return
     */
    public static String getProjectDir(String projectPath) {
        File project = new File(projectPath);
        // projectPath 不能是盘符
        assert project.getParentFile() != null;
        return project.getParentFile().getAbsolutePath();
    }

    /**
     * 获取文件/文件夹以 basePath 为根的相对路径
     * @param basePath 根路径
     * @param filePath 文件/文件夹路径
     * @return 文件/文件夹相对根目录的路径
     */
    public static String getRelativePath(String basePath, String filePath) {
        File baseFile = new File(basePath);
        assert baseFile.isDirectory();
        File file = new File(filePath);
        basePath = baseFile.getAbsolutePath();
        filePath = file.getAbsolutePath();
        assert filePath.indexOf(basePath) == 0;
        String subPath = filePath.substring(basePath.length());
        return "." + subPath;
    }
}

package com.ecnu.adsmls.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        JSON(".json"),
        ADSML(".adsml"),
        WEATHER(".weather"),
        MAP(".xodr"),
        EXE(".exe"),
        XML(".xml"),
        PROP(".properties"),
        DIR("");

        public String value;

        Suffix(String value) {
            this.value = value;
        }
    }

    /**
     * 创建文件
     *
     * @param directory 目录路径名
     * @param filename  文件名
     * @return 是否创建成功
     */
    public static boolean createFile(String directory, String filename) {
        File file = new File(directory, filename);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createFile(File directory, String filename) {
        return createFile(directory.getAbsolutePath(), filename);
    }

    /**
     * 创建目录
     *
     * @param directory 目录路径名
     * @return 是否创建成功
     */
    public static boolean createDir(String directory) {
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        File dir = new File(directory);
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件对象
     * @return 是否删除成功
     */
    public static boolean deleteFile(File file) {
        if (file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filename) {
        File file = new File(filename);
        return deleteFile(file);
    }

    /**
     * 删除文件夹及其子文件和子文件夹
     *
     * @param file 文件对象
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(File file) {
        boolean success = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        success = deleteFile(f);
                    } else if (f.isDirectory()) {
                        success = deleteDirectory(f);
                    } else {
                        success = false;
                    }

                    if (!success) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return file.delete();
    }

    public static boolean deleteDirectory(String directoryName) {
        File dir = new File(directoryName);
        return deleteDirectory(dir);
    }

    /**
     * 获取文件的后缀
     *
     * @param file File 对象
     * @return 后缀字符串，含 .
     */
    public static String getSuffix(File file) {
        if (file.isFile()) {
            String filename = file.getName();
            int lastIndexOf = filename.lastIndexOf(".");
            return filename.substring(lastIndexOf);
        } else if (file.isDirectory()) {
            return Suffix.DIR.value;
        } else {
            System.out.println("invalid file");
            return null;
        }
    }

    /**
     * 不管是不是文件, 获取其后缀
     *
     * @param filename
     * @return
     */
    public static String getSuffix(String filename) {
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf != -1) {
            return filename.substring(lastIndexOf);
        } else {
            return null;
        }
    }

    public static String removeSuffix(File file) {
        if (file.isFile()) {
            String filename = file.getAbsolutePath();
            filename = filename.replaceAll("\\..*$", "");
            return filename;
        } else {
            System.out.println("invalid file");
            return null;
        }
    }

    public static String removeSuffix(String filename) {
        filename = filename.replaceAll("\\..*$", "");
        return filename;
    }

    /**
     * 打开 Project 时，获取 Project 名称
     *
     * @param projectPath Project 路径
     * @return Project 名称
     */
    public static String getProjectName(String projectPath) {
        File project = new File(projectPath);
        String name = project.getName();
        if (Objects.equals(name, "")) {
            name = project.getPath();
        }
        return name;
    }

    /**
     * 打开 Project 时，获取 Project 所在文件夹路径
     *
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
     *
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
        String subPath = filePath.substring(basePath.length() + 1);
        return subPath;
    }

    public static String concatAbsolutePath(String basePath, String relativePath) {
        File file = new File(basePath, relativePath);
        return file.getAbsolutePath();
    }

    public static String getRegSuffix(Suffix suffix) {
        return "*" + suffix.value;
    }

    /**
     * 读取单行 JSON
     *
     * @param file
     * @return
     */
    public static String JSONReader(File file) {
        String content = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            content = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void JSONWriter(File file, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

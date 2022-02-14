package com.ecnu.adsmls.utils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileSystem {
    public enum Suffix {
        TREE(".tree"),
        MODEL(".model"),
        JSON(".json");

        public String value;

        Suffix(String value) {
            this.value = value;
        }
    }

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

    public static String getSuffix(File file) {
        assert file.isFile();
        String filename = file.getName();
        int lastIndexOf = filename.lastIndexOf(".");
        return filename.substring(lastIndexOf);
    }

    public static String getProjectName(String projectPath) {
        File project = new File(projectPath);
        String name = project.getName();
        if(Objects.equals(name, "")) {
            name = project.getPath();
        }
        return name;
    }

    public static String getProjectDir(String projectPath) {
        File project = new File(projectPath);
        // projectPath 不能是盘符
        assert project.getParentFile() != null;
        return project.getParentFile().getAbsolutePath();
    }
}

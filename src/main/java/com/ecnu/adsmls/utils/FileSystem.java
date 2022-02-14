package com.ecnu.adsmls.utils;

import java.io.File;
import java.io.IOException;

public class FileSystem {
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
        File dir = new File(directory);
        if(!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        if(dir.mkdirs()) {
            return true;
        }
        else {
            return false;
        }
    }
}

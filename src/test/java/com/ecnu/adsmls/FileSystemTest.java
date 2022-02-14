package com.ecnu.adsmls;

import com.ecnu.adsmls.utils.FileSystem;
import org.junit.jupiter.api.Test;

import java.io.File;

public class FileSystemTest {
    @Test
    public void getProjectName() {
        String res = FileSystem.getProjectName("D:\\");
        System.out.println(res);
    }

    @Test
    public void getProjectDir() {
        String res = FileSystem.getProjectDir("D:\\test");
        System.out.println(res);
    }

    @Test
    public void fileTest() {
        File file = new File("D:/");
        System.out.println(file.getPath());
        System.out.println(file.getName());
    }
}

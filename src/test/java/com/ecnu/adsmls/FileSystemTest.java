package com.ecnu.adsmls;

import com.ecnu.adsmls.utils.FileSystem;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
        File file = new File("D:/", "./test/test.tree");
        System.out.println(file.getPath());
        System.out.println(file.getName());
    }

    public void relativePath(String basePath, String filePath) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(basePath, filePath)), StandardCharsets.UTF_8));
            System.out.println(br.readLine());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void relativePathTest() {
        String base = "D:/test";
        String file = "D:/test/1.tree";
        System.out.println(new File(file).getAbsolutePath());
//        this.relativePath(base, FileSystem.getRelativePath(base, file));
        System.out.println(FileSystem.getRelativePath(base, file));
    }

    @Test
    public void getSuffix() {
        String filename = "D:/test/a/t.model";
        System.out.println(FileSystem.removeSuffix(new File(filename)));
    }

    @Test
    public void absoluteTest() {
        File file1 = new File("D:/a/b");
        File file2 = new File("./a/b");
        File file3 = new File("");
        System.out.println(file3.isAbsolute());
    }
}

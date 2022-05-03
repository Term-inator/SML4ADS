package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.importer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class XODRInputReader {

    public static String readFromFile(String xodrPath) {
//        log.info("OpenDRIVE地图文件路径：{}", xodrPath);
        log.info("The path of OpenDRIVE map file: {}", xodrPath);

        String xodrStr = null;
        try {
            xodrStr = FileUtils.readFileToString(new File(xodrPath), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return xodrStr;
    }


}

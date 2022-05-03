package com.ecnu.adsmls.verifier.convert.src.main.java.json.importer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JSONInputReader {

    public static String readFromFile(String JSONPath) {

        if(JSONPath.endsWith(".tree")) {
            int start = 0;
            if(JSONPath.contains("/")) { // Mac, Linux
                start = JSONPath.lastIndexOf('/');
            } else if (JSONPath.contains("\\")) { // Windows
                start = JSONPath.lastIndexOf("\\");
            }
//            log.info("开始解析{}的动态行为模型(JSON)...",
//                    JSONPath.substring(start+1, JSONPath.lastIndexOf('.')));
            log.info("Start parsing the dynamic model(JSON) of {}...",
                    JSONPath.substring(start+1, JSONPath.lastIndexOf('.')));
        } else {
//            log.info("开始解析JSON文件：{}...", JSONPath);
            log.info("Start parsing JSON file: {}...", JSONPath);
        }


        String jsonStr = null;
        try {
            jsonStr = FileUtils.readFileToString(new File(JSONPath), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        log.info("完成解析JSON!");
        log.info("The parse of JSON is completed.");
        return jsonStr;
    }

}

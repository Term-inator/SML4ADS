package com.ecnu.adsmls.verifier.convert.src.main.java;

import com.ecnu.adsmls.verifier.convert.src.main.java.json.exporter.XMLWriter;
import com.ecnu.adsmls.verifier.convert.src.main.java.json.importer.JSONInputReader;
import com.ecnu.adsmls.verifier.convert.src.main.java.json.importer.JSONParser;
import com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.TreeDataContainer;

public class Convert {

    /**
     * @param ADSML_PATH 项目路径，例如："src/main/resources/ADSML/Example/"
     * @param JSON_PATH 项目路径下需要解析的.model文件路径，例如："example.model"
     * @param XML_PATH 生成的XML文件路径，例如："src/main/resources/models/Example.xml"
     * @description 该函数将输入的ADSML文件转化为Uppaal SMC支持的XML文件
     */
    public static void ADSML2Uppaal(String ADSML_PATH, String JSON_PATH, String XML_PATH) {

        try{
            // 1. 读取
            String input = JSONInputReader.readFromFile(JSON_PATH);
            // 2. 解析
            TreeDataContainer container = JSONParser.parse(input, ADSML_PATH);
            // 3. 写入
            XMLWriter.write(container, XML_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        /*
            Example: "src/main/resources/ADSML/Example/" "example.model" "src/main/resources/models/Example.xml"
         */
        final String ADSML_PATH = args[0];
        final String JSON_PATH = ADSML_PATH + args[1];
        final String XML_PATH = args[2];

        ADSML2Uppaal(ADSML_PATH, JSON_PATH, XML_PATH);

    }

}

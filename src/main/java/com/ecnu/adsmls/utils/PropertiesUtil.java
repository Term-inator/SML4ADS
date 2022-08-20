package com.ecnu.adsmls.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Kyrie Lee
 * @date 2022/8/20
 */
public class PropertiesUtil {
    /**
     * 仿真器选项数据
     */
    private static final List<String> simulators = new ArrayList<>(8);
    private static final String SIMULATOR_TYPE = "SML4ADS.simulator.type";

    public static List<String> getSimulators() {
        return simulators;
    }

    static {
        Properties prop = new Properties();
        try {
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("system.properties");
            prop.load(in);
            // 获取properties文件中的仿真器选项数据
            String[] st = prop.getProperty(SIMULATOR_TYPE).split(",");
            simulators.addAll(Arrays.asList(st));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

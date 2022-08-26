package com.ecnu.adsmls.router.params;

/**
 * 全局变量
 */
public class Global {
    public static String simulatorType = "CARLA";

    public static Integer simulationPort = 20225;

    public static void clear() {
        simulatorType = "CARLA";
        simulationPort = 20225;
    }
}

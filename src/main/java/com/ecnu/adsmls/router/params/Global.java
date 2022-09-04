package com.ecnu.adsmls.router.params;

import com.ecnu.adsmls.utils.SimulatorConstant;

/**
 * 全局变量
 */
public class Global {
    public static SimulatorConstant.SimulatorType simulatorType = SimulatorConstant.SimulatorType.CARLA;

    public static Integer simulationPort = 20225;

    public static void clear() {
        simulatorType = SimulatorConstant.SimulatorType.CARLA;
        simulationPort = 20225;
    }
}

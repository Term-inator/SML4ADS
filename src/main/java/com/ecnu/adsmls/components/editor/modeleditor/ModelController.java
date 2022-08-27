package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.utils.PropertiesUtil;
import com.ecnu.adsmls.utils.SimulatorConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 建模用到的常量
 */
public class ModelController {
    private SimulatorConstant.Simulator simulator;

    // TODO 如果之后有多种 Pane 的需求，为他们建立父类或接口
    private List<CarPane> simulatorListener = new ArrayList<>();

    public ModelController() {
    }

    // TODO 有更好的方法
    public void setSimulator(String simulatorType) {
        String[] simulators = PropertiesUtil.getSimulators().toArray(new String[0]);
        if (Objects.equals(simulatorType, simulators[0])) {
            simulator = SimulatorConstant.Simulator.CARLA;
        } else if (Objects.equals(simulatorType, simulators[1])) {
            simulator = SimulatorConstant.Simulator.LGSVL;
        } else {
            simulator = SimulatorConstant.Simulator.CARLA;
        }
        for (CarPane carPane : this.simulatorListener) {
            carPane.notifyModel(this.getModel());
        }
    }

    public SimulatorConstant.Simulator getSimulator() {
        return simulator;
    }

    public void addSimulatorListener(CarPane carPane) {
        this.simulatorListener.add(carPane);
        // 新增时立即根据 simulator 类型更新
        carPane.notifyModel(this.getModel());
    }

    public void removeSimulatorListener(CarPane carPane) {
        this.simulatorListener.remove(carPane);
    }

    /**
     * 获取蓝图
     */
    public String[] getModel() {
        return SimulatorConstant.getModel(this.simulator);
    }

    /**
     * 获取天气
     */
    public String[] getWeather() {
        return SimulatorConstant.getWeather(this.simulator);
    }
}

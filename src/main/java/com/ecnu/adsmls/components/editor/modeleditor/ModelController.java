package com.ecnu.adsmls.components.editor.modeleditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 建模用到的常量
 */
public class ModelController {
    private ModelConstant.Simulator simulator;

    // TODO 如果之后有多种 Pane 的需求，为他们建立父类或接口
    private List<CarPane> simulatorListener = new ArrayList<>();

    public ModelController() {
    }

    // TODO 有更好的方法
    public void setSimulator(String simulatorType) {
        if(Objects.equals(simulatorType, "carla")) {
            simulator = ModelConstant.Simulator.CARLA;
        }
        else if(Objects.equals(simulatorType, "lgsvl")) {
            simulator = ModelConstant.Simulator.LGSVL;
        }
        else {
            simulator = ModelConstant.Simulator.CARLA;
        }
        for(CarPane carPane : this.simulatorListener) {
            carPane.notifyModel(this.getModel());
        }
    }

    public ModelConstant.Simulator getSimulator() {
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
        return ModelConstant.getModel(this.simulator);
    }
}

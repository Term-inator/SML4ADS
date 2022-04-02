package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.Positive;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LocationRegister extends FunctionRegister {
    // 位置类型，参数名，参数类型
    private static LinkedHashMap<String, List<FunctionParam>> locationFunctions = new LinkedHashMap<>();

    @Override
    public void init() {
        this.register("Global Position", new ArrayList<>(List.of(
                this.new FunctionParam("x", DataType.DOUBLE, Necessity.REQUIRED),
                this.new FunctionParam("y", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        // 若 lateral offset 超过 lane 宽度, 则报错
        double laneWidth = 4.0;
        this.register("Lane Position", new ArrayList<>(List.of(
                new FunctionParam("road id", DataType.INT, Necessity.REQUIRED),
                new FunctionParam("lane id", DataType.INT, Necessity.REQUIRED),
                new FunctionParam("min lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("min longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        this.register("Road Position", new ArrayList<>(List.of(
                new FunctionParam("road id", DataType.INT, Necessity.REQUIRED),
                new FunctionParam("min lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("min longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        this.register("Related Position", new ArrayList<>(List.of(
                new FunctionParam("entity reference", DataType.STRING, Necessity.REQUIRED),
                new FunctionParam("min lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("min longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED)
        )));
    }

    @Override
    public void register(String locationType, List<FunctionParam> params) {
        if (locationFunctions.containsKey(locationType)) {
            return;
        }
        locationFunctions.put(locationType, params);
    }

    public static List<String> getLocationTypes() {
        return new ArrayList<>(locationFunctions.keySet());
    }

    public static List<FunctionParam> getParams(String locationType) {
        return locationFunctions.get(locationType);
    }
}

package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.utils.FunctionRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LocationRegister extends FunctionRegister {
    // 位置类型，参数名，参数类型
    private static LinkedHashMap<String, List<FunctionParam>> locationFunctions = new LinkedHashMap<>();

    static {
        register("Global Position", new ArrayList<>(List.of(
                new FunctionParam("x", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("y", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        // TODO 若 lateral offset 超过 lane 宽度, 则报错
        register("Lane Position", new ArrayList<>(List.of(
                new FunctionParam("road id", DataType.INT, Necessity.REQUIRED),
                new FunctionParam("lane id", DataType.INT, Necessity.REQUIRED),
                new FunctionParam("min lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("min longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        register("Road Position", new ArrayList<>(List.of(
                new FunctionParam("road id", DataType.INT, Necessity.REQUIRED),
                new FunctionParam("min lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("min longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        register("Related Position", new ArrayList<>(List.of(
                new FunctionParam("entity reference", DataType.STRING, Necessity.REQUIRED),
                new FunctionParam("min lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max lateral offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("min longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("max longitudinal offset", DataType.DOUBLE, Necessity.REQUIRED)
        )));
    }

    public static void register(String locationType, List<FunctionParam> params) {
        if(locationFunctions.containsKey(locationType)) {
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

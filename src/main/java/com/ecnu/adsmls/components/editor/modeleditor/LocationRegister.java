package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.utils.register.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocationRegister extends FunctionRegister {
    // 位置类型，参数名，参数类型
    private static List<Function> locationFunctions = new ArrayList<>();
//    private static LinkedHashMap<String, List<Function.FunctionParam>> locationFunctions = new LinkedHashMap<>();

    @Override
    public void init() {
        Function globalPosition = new Function("Global Position");
        globalPosition.addParam("x", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        globalPosition.addParam("y", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        // 若 lateral offset 超过 lane 宽度, 则报错
        double laneWidth = 4.0;
        Function lanePosition = new Function("Lane Position");
        lanePosition.addParam("road id", Function.DataType.INT, Function.Necessity.REQUIRED);
        lanePosition.addParam("lane id", Function.DataType.INT, Function.Necessity.REQUIRED);
        lanePosition.addParam("min lateral offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(laneWidth), "[]"));
        lanePosition.addParam("max lateral offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("min lateral offset"), new Value(laneWidth), "[]"));
        lanePosition.addParam("min longitudinal offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        lanePosition.addParam("max longitudinal offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("min longitudinal offset"), new Value(Double.MAX_VALUE), "[)"));

        // TODO entity reference 检查
        Function relatedPosition = new Function("Related Position");
        relatedPosition.addParam("entity reference", Function.DataType.STRING, Function.Necessity.REQUIRED);
        relatedPosition.addParam("min lateral offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(laneWidth), "[]"));
        relatedPosition.addParam("max lateral offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("min lateral offset"), new Value(laneWidth), "[]"));
        relatedPosition.addParam("min longitudinal offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        relatedPosition.addParam("max longitudinal offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("min longitudinal offset"), new Value(Double.MAX_VALUE), "[)"));

        locationFunctions.add(globalPosition);
        locationFunctions.add(lanePosition);
        locationFunctions.add(relatedPosition);
    }

    public static List<String> getLocationTypes() {
        return locationFunctions.stream().map(Function::getFunctionName).collect(Collectors.toList());
    }

    public static Function getLocationFunction(String locationType) {
        for(Function function : locationFunctions) {
            if(Objects.equals(function.getFunctionName(), locationType)) {
                return function;
            }
        }
        return null;
    }
}

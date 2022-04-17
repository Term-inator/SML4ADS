package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.Reference;
import com.ecnu.adsmls.utils.register.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocationRegister extends FunctionRegister {
    private static List<Function> locationFunctions = new ArrayList<>();

    @Override
    public void init() {
        Function globalPosition = new Function("Global Position");
        globalPosition.addParam("x", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        globalPosition.addParam("y", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        // 若 lateral offset 超过 lane 宽度, 则报错
        double laneWidth = 4.5;
        Function lanePosition = new Function("Lane Position");
        lanePosition.addParam("roadId", Function.DataType.INT, Function.Necessity.REQUIRED);
        lanePosition.addParam("laneId", Function.DataType.INT, Function.Necessity.REQUIRED);
        lanePosition.addParam("minLateralOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(laneWidth), "[]"));
        lanePosition.addParam("maxLateralOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("minLateralOffset"), new Value(laneWidth), "[]"));
        lanePosition.addParam("minLongitudinalOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        lanePosition.addParam("maxLongitudinalOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("minLongitudinalOffset"), new Value(Double.MAX_VALUE), "[)"));

        Function roadPosition = new Function("Road Position");
        roadPosition.addParam("roadId", Function.DataType.INT, Function.Necessity.REQUIRED);
        roadPosition.addParam("minLateralOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        // 根据 lateral 偏移确定 lane
        roadPosition.addParam("maxLateralOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("minLateralOffset"), new Value(Double.MAX_VALUE), "[)"));
        roadPosition.addParam("minLongitudinalOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        roadPosition.addParam("maxLongitudinalOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("minLongitudinalOffset"), new Value(Double.MAX_VALUE), "[)"));

        // entity reference 的检查交给运行时
        Function relatedPosition = new Function("Related Position");
        relatedPosition.addParam("actorRef", Function.DataType.STRING, Function.Necessity.REQUIRED);
        relatedPosition.addParam("minLateralOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(laneWidth), "[]"));
        relatedPosition.addParam("maxLateralOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("minLateralOffset"), new Value(laneWidth), "[]"));
        relatedPosition.addParam("minLongitudinalOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        relatedPosition.addParam("maxLongitudinalOffset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Reference("minLongitudinalOffset"), new Value(Double.MAX_VALUE), "[)"));

        locationFunctions.add(globalPosition);
        locationFunctions.add(lanePosition);
        locationFunctions.add(roadPosition);
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

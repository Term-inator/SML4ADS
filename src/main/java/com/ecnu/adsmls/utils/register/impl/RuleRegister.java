package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.SimulatorConstant;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RuleRegister extends FunctionRegister {
    private static List<Function> ruleFunctions = new ArrayList<>();

    @Override
    public void init() {
        Function roadSpeedRule = new Function("road speed");
        roadSpeedRule.addParam("road id", Function.DataType.INT, Function.Necessity.REQUIRED);
        roadSpeedRule.addParam("max speed", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        Function laneSpeedRule = new Function("lane speed");
        laneSpeedRule.addParam("lane id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneSpeedRule.addParam("max speed", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        Function laneChangeRule = new Function("lane change");
        laneChangeRule.addParam("road id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneChangeRule.addParam("lane id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneChangeRule.addParam("lane section id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneChangeRule.addParam("start", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new NotNegative());
        laneChangeRule.addParam("length", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new NotNegative());
        laneChangeRule.addParam("lane change", Function.DataType.BOOL, Function.Necessity.REQUIRED);

        Function overtakeRule = new Function("overtaking");
        laneChangeRule.addParam("road id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneChangeRule.addParam("lane id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneChangeRule.addParam("lane section id", Function.DataType.INT, Function.Necessity.REQUIRED);
        laneChangeRule.addParam("start", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new NotNegative());
        laneChangeRule.addParam("length", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new NotNegative());
        laneChangeRule.addParam("overtaking", Function.DataType.BOOL, Function.Necessity.REQUIRED);

        ruleFunctions.add(roadSpeedRule);
        ruleFunctions.add(laneSpeedRule);
        ruleFunctions.add(laneChangeRule);
        ruleFunctions.add(laneSpeedRule);
    }

    public static List<String> getFunctionNames() {
        return ruleFunctions.stream().map(Function::getFunctionName).collect(Collectors.toList());
    }

    public static Function getFunction(String functionName) {
        for (Function function : ruleFunctions) {
            if (Objects.equals(function.getFunctionName(), functionName)) {
                return function;
            }
        }
        return null;
    }
}

package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;

import java.util.ArrayList;
import java.util.Arrays;

public class RuleRegister extends FunctionRegister {
    private final FunctionCategory functionCategory = FunctionCategory.RULE;

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
        overtakeRule.addParam("road id", Function.DataType.INT, Function.Necessity.REQUIRED);
        overtakeRule.addParam("lane id", Function.DataType.INT, Function.Necessity.REQUIRED);
        overtakeRule.addParam("lane section id", Function.DataType.INT, Function.Necessity.REQUIRED);
        overtakeRule.addParam("start", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new NotNegative());
        overtakeRule.addParam("length", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new NotNegative());
        overtakeRule.addParam("overtaking", Function.DataType.BOOL, Function.Necessity.REQUIRED);

        functions.put(functionCategory, Arrays.asList(roadSpeedRule, laneSpeedRule, laneChangeRule, overtakeRule));
    }
}

package com.ecnu.adsmls.components.editor.treeeditor.impl;

import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.NotNegative;

import java.util.*;

public class BehaviorRegister extends FunctionRegister {
    // 行为名，参数名，参数类型，参数必要性
    private static LinkedHashMap<String, List<FunctionParam>> behaviorFunctions = new LinkedHashMap<>();

    // 初始化内置 behavior 及其参数
    @Override
    public void init() {
        // 匀速
        this.register("Keep", new ArrayList<>(List.of(
                new FunctionParam("duration", DataType.INT, Necessity.OPTIONAL))
        ));

        this.register("Accelerate", new ArrayList<>(List.of(
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.REQUIRED, new NotNegative()),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("duration", DataType.INT, Necessity.OPTIONAL))
        ));

        this.register("Decelerate", new ArrayList<>(List.of(
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.REQUIRED, new NotNegative()),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("duration", DataType.INT, Necessity.OPTIONAL))
        ));

        this.register("ChangeLeft", new ArrayList<>(List.of(
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.OPTIONAL),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.OPTIONAL)
        )));
        this.register("ChangeRight", new ArrayList<>(List.of(
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.OPTIONAL),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.OPTIONAL)
        )));

        this.register("TurnLeft", new ArrayList<>(List.of(
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.REQUIRED)
        )));
        this.register("TurnRight", new ArrayList<>(List.of(
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.REQUIRED)
        )));

        this.register("LaneOffset", new ArrayList<>(List.of(
                new FunctionParam("offset", DataType.DOUBLE, Necessity.REQUIRED),
                new FunctionParam("acceleration", DataType.DOUBLE, Necessity.OPTIONAL),
                new FunctionParam("target speed", DataType.DOUBLE, Necessity.OPTIONAL),
                new FunctionParam("duration", DataType.DOUBLE, Necessity.OPTIONAL)
        )));

        // 静止且什么都不做
        this.register("Idle", new ArrayList<>(List.of(
                new FunctionParam("duration", DataType.DOUBLE, Necessity.OPTIONAL)
        )));
    }

    @Override
    public void register(String behaviorName, List<FunctionParam> params) {
        if(behaviorFunctions.containsKey(behaviorName)) {
            return;
        }
        behaviorFunctions.put(behaviorName, params);
    }

    public static List<String> getBehaviorNames() {
        return new ArrayList<>(behaviorFunctions.keySet());
    }

    public static List<FunctionParam> getParams(String behaviorName) {
        return behaviorFunctions.get(behaviorName);
    }
}

package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BehaviorRegister extends FunctionRegister {
    private static List<Function> behaviorFunctions = new ArrayList<>();

    // 初始化内置 behavior 及其参数
    @Override
    public void init() {
        // 匀速
        Function keep = new Function("Keep");
        keep.addParam("duration", Function.DataType.INT, Function.Necessity.OPTIONAL,
                new Positive());

        Function accelerate = new Function("Accelerate");
        accelerate.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        accelerate.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        accelerate.addParam("duration", Function.DataType.INT, Function.Necessity.OPTIONAL,
                new Positive());

        Function decelerate = new Function("Decelerate");
        decelerate.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new NotNegative());
        decelerate.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        decelerate.addParam("duration", Function.DataType.INT, Function.Necessity.OPTIONAL,
                new Positive());

        Function changeLeft = new Function("ChangeLeft");
        changeLeft.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL);
        changeLeft.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL);

        Function changeRight = new Function("ChangeRight");
        changeRight.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL);
        changeRight.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL);

        Function turnLeft = new Function("TurnLeft");
        turnLeft.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        turnLeft.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        Function turnRight = new Function("TurnRight");
        turnRight.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        turnRight.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        Function laneOffset = new Function("LaneOffset");
        laneOffset.addParam("offset", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);
        laneOffset.addParam("acceleration", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL);
        laneOffset.addParam("target speed", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL);
        laneOffset.addParam("duration", Function.DataType.DOUBLE, Function.Necessity.OPTIONAL,
                new Positive());

        // 静止且什么都不做
        Function idle = new Function("Idle");
        idle.addParam("duration", Function.DataType.INT, Function.Necessity.OPTIONAL,
                new Positive());

        behaviorFunctions.add(keep);
        behaviorFunctions.add(accelerate);
        behaviorFunctions.add(decelerate);
        behaviorFunctions.add(changeLeft);
        behaviorFunctions.add(changeRight);
        behaviorFunctions.add(turnLeft);
        behaviorFunctions.add(turnRight);
        behaviorFunctions.add(laneOffset);
        behaviorFunctions.add(idle);
    }

    public static List<String> getBehaviorNames() {
        return behaviorFunctions.stream().map(Function::getFunctionName).collect(Collectors.toList());
    }

    public static Function getBehaviorFunction(String behaviorName) {
        for (Function function : behaviorFunctions) {
            if (Objects.equals(function.getFunctionName(), behaviorName)) {
                return function;
            }
        }
        return null;
    }
}

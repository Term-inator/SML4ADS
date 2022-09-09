package com.ecnu.adsmls.utils.register;


import java.util.*;
import java.util.stream.Collectors;

public abstract class FunctionRegister {
    public enum FunctionCategory {
        BEHAVIOR, LOCATION, WEATHER, RULE
    }

    protected static Map<FunctionCategory, List<Function>> functions = new HashMap<>();
    public abstract void init();

    public static List<String> getFunctionNames(FunctionCategory functionCategory) {
        return functions.get(functionCategory).stream().map(Function::getFunctionName).collect(Collectors.toList());
    }

    public static Function getFunction(FunctionCategory functionCategory, String functionName) {
        for (Function function : functions.get(functionCategory)) {
            if (Objects.equals(function.getFunctionName(), functionName)) {
                return function;
            }
        }
        return null;
    }
}

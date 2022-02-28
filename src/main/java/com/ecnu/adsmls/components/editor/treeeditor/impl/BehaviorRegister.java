package com.ecnu.adsmls.components.editor.treeeditor.impl;

import java.util.*;

public class BehaviorRegister {
    // 行为名，参数名，参数类型
    private static LinkedHashMap<String, LinkedHashMap<String, String>> behaviorFunctions = new LinkedHashMap<>();

    // 初始化内置 behavior 及其参数
    static {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        params.put("duration", "int");
        register("Keep", (LinkedHashMap<String, String>) params.clone());

        params.clear();
        params.put("acceleration", "double");
        params.put("target speed", "double");
        params.put("duration", "int");
        register("Accelerate", (LinkedHashMap<String, String>) params.clone());

        params.clear();
        params.put("acceleration", "double");
        params.put("target speed", "double");
        register("ChangeLeft", (LinkedHashMap<String, String>) params.clone());
        register("ChangeRight", (LinkedHashMap<String, String>) params.clone());
        register("TurnLeft", (LinkedHashMap<String, String>) params.clone());
        register("TurnRight",(LinkedHashMap<String, String>) params.clone());
    }

    public static void register(String behaviorName, LinkedHashMap<String, String> params) {
        if(behaviorFunctions.containsKey(behaviorName)) {
            return;
        }
        behaviorFunctions.put(behaviorName, params);
    }

    public static List<String> getBehaviorNames() {
        return new ArrayList<>(behaviorFunctions.keySet());
    }

    public static LinkedHashMap<String, String> getParams(String behaviorName) {
        return behaviorFunctions.get(behaviorName);
    }
}

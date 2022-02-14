package com.ecnu.adsmls.components.editor.treeeditor.impl;

import java.util.*;

public class BehaviorRegister {
    // 行为名，参数名，参数类型
    private static LinkedHashMap<String, LinkedHashMap<String, String>> behaviorFunctions = new LinkedHashMap<>();

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

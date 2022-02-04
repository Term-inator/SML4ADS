package com.ecnu.adsmls.components.editor.impl;

import javafx.util.Pair;

import java.util.*;

public class BehaviorRegister {
    // 行为名，参数名，参数类型
    private static Map<String, Map<String, String>> behaviorFunctions = new LinkedHashMap<>();

    public static void register(String behaviorName, Map<String, String> params) {
        if(behaviorFunctions.containsKey(behaviorName)) {
            return;
        }
        behaviorFunctions.put(behaviorName, params);
    }

    public static List<String> getBehaviorNames() {
        return new ArrayList<>(behaviorFunctions.keySet());
    }

    public static Map<String, String> getParams(String behaviorName) {
        return behaviorFunctions.get(behaviorName);
    }
}

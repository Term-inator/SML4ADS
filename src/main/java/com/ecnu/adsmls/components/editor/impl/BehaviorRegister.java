package com.ecnu.adsmls.components.editor.impl;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BehaviorRegister {
    // 行为名，参数名，参数类型
    private static Map<String, List<Pair<String, String>>> behaviorFunctions = new HashMap<>();

    @SafeVarargs
    public static void register(String behaviorName, Pair<String, String>... params) {
        if(behaviorFunctions.containsKey(behaviorName)) {
            return;
        }
        behaviorFunctions.put(behaviorName, List.of(params));
    }

    public static List<String> getBehaviorNames() {
        return new ArrayList<>(behaviorFunctions.keySet());
    }

    public static List<Pair<String, String>> getParams(String behaviorName) {
        return behaviorFunctions.get(behaviorName);
    }
}

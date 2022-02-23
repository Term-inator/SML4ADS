package com.ecnu.adsmls.components.editor.modeleditor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LocationRegister {
    // 位置类型，参数名，参数类型
    private static LinkedHashMap<String, LinkedHashMap<String, String>> locationFunctions = new LinkedHashMap<>();

    static {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        params.put("x", "double");
        params.put("y", "double");
        register("Global Position", (LinkedHashMap<String, String>) params.clone());

        params.clear();
        params.put("road id", "int");
        params.put("lane id", "int");
        params.put("lateral offset", "double");
        params.put("min longitudinal offset", "double");
        params.put("max longitudinal offset", "double");
        register("Lane Position", (LinkedHashMap<String, String>) params.clone());
    }

    public static void register(String locationType, LinkedHashMap<String, String> params) {
        if(locationFunctions.containsKey(locationType)) {
            return;
        }
        locationFunctions.put(locationType, params);
    }

    public static List<String> getLocationTypes() {
        return new ArrayList<>(locationFunctions.keySet());
    }

    public static LinkedHashMap<String, String> getParams(String locationType) {
        return locationFunctions.get(locationType);
    }
}

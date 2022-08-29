package com.ecnu.adsmls.utils.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;

public class Factory<K, V> {
    protected Map<K, String> classNameMap = new HashMap<>();

    public V getProduct(K key) {
        Class clazz = null;
        try {
            clazz = Class.forName(this.classNameMap.get(key));
            return (V) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}

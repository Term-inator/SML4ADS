package com.ecnu.adsmls.utils.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @param <K> Map 的 key 类型
 * @param <V> 返回的对象的类型
 */
public class Factory<K, V> {
    protected Map<K, String> classNameMap = new HashMap<>();
    protected Class[] args = {};

    public V getProduct(K key, Object... initArgs) {
        Class clazz = null;
        try {
            clazz = Class.forName(this.classNameMap.get(key));
            return (V) clazz.getDeclaredConstructor(args).newInstance(initArgs);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

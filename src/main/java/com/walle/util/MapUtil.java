package com.walle.util;

import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class MapUtil {
    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    public static String getStr(Map<String, Object> map, String key) {
        return (String) getValue(map, key);
    }

    public static Integer getInteger(Map<String, Object> map, String key) {
        return (Integer) getValue(map, key);
    }

    public static int getInt(Map<String, Object> map, String key) {
        Integer value = (Integer) getValue(map, key);
        return value != null ? value : 0;
    }

    public static Long getLong(Map<String, Object> map, String key) {
        return (Long) getValue(map, key);
    }

    public static boolean getBoolean(Map<String, Object> map, String key) {
        Boolean value = (Boolean) getValue(map, key);
        return value != null && value;
    }

    public static Map<String, Object> getMap(Map<String, Object> map, String key) {
        return (Map<String, Object>) getValue(map, key);
    }

    public static List<Object> getList(Map<String, Object> map, String key) {
        return (List<Object>) getValue(map, key);
    }

    public static Object getValue(Map<String, Object> map, String key) {
        return StrUtil.isEmpty(key) || isEmpty(map) || !map.containsKey(key) ? null : map.get(key);
    }
}

package com.example.sharding.util;

import com.alibaba.fastjson.JSON;

public class JSONUtils {
    private JSONUtils() {
    }

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return json != null && json.trim().length() != 0 ? JSON.parseObject(json, clazz) : null;
    }
}

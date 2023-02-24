package com.afanty.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContextUtils {
    private static final Map<String, Object> mObjects = new HashMap<>();
    @SuppressLint("StaticFieldLeak")
    private static volatile Context mContext = null;

    private ContextUtils() {
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * set the global Context object, to be used by getContext() later.
     */
    public static void setContext(Context context) {
        if (mContext != null || context == null) {
            return;
        }
        mContext = context.getApplicationContext();
    }

    public static Object get(String key) {
        Assert.notNull(key);
        Object obj = null;
        synchronized (mObjects) {
            obj = mObjects.get(key);
        }
        return obj;
    }

    public static Object remove(String key) {
        Assert.notNull(key);
        Object obj = null;
        synchronized (mObjects) {
            obj = mObjects.remove(key);
        }
        return obj;
    }

    public static void add(String key, Object obj) {
        synchronized (mObjects) {
            mObjects.put(key, obj);
        }
    }

    public static String add(Object obj) {
        String key = UUID.randomUUID().toString();
        synchronized (mObjects) {
            mObjects.put(key, obj);
        }
        return key;
    }
}

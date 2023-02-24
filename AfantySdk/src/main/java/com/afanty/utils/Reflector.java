package com.afanty.utils;

import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Reflector {

    private Reflector() {

    }

    public static Class<?> getReflectionClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    public static boolean hasNecessaryClazz(String clazzName) {
        try {
            getReflectionClass(clazzName);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static Object getFieldValue(Object bean, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Assert.notNull(bean);
        Assert.notNEWS(fieldName);
        Field field = bean.getClass().getDeclaredField(fieldName);
        boolean originAccessible = field.isAccessible();
        try {
            if (!originAccessible)
                field.setAccessible(true);
            return field.get(bean);
        } finally {
            field.setAccessible(originAccessible);
        }
    }


    public static Object invokeMethod(Object bean, String methodName, Class<?>[] paramTypes, Object[] args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Assert.notNull(bean);
        Assert.notNEWS(methodName);
        Method method = bean.getClass().getDeclaredMethod(methodName, paramTypes);
        boolean originAccessible = method.isAccessible();
        try {
            if (!originAccessible)
                method.setAccessible(true);
            return method.invoke(bean, args);
        } finally {
            method.setAccessible(originAccessible);
        }
    }

    public static Object createInstanceOfClassByClazzName(String clazzName, Object[] args, Class<?>... argtypes) {
        if (TextUtils.isEmpty(clazzName))
            return null;

        Class<?> reflectionClass = null;
        try {
            reflectionClass = getReflectionClass(clazzName);
        } catch (ClassNotFoundException e) {
        }

        if (reflectionClass == null)
            return null;

        Object ret = null;
        try {
            if (args == null)
                ret = reflectionClass.newInstance();
            else {
                Constructor<?> con = reflectionClass.getDeclaredConstructor(argtypes);
                boolean originAccessible = con.isAccessible();
                try {
                    if (!originAccessible) {
                        con.setAccessible(true);
                    }
                    ret = con.newInstance(args);
                } catch (Exception e) {
                } finally {
                    con.setAccessible(originAccessible);
                }
            }
        } catch (Exception e) {
        }
        return ret;
    }

    public static Object invokeStaticMethod(Class clazz, String methodName, Class<?>[] paramTypes, Object[] args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        boolean originAccessible = method.isAccessible();
        try {
            if (!originAccessible)
                method.setAccessible(true);
            return method.invoke(null, args);
        } finally {
            method.setAccessible(originAccessible);
        }
    }
}

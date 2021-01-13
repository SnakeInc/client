/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package de.uol.snakeinc.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WhiteBox {

    public static Object getInternalState(Object target, String field) {
        Class<?> c = target.getClass();
        try {
            Field f = getFieldFromHierarchy(c, field);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get internal state on a private field." +
                " Please report to mockito mailing list.", e);
        }
    }

    public static void setInternalState(Object target, String field, Object value) {
        Class<?> c = target.getClass();
        try {
            Field f = getFieldFromHierarchy(c, field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Unable to set internal state on a private field." +
                " Please report to mockito mailing list.", e);
        }
    }

    public static Method getPrivateMethod(Object target, String methodName, Class<?>...parameters) {
        try {
            Method method = getMethodFromHierarchy(target.getClass(), methodName, parameters);
            method.setAccessible(true);
            return method;
        } catch (SecurityException exception) {
            throw new RuntimeException("Unable to invoke method on a private method, security-issue.", exception);
        }
    }

    private static Field getFieldFromHierarchy(Class<?> clazz, String field) {
        Field f = getField(clazz, field);
        while (f == null && clazz != Object.class) {
            clazz = clazz.getSuperclass();
            f = getField(clazz, field);
        }
        if (f == null) {
            throw new RuntimeException(
                "You want me to get this field: '" + field +
                    "' on this class: '" + clazz.getSimpleName() +
                    "' but this field is not declared within the hierarchy of this class!");
        }
        return f;
    }

    private static Method getMethodFromHierarchy(Class<?> clazz, String methodName, Class<?>... types) {
        Method method = getMethod(clazz, methodName, types);
        while (method == null && clazz != Object.class) {
            clazz = clazz.getSuperclass();
            method = getMethod(clazz, methodName, types);
        }
        if (method == null) {
            throw new RuntimeException(
                "You want me to get this method: '" + methodName +
                    "' on this class: '" + clazz.getSimpleName() +
                    "' but this method is not declared within the hierarchy of this class!");
        }
        return method;
    }

    private static Field getField(Class<?> clazz, String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
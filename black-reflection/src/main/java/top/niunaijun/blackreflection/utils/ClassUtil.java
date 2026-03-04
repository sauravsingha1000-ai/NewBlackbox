package top.niunaijun.blackreflection.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for reflective class/method/field lookup.
 */
public class ClassUtil {
    private static final String TAG = "ClassUtil";

    private static final Map<String, Class<?>> sClassCache = new HashMap<>();
    private static final Map<String, Method> sMethodCache = new HashMap<>();
    private static final Map<String, Field> sFieldCache = new HashMap<>();

    /**
     * Find a class by name, using cache.
     */
    public static Class<?> findClass(String className, ClassLoader classLoader) {
        if (className == null || className.isEmpty()) return null;
        Class<?> cached = sClassCache.get(className);
        if (cached != null) return cached;

        try {
            Class<?> clz = Class.forName(className, false,
                    classLoader != null ? classLoader : ClassUtil.class.getClassLoader());
            sClassCache.put(className, clz);
            return clz;
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "Class not found: " + className);
            return null;
        }
    }

    /**
     * Find a declared method, walking up the class hierarchy.
     */
    public static Method findMethod(Class<?> clz, String methodName, Class<?>... paramTypes) {
        if (clz == null || methodName == null) return null;
        String key = clz.getName() + "#" + methodName + Arrays.toString(paramTypes);
        Method cached = sMethodCache.get(key);
        if (cached != null) return cached;

        Class<?> current = clz;
        while (current != null && current != Object.class) {
            try {
                Method m = current.getDeclaredMethod(methodName, paramTypes);
                m.setAccessible(true);
                sMethodCache.put(key, m);
                return m;
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        Log.w(TAG, "Method not found: " + methodName + " in " + clz.getName());
        return null;
    }

    /**
     * Find a declared field, walking up the class hierarchy.
     */
    public static Field findField(Class<?> clz, String fieldName) {
        if (clz == null || fieldName == null) return null;
        String key = clz.getName() + "#" + fieldName;
        Field cached = sFieldCache.get(key);
        if (cached != null) return cached;

        Class<?> current = clz;
        while (current != null && current != Object.class) {
            try {
                Field f = current.getDeclaredField(fieldName);
                f.setAccessible(true);
                sFieldCache.put(key, f);
                return f;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        Log.w(TAG, "Field not found: " + fieldName + " in " + clz.getName());
        return null;
    }

    /**
     * Clear all reflection caches.
     */
    public static void clearCache() {
        sClassCache.clear();
        sMethodCache.clear();
        sFieldCache.clear();
    }
}

package top.niunaijun.blackreflection.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Convenience wrapper for common reflection operations.
 */
public final class Reflector {
    private static final String TAG = "Reflector";

    private Reflector() {}

    public static Object invokeMethod(Object receiver, String methodName,
                                      Class<?>[] paramTypes, Object... args) {
        try {
            Method m = ClassUtil.findMethod(receiver.getClass(), methodName, paramTypes);
            if (m != null) return m.invoke(receiver, args);
        } catch (Exception e) {
            Log.e(TAG, "invokeMethod failed: " + methodName, e);
        }
        return null;
    }

    public static Object getField(Object receiver, String fieldName) {
        try {
            Field f = ClassUtil.findField(receiver.getClass(), fieldName);
            if (f != null) return f.get(receiver);
        } catch (Exception e) {
            Log.e(TAG, "getField failed: " + fieldName, e);
        }
        return null;
    }

    public static void setField(Object receiver, String fieldName, Object value) {
        try {
            Field f = ClassUtil.findField(receiver.getClass(), fieldName);
            if (f != null) f.set(receiver, value);
        } catch (Exception e) {
            Log.e(TAG, "setField failed: " + fieldName, e);
        }
    }

    public static Object getStaticField(Class<?> clz, String fieldName) {
        try {
            Field f = ClassUtil.findField(clz, fieldName);
            if (f != null) return f.get(null);
        } catch (Exception e) {
            Log.e(TAG, "getStaticField failed: " + fieldName, e);
        }
        return null;
    }

    public static void setStaticField(Class<?> clz, String fieldName, Object value) {
        try {
            Field f = ClassUtil.findField(clz, fieldName);
            if (f != null) f.set(null, value);
        } catch (Exception e) {
            Log.e(TAG, "setStaticField failed: " + fieldName, e);
        }
    }
}

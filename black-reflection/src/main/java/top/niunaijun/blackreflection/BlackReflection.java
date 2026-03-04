package top.niunaijun.blackreflection;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackreflection.annotation.BClass;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClass;
import top.niunaijun.blackreflection.annotation.BParamClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;
import top.niunaijun.blackreflection.utils.ClassUtil;

/**
 * Core reflection framework: turns annotated interfaces into dynamic proxies
 * that call hidden Android APIs reflectively at runtime.
 *
 * <p>Usage:
 * <pre>
 *   {@literal @}BClass("android.app.ActivityManager")
 *   interface IActivityManagerBlack {
 *       {@literal @}BMethod
 *       List getRecentTasks(int maxNum, int flags);
 *   }
 *
 *   IActivityManagerBlack proxy = BlackReflection.get(IActivityManagerBlack.class, instance);
 *   proxy.getRecentTasks(10, 0);
 * </pre>
 */
public class BlackReflection {
    private static final String TAG = "BlackReflection";
    private static final Map<Class<?>, Object> sProxyCache = new HashMap<>();
    private static ClassLoader sClassLoader;

    /** Set the ClassLoader used for hidden API class resolution. */
    public static void setClassLoader(ClassLoader classLoader) {
        sClassLoader = classLoader;
    }

    /**
     * Create (or return cached) a proxy for the given annotated interface.
     *
     * @param interfaceClass  annotated interface
     * @param target          the real Android object (may be null for static-only interfaces)
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> interfaceClass, Object target) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass.getName() + " must be an interface");
        }
        BClass bClass = interfaceClass.getAnnotation(BClass.class);
        if (bClass == null) {
            throw new IllegalArgumentException(interfaceClass.getName() + " missing @BClass");
        }

        String realClassName = bClass.value().isEmpty()
                ? interfaceClass.getSimpleName()
                : bClass.value();
        Class<?> realClass = ClassUtil.findClass(realClassName, sClassLoader);

        Object proxy = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new BlackInvocationHandler(realClass, target));

        return (T) proxy;
    }

    // ── Invocation handler ────────────────────────────────────────────────
    private static class BlackInvocationHandler implements InvocationHandler {
        private final Class<?> realClass;
        private final Object target;

        BlackInvocationHandler(Class<?> realClass, Object target) {
            this.realClass = realClass;
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Resolve parameter types
            Class<?>[] paramTypes = resolveParamTypes(method, args);

            // BConstructor
            if (method.isAnnotationPresent(BConstructor.class)) {
                if (realClass == null) return null;
                try {
                    java.lang.reflect.Constructor<?> ctor =
                            realClass.getDeclaredConstructor(paramTypes);
                    ctor.setAccessible(true);
                    return ctor.newInstance(args);
                } catch (Exception e) {
                    Log.e(TAG, "Constructor invoke failed", e);
                    return null;
                }
            }

            // BStaticMethod
            BStaticMethod bStaticMethod = method.getAnnotation(BStaticMethod.class);
            if (bStaticMethod != null) {
                String name = bStaticMethod.value().isEmpty() ? method.getName() : bStaticMethod.value();
                return ClassUtil.findMethod(realClass, name, paramTypes) != null
                        ? ClassUtil.findMethod(realClass, name, paramTypes).invoke(null, args)
                        : null;
            }

            // BStaticField (getter)
            BStaticField bStaticField = method.getAnnotation(BStaticField.class);
            if (bStaticField != null) {
                String name = bStaticField.value().isEmpty() ? method.getName() : bStaticField.value();
                java.lang.reflect.Field f = ClassUtil.findField(realClass, name);
                return f != null ? f.get(null) : null;
            }

            // BField (getter)
            BField bField = method.getAnnotation(BField.class);
            if (bField != null) {
                String name = bField.value().isEmpty() ? method.getName() : bField.value();
                java.lang.reflect.Field f = ClassUtil.findField(
                        target != null ? target.getClass() : realClass, name);
                return f != null ? f.get(target) : null;
            }

            // BMethod (default)
            BMethod bMethod = method.getAnnotation(BMethod.class);
            String methodName = (bMethod != null && !bMethod.value().isEmpty())
                    ? bMethod.value() : method.getName();

            if (realClass == null || target == null) return null;
            java.lang.reflect.Method m = ClassUtil.findMethod(realClass, methodName, paramTypes);
            if (m == null) {
                Log.w(TAG, "Method not found: " + methodName);
                return null;
            }
            return m.invoke(target, args);
        }

        private Class<?>[] resolveParamTypes(Method method, Object[] args) {
            if (args == null) return new Class<?>[0];
            java.lang.annotation.Annotation[][] paramAnnotations = method.getParameterAnnotations();
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = null;
                for (java.lang.annotation.Annotation a : paramAnnotations[i]) {
                    if (a instanceof BParamClass) {
                        types[i] = ((BParamClass) a).value();
                        break;
                    } else if (a instanceof BParamClassName) {
                        types[i] = ClassUtil.findClass(((BParamClassName) a).value(), sClassLoader);
                        break;
                    }
                }
                if (types[i] == null) {
                    types[i] = args[i] != null ? args[i].getClass() : Object.class;
                }
            }
            return types;
        }
    }
}

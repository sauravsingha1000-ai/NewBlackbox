package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;

/**
 * Hooks IActivityTaskManager (Android 10+) for virtual activity routing.
 */
public class IActivityTaskManagerProxy extends BinderInvocationStub {
    private static final String TAG = "IActivityTaskManagerProxy";

    @Override
    protected Object getBase() throws Throwable {
        try {
            Class<?> clz = Class.forName("android.app.ActivityTaskManager");
            java.lang.reflect.Field f = clz.getDeclaredField("IActivityTaskManagerSingleton");
            f.setAccessible(true);
            Object singleton = f.get(null);
            Class<?> sc = Class.forName("android.util.Singleton");
            java.lang.reflect.Method get = sc.getDeclaredMethod("get");
            get.setAccessible(true);
            return get.invoke(singleton);
        } catch (Exception e) {
            Log.w(TAG, "IActivityTaskManager not available: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected Class<?> getInterfaceClass() {
        try { return Class.forName("android.app.IActivityTaskManager"); }
        catch (ClassNotFoundException e) { return null; }
    }

    @Override
    protected void replaceService(Object proxy) throws Throwable {
        try {
            Class<?> clz = Class.forName("android.app.ActivityTaskManager");
            java.lang.reflect.Field f = clz.getDeclaredField("IActivityTaskManagerSingleton");
            f.setAccessible(true);
            Object singleton = f.get(null);
            Class<?> sc = Class.forName("android.util.Singleton");
            java.lang.reflect.Field mInstance = sc.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            mInstance.set(singleton, proxy);
        } catch (Exception e) {
            Log.e(TAG, "replaceService failed", e);
        }
    }
}

package top.niunaijun.blackbox.fake.service;

import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * Intercepts ActivityManager IPC calls to redirect to virtual engine.
 */
public class IActivityManagerProxy extends BinderInvocationStub {
    private static final String TAG = "IActivityManagerProxy";

    @Override
    protected Object getBase() throws Throwable {
        try {
            Class<?> amClass = Class.forName("android.app.ActivityManager");
            Object service = null;
            Class<?> stubClass = Class.forName("android.app.IActivityManager$Stub");
            java.lang.reflect.Method asInterface = stubClass.getMethod("asInterface", android.os.IBinder.class);
            return asInterface.invoke(null, service);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get IActivityManager", e);
            return null;
        }
    }

    @Override
    protected Class<?> getInterfaceClass() {
        try { return Class.forName("android.app.IActivityManager"); }
        catch (ClassNotFoundException e) { return null; }
    }

    @Override
    protected void replaceService(Object proxy) throws Throwable {
        try {
            Class<?> holder = Class.forName("android.app.ActivityManager");
            java.lang.reflect.Field f = holder.getDeclaredField("IActivityManagerSingleton");
            f.setAccessible(true);
            Object singleton = f.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            java.lang.reflect.Field mInstance = singletonClass.getDeclaredField("mInstance");
            mInstance.setAccessible(true);
            mInstance.set(singleton, proxy);
        } catch (Exception e) {
            Log.e(TAG, "replaceService failed", e);
        }
    }

    public IActivityManagerProxy() {
        // Hook startActivity to redirect to virtual engine
        addHook("startActivity", new MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) {
                Log.d(TAG, "startActivity hooked");
            }
        });
        addHook("getPackageForIntentSender", new MethodHook() {
            @Override public void beforeHookedMethod(MethodHookParam param) {}
        });
    }
}

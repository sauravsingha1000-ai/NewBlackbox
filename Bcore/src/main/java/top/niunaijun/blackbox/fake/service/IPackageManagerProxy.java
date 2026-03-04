package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * Hooks IPackageManager to return virtual package information.
 */
public class IPackageManagerProxy extends BinderInvocationStub {
    private static final String TAG = "IPackageManagerProxy";

    @Override
    protected Object getBase() throws Throwable {
        try {
            Class<?> clz = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Method m = clz.getDeclaredMethod("getPackageManager");
            m.setAccessible(true);
            return m.invoke(null);
        } catch (Exception e) {
            Log.e(TAG, "getBase failed", e);
            return null;
        }
    }

    @Override
    protected Class<?> getInterfaceClass() {
        try { return Class.forName("android.content.pm.IPackageManager"); }
        catch (ClassNotFoundException e) { return null; }
    }

    @Override
    protected void replaceService(Object proxy) throws Throwable {
        try {
            Class<?> clz = Class.forName("android.app.ActivityThread");
            java.lang.reflect.Field f = clz.getDeclaredField("sPackageManager");
            f.setAccessible(true);
            f.set(null, proxy);
        } catch (Exception e) {
            Log.e(TAG, "replaceService failed", e);
        }
    }

    public IPackageManagerProxy() {
        addHook("getApplicationInfo", new MethodHook() {
            @Override public void beforeHookedMethod(MethodHookParam param) {
                // Return virtual package info when requested
            }
        });
    }
}

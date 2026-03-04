package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.location.BLocationManagerService;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * Intercepts location manager calls to inject fake GPS coordinates.
 */
public class ILocationManagerProxy extends BinderInvocationStub {
    private static final String TAG = "ILocationManagerProxy";

    @Override
    protected Object getBase() throws Throwable {
        return null;
    }

    @Override
    protected Class<?> getInterfaceClass() {
        try { return Class.forName("android.location.ILocationManager"); }
        catch (Exception e) { return null; }
    }

    @Override
    protected void replaceService(Object proxy) throws Throwable {
        try {
            Class<?> clz = Class.forName("android.location.LocationManager");
            java.lang.reflect.Field f = clz.getDeclaredField("mService");
            f.setAccessible(true);
            // Replace in current thread's LocationManager instance
        } catch (Exception e) {
            Log.e(TAG, "replaceService failed", e);
        }
    }

    public ILocationManagerProxy() {
        addHook("getLastLocation", new MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) {
                BLocation fake = BLocationManagerService.get().getFakeLocation(0);
                if (fake != null) {
                    param.setResult(fake.toAndroidLocation());
                }
            }
        });
    }
}

package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * Intercepts TelephonyManager calls to return spoofed device identifiers.
 */
public class DeviceIdProxy extends BinderInvocationStub {
    private static final String TAG = "DeviceIdProxy";
    private String mFakeImei;
    private String mFakeImsi;

    public DeviceIdProxy() {
        addHook("getDeviceId", new MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) {
                if (mFakeImei != null) param.setResult(mFakeImei);
            }
        });
        addHook("getSubscriberId", new MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) {
                if (mFakeImsi != null) param.setResult(mFakeImsi);
            }
        });
    }

    public void setFakeImei(String imei) { mFakeImei = imei; }
    public void setFakeImsi(String imsi) { mFakeImsi = imsi; }

    @Override protected Object getBase() throws Throwable { return null; }
    @Override protected Class<?> getInterfaceClass() { return null; }
}

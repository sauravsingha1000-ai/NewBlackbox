package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * Intercepts Settings.Secure.getString calls to return a virtual Android ID.
 */
public class AndroidIdProxy extends BinderInvocationStub {
    private static final String TAG = "AndroidIdProxy";
    private String mFakeAndroidId;

    public AndroidIdProxy() {
        addHook("getString", new MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParam param) {
                if (param.args != null && param.args.length >= 2
                        && "android_id".equals(param.args[1])) {
                    if (mFakeAndroidId != null) {
                        param.setResult(mFakeAndroidId);
                    }
                }
            }
        });
    }

    public void setFakeAndroidId(String id) { mFakeAndroidId = id; }

    @Override protected Object getBase() throws Throwable { return null; }
    @Override protected Class<?> getInterfaceClass() { return null; }
}

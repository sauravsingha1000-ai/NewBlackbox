package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;

/**
 * Proxy stub for IStorageManager IPC intercepts.
 */
public class IStorageManagerProxy extends BinderInvocationStub {
    private static final String TAG = "IStorageManagerProxy";

    @Override
    protected Object getBase() throws Throwable {
        Log.d(TAG, "getBase called");
        return null;
    }

    @Override
    protected Class<?> getInterfaceClass() {
        return null;
    }
}

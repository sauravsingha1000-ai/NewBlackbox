package top.niunaijun.blackbox.fake.service;

import android.util.Log;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;

/**
 * Proxy stub for IInputMethodManager IPC intercepts.
 */
public class IInputMethodManagerProxy extends BinderInvocationStub {
    private static final String TAG = "IInputMethodManagerProxy";

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

package top.niunaijun.blackbox.fake;

import android.content.Context;
import android.util.Log;

import top.niunaijun.blackbox.fake.hook.HookManager;

/**
 * Bootstraps all Java-level Binder/API hooks for a virtual process.
 */
public class FakeCore {
    private static final String TAG = "FakeCore";
    private static volatile boolean sInitialized = false;

    public static void init(Context context) {
        if (sInitialized) return;
        sInitialized = true;
        HookManager.get().installHooks(context);
        Log.d(TAG, "FakeCore initialized — hooks installed");
    }
}

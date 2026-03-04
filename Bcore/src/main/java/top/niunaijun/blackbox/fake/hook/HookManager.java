package top.niunaijun.blackbox.fake.hook;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Central manager that installs all virtual API hooks.
 */
public class HookManager {
    private static final String TAG = "HookManager";
    private static volatile HookManager sInstance;
    private final List<IInjectHook> mHooks = new ArrayList<>();
    private boolean mInstalled = false;

    private HookManager() {}

    public static HookManager get() {
        if (sInstance == null) {
            synchronized (HookManager.class) {
                if (sInstance == null) sInstance = new HookManager();
            }
        }
        return sInstance;
    }

    public void installHooks(Context context) {
        if (mInstalled) return;
        mInstalled = true;

        // Register all proxy hooks
        registerHooks();

        for (IInjectHook hook : mHooks) {
            try {
                hook.inject();
                Log.d(TAG, "Hook installed: " + hook.getClass().getSimpleName());
            } catch (Throwable t) {
                Log.e(TAG, "Hook failed: " + hook.getClass().getSimpleName(), t);
            }
        }
    }

    private void registerHooks() {
        // Activity Manager hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.IActivityManagerProxy());
        mHooks.add(new top.niunaijun.blackbox.fake.service.IActivityTaskManagerProxy());
        // Package Manager hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.IPackageManagerProxy());
        // Location hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.ILocationManagerProxy());
        // Other hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.AndroidIdProxy());
        mHooks.add(new top.niunaijun.blackbox.fake.service.DeviceIdProxy());
    }

    public void addHook(IInjectHook hook) {
        mHooks.add(hook);
    }
}

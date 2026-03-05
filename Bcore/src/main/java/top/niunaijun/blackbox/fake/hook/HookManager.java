package top.niunaijun.blackbox.fake.hook;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Central manager that installs all virtual API hooks.
 * Android 15 compatible version - supports safe mode without dangerous hooks.
 */
public class HookManager {
    private static final String TAG = "HookManager";
    private static volatile HookManager sInstance;
    private final List<IInjectHook> mHooks = new ArrayList<>();
    private boolean mInstalled = false;
    
    // Android 15+ compatibility flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;
    private static boolean sSafeMode = false;

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
        
        // Android 15+ check: Use safe mode (no dangerous hooks)
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.w(TAG, "Android 15+ detected - using SAFE MODE (limited hooks)");
            installSafeModeHooks(context);
        } else {
            Log.i(TAG, "Installing full hook set (normal mode)");
            installFullHooks(context);
        }
        
        mInstalled = true;
    }
    
    /**
     * Safe mode for Android 15+ - only essential, non-dangerous hooks.
     * Skips Binder hooks and device spoofing that cause crashes on Android 15.
     */
    private void installSafeModeHooks(Context context) {
        sSafeMode = true;
        
        // Only register safe hooks that don't use native Binder manipulation
        registerSafeHooks();
        
        // Install hooks with error handling
        for (IInjectHook hook : mHooks) {
            try {
                hook.inject();
                Log.d(TAG, "Safe mode hook installed: " + hook.getClass().getSimpleName());
            } catch (Throwable t) {
                Log.e(TAG, "Safe mode hook failed (non-critical): " + hook.getClass().getSimpleName(), t);
                // Continue even if one hook fails - app can still work
            }
        }
        
        Log.i(TAG, "Safe mode initialization complete - " + mHooks.size() + " hooks active");
    }
    
    /**
     * Full hook set for Android 14 and below - all features enabled.
     */
    private void installFullHooks(Context context) {
        sSafeMode = false;
        
        // Register all hooks including dangerous ones
        registerFullHooks();
        
        // Install hooks
        for (IInjectHook hook : mHooks) {
            try {
                hook.inject();
                Log.d(TAG, "Hook installed: " + hook.getClass().getSimpleName());
            } catch (Throwable t) {
                Log.e(TAG, "Hook failed: " + hook.getClass().getSimpleName(), t);
            }
        }
        
        Log.i(TAG, "Full mode initialization complete - " + mHooks.size() + " hooks active");
    }

    /**
     * Safe hooks for Android 15+ - minimal set that won't crash.
     * These hooks use only Java-level reflection, no native Binder manipulation.
     */
    private void registerSafeHooks() {
        // Only PackageManager - required for app launching
        // Uses Java reflection only, safe for Android 15
        mHooks.add(new top.niunaijun.blackbox.fake.service.IPackageManagerProxy());
        
        // Note: ActivityManager hooks are skipped in safe mode
        // because they require Binder proxy manipulation that Android 15 blocks
    }
    
    /**
     * Full hook set for Android 14 and below.
     * Includes all spoofing and interception features.
     */
    private void registerFullHooks() {
        // Activity Manager hooks - requires Binder manipulation
        mHooks.add(new top.niunaijun.blackbox.fake.service.IActivityManagerProxy());
        mHooks.add(new top.niunaijun.blackbox.fake.service.IActivityTaskManagerProxy());
        
        // Package Manager hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.IPackageManagerProxy());
        
        // Location hooks - GPS spoofing
        mHooks.add(new top.niunaijun.blackbox.fake.service.ILocationManagerProxy());
        
        // Device ID spoofing hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.AndroidIdProxy());
        mHooks.add(new top.niunaijun.blackbox.fake.service.DeviceIdProxy());
        
        // Additional hooks
        mHooks.add(new top.niunaijun.blackbox.fake.service.ITelephonyManagerProxy());
        mHooks.add(new top.niunaijun.blackbox.fake.service.IWifiManagerProxy());
    }

    public void addHook(IInjectHook hook) {
        if (hook == null) return;
        mHooks.add(hook);
        Log.d(TAG, "Custom hook added: " + hook.getClass().getSimpleName());
    }
    
    /** Check if running in safe mode (Android 15+ limited functionality) */
    public boolean isSafeMode() {
        return sSafeMode;
    }
    
    /** Check if hooks have been installed */
    public boolean isInstalled() {
        return mInstalled;
    }
    
    /** Get number of active hooks */
    public int getHookCount() {
        return mHooks.size();
    }
    
    /**
     * Manually enable safe mode even on older Android versions.
     * Useful for debugging or if native hooks cause issues.
     */
    public void forceSafeMode() {
        if (mInstalled) {
            Log.w(TAG, "Cannot force safe mode - hooks already installed");
            return;
        }
        sSafeMode = true;
        Log.w(TAG, "Safe mode forced manually");
    }
}

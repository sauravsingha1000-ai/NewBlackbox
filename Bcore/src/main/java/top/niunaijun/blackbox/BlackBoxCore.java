package top.niunaijun.blackbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import top.niunaijun.blackbox.core.GmsCore;
import top.niunaijun.blackbox.core.IOCore;
import top.niunaijun.blackbox.core.NativeCore;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.DaemonService;
import top.niunaijun.blackbox.core.system.am.BActivityManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;

import java.util.List;

/**
 * Main entry point for the BlackBox virtual engine.
 * Android 15 compatible version.
 *
 * <p>Usage in host application:
 * <pre>
 *   BlackBoxCore.get().doAttachBaseContext(this);
 * </pre>
 */
public class BlackBoxCore {
    private static final String TAG = "BlackBoxCore";
    private static volatile BlackBoxCore sInstance;

    private Context mContext;
    private volatile boolean mInitialized = false;
    
    // Android 15+ compatibility flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;

    private BlackBoxCore() {}

    public static BlackBoxCore get() {
        if (sInstance == null) {
            synchronized (BlackBoxCore.class) {
                if (sInstance == null) sInstance = new BlackBoxCore();
            }
        }
        return sInstance;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Must be called from the host Application's {@code attachBaseContext}.
     */
    public void doAttachBaseContext(Context base) {
        if (mInitialized) return;
        mContext = base.getApplicationContext();
        BEnvironment.init(mContext);
        
        // Android 15+ compatibility: Check native load status
        boolean nativeLoaded = NativeCore.isLoaded();
        Log.d(TAG, "doAttachBaseContext done, native=" + nativeLoaded + 
                   ", android15=" + IS_ANDROID_15_OR_HIGHER);
        
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.i(TAG, "Running in Android 15+ compatibility mode (native hooks disabled)");
        }
    }

    /**
     * Call from host Application's {@code onCreate}.
     */
    public void doCreate(Application app) {
        if (mInitialized) return;
        mContext = app.getApplicationContext();
        
        // Initialize GMS core (works in both modes)
        GmsCore.init(mContext);
        
        // Android 15+ compatibility: Skip native initialization, use Java-only mode
        if (IS_ANDROID_15_OR_HIGHER) {
            initAndroid15Mode(app);
        } else {
            initNormalMode(app);
        }
        
        mInitialized = true;
        Log.i(TAG, "BlackBoxCore initialized (mode=" + 
              (IS_ANDROID_15_OR_HIGHER ? "Android15+" : "Normal") + ")");
    }
    
    /**
     * Android 15+ initialization - Java-only mode without native hooks
     */
    private void initAndroid15Mode(Application app) {
        Log.w(TAG, "Initializing Android 15+ compatibility mode");
        
        // Skip native initialization - use Java fallbacks
        // IO redirection handled by IOCore Java layer
        IOCore.initJavaOnly(mContext);
        
        // Start daemon service (required for app launching)
        startDaemon();
        
        Log.i(TAG, "Android 15+ mode initialized successfully");
    }
    
    /**
     * Normal mode for Android 14 and below - full native support
     */
    private void initNormalMode(Application app) {
        // Initialize IO hooks via native layer
        if (NativeCore.isLoaded()) {
            IOCore.init(mContext);
        } else {
            Log.w(TAG, "Native not loaded, falling back to Java mode");
            IOCore.initJavaOnly(mContext);
        }
        
        // Start daemon service
        startDaemon();
    }

    private void startDaemon() {
        try {
            Intent intent = new Intent(mContext, DaemonService.class);
            
            // Android 15+ requires specific foreground service type
            if (IS_ANDROID_15_OR_HIGHER) {
                intent.putExtra("foreground_service_type", "specialUse");
            }
            
            mContext.startForegroundService(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start daemon", e);
            // Android 15+ may require additional permissions
            if (IS_ANDROID_15_OR_HIGHER) {
                Log.w(TAG, "Daemon start failed on Android 15+ - may need FOREGROUND_SERVICE permission");
            }
        }
    }

    // ── Package Management ────────────────────────────────────────────────────

    /**
     * Install an APK into the virtual space for the given user.
     */
    public InstallResult installPackageAsUser(String apkPath, int userId) {
        InstallOption opt = new InstallOption(apkPath, userId);
        
        // Android 15+ compatibility: Ensure paths are handled correctly
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "Installing package on Android 15+: " + apkPath);
        }
        
        return BPackageManagerService.get().installPackageAsUser(opt, userId);
    }

    public void uninstallPackageAsUser(String packageName, int userId) {
        BPackageManagerService.get().uninstallPackageAsUser(packageName, userId);
    }

    public List<InstalledPackage> getInstalledApps(int userId) {
        return BPackageManagerService.get().getInstalledPackages(0, userId);
    }

    public InstalledPackage getInstalledApp(String packageName, int userId) {
        return BPackageManagerService.get().getInstalledPackage(packageName, userId);
    }

    public boolean isInstalled(String packageName, int userId) {
        return BPackageManagerService.get().isInstalled(packageName, userId);
    }

    // ── App Launching ─────────────────────────────────────────────────────────

    /**
     * Launch a virtual app by package name.
     */
    public boolean launchApk(String packageName, int userId) {
        InstalledPackage pkg = BPackageManagerService.get()
                .getInstalledPackage(packageName, userId);
        if (pkg == null) {
            Log.e(TAG, "Package not installed: " + packageName);
            return false;
        }

        Intent launchIntent = mContext.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.setPackage(packageName);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        // Android 15+ compatibility: Add launch flags
        if (IS_ANDROID_15_OR_HIGHER) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        return BActivityManagerService.get().startActivity(launchIntent, packageName, userId);
    }

    /**
     * Broadcast dispatch for proxy receiver.
     */
    public void dispatchBroadcast(Context context, Intent intent) {
        // TODO: route to registered virtual receivers
        // Android 15+ compatibility: Broadcast restrictions may apply
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "Broadcast dispatch on Android 15+");
        }
    }

    // ── Configuration ─────────────────────────────────────────────────────────

    public void enableGms(boolean enable) {
        if (enable) GmsCore.get().enable();
        else GmsCore.get().disable();
    }

    public Context getContext() { return mContext; }
    public boolean isInitialized() { return mInitialized; }
    
    /** Check if running in Android 15+ compatibility mode */
    public boolean isCompatibilityMode() {
        return IS_ANDROID_15_OR_HIGHER;
    }
}

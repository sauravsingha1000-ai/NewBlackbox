package top.niunaijun.blackbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
        NativeCore.isLoaded(); // triggers static init / load
        Log.d(TAG, "doAttachBaseContext done, native=" + NativeCore.isLoaded());
    }

    /**
     * Call from host Application's {@code onCreate}.
     */
    public void doCreate(Application app) {
        if (mInitialized) return;
        mInitialized = true;
        mContext = app.getApplicationContext();
        GmsCore.init(mContext);
        startDaemon();
        Log.d(TAG, "BlackBoxCore initialized");
    }

    private void startDaemon() {
        try {
            Intent intent = new Intent(mContext, DaemonService.class);
            mContext.startForegroundService(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start daemon", e);
        }
    }

    // ── Package Management ────────────────────────────────────────────────────

    /**
     * Install an APK into the virtual space for the given user.
     */
    public InstallResult installPackageAsUser(String apkPath, int userId) {
        InstallOption opt = new InstallOption(apkPath, userId);
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

        return BActivityManagerService.get().startActivity(launchIntent, packageName, userId);
    }

    /**
     * Broadcast dispatch for proxy receiver.
     */
    public void dispatchBroadcast(Context context, Intent intent) {
        // TODO: route to registered virtual receivers
    }

    // ── Configuration ─────────────────────────────────────────────────────────

    public void enableGms(boolean enable) {
        if (enable) GmsCore.get().enable();
        else GmsCore.get().disable();
    }

    public Context getContext() { return mContext; }
    public boolean isInitialized() { return mInitialized; }
}

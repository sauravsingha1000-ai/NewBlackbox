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

public class BlackBoxCore {

    private static final String TAG = "BlackBoxCore";
    private static volatile BlackBoxCore sInstance;

    private Context mContext;
    private volatile boolean mInitialized = false;

    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;

    private BlackBoxCore() {}

    public static BlackBoxCore get() {
        if (sInstance == null) {
            synchronized (BlackBoxCore.class) {
                if (sInstance == null) {
                    sInstance = new BlackBoxCore();
                }
            }
        }
        return sInstance;
    }

    // ─────────────────────────────────────────────────────────────
    // attachBaseContext
    // ─────────────────────────────────────────────────────────────

    public void doAttachBaseContext(Context base) {

        if (mInitialized) return;

        if (base == null) {
            Log.e(TAG, "attachBaseContext received null base!");
            return;
        }

        Context appContext = base.getApplicationContext();
        mContext = appContext != null ? appContext : base;

        try {
            BEnvironment.init(mContext);
        } catch (Throwable e) {
            Log.e(TAG, "BEnvironment init failed", e);
        }

        boolean nativeLoaded = NativeCore.isLoaded();

        Log.d(TAG,
                "doAttachBaseContext done | native=" + nativeLoaded +
                " | android15=" + IS_ANDROID_15_OR_HIGHER);

        if (IS_ANDROID_15_OR_HIGHER) {
            Log.i(TAG, "Android 15+ compatibility mode enabled");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // onCreate
    // ─────────────────────────────────────────────────────────────

    public void doCreate(Application app) {

        if (mInitialized) return;

        if (app == null) {
            Log.e(TAG, "doCreate received null application!");
            return;
        }

        Context appContext = app.getApplicationContext();
        mContext = appContext != null ? appContext : app;

        try {
            GmsCore.init(mContext);
        } catch (Throwable e) {
            Log.e(TAG, "GmsCore init failed", e);
        }

        if (IS_ANDROID_15_OR_HIGHER) {
            initAndroid15Mode(app);
        } else {
            initNormalMode(app);
        }

        mInitialized = true;

        Log.i(TAG,
                "BlackBoxCore initialized | mode=" +
                (IS_ANDROID_15_OR_HIGHER ? "Android15+" : "Normal"));
    }

    // ─────────────────────────────────────────────────────────────
    // Android 15 mode
    // ─────────────────────────────────────────────────────────────

    private void initAndroid15Mode(Application app) {

        Log.w(TAG, "Starting Android 15 compatibility mode");

        try {
            IOCore.initJavaOnly(mContext);
        } catch (Throwable e) {
            Log.e(TAG, "IOCore Java init failed", e);
        }

        startDaemon();
    }

    // ─────────────────────────────────────────────────────────────
    // Normal mode
    // ─────────────────────────────────────────────────────────────

    private void initNormalMode(Application app) {

        try {

            if (NativeCore.isLoaded()) {
                IOCore.init(mContext);
            } else {
                Log.w(TAG, "Native hooks missing → Java mode fallback");
                IOCore.initJavaOnly(mContext);
            }

        } catch (Throwable e) {
            Log.e(TAG, "IOCore init failed", e);
        }

        startDaemon();
    }

    // ─────────────────────────────────────────────────────────────
    // Daemon
    // ─────────────────────────────────────────────────────────────

    private void startDaemon() {

        try {

            Intent intent = new Intent(mContext, DaemonService.class);

            if (IS_ANDROID_15_OR_HIGHER) {
                intent.putExtra("foreground_service_type", "specialUse");
            }

            mContext.startForegroundService(intent);

        } catch (Throwable e) {

            Log.e(TAG, "Daemon start failed", e);

            if (IS_ANDROID_15_OR_HIGHER) {
                Log.w(TAG,
                        "Android 15 may require FOREGROUND_SERVICE permission");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Package management
    // ─────────────────────────────────────────────────────────────

    public InstallResult installPackageAsUser(String apkPath, int userId) {

        InstallOption opt = new InstallOption(apkPath, userId);

        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "Installing on Android 15+: " + apkPath);
        }

        return BPackageManagerService.get()
                .installPackageAsUser(opt, userId);
    }

    public void uninstallPackageAsUser(String packageName, int userId) {
        BPackageManagerService.get()
                .uninstallPackageAsUser(packageName, userId);
    }

    public List<InstalledPackage> getInstalledApps(int userId) {
        return BPackageManagerService.get()
                .getInstalledPackages(0, userId);
    }

    public InstalledPackage getInstalledApp(String packageName, int userId) {
        return BPackageManagerService.get()
                .getInstalledPackage(packageName, userId);
    }

    public boolean isInstalled(String packageName, int userId) {
        return BPackageManagerService.get()
                .isInstalled(packageName, userId);
    }

    // ─────────────────────────────────────────────────────────────
    // Launch virtual app
    // ─────────────────────────────────────────────────────────────

    public boolean launchApk(String packageName, int userId) {

        InstalledPackage pkg =
                BPackageManagerService.get()
                        .getInstalledPackage(packageName, userId);

        if (pkg == null) {
            Log.e(TAG, "Package not installed: " + packageName);
            return false;
        }

        Intent launchIntent =
                mContext.getPackageManager()
                        .getLaunchIntentForPackage(packageName);

        if (launchIntent == null) {

            launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.setPackage(packageName);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        }

        if (IS_ANDROID_15_OR_HIGHER) {

            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        return BActivityManagerService.get()
                .startActivity(launchIntent, packageName, userId);
    }

    // ─────────────────────────────────────────────────────────────

    public void dispatchBroadcast(Context context, Intent intent) {

        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "Broadcast dispatch Android 15+");
        }
    }

    public void enableGms(boolean enable) {

        if (enable) {
            GmsCore.get().enable();
        } else {
            GmsCore.get().disable();
        }
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    public boolean isCompatibilityMode() {
        return IS_ANDROID_15_OR_HIGHER;
    }
}

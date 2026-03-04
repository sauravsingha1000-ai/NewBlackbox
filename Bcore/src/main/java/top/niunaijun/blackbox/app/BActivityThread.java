package top.niunaijun.blackbox.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.CrashHandler;
import top.niunaijun.blackbox.core.IOCore;
import top.niunaijun.blackbox.core.NativeCore;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.env.VirtualRuntime;
import top.niunaijun.blackbox.entity.am.ReceiverData;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;
import top.niunaijun.blackbox.fake.FakeCore;

/**
 * The activity thread equivalent for virtual processes.
 * Called when a virtual process is being attached to host.
 */
public class BActivityThread {
    private static final String TAG = "BActivityThread";
    private static volatile BActivityThread sInstance;

    private Context mContext;
    private AppSystemEnv mEnv;

    private BActivityThread() {}

    public static BActivityThread get() {
        if (sInstance == null) {
            synchronized (BActivityThread.class) {
                if (sInstance == null) sInstance = new BActivityThread();
            }
        }
        return sInstance;
    }

    /**
     * Initialize the virtual process for the given package.
     * Called before the guest Application.onCreate().
     */
    public void init(Context base, InstalledPackage pkg, int userId) {
        mContext = base;
        ApplicationInfo ai = pkg.applicationInfo;

        mEnv = new AppSystemEnv(ai, ai.processName, userId,
                android.os.Process.myUid());

        // Setup IO redirects
        String realData = base.getDataDir().getAbsolutePath();
        String virtualData = BEnvironment.getDataDir(pkg.packageName, userId).getAbsolutePath();
        IOCore.setupAppRedirects(realData, virtualData);

        // Init native hooks
        if (NativeCore.isLoaded()) {
            NativeCore.nativeInit(ai.sourceDir, pkg.packageName, virtualData,
                    android.os.Build.VERSION.SDK_INT);
            NativeCore.setPackageName(pkg.packageName);
        }

        // Install Binder hooks
        FakeCore.init(base);

        // Install crash handler
        CrashHandler.install(new java.io.File(virtualData, "crashes"));

        Log.d(TAG, "BActivityThread init for " + pkg.packageName + " user=" + userId);
    }

    public AppSystemEnv getEnv() { return mEnv; }
}

package top.niunaijun.blackbox.core.system;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import top.niunaijun.blackbox.core.NativeCore;
import top.niunaijun.blackbox.core.system.am.BActivityManagerService;
import top.niunaijun.blackbox.core.system.am.BJobManagerService;
import top.niunaijun.blackbox.core.system.location.BLocationManagerService;
import top.niunaijun.blackbox.core.system.notification.BNotificationManagerService;
import top.niunaijun.blackbox.core.system.os.BStorageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.user.BUserManagerService;

/**
 * Initializes and starts all virtual system services (analogous to SystemServer).
 * Android 15 compatible version - handles missing native support gracefully.
 */
public class BlackBoxSystem {
    private static final String TAG = "BlackBoxSystem";
    
    // Android 15+ compatibility flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;
    private static volatile boolean sStarted = false;

    /**
     * Start all virtual system services.
     * On Android 15+, skips services that depend on native hooks.
     */
    public static void start(Context context) {
        if (sStarted) {
            Log.w(TAG, "System services already started");
            return;
        }
        
        Log.i(TAG, "Starting BlackBox system services... (Android " + 
              Build.VERSION.SDK_INT + ")");
        
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.w(TAG, "Android 15+ detected - using compatibility mode");
            startAndroid15Mode(context);
        } else {
            startNormalMode(context);
        }
        
        sStarted = true;
        Log.i(TAG, "BlackBox system services started successfully.");
    }
    
    /**
     * Android 15+ mode - only essential services that don't need native hooks.
     */
    private static void startAndroid15Mode(Context context) {
        // Essential: PackageManager - required for app installation/launching
        try {
            BPackageManagerService pms = BPackageManagerService.get();
            pms.onStart(context);
            ServiceManager.addService(ServiceManager.SERVICE_PACKAGE_MANAGER, pms.asBinder());
            Log.d(TAG, "[Android15+] PackageManager started");
        } catch (Exception e) {
            Log.e(TAG, "[Android15+] PackageManager failed", e);
        }
        
        // Essential: ActivityManager - required for app launching
        // Note: Uses Java-only implementation on Android 15+
        try {
            BActivityManagerService ams = BActivityManagerService.get();
            ams.onStart(context);
            ServiceManager.addService(ServiceManager.SERVICE_ACTIVITY_MANAGER, ams.asBinder());
            Log.d(TAG, "[Android15+] ActivityManager started");
        } catch (Exception e) {
            Log.e(TAG, "[Android15+] ActivityManager failed", e);
        }
        
        // Essential: UserManager - required for multi-user support
        try {
            BUserManagerService ums = BUserManagerService.get();
            // Don't call onStart if it requires native hooks
            ServiceManager.addService(ServiceManager.SERVICE_USER_MANAGER, ums.asBinder());
            Log.d(TAG, "[Android15+] UserManager registered");
        } catch (Exception e) {
            Log.e(TAG, "[Android15+] UserManager failed", e);
        }
        
        // Optional: StorageManager - file system operations
        // May work in Java-only mode
        try {
            BStorageManagerService sms = BStorageManagerService.get();
            sms.onStart(context);
            ServiceManager.addService(ServiceManager.SERVICE_STORAGE_MANAGER, sms.asBinder());
            Log.d(TAG, "[Android15+] StorageManager started");
        } catch (Exception e) {
            Log.w(TAG, "[Android15+] StorageManager skipped (may need native): " + e.getMessage());
        }
        
        // Skip on Android 15+ (require native hooks):
        // - LocationManager (GPS spoofing) - blocked by Android 15 location restrictions
        // - NotificationManager - may use Binder hooks
        // - JobManager - may use system job scheduler
        
        Log.w(TAG, "[Android15+] Skipped: LocationManager, NotificationManager, JobManager");
        
        // Boot completed for started services
        notifyBootCompletedAndroid15();
    }
    
    /**
     * Normal mode for Android 14 and below - all services enabled.
     */
    private static void startNormalMode(Context context) {
        // PackageManager
        BPackageManagerService pms = BPackageManagerService.get();
        pms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_PACKAGE_MANAGER, pms.asBinder());

        // ActivityManager
        BActivityManagerService ams = BActivityManagerService.get();
        ams.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_ACTIVITY_MANAGER, ams.asBinder());

        // UserManager
        BUserManagerService ums = BUserManagerService.get();
        ServiceManager.addService(ServiceManager.SERVICE_USER_MANAGER, ums.asBinder());

        // LocationManager (GPS spoofing)
        BLocationManagerService lms = BLocationManagerService.get();
        lms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_LOCATION_MANAGER, lms.asBinder());

        // NotificationManager
        BNotificationManagerService nms = BNotificationManagerService.get();
        nms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_NOTIFICATION_MGR, nms.asBinder());

        // StorageManager
        BStorageManagerService sms = BStorageManagerService.get();
        sms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_STORAGE_MANAGER, sms.asBinder());

        // JobManager
        BJobManagerService jms = BJobManagerService.get();
        jms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_JOB_MANAGER, jms.asBinder());

        // Boot completed
        notifyBootCompletedNormal(pms, ams, lms, nms, sms, jms);
    }
    
    /**
     * Android 15+ boot completed notification - only for started services.
     */
    private static void notifyBootCompletedAndroid15() {
        try {
            BPackageManagerService pms = BPackageManagerService.get();
            if (pms instanceof ISystemService) {
                ((ISystemService) pms).onBootCompleted();
            }
        } catch (Exception e) {
            Log.w(TAG, "Boot completed failed for PM", e);
        }
        
        try {
            BActivityManagerService ams = BActivityManagerService.get();
            if (ams instanceof ISystemService) {
                ((ISystemService) ams).onBootCompleted();
            }
        } catch (Exception e) {
            Log.w(TAG, "Boot completed failed for AM", e);
        }
        
        try {
            BStorageManagerService sms = BStorageManagerService.get();
            if (sms instanceof ISystemService) {
                ((ISystemService) sms).onBootCompleted();
            }
        } catch (Exception e) {
            Log.w(TAG, "Boot completed failed for SM", e);
        }
    }
    
    /**
     * Normal boot completed notification.
     */
    private static void notifyBootCompletedNormal(ISystemService... services) {
        for (ISystemService svc : services) {
            try {
                if (svc != null) {
                    svc.onBootCompleted();
                }
            } catch (Exception e) {
                Log.w(TAG, "Boot completed failed for " + svc.getClass().getSimpleName(), e);
            }
        }
    }
    
    /**
     * Check if system services have been started.
     */
    public static boolean isStarted() {
        return sStarted;
    }
    
    /**
     * Get startup mode description.
     */
    public static String getMode() {
        if (!sStarted) return "Not started";
        return IS_ANDROID_15_OR_HIGHER ? "Android 15+ Compatibility" : "Normal";
    }
}

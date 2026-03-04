package top.niunaijun.blackbox.core.system;

import android.content.Context;
import android.util.Log;

import top.niunaijun.blackbox.core.system.am.BActivityManagerService;
import top.niunaijun.blackbox.core.system.am.BJobManagerService;
import top.niunaijun.blackbox.core.system.location.BLocationManagerService;
import top.niunaijun.blackbox.core.system.notification.BNotificationManagerService;
import top.niunaijun.blackbox.core.system.os.BStorageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.user.BUserManagerService;

/**
 * Initializes and starts all virtual system services (analogous to SystemServer).
 */
public class BlackBoxSystem {
    private static final String TAG = "BlackBoxSystem";

    public static void start(Context context) {
        Log.d(TAG, "Starting BlackBox system services...");

        BPackageManagerService pms = BPackageManagerService.get();
        pms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_PACKAGE_MANAGER, pms.asBinder());

        BActivityManagerService ams = BActivityManagerService.get();
        ams.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_ACTIVITY_MANAGER, ams.asBinder());

        BUserManagerService ums = BUserManagerService.get();
        ServiceManager.addService(ServiceManager.SERVICE_USER_MANAGER, ums.asBinder());

        BLocationManagerService lms = BLocationManagerService.get();
        lms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_LOCATION_MANAGER, lms.asBinder());

        BNotificationManagerService nms = BNotificationManagerService.get();
        nms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_NOTIFICATION_MGR, nms.asBinder());

        BStorageManagerService sms = BStorageManagerService.get();
        sms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_STORAGE_MANAGER, sms.asBinder());

        BJobManagerService jms = BJobManagerService.get();
        jms.onStart(context);
        ServiceManager.addService(ServiceManager.SERVICE_JOB_MANAGER, jms.asBinder());

        // Boot completed
        for (ISystemService svc : new ISystemService[]{pms, ams, lms, nms, sms, jms}) {
            svc.onBootCompleted();
        }

        Log.d(TAG, "BlackBox system services started successfully.");
    }
}

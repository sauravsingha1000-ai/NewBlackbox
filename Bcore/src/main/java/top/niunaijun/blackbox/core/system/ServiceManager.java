package top.niunaijun.blackbox.core.system;

import android.os.IBinder;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for virtual system services (analogous to android.os.ServiceManager).
 */
public class ServiceManager {
    private static final Map<String, IBinder> sServices = new HashMap<>();

    public static final String SERVICE_ACTIVITY_MANAGER  = "blackbox_am";
    public static final String SERVICE_PACKAGE_MANAGER   = "blackbox_pm";
    public static final String SERVICE_USER_MANAGER      = "blackbox_um";
    public static final String SERVICE_LOCATION_MANAGER  = "blackbox_lm";
    public static final String SERVICE_NOTIFICATION_MGR  = "blackbox_nm";
    public static final String SERVICE_STORAGE_MANAGER   = "blackbox_sm";
    public static final String SERVICE_ACCOUNT_MANAGER   = "blackbox_acm";
    public static final String SERVICE_JOB_MANAGER       = "blackbox_jm";

    private ServiceManager() {}

    public static void addService(String name, IBinder binder) {
        sServices.put(name, binder);
    }

    public static IBinder getService(String name) {
        return sServices.get(name);
    }

    public static boolean hasService(String name) {
        return sServices.containsKey(name);
    }
}

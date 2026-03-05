package top.niunaijun.blackbox.core.system;

import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for virtual system services (analogous to android.os.ServiceManager).
 * Android 15 compatible version - thread-safe and defensive.
 */
public class ServiceManager {
    private static final String TAG = "BlackBoxServiceManager";
    
    // Thread-safe map for concurrent access
    private static final Map<String, IBinder> sServices = new ConcurrentHashMap<>();
    
    // Android 15+ compatibility flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;

    public static final String SERVICE_ACTIVITY_MANAGER  = "blackbox_am";
    public static final String SERVICE_PACKAGE_MANAGER   = "blackbox_pm";
    public static final String SERVICE_USER_MANAGER      = "blackbox_um";
    public static final String SERVICE_LOCATION_MANAGER  = "blackbox_lm";
    public static final String SERVICE_NOTIFICATION_MGR  = "blackbox_nm";
    public static final String SERVICE_STORAGE_MANAGER   = "blackbox_sm";
    public static final String SERVICE_ACCOUNT_MANAGER   = "blackbox_acm";
    public static final String SERVICE_JOB_MANAGER       = "blackbox_jm";

    private ServiceManager() {}

    /**
     * Register a virtual service.
     * Thread-safe for Android 15+ concurrent access.
     */
    public static void addService(String name, IBinder binder) {
        if (name == null || binder == null) {
            Log.w(TAG, "Cannot add null service");
            return;
        }
        
        sServices.put(name, binder);
        
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "Service registered (Android 15+): " + name);
        }
    }

    /**
     * Retrieve a virtual service.
     * Returns null if service not found.
     */
    public static IBinder getService(String name) {
        if (name == null) return null;
        
        IBinder service = sServices.get(name);
        
        // Defensive logging for Android 15 troubleshooting
        if (service == null && IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "Service not found: " + name);
        }
        
        return service;
    }

    /**
     * Check if service exists.
     */
    public static boolean hasService(String name) {
        if (name == null) return false;
        return sServices.containsKey(name);
    }
    
    /**
     * Remove a service from registry.
     * Useful for cleanup.
     */
    public static void removeService(String name) {
        if (name == null) return;
        
        sServices.remove(name);
        Log.d(TAG, "Service removed: " + name);
    }
    
    /**
     * Get list of all registered service names.
     * For debugging purposes.
     */
    public static String[] listServices() {
        return sServices.keySet().toArray(new String[0]);
    }
    
    /**
     * Clear all services.
     * Use with caution - can break running virtual apps.
     */
    public static void clearServices() {
        sServices.clear();
        Log.w(TAG, "All services cleared");
    }
    
    /**
     * Get count of registered services.
     */
    public static int getServiceCount() {
        return sServices.size();
    }
    
    /**
     * Check if running on Android 15+.
     * Utility method for other components.
     */
    public static boolean isAndroid15OrHigher() {
        return IS_ANDROID_15_OR_HIGHER;
    }
}

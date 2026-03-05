package top.niunaijun.blackbox.core.system;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.service.notification.StatusBarNotification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

/**
 * Persistent foreground daemon service running in the :daemon process.
 * Hosts all virtual system services for their lifetime.
 * Android 15 compatible version with enhanced foreground service handling.
 */
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";
    private static final String CHANNEL_ID = "blackbox_daemon";
    private static final int NOTIF_ID = 1001;

    // Android 15+ flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;
    private static volatile boolean sSystemStarted = false;

    private final IBinder mBinder = new Binder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "DaemonService onCreate (Android " + Build.VERSION.SDK_INT + ")");
        
        // Create notification channel first
        createNotificationChannel();
        
        // Start foreground service with appropriate type for Android version
        startForegroundWithProperType();
        
        // Start BlackBox system services (only once)
        if (!sSystemStarted) {
            try {
                BlackBoxSystem.start(getApplicationContext());
                sSystemStarted = true;
                Log.i(TAG, "BlackBoxSystem started successfully in daemon");
            } catch (Exception e) {
                Log.e(TAG, "Failed to start BlackBoxSystem", e);
                // Don't crash - service can retry
            }
        } else {
            Log.d(TAG, "BlackBoxSystem already started, skipping");
        }
    }

    /**
     * Start foreground service with proper type for Android version.
     * Android 15+ requires specific handling.
     */
    private void startForegroundWithProperType() {
        Notification notification = buildNotification();
        
        try {
            if (IS_ANDROID_15_OR_HIGHER) {
                // Android 15+ (API 35+) - use special use with additional flags
                Log.d(TAG, "Starting foreground service (Android 15+ mode)");
                startForeground(NOTIF_ID, notification, 
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
                
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14 (API 34) - use special use
                Log.d(TAG, "Starting foreground service (Android 14 mode)");
                startForeground(NOTIF_ID, notification, 
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
                    
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10-13 (API 29-33) - no type required but can use
                Log.d(TAG, "Starting foreground service (Android 10-13 mode)");
                startForeground(NOTIF_ID, notification);
                
            } else {
                // Android 9 and below (API 28-)
                Log.d(TAG, "Starting foreground service (legacy mode)");
                startForeground(NOTIF_ID, notification);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to start foreground service, retrying without type", e);
            
            // Fallback: try without type (may work on some Android 15 devices)
            try {
                startForeground(NOTIF_ID, notification);
                Log.w(TAG, "Fallback foreground service started");
            } catch (Exception e2) {
                Log.e(TAG, "Critical: Cannot start foreground service", e2);
                // Service will be killed by system, but we logged the error
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand flags=" + flags + " startId=" + startId);
        
        // Ensure we're still foreground (Android 15 may be stricter)
        if (IS_ANDROID_15_OR_HIGHER) {
            ensureForegroundOnAndroid15();
        }
        
        return START_STICKY;
    }
    
    /**
     * Android 15+ specific: Ensure service stays in foreground.
     * May need to re-post notification or check status.
     */
    private void ensureForegroundOnAndroid15() {
        // Android 15 has stricter foreground service policies
        // This method ensures we remain compliant
        try {
            // Verify notification is still showing
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                StatusBarNotification[] notifications = nm.getActiveNotifications();
                boolean found = false;
                for (StatusBarNotification notif : notifications) {
                    if (notif.getId() == NOTIF_ID) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Log.w(TAG, "Notification not found, re-starting foreground");
                    startForegroundWithProperType();
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error checking foreground status", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + intent);
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "DaemonService onDestroy - attempting restart...");
        
        // Android 15+ may restrict restart, use alarm or WorkManager as fallback
        scheduleRestart();
    }
    
    /**
     * Schedule service restart with Android 15 compatibility.
     */
    private void scheduleRestart() {
        try {
            Intent restart = new Intent(getApplicationContext(), DaemonService.class);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(restart);
            } else {
                startService(restart);
            }
            
            Log.i(TAG, "Service restart scheduled");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule restart", e);
            
            // Android 15+ may block immediate restart, log for debugging
            if (IS_ANDROID_15_OR_HIGHER) {
                Log.w(TAG, "Android 15+ may have blocked service restart - " +
                    "this is expected due to stricter background restrictions");
            }
        }
    }

    @Override
    public void onTimeout(int startId) {
        super.onTimeout(startId);
        Log.w(TAG, "Foreground service timeout for startId: " + startId);
        
        // Android 15+ specific timeout handling
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.w(TAG, "Android 15+ service timeout - attempting recovery");
            // Try to restart foreground
            try {
                startForegroundWithProperType();
            } catch (Exception e) {
                Log.e(TAG, "Recovery failed", e);
            }
        }
    }
    
    @Override
    public void onTimeout(int startId, int fgsType) {
        // Android 14+ (API 34+) overload with fgsType
        super.onTimeout(startId, fgsType);
        Log.w(TAG, "Foreground service timeout for startId: " + startId + 
              " fgsType: " + fgsType);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, "BlackBox Engine",
                        NotificationManager.IMPORTANCE_MIN);
                channel.setShowBadge(false);
                channel.setDescription("Virtual app engine running in background");
                
                NotificationManager nm = getSystemService(NotificationManager.class);
                if (nm != null) {
                    nm.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to create notification channel", e);
            }
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        
        builder.setContentTitle("BlackBox Engine")
                .setContentText("Virtual engine running" + 
                    (IS_ANDROID_15_OR_HIGHER ? " (Android 15+ mode)" : ""))
                .setSmallIcon(android.R.drawable.ic_menu_manage)
                .setOngoing(true); // Prevent swipe dismissal

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setCategory(Notification.CATEGORY_SERVICE);
        }
        
        // Android 15+ specific notification flags
        if (IS_ANDROID_15_OR_HIGHER && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(
                Notification.FOREGROUND_SERVICE_IMMEDIATE);
        }

        return builder.build();
    }
    
    /**
     * Check if system services have been started by this daemon.
     */
    public static boolean isSystemStarted() {
        return sSystemStarted;
    }
}

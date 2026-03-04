package top.niunaijun.blackbox.core.system;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
 */
public class DaemonService extends Service {
    private static final String TAG = "DaemonService";
    private static final String CHANNEL_ID = "blackbox_daemon";
    private static final int NOTIF_ID = 1001;

    private final IBinder mBinder = new Binder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "DaemonService onCreate");
        createNotificationChannel();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIF_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIF_ID, buildNotification());
        }
        
        BlackBoxSystem.start(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DaemonService onDestroy - restarting...");
        Intent restart = new Intent(getApplicationContext(), DaemonService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(restart);
        } else {
            startService(restart);
        }
    }

    @Override
    public void onTimeout(int startId) {
        super.onTimeout(startId);
        Log.w(TAG, "Foreground service timeout reached for startId: " + startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "BlackBox Engine",
                    NotificationManager.IMPORTANCE_MIN);
            channel.setShowBadge(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        
        builder.setContentTitle("BlackBox Engine")
                .setContentText("Virtual engine running")
                .setSmallIcon(android.R.drawable.ic_menu_manage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setCategory(Notification.CATEGORY_SERVICE);
        }

        return builder.build();
    }
}

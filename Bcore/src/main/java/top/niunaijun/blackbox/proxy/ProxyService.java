package top.niunaijun.blackbox.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Stub service slot 0 — hosts virtual app service.
 */
public class ProxyService extends Service {
    private static final String TAG = "ProxyService";
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}

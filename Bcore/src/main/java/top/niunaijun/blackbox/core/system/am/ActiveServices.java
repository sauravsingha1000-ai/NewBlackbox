package top.niunaijun.blackbox.core.system.am;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.entity.am.RunningServiceInfo;

/**
 * Manages virtual services lifecycle.
 */
public class ActiveServices {
    private static final String TAG = "ActiveServices";
    // serviceKey -> IBinder
    private final Map<String, IBinder> mRunningServices = new HashMap<>();
    private final Map<IBinder, String> mConnections = new HashMap<>();

    public boolean startService(Context context, Intent intent, int userId) {
        try {
            String key = buildKey(intent, userId);
            if (!mRunningServices.containsKey(key)) {
                IBinder token = new Binder();
                mRunningServices.put(key, token);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "startService failed", e);
            return false;
        }
    }

    public boolean stopService(Intent intent, int userId) {
        String key = buildKey(intent, userId);
        mRunningServices.remove(key);
        return true;
    }

    public IBinder bindService(Context context, Intent intent, IBinder connection,
                                int flags, int userId) {
        String key = buildKey(intent, userId);
        IBinder svcBinder = mRunningServices.get(key);
        if (svcBinder == null) {
            svcBinder = new Binder();
            mRunningServices.put(key, svcBinder);
        }
        mConnections.put(connection, key);
        return svcBinder;
    }

    public void unbindService(IBinder connection) {
        String key = mConnections.remove(connection);
        if (key != null && !mConnections.containsValue(key)) {
            mRunningServices.remove(key);
        }
    }

    public List<RunningServiceInfo> getRunningServices(int userId) {
        List<RunningServiceInfo> list = new ArrayList<>();
        for (String key : mRunningServices.keySet()) {
            if (key.endsWith(":" + userId)) {
                RunningServiceInfo info = new RunningServiceInfo();
                info.userId = userId;
                list.add(info);
            }
        }
        return list;
    }

    private String buildKey(Intent intent, int userId) {
        ComponentName cn = intent.getComponent();
        if (cn != null) return cn.flattenToString() + ":" + userId;
        if (intent.getAction() != null) return intent.getAction() + ":" + userId;
        return intent.toString() + ":" + userId;
    }
}

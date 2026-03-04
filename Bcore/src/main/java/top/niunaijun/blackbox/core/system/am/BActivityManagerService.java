package top.niunaijun.blackbox.core.system.am;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.ProcessRecord;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;

/**
 * Virtual Activity Manager Service — manages virtual process lifecycle,
 * activity starts, service binding and broadcast dispatch.
 */
public class BActivityManagerService extends Binder implements ISystemService {
    private static final String TAG = "BActivityManagerService";
    private static volatile BActivityManagerService sInstance;

    private Context mContext;

    // userId -> processName -> ProcessRecord
    private final SparseArray<Map<String, ProcessRecord>> mProcesses = new SparseArray<>();

    private final ActivityStack mActivityStack = new ActivityStack();
    private final ActiveServices mActiveServices = new ActiveServices();
    private final BroadcastManager mBroadcastManager;

    private BActivityManagerService() {
        mBroadcastManager = new BroadcastManager(this);
    }

    public static BActivityManagerService get() {
        if (sInstance == null) {
            synchronized (BActivityManagerService.class) {
                if (sInstance == null) sInstance = new BActivityManagerService();
            }
        }
        return sInstance;
    }

    @Override
    public void onStart(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onBootCompleted() {}

    public boolean startActivity(Intent intent, String callerPackage, int userId) {
        if (intent == null) return false;

        InstalledPackage pkg = resolvePackage(intent, userId);
        if (pkg == null) {
            Log.w(TAG, "Cannot resolve activity intent: " + intent);
            return false;
        }

        return mActivityStack.startActivity(mContext, intent, pkg, userId);
    }

    public void finishActivity(IBinder token) {
        mActivityStack.finishActivity(token);
    }

    public boolean startService(Intent intent, String resolvedType, int userId) {
        if (intent == null) return false;
        return mActiveServices.startService(mContext, intent, userId);
    }

    public boolean stopService(Intent intent, int userId) {
        return mActiveServices.stopService(intent, userId);
    }

    public IBinder bindService(Intent intent, IBinder connection, int flags, int userId) {
        return mActiveServices.bindService(mContext, intent, connection, flags, userId);
    }

    public void unbindService(IBinder connection) {
        mActiveServices.unbindService(connection);
    }

    public void sendBroadcast(Intent intent, String callerPackage, int userId) {
        mBroadcastManager.sendBroadcast(intent, callerPackage, userId);
    }

    public boolean killProcess(String packageName, int userId) {
        synchronized (mProcesses) {
            Map<String, ProcessRecord> userProcs = mProcesses.get(userId);
            if (userProcs != null) {
                for (ProcessRecord pr : userProcs.values()) {
                    if (packageName.equals(pr.packageName)) {
                        android.os.Process.killProcess(pr.pid);
                        userProcs.remove(pr.processName);
                        Log.d(TAG, "Killed process: " + pr);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void killAllProcess(int userId) {
        synchronized (mProcesses) {
            Map<String, ProcessRecord> userProcs = mProcesses.get(userId);
            if (userProcs != null) {
                for (ProcessRecord pr : userProcs.values()) {
                    android.os.Process.killProcess(pr.pid);
                }
                userProcs.clear();
            }
        }
    }

    public List<RunningAppProcessInfo> getRunningAppProcesses(int userId) {
        List<RunningAppProcessInfo> result = new ArrayList<>();

        synchronized (mProcesses) {
            Map<String, ProcessRecord> userProcs = mProcesses.get(userId);
            if (userProcs == null) return result;

            for (ProcessRecord pr : userProcs.values()) {
                RunningAppProcessInfo info = new RunningAppProcessInfo();
                info.processName = pr.processName;
                info.pid = pr.pid;
                info.userId = pr.userId;
                info.pkgList = new String[]{pr.packageName};
                result.add(info);
            }
        }

        return result;
    }

    public List<RunningServiceInfo> getRunningServices(int userId) {
        return mActiveServices.getRunningServices(userId);
    }

    public boolean isRunning(String packageName, int userId) {
        synchronized (mProcesses) {
            Map<String, ProcessRecord> userProcs = mProcesses.get(userId);
            if (userProcs == null) return false;

            for (ProcessRecord pr : userProcs.values()) {
                if (packageName.equals(pr.packageName)) return true;
            }
        }

        return false;
    }

    public void registerProcess(ProcessRecord record) {
        synchronized (mProcesses) {
            Map<String, ProcessRecord> userProcs = mProcesses.get(record.userId);

            if (userProcs == null) {
                userProcs = new ConcurrentHashMap<>();
                mProcesses.put(record.userId, userProcs);
            }

            userProcs.put(record.processName, record);
        }
    }

    private InstalledPackage resolvePackage(Intent intent, int userId) {
        if (intent.getPackage() != null) {
            return BPackageManagerService.get()
                    .getInstalledPackage(intent.getPackage(), userId);
        }

        if (intent.getComponent() != null) {
            return BPackageManagerService.get()
                    .getInstalledPackage(intent.getComponent().getPackageName(), userId);
        }

        return null;
    }

    public IBinder asBinder() {
        return this;
    }
}
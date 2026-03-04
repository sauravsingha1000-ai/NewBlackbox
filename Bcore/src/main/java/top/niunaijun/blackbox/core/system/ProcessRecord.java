package top.niunaijun.blackbox.core.system;

import android.os.IBinder;

/**
 * Records information about a running virtual process.
 */
public class ProcessRecord {
    public final int pid;
    public final int userId;
    public final String packageName;
    public final String processName;
    public IBinder activityThread;
    public long startTime;
    public boolean killed;

    public ProcessRecord(int pid, int userId, String packageName, String processName) {
        this.pid = pid;
        this.userId = userId;
        this.packageName = packageName;
        this.processName = processName;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "ProcessRecord{pid=" + pid + ", pkg=" + packageName
                + ", user=" + userId + "}";
    }
}

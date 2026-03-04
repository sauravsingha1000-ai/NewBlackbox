package top.niunaijun.blackbox.entity;

import android.content.ComponentName;
import android.content.Intent;

/**
 * Tracks a running virtual service.
 */
public class ServiceRecord {
    public String packageName;
    public ComponentName component;
    public Intent intent;
    public int userId;
    public int pid;
    public boolean bound;
    public long startTime;

    public ServiceRecord(String packageName, ComponentName component, Intent intent, int userId) {
        this.packageName = packageName;
        this.component = component;
        this.intent = intent;
        this.userId = userId;
        this.startTime = System.currentTimeMillis();
    }
}

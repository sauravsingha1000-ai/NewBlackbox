package top.niunaijun.blackbox.core.system.am;

import android.content.Intent;
import android.os.IBinder;

/**
 * Tracks a single virtual activity.
 */
public class ActivityRecord {
    public final IBinder token;
    public final Intent intent;
    public final String packageName;
    public final String className;
    public final int userId;
    public boolean finishing;

    public ActivityRecord(IBinder token, Intent intent, String packageName,
                           String className, int userId) {
        this.token = token;
        this.intent = intent;
        this.packageName = packageName;
        this.className = className;
        this.userId = userId;
    }
}

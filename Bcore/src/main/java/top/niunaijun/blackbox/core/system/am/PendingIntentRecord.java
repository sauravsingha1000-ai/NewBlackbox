package top.niunaijun.blackbox.core.system.am;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

/**
 * Holds a pending intent record for deferred execution in the virtual env.
 */
public class PendingIntentRecord {
    public int requestCode;
    public Intent intent;
    public int flags;
    public String packageName;
    public int userId;
    public int type; // 0=activity, 1=service, 2=broadcast

    public PendingIntentRecord(int requestCode, Intent intent, int flags,
                                String packageName, int userId, int type) {
        this.requestCode = requestCode;
        this.intent = intent;
        this.flags = flags;
        this.packageName = packageName;
        this.userId = userId;
        this.type = type;
    }
}

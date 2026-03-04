package top.niunaijun.blackbox.core.system.notification;

import android.app.Notification;

public class NotificationRecord {
    public String packageName;
    public String tag;
    public int id;
    public Notification notification;
    public int userId;
    public long postTime;

    public NotificationRecord(String packageName, String tag, int id,
                               Notification notification, int userId) {
        this.packageName = packageName;
        this.tag = tag;
        this.id = id;
        this.notification = notification;
        this.userId = userId;
        this.postTime = System.currentTimeMillis();
    }
}

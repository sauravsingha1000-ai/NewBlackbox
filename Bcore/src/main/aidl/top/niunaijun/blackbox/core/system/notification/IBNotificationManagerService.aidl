package top.niunaijun.blackbox.core.system.notification;
interface IBNotificationManagerService {
    void enqueueNotification(String pkg, String tag, int id, in Notification notification, int userId);
    void cancelNotification(String pkg, String tag, int id, int userId);
    void cancelAllNotifications(String pkg, int userId);
}

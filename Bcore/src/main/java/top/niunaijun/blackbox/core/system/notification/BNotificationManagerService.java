package top.niunaijun.blackbox.core.system.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import top.niunaijun.blackbox.core.system.ISystemService;

/**

* Virtual Notification Manager — forwards virtual app notifications to the host.
  */
  public class BNotificationManagerService extends Binder implements ISystemService {
  private static final String TAG = "BNotificationManagerService";
  private static volatile BNotificationManagerService sInstance;
  private Context mContext;
  private NotificationManager mNm;
  
  private BNotificationManagerService() {}
  
  public static BNotificationManagerService get() {
  if (sInstance == null) {
  synchronized (BNotificationManagerService.class) {
  if (sInstance == null) sInstance = new BNotificationManagerService();
  }
  }
  return sInstance;
  }
  
  @Override
  public void onStart(Context context) {
  mContext = context.getApplicationContext();
  mNm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
  
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
     NotificationChannel ch = new NotificationChannel(
             "blackbox_virtual",
             "Virtual App Notifications",
             NotificationManager.IMPORTANCE_DEFAULT
     );
     mNm.createNotificationChannel(ch);
 }
  
  }
  
  @Override
  public void onBootCompleted() {}
  
  public void enqueueNotification(String pkg, String tag, int id,
  Notification notification, int userId) {
  if (mNm == null) return;
  
   try {
     mNm.notify(tag, id + userId * 10000, notification);
 } catch (Exception e) {
     Log.e(TAG, "enqueueNotification failed", e);
 }
  
  }
  
  public void cancelNotification(String pkg, String tag, int id, int userId) {
  if (mNm == null) return;
  mNm.cancel(tag, id + userId * 10000);
  }
  
  public void cancelAllNotifications(String pkg, int userId) {
  if (mNm == null) return;
  mNm.cancelAll();
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
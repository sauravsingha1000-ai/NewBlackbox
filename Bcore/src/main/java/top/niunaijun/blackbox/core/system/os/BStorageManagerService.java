package top.niunaijun.blackbox.core.system.os;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import android.content.Context;

/**

* Virtual Storage Manager — maps virtual storage paths to real paths.
  */
  public class BStorageManagerService extends Binder implements ISystemService {
  private static final String TAG = "BStorageManagerService";
  private static volatile BStorageManagerService sInstance;
  
  private BStorageManagerService() {}
  
  public static BStorageManagerService get() {
  if (sInstance == null) {
  synchronized (BStorageManagerService.class) {
  if (sInstance == null) sInstance = new BStorageManagerService();
  }
  }
  return sInstance;
  }
  
  @Override
  public void onStart(Context context) {}
  
  @Override
  public void onBootCompleted() {}
  
  public String getVirtualStoragePath(String packageName, int userId) {
  return BEnvironment.getDataDir(packageName, userId).getAbsolutePath();
  }
  
  public void mkdirPackageStorage(String packageName, int userId) {
  BEnvironment.getDataDir(packageName, userId).mkdirs();
  BEnvironment.getSdcardDir(userId).mkdirs();
  Log.d(TAG, "Created storage for " + packageName + " user " + userId);
  }
  
  public void removePackageStorage(String packageName, int userId) {
  deleteDir(BEnvironment.getDataDir(packageName, userId));
  deleteDir(BEnvironment.getApkDir(packageName, userId));
  }
  
  private void deleteDir(File dir) {
  if (dir == null || !dir.exists()) return;
  
   File[] files = dir.listFiles();
 if (files != null) {
     for (File f : files) {
         if (f.isDirectory()) {
             deleteDir(f);
         } else {
             f.delete();
         }
     }
 }

 dir.delete();
  
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
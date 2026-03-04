package top.niunaijun.blackbox.core.system.pm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.entity.pm.InstallResult;
import top.niunaijun.blackbox.entity.pm.InstalledPackage;

/**

* Virtual Package Manager Service.

* Handles installation, uninstallation, and querying of virtual apps.
  */
  public class BPackageManagerService extends Binder implements ISystemService {
  private static final String TAG = "BPackageManagerService";
  private static volatile BPackageManagerService sInstance;
  
  private Context mContext;
  
  // userId -> packageName -> InstalledPackage
  private final Map<Integer, Map<String, InstalledPackage>> mPackages = new HashMap<>();
  
  private final Gson mGson = new Gson();
  
  private BPackageManagerService() {}
  
  public static BPackageManagerService get() {
  if (sInstance == null) {
  synchronized (BPackageManagerService.class) {
  if (sInstance == null) sInstance = new BPackageManagerService();
  }
  }
  return sInstance;
  }
  
  @Override
  public void onStart(Context context) {
  mContext = context.getApplicationContext();
  loadSettings();
  }
  
  @Override
  public void onBootCompleted() {}
  
  /** Install an APK into the virtual space for the given userId. */
  public InstallResult installPackageAsUser(InstallOption option, int userId) {
  if (option == null || option.apkPath == null) {
  return InstallResult.failed(InstallResult.CODE_FAILED_INVALID_APK, "Null option");
  }
  
   File apkFile = new File(option.apkPath);
 if (!apkFile.exists()) {
     return InstallResult.failed(
             InstallResult.CODE_FAILED_INVALID_APK,
             "APK not found: " + option.apkPath
     );
 }

 try {
     PackageManager pm = mContext.getPackageManager();

     PackageInfo pi = pm.getPackageArchiveInfo(
             option.apkPath,
             PackageManager.GET_ACTIVITIES |
                     PackageManager.GET_SERVICES |
                     PackageManager.GET_RECEIVERS |
                     PackageManager.GET_PROVIDERS
     );

     if (pi == null) {
         return InstallResult.failed(
                 InstallResult.CODE_FAILED_INVALID_APK,
                 "Parse failed"
         );
     }

     // Check duplicate
     if (!option.overrideInstall && isInstalled(pi.packageName, userId)) {
         return InstallResult.failed(
                 InstallResult.CODE_FAILED_DUPLICATE,
                 "Already installed: " + pi.packageName
         );
     }

     pi.applicationInfo.sourceDir = option.apkPath;
     pi.applicationInfo.publicSourceDir = option.apkPath;

     // Create virtual data directories
     File dataDir = BEnvironment.getDataDir(pi.packageName, userId);
     File apkDir = BEnvironment.getApkDir(pi.packageName, userId);

     apkDir.mkdirs();

     // Copy APK to virtual storage
     File destApk = new File(apkDir, "base.apk");

     if (!apkFile.getAbsolutePath().equals(destApk.getAbsolutePath())) {
         copyFile(apkFile, destApk);
     }

     InstalledPackage pkg = new InstalledPackage();

     pkg.packageName = pi.packageName;
     pkg.apkPath = destApk.getAbsolutePath();
     pkg.dataDir = dataDir.getAbsolutePath();
     pkg.userId = userId;

     pkg.installTime = System.currentTimeMillis();
     pkg.updateTime = pkg.installTime;

     pkg.packageInfo = pi;
     pkg.applicationInfo = pi.applicationInfo;

     pkg.versionCode = (int) pi.getLongVersionCode();
     pkg.versionName = pi.versionName;

     synchronized (mPackages) {
         mPackages
                 .computeIfAbsent(userId, k -> new HashMap<>())
                 .put(pi.packageName, pkg);
     }

     saveSettings();

     Log.d(TAG, "Installed " + pi.packageName + " for user " + userId);

     return InstallResult.success(pi.packageName);

 } catch (Exception e) {
     Log.e(TAG, "Install failed", e);

     return InstallResult.failed(
             InstallResult.CODE_FAILED_UNKNOWN,
             e.getMessage()
     );
 }
  
  }
  
  public void uninstallPackageAsUser(String packageName, int userId) {
  synchronized (mPackages) {
  Map<String, InstalledPackage> userPkgs = mPackages.get(userId);
  if (userPkgs != null) userPkgs.remove(packageName);
  }
  
   // Remove virtual data
 deleteDir(BEnvironment.getDataDir(packageName, userId));
 deleteDir(BEnvironment.getApkDir(packageName, userId));

 saveSettings();

 Log.d(TAG, "Uninstalled " + packageName + " for user " + userId);
  
  }
  
  public List<InstalledPackage> getInstalledPackages(int flags, int userId) {
  synchronized (mPackages) {
  Map<String, InstalledPackage> userPkgs = mPackages.get(userId);
  if (userPkgs == null) return new ArrayList<>();
  return new ArrayList<>(userPkgs.values());
  }
  }
  
  public InstalledPackage getInstalledPackage(String packageName, int userId) {
  synchronized (mPackages) {
  Map<String, InstalledPackage> userPkgs = mPackages.get(userId);
  if (userPkgs == null) return null;
  return userPkgs.get(packageName);
  }
  }
  
  public boolean isInstalled(String packageName, int userId) {
  return getInstalledPackage(packageName, userId) != null;
  }
  
  public List<String> getInstalledPackageNames(int userId) {
  synchronized (mPackages) {
  Map<String, InstalledPackage> userPkgs = mPackages.get(userId);
  if (userPkgs == null) return new ArrayList<>();
  return new ArrayList<>(userPkgs.keySet());
  }
  }
  
  public void clearPackage(String packageName, int userId) {
  File dataDir = BEnvironment.getDataDir(packageName, userId);
  deleteDir(dataDir);
  dataDir.mkdirs();
  }
  
  // ── Persistence ─────────────────────────────────────────────
  
  private void saveSettings() {
  // Simplified: each user's package list as JSON
  }
  
  private void loadSettings() {
  // Load from BEnvironment.getPackageSettingsDir()
  }
  
  // ── Helpers ────────────────────────────────────────────────
  
  private void copyFile(File src, File dst) throws Exception {
  try (
  java.io.InputStream in = new java.io.FileInputStream(src);
  java.io.OutputStream out = new java.io.FileOutputStream(dst)
  ) {
  byte[] buf = new byte[8192];
  int len;
  
       while ((len = in.read(buf)) > 0) {
         out.write(buf, 0, len);
     }
 }
  
  }
  
  private void deleteDir(File dir) {
  if (dir == null || !dir.exists()) return;
  
   File[] files = dir.listFiles();

 if (files != null) {
     for (File f : files) {
         if (f.isDirectory()) deleteDir(f);
         else f.delete();
     }
 }

 dir.delete();
  
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
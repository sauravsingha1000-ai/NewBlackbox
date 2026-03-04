package top.niunaijun.blackbox.core.system.location;

import android.os.Binder;
import android.os.IBinder;
import android.util.SparseArray;

import top.niunaijun.blackbox.core.system.ISystemService;
import android.content.Context;
import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.entity.location.BLocationConfig;

/**

* Virtual Location Manager Service — manages fake GPS coordinates per user.
  */
  public class BLocationManagerService extends Binder implements ISystemService {
  private static volatile BLocationManagerService sInstance;
  private final SparseArray<BLocation> mFakeLocations = new SparseArray<>();
  private final SparseArray<BLocationConfig> mLocationConfigs = new SparseArray<>();
  
  private BLocationManagerService() {}
  
  public static BLocationManagerService get() {
  if (sInstance == null) {
  synchronized (BLocationManagerService.class) {
  if (sInstance == null) sInstance = new BLocationManagerService();
  }
  }
  return sInstance;
  }
  
  @Override
  public void onStart(Context context) {}
  
  @Override
  public void onBootCompleted() {}
  
  public void setFakeLocation(BLocation location, int userId) {
  synchronized (this) {
  mFakeLocations.put(userId, location);
  }
  }
  
  public BLocation getFakeLocation(int userId) {
  synchronized (this) {
  return mFakeLocations.get(userId);
  }
  }
  
  public void setLocationConfig(BLocationConfig config, int userId) {
  synchronized (this) {
  mLocationConfigs.put(userId, config);
  }
  }
  
  public BLocationConfig getLocationConfig(int userId) {
  synchronized (this) {
  return mLocationConfigs.get(userId);
  }
  }
  
  public void clearFakeLocation(int userId) {
  synchronized (this) {
  mFakeLocations.remove(userId);
  }
  }
  
  public boolean isFakeEnabled(int userId) {
  BLocationConfig cfg = getLocationConfig(userId);
  return cfg != null && cfg.enabled;
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
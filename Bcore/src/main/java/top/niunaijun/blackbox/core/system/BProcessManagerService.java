package top.niunaijun.blackbox.core.system;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

* Manages virtual process records across all users.
  */
  public class BProcessManagerService extends Binder {
  private static final String TAG = "BProcessManagerService";
  private static volatile BProcessManagerService sInstance;
  
  // userId -> pid -> ProcessRecord
  private final SparseArray<Map<Integer, ProcessRecord>> mProcesses = new SparseArray<>();
  
  private BProcessManagerService() {}
  
  public static BProcessManagerService get() {
  if (sInstance == null) {
  synchronized (BProcessManagerService.class) {
  if (sInstance == null) sInstance = new BProcessManagerService();
  }
  }
  return sInstance;
  }
  
  public void registerProcess(ProcessRecord record) {
  synchronized (mProcesses) {
  Map<Integer, ProcessRecord> userMap = mProcesses.get(record.userId);
  
       if (userMap == null) {
         userMap = new ConcurrentHashMap<>();
         mProcesses.put(record.userId, userMap);
     }

     userMap.put(record.pid, record);
     Log.d(TAG, "Registered: " + record);
 }
  
  }
  
  public void unregisterProcess(int pid, int userId) {
  synchronized (mProcesses) {
  Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
  if (userMap != null) userMap.remove(pid);
  }
  }
  
  public ProcessRecord getProcess(int pid, int userId) {
  synchronized (mProcesses) {
  Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
  if (userMap == null) return null;
  
       return userMap.get(pid);
 }
  
  }
  
  public ProcessRecord findProcessByPackage(String packageName, int userId) {
  synchronized (mProcesses) {
  Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
  if (userMap == null) return null;
  
       for (ProcessRecord pr : userMap.values()) {
         if (packageName.equals(pr.packageName)) return pr;
     }
 }

 return null;
  
  }
  
  public List<ProcessRecord> getProcesses(int userId) {
  synchronized (mProcesses) {
  Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
  
       if (userMap == null) {
         return new ArrayList<>();
     }

     return new ArrayList<>(userMap.values());
 }
  
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
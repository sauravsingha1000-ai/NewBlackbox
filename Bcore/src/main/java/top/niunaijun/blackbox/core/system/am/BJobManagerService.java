package top.niunaijun.blackbox.core.system.am;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import top.niunaijun.blackbox.core.system.ISystemService;
import android.content.Context;
import top.niunaijun.blackbox.entity.JobRecord;

/**

* Virtual Job Scheduler service.
  */
  public class BJobManagerService extends Binder implements ISystemService {
  private static final String TAG = "BJobManagerService";
  private static volatile BJobManagerService sInstance;
  
  // userId -> jobId -> JobRecord
  private final SparseArray<Map<Integer, JobRecord>> mJobs = new SparseArray<>();
  private int mNextJobId = 1;
  
  private BJobManagerService() {}
  
  public static BJobManagerService get() {
  if (sInstance == null) {
  synchronized (BJobManagerService.class) {
  if (sInstance == null) sInstance = new BJobManagerService();
  }
  }
  return sInstance;
  }
  
  @Override
  public void onStart(Context context) {}
  
  @Override
  public void onBootCompleted() {}
  
  public int scheduleJob(JobRecord job, int userId) {
  synchronized (mJobs) {
  int id = mNextJobId++;
  job.jobId = id;
  
       if (mJobs.get(userId) == null) {
         mJobs.put(userId, null);
     }

     Map<Integer, JobRecord> userJobs = mJobs.get(userId);
     if (userJobs == null) {
         userJobs = new HashMap<>();
         mJobs.put(userId, userJobs);
     }

     userJobs.put(id, job);
     Log.d(TAG, "Scheduled job #" + id + " for " + job.packageName);

     return id;
 }
  
  }
  
  public void cancelJob(int jobId, String packageName, int userId) {
  synchronized (mJobs) {
  Map<Integer, JobRecord> userJobs = mJobs.get(userId);
  if (userJobs != null) userJobs.remove(jobId);
  }
  }
  
  public void cancelAll(String packageName, int userId) {
  synchronized (mJobs) {
  Map<Integer, JobRecord> userJobs = mJobs.get(userId);
  if (userJobs != null) {
  userJobs.entrySet().removeIf(e ->
  packageName.equals(e.getValue().packageName));
  }
  }
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
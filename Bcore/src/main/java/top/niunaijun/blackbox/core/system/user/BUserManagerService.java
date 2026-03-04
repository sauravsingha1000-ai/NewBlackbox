package top.niunaijun.blackbox.core.system.user;

import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**

* Virtual user manager service.
  */
  public class BUserManagerService extends top.niunaijun.blackbox.core.system.IBUserManagerStub {
  
  private static final String TAG = "BUserManagerService";
  private static final int MAX_USERS = 10;
  
  private static volatile BUserManagerService sInstance;
  
  private final SparseArray<BUserInfo> mUsers = new SparseArray<>();
  private final SparseArray<BUserStatus> mUserStatus = new SparseArray<>();
  
  private int mNextUserId = 1;
  
  private BUserManagerService() {
  // Create default system user
  BUserInfo systemUser = new BUserInfo(
  0,
  "System",
  BUserInfo.FLAG_PRIMARY | BUserInfo.FLAG_ADMIN
  );
  
   mUsers.put(0, systemUser);
 mUserStatus.put(0, new BUserStatus(0, BUserStatus.STATUS_RUNNING_UNLOCKED));
  
  }
  
  public static BUserManagerService get() {
  if (sInstance == null) {
  synchronized (BUserManagerService.class) {
  if (sInstance == null) {
  sInstance = new BUserManagerService();
  }
  }
  }
  return sInstance;
  }
  
  public BUserInfo createUser(String name, int flags) {
  synchronized (this) {
  if (mUsers.size() >= MAX_USERS) {
  return null;
  }
  
       int newId = mNextUserId++;

     BUserInfo info = new BUserInfo(newId, name, flags);

     mUsers.put(newId, info);
     mUserStatus.put(newId,
             new BUserStatus(newId, BUserStatus.STATUS_RUNNING_UNLOCKED));

     Log.d(TAG, "Created user: " + newId + " (" + name + ")");

     return info;
 }
  
  }
  
  public boolean removeUser(int userId) {
  if (userId == 0) {
  return false; // cannot remove system user
  }
  
   synchronized (this) {
     mUsers.remove(userId);
     mUserStatus.remove(userId);
 }

 return true;
  
  }
  
  public List<BUserInfo> getUsers(boolean excludeDying) {
  List<BUserInfo> result = new ArrayList<>();
  
   synchronized (this) {
     for (int i = 0; i < mUsers.size(); i++) {
         BUserInfo info = mUsers.valueAt(i);
         BUserStatus status = mUserStatus.get(info.id);

         if (excludeDying && status != null && !status.isRunning()) {
             continue;
         }

         result.add(info);
     }
 }

 return result;
  
  }
  
  public BUserInfo getUserInfo(int userId) {
  synchronized (this) {
  return mUsers.get(userId);
  }
  }
  
  public boolean isUserRunning(int userId) {
  synchronized (this) {
  BUserStatus s = mUserStatus.get(userId);
  return s != null && s.isRunning();
  }
  }
  
  public boolean isUserExists(int userId) {
  synchronized (this) {
  return mUsers.indexOfKey(userId) >= 0;
  }
  }
  
  public IBinder asBinder() {
  return this;
  }
  }
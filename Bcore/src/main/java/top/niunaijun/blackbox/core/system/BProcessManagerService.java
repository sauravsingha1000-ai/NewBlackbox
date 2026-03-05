package top.niunaijun.blackbox.core.system;

import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages virtual process records across all users.
 * Android 15 compatible version with enhanced process lifecycle handling.
 */
public class BProcessManagerService extends Binder {
    private static final String TAG = "BProcessManagerService";
    private static volatile BProcessManagerService sInstance;
    
    // Android 15+ flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;
    
    // userId -> pid -> ProcessRecord
    private final SparseArray<Map<Integer, ProcessRecord>> mProcesses = new SparseArray<>();
    
    // Track total process count for Android 15 monitoring
    private volatile int mTotalProcessCount = 0;
    private static final int ANDROID_15_PROCESS_LIMIT = 50; // Conservative limit

    private BProcessManagerService() {}

    public static BProcessManagerService get() {
        if (sInstance == null) {
            synchronized (BProcessManagerService.class) {
                if (sInstance == null) sInstance = new BProcessManagerService();
            }
        }
        return sInstance;
    }

    /**
     * Register a new virtual process.
     * On Android 15+, includes additional validation and limits.
     */
    public void registerProcess(ProcessRecord record) {
        if (record == null) {
            Log.w(TAG, "Cannot register null process");
            return;
        }
        
        // Android 15+ process limit check
        if (IS_ANDROID_15_OR_HIGHER && mTotalProcessCount >= ANDROID_15_PROCESS_LIMIT) {
            Log.w(TAG, "Android 15+ process limit reached (" + ANDROID_15_PROCESS_LIMIT + 
                  "), rejecting new process: " + record.packageName);
            // Don't crash - just log and skip registration
            // The calling code should handle this gracefully
            return;
        }

        synchronized (mProcesses) {
            Map<Integer, ProcessRecord> userMap = mProcesses.get(record.userId);
            
            if (userMap == null) {
                userMap = new ConcurrentHashMap<>();
                mProcesses.put(record.userId, userMap);
            }

            // Check for duplicate PID
            ProcessRecord existing = userMap.get(record.pid);
            if (existing != null) {
                Log.w(TAG, "Replacing existing process record: " + existing + " with " + record);
            }
            
            userMap.put(record.pid, record);
            mTotalProcessCount++;
            
            Log.d(TAG, "Registered: " + record + " (total: " + mTotalProcessCount + ")");
        }
        
        // Android 15+ specific: Validate process is actually alive
        if (IS_ANDROID_15_OR_HIGHER) {
            validateProcessAlive(record);
        }
    }

    /**
     * Unregister a process when it dies or is killed.
     */
    public void unregisterProcess(int pid, int userId) {
        if (pid <= 0) {
            Log.w(TAG, "Invalid PID: " + pid);
            return;
        }

        synchronized (mProcesses) {
            Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
            if (userMap == null) return;
            
            ProcessRecord removed = userMap.remove(pid);
            if (removed != null) {
                mTotalProcessCount--;
                Log.d(TAG, "Unregistered: pid=" + pid + " user=" + userId + 
                      " (total: " + mTotalProcessCount + ")");
            }
        }
    }

    /**
     * Get process record by PID.
     */
    public ProcessRecord getProcess(int pid, int userId) {
        if (pid <= 0) return null;
        
        synchronized (mProcesses) {
            Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
            if (userMap == null) return null;
            
            return userMap.get(pid);
        }
    }

    /**
     * Find process by package name.
     */
    public ProcessRecord findProcessByPackage(String packageName, int userId) {
        if (packageName == null) return null;
        
        synchronized (mProcesses) {
            Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
            if (userMap == null) return null;
            
            for (ProcessRecord pr : userMap.values()) {
                if (packageName.equals(pr.packageName)) return pr;
            }
        }
        
        return null;
    }

    /**
     * Get all processes for a user.
     */
    public List<ProcessRecord> getProcesses(int userId) {
        synchronized (mProcesses) {
            Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
            
            if (userMap == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(userMap.values());
        }
    }
    
    /**
     * Get total process count across all users.
     * Useful for monitoring on Android 15+.
     */
    public int getTotalProcessCount() {
        return mTotalProcessCount;
    }
    
    /**
     * Check if we're near the Android 15 process limit.
     */
    public boolean isNearProcessLimit() {
        if (!IS_ANDROID_15_OR_HIGHER) return false;
        return mTotalProcessCount >= (ANDROID_15_PROCESS_LIMIT * 0.8); // 80% threshold
    }

    /**
     * Kill all processes for a specific user.
     * Used during user cleanup.
     */
    public void killAllProcessesForUser(int userId) {
        synchronized (mProcesses) {
            Map<Integer, ProcessRecord> userMap = mProcesses.get(userId);
            if (userMap == null) return;
            
            Log.w(TAG, "Killing all " + userMap.size() + " processes for user " + userId);
            
            for (ProcessRecord pr : new ArrayList<>(userMap.values())) {
                try {
                    if (pr.pid > 0) {
                        Log.d(TAG, "Killing process: " + pr.pid);
                        Process.killProcess(pr.pid);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to kill process " + pr.pid, e);
                }
            }
            
            int removed = userMap.size();
            userMap.clear();
            mTotalProcessCount -= removed;
            
            mProcesses.remove(userId);
        }
    }
    
    /**
     * Clean up dead processes.
     * Should be called periodically or when issues are detected.
     */
    public void cleanupDeadProcesses() {
        synchronized (mProcesses) {
            int cleaned = 0;
            
            for (int i = 0; i < mProcesses.size(); i++) {
                int userId = mProcesses.keyAt(i);
                Map<Integer, ProcessRecord> userMap = mProcesses.valueAt(i);
                
                for (ProcessRecord pr : new ArrayList<>(userMap.values())) {
                    if (!isProcessAlive(pr.pid)) {
                        userMap.remove(pr.pid);
                        mTotalProcessCount--;
                        cleaned++;
                        Log.d(TAG, "Cleaned up dead process: " + pr.pid);
                    }
                }
                
                // Remove empty user maps
                if (userMap.isEmpty()) {
                    mProcesses.remove(userId);
                }
            }
            
            if (cleaned > 0) {
                Log.i(TAG, "Cleaned up " + cleaned + " dead processes");
            }
        }
    }
    
    /**
     * Check if a process is still alive.
     */
    private boolean isProcessAlive(int pid) {
        if (pid <= 0) return false;
        
        try {
            // Check if we can signal the process
            Process.sendSignal(pid, 0); // Signal 0 is "check if exists"
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Android 15+ specific: Validate process is alive after registration.
     * Android 15 may kill processes more aggressively.
     */
    private void validateProcessAlive(ProcessRecord record) {
        if (record == null || record.pid <= 0) return;
        
        // Delayed check - process may not be fully started yet
        // In real implementation, use Handler.postDelayed
        // For now, just log
        if (!isProcessAlive(record.pid)) {
            Log.w(TAG, "Android 15+ warning: Process " + record.pid + 
                  " (" + record.packageName + ") may not be alive after registration");
        }
    }

    public IBinder asBinder() {
        return this;
    }
    
    /**
     * Get Android version info for debugging.
     */
    public static String getVersionInfo() {
        return "Android " + Build.VERSION.SDK_INT + 
               (IS_ANDROID_15_OR_HIGHER ? " (15+ mode)" : "");
    }
}

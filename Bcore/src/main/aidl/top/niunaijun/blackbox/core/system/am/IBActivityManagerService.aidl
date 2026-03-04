package top.niunaijun.blackbox.core.system.am;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.entity.am.ReceiverData;
import top.niunaijun.blackbox.entity.am.PendingResultData;

interface IBActivityManagerService {
    boolean startActivity(in Intent intent, in String callerPackage, int userId);
    boolean startActivityInNewTask(in Intent intent, int userId);
    void finishActivity(in IBinder token);
    List<RunningAppProcessInfo> getRunningAppProcesses(int userId);
    List<RunningServiceInfo> getRunningServices(int userId);
    boolean killProcess(String packageName, int userId);
    void killAllProcess(int userId);
    IBinder acquireProviderClient(in Intent intent, int userId);
    boolean startService(in Intent intent, in String resolvedType, int userId);
    boolean stopService(in Intent intent, int userId);
    IBinder bindService(in Intent intent, in IBinder connection, int flags, int userId);
    void unbindService(in IBinder connection);
    void sendBroadcast(in Intent intent, in String callerPackage, int userId);
    void processRestarted(String packageName, String processName, int userId);
    boolean isRunning(String packageName, int userId);
}

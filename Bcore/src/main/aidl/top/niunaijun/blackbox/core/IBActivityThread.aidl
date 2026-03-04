package top.niunaijun.blackbox.core;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.am.ReceiverData;

interface IBActivityThread {
    void scheduleLaunchActivity(in Intent intent, in ActivityInfo info, in Bundle savedInstanceState);
    void scheduleReceiver(in ReceiverData receiverData);
    void scheduleStartService(in Intent intent, in ServiceInfo serviceInfo, boolean taskRemoved);
    void scheduleStopService(in Intent intent);
    void scheduleBindService(in Intent intent, in IBinder binder, boolean rebind);
    void scheduleUnbindService(in Intent intent);
    void scheduleApplicationDeadSignal();
    void scheduleTrimMemory(int level);
    String getProcessName();
    int getPid();
}

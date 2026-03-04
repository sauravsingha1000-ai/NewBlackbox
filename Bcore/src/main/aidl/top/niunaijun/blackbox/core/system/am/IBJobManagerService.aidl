package top.niunaijun.blackbox.core.system.am;
import top.niunaijun.blackbox.entity.JobRecord;

interface IBJobManagerService {
    int scheduleJob(in JobRecord job, int userId);
    void cancelJob(int jobId, String packageName, int userId);
    void cancelAll(String packageName, int userId);
}

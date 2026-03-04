package top.niunaijun.blackbox.core.system.am;

import android.os.IBinder;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a virtual task (activity stack root).
 */
public class TaskRecord {
    public int taskId;
    public String affinity;
    public List<ActivityRecord> activities = new ArrayList<>();

    public TaskRecord(int taskId, String affinity) {
        this.taskId = taskId;
        this.affinity = affinity;
    }

    public void addActivity(ActivityRecord record) {
        activities.add(record);
    }

    public ActivityRecord getTop() {
        return activities.isEmpty() ? null : activities.get(activities.size() - 1);
    }
}

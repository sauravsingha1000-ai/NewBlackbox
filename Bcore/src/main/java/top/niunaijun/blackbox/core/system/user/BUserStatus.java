package top.niunaijun.blackbox.core.system.user;

public class BUserStatus {
    public static final int STATUS_RUNNING_UNLOCKED = 0;
    public static final int STATUS_RUNNING_LOCKED   = 1;
    public static final int STATUS_STOPPING         = 2;
    public static final int STATUS_SHUTDOWN         = 3;

    public int userId;
    public int status;

    public BUserStatus(int userId, int status) {
        this.userId = userId;
        this.status = status;
    }

    public boolean isRunning() {
        return status == STATUS_RUNNING_UNLOCKED || status == STATUS_RUNNING_LOCKED;
    }
}

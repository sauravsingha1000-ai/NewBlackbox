package top.niunaijun.blackbox.core.system.user;

/**
 * Virtual user handle utilities.
 */
public final class BUserHandle {
    public static final int USER_SYSTEM = 0;
    public static final int USER_ALL = -1;
    public static final int USER_CURRENT = -2;

    private BUserHandle() {}

    public static boolean isSameUser(int uid1, int uid2) {
        return getUserId(uid1) == getUserId(uid2);
    }

    public static int getUserId(int uid) {
        return uid / 100000;
    }

    public static int getAppId(int uid) {
        return uid % 100000;
    }

    public static int getUid(int userId, int appId) {
        return userId * 100000 + appId;
    }

    public static boolean isApp(int uid) {
        int appId = getAppId(uid);
        return appId >= android.os.Process.FIRST_APPLICATION_UID
                && appId <= android.os.Process.LAST_APPLICATION_UID;
    }
}

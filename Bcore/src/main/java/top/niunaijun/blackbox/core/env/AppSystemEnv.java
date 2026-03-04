package top.niunaijun.blackbox.core.env;

import android.content.pm.ApplicationInfo;

/**
 * Holds the ApplicationInfo and contextual data for a running virtual app.
 */
public class AppSystemEnv {
    public final ApplicationInfo applicationInfo;
    public final String processName;
    public final int userId;
    public final int vUid;
    public ClassLoader classLoader;

    public AppSystemEnv(ApplicationInfo applicationInfo, String processName,
                         int userId, int vUid) {
        this.applicationInfo = applicationInfo;
        this.processName = processName;
        this.userId = userId;
        this.vUid = vUid;
    }
}

package top.niunaijun.blackbox.core.system.am;

import android.os.IBinder;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the virtual space allocated to a user.
 */
public class UserSpace {
    public final int userId;
    public final Map<String, IBinder> services = new HashMap<>();
    public boolean started;

    public UserSpace(int userId) {
        this.userId = userId;
    }
}

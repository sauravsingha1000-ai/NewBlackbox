package top.niunaijun.blackbox.core.system.am;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Manages broadcast delivery in the virtual environment.
 */
public class BroadcastManager {
    private static final String TAG = "BroadcastManager";
    private final BActivityManagerService mAMS;

    public BroadcastManager(BActivityManagerService ams) {
        mAMS = ams;
    }

    public void sendBroadcast(Intent intent, String callerPackage, int userId) {
        Log.d(TAG, "sendBroadcast: " + intent.getAction() + " user=" + userId);
        // Deliver to registered virtual receivers
        // TODO: walk registered receivers and dispatch
    }

    public void sendOrderedBroadcast(Intent intent, String callerPackage,
                                      int userId, int initialCode, String initialData) {
        Log.d(TAG, "sendOrderedBroadcast: " + intent.getAction());
    }
}

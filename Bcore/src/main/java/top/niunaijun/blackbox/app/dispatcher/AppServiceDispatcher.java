package top.niunaijun.blackbox.app.dispatcher;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Dispatches service lifecycle calls to virtual apps.
 */
public class AppServiceDispatcher {
    private static final String TAG = "AppServiceDispatcher";

    public static void dispatchStartService(Context context, Intent intent) {
        Log.d(TAG, "dispatchStartService: " + intent);
    }

    public static void dispatchStopService(Context context, Intent intent) {
        Log.d(TAG, "dispatchStopService: " + intent);
    }

    public static void dispatchBindService(Context context, Intent intent) {
        Log.d(TAG, "dispatchBindService: " + intent);
    }
}

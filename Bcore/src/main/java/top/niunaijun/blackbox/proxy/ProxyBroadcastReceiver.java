package top.niunaijun.blackbox.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Stub broadcast receiver — forwards broadcasts to virtual app receivers.
 */
public class ProxyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ProxyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
        // Forward to virtual app's registered receivers
        BlackBoxCore.get().dispatchBroadcast(context, intent);
    }
}

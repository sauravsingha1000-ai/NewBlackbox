package top.niunaijun.blackbox.core.system.am;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Stack;

import top.niunaijun.blackbox.entity.pm.InstalledPackage;
import top.niunaijun.blackbox.proxy.ProxyManifest;

/**
 * Manages the virtual activity back stack.
 */
public class ActivityStack {
    private static final String TAG = "ActivityStack";
    private final Stack<ActivityRecord> mStack = new Stack<>();

    public boolean startActivity(Context context, Intent intent,
                                  InstalledPackage pkg, int userId) {
        try {
            IBinder token = new Binder();
            ActivityRecord record = new ActivityRecord(token, intent,
                    pkg.packageName,
                    intent.getComponent() != null
                            ? intent.getComponent().getClassName()
                            : pkg.applicationInfo.className,
                    userId);

            mStack.push(record);

            // Launch via proxy activity
            Intent proxyIntent = ProxyManifest.createProxyIntent(context, intent, userId);
            proxyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(proxyIntent);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to start activity", e);
            return false;
        }
    }

    public void finishActivity(IBinder token) {
        mStack.removeIf(r -> r.token == token);
    }

    public ActivityRecord findActivity(IBinder token) {
        for (ActivityRecord r : mStack) {
            if (r.token == token) return r;
        }
        return null;
    }

    public int size() { return mStack.size(); }
}

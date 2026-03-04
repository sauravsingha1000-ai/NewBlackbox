package top.niunaijun.blackbox.core;

import android.content.Context;
import android.util.Log;

/**
 * Manages Google Mobile Services (GMS) compatibility for virtual apps.
 */
public class GmsCore {
    private static final String TAG = "GmsCore";
    private static final String GMS_PACKAGE = "com.google.android.gms";
    private static volatile GmsCore sInstance;
    private Context mContext;
    private boolean mGmsEnabled;

    private GmsCore(Context context) {
        mContext = context.getApplicationContext();
    }

    public static GmsCore get() {
        return sInstance;
    }

    public static void init(Context context) {
        if (sInstance == null) {
            synchronized (GmsCore.class) {
                if (sInstance == null) {
                    sInstance = new GmsCore(context);
                }
            }
        }
    }

    public void enable() {
        mGmsEnabled = true;
        Log.d(TAG, "GMS compatibility enabled");
    }

    public void disable() {
        mGmsEnabled = false;
    }

    public boolean isEnabled() { return mGmsEnabled; }

    public boolean isGmsInstalled() {
        try {
            mContext.getPackageManager().getPackageInfo(GMS_PACKAGE, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

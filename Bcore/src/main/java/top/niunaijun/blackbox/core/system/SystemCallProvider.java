package top.niunaijun.blackbox.core.system;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import top.niunaijun.blackbox.core.system.BlackBoxSystem;

/**
 * ContentProvider used as an IPC entry point from client processes to the
 * daemon process.  The actual service calls are forwarded via Binder.
 */
public class SystemCallProvider extends ContentProvider {
    private static final String TAG = "SystemCallProvider";

    public static final String AUTHORITY_SUFFIX = ".BlackBoxCore";

    // Call method names
    public static final String METHOD_GET_BINDER = "get_binder";

    @Override
    public boolean onCreate() {
        Log.d(TAG, "SystemCallProvider starting in process: "
                + android.os.Process.myPid());
        BlackBoxSystem.start(getContext());
        return true;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (METHOD_GET_BINDER.equals(method)) {
            Bundle result = new Bundle();
            result.putBinder("binder",
                    ServiceManager.getService(arg != null ? arg : ""));
            return result;
        }
        return null;
    }

    @Override public Cursor query(Uri uri, String[] proj, String sel, String[] args, String sort) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] args) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String sel, String[] args) { return 0; }
}

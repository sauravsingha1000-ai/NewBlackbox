package top.niunaijun.blackbox.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Transparent launcher trampoline for virtual apps.
 */
public class LauncherActivity extends Activity {
    private static final String TAG = "LauncherActivity";
    public static final String EXTRA_PKG  = "bb_launcher_pkg";
    public static final String EXTRA_USER = "bb_launcher_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pkg  = getIntent().getStringExtra(EXTRA_PKG);
        int userId  = getIntent().getIntExtra(EXTRA_USER, 0);

        if (pkg != null) {
            Log.d(TAG, "Launching " + pkg + " for user " + userId);
            BlackBoxCore.get().launchApk(pkg, userId);
        }
        finish();
    }
}

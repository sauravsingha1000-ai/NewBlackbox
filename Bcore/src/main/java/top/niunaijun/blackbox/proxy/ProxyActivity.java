package top.niunaijun.blackbox.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Transparent stub activity that acts as a container for virtual app activities.
 */
public class ProxyActivity extends Activity {
    private static final String TAG = "ProxyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent targetIntent = getIntent().getParcelableExtra(ProxyManifest.EXTRA_TARGET_INTENT);
        int userId = getIntent().getIntExtra(ProxyManifest.EXTRA_USER_ID, 0);
        String pkgName = getIntent().getStringExtra(ProxyManifest.EXTRA_PKG_NAME);

        if (targetIntent == null) {
            Log.w(TAG, "No target intent — finishing");
            finish();
            return;
        }

        Log.d(TAG, "Launching virtual activity: " + targetIntent.getComponent()
                + " pkg=" + pkgName + " user=" + userId);

        try {
            BlackBoxCore.get().launchApk(pkgName, userId);
        } catch (Exception e) {
            Log.e(TAG, "Launch failed", e);
            finish();
        }
    }
}

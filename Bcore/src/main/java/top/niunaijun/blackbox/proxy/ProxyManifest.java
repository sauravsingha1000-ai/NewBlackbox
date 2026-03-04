package top.niunaijun.blackbox.proxy;

import android.content.Context;
import android.content.Intent;

/**
 * Creates proxy intents that route through BlackBox's stub activities/services.
 */
public class ProxyManifest {
    public static final String EXTRA_TARGET_INTENT = "bb_target_intent";
    public static final String EXTRA_USER_ID       = "bb_user_id";
    public static final String EXTRA_PKG_NAME      = "bb_pkg_name";

    /** Wrap a real intent for delivery via ProxyActivity. */
    public static Intent createProxyIntent(Context context, Intent realIntent, int userId) {
        Intent proxy = new Intent(context, ProxyActivity.class);
        proxy.putExtra(EXTRA_TARGET_INTENT, realIntent);
        proxy.putExtra(EXTRA_USER_ID, userId);
        if (realIntent.getPackage() != null) {
            proxy.putExtra(EXTRA_PKG_NAME, realIntent.getPackage());
        } else if (realIntent.getComponent() != null) {
            proxy.putExtra(EXTRA_PKG_NAME, realIntent.getComponent().getPackageName());
        }
        return proxy;
    }
}

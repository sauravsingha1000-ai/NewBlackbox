package top.niunaijun.blackbox.app.configuration;

import android.app.Activity;
import android.os.Bundle;

/**
 * Callback interface for virtual app lifecycle events.
 */
public interface AppLifecycleCallback {
    default void onVirtualAppCreated(String packageName, int userId) {}
    default void onVirtualAppStarted(String packageName, int userId) {}
    default void onVirtualAppStopped(String packageName, int userId) {}
    default void onVirtualActivityCreated(Activity activity, Bundle savedState) {}
    default void onVirtualActivityDestroyed(Activity activity) {}
}

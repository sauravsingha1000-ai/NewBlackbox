package top.niunaijun.blackbox.core.system;

import android.content.Context;

/**
 * Lifecycle interface for all virtual system services.
 */
public interface ISystemService {
    void onStart(Context context);
    void onBootCompleted();
}

package top.niunaijun.blackbox.core;

import android.util.Log;

/**
 * JNI bridge to the native virtual engine (BoxCore.so).
 */
public class NativeCore {
    private static final String TAG = "NativeCore";
    private static boolean sLoaded = false;

    static {
        try {
            System.loadLibrary("BoxCore");
            sLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load BoxCore native library", e);
        }
    }

    public static boolean isLoaded() { return sLoaded; }

    /** Initialize the native engine for a virtual process. */
    public static native int nativeInit(String selfPath, String packageName,
                                         String dataPath, int sdkInt);

    /** Install IO hooks (file redirection). */
    public static native boolean installIOHooks(String sourcePath, String targetPath);

    /** Install Binder hooks for system service interception. */
    public static native boolean installBinderHooks();

    /** Set the virtual package name reported to the process. */
    public static native boolean setPackageName(String packageName);

    /** Redirect a source path to a target path in native IO layer. */
    public static native boolean addIORedirect(String sourcePath, String targetPath);

    /** Remove an IO redirect. */
    public static native boolean removeIORedirect(String sourcePath);

    /** Get the real path before any IO redirect. */
    public static native String getRealPath(String virtualPath);

    /** Check whether Dobby hooks are active. */
    public static native boolean areHooksInstalled();
}

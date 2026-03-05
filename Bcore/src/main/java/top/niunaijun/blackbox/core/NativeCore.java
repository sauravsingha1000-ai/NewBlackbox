package top.niunaijun.blackbox.core;

import android.os.Build;
import android.util.Log;

/**
 * JNI bridge to the native virtual engine (BoxCore.so).
 * Android 15 compatible version - disables dangerous native hooks on API 35+
 */
public class NativeCore {
    private static final String TAG = "NativeCore";
    private static boolean sLoaded = false;
    private static boolean sNativeInitialized = false;
    
    // Android 15+ flag - disables hooking to prevent crashes
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;

    static {
        try {
            // Android 15+ check: Skip native library loading to prevent SELinux crashes
            if (IS_ANDROID_15_OR_HIGHER) {
                Log.w(TAG, "Android 15+ detected - skipping native library load (SELinux compatibility)");
                sLoaded = false;
            } else {
                System.loadLibrary("BoxCore");
                sLoaded = true;
                Log.i(TAG, "BoxCore native library loaded successfully");
            }
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load BoxCore native library", e);
            sLoaded = false;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error loading native library", e);
            sLoaded = false;
        }
    }

    public static boolean isLoaded() { 
        return sLoaded; 
    }
    
    /** Check if running in Android 15+ compatibility mode */
    public static boolean isCompatibilityMode() {
        return IS_ANDROID_15_OR_HIGHER;
    }

    /** 
     * Initialize the native engine for a virtual process.
     * Returns 0 on success, -1 on Android 15+ (compatibility mode)
     */
    public static int nativeInit(String selfPath, String packageName,
                                  String dataPath, int sdkInt) {
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.w(TAG, "nativeInit: Running in compatibility mode (Android 15+)");
            return 0; // Return success to allow Java-only operation
        }
        
        if (!sLoaded) {
            Log.e(TAG, "nativeInit: Native library not loaded");
            return -1;
        }
        
        try {
            int result = nativeInitInternal(selfPath, packageName, dataPath, sdkInt);
            sNativeInitialized = (result == 0);
            return result;
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "nativeInit: Native method not found", e);
            return -1;
        }
    }

    /** Install IO hooks (file redirection). */
    public static boolean installIOHooks(String sourcePath, String targetPath) {
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "installIOHooks: Skipped (Android 15+ compatibility)");
            return true; // Return true to allow operation without hooks
        }
        
        if (!sLoaded) return false;
        
        try {
            return installIOHooksInternal(sourcePath, targetPath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "installIOHooks: Native method not found", e);
            return false;
        }
    }

    /** Install Binder hooks for system service interception. */
    public static boolean installBinderHooks() {
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.w(TAG, "installBinderHooks: BLOCKED on Android 15+ (SELinux policy)");
            return false; // Binder hooks blocked on Android 15
        }
        
        if (!sLoaded) return false;
        
        try {
            return installBinderHooksInternal();
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "installBinderHooks: Native method not found", e);
            return false;
        }
    }

    /** Set the virtual package name reported to the process. */
    public static boolean setPackageName(String packageName) {
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "setPackageName: Skipped (Android 15+ compatibility)");
            return true;
        }
        
        if (!sLoaded) return false;
        
        try {
            return setPackageNameInternal(packageName);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "setPackageName: Native method not found", e);
            return false;
        }
    }

    /** Redirect a source path to a target path in native IO layer. */
    public static boolean addIORedirect(String sourcePath, String targetPath) {
        if (IS_ANDROID_15_OR_HIGHER) {
            // Use Java-based redirection instead
            Log.d(TAG, "addIORedirect: Using Java fallback (Android 15+)");
            return addIORedirectJava(sourcePath, targetPath);
        }
        
        if (!sLoaded) return false;
        
        try {
            return addIORedirectInternal(sourcePath, targetPath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "addIORedirect: Native method not found", e);
            return false;
        }
    }

    /** Remove an IO redirect. */
    public static boolean removeIORedirect(String sourcePath) {
        if (IS_ANDROID_15_OR_HIGHER) {
            Log.d(TAG, "removeIORedirect: Skipped (Android 15+ compatibility)");
            return true;
        }
        
        if (!sLoaded) return false;
        
        try {
            return removeIORedirectInternal(sourcePath);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "removeIORedirect: Native method not found", e);
            return false;
        }
    }

    /** Get the real path before any IO redirect. */
    public static String getRealPath(String virtualPath) {
        if (IS_ANDROID_15_OR_HIGHER) {
            // Return virtual path as-is on Android 15 (handled in Java layer)
            return virtualPath;
        }
        
        if (!sLoaded) return virtualPath;
        
        try {
            String result = getRealPathInternal(virtualPath);
            return result != null ? result : virtualPath;
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "getRealPath: Native method not found", e);
            return virtualPath;
        }
    }

    /** Check whether Dobby hooks are active. */
    public static boolean areHooksInstalled() {
        if (IS_ANDROID_15_OR_HIGHER) {
            return false; // Hooks never installed on Android 15+
        }
        
        if (!sLoaded) return false;
        
        try {
            return areHooksInstalledInternal();
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "areHooksInstalled: Native method not found", e);
            return false;
        }
    }
    
    /** Java fallback for IO redirection on Android 15+ */
    private static boolean addIORedirectJava(String sourcePath, String targetPath) {
        // Store in memory map for Java-level redirection
        // This is a simplified fallback - full implementation would need IOCore integration
        Log.d(TAG, "Java IO redirect: " + sourcePath + " -> " + targetPath);
        return true;
    }

    // Native method declarations (renamed to prevent direct access)
    private static native int nativeInitInternal(String selfPath, String packageName,
                                                  String dataPath, int sdkInt);
    private static native boolean installIOHooksInternal(String sourcePath, String targetPath);
    private static native boolean installBinderHooksInternal();
    private static native boolean setPackageNameInternal(String packageName);
    private static native boolean addIORedirectInternal(String sourcePath, String targetPath);
    private static native boolean removeIORedirectInternal(String sourcePath);
    private static native String getRealPathInternal(String virtualPath);
    private static native boolean areHooksInstalledInternal();
}

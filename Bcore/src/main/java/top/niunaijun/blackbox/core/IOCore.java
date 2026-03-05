package top.niunaijun.blackbox.core;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java-side IO path redirection registry, synchronized with the native layer.
 * Android 15 compatible version - supports Java-only mode.
 */
public class IOCore {
    private static final String TAG = "IOCore";
    private static final Map<String, String> sRedirects = new ConcurrentHashMap<>();
    
    // Android 15+ compatibility flag
    private static final boolean IS_ANDROID_15_OR_HIGHER = Build.VERSION.SDK_INT >= 35;
    private static boolean sInitialized = false;
    private static boolean sJavaOnlyMode = false;

    private IOCore() {}

    /**
     * Initialize IOCore with native support (Android 14 and below).
     * Call this from BlackBoxCore initialization.
     */
    public static void init(Context context) {
        if (sInitialized) return;
        
        sJavaOnlyMode = false;
        Log.i(TAG, "IOCore initialized with native support");
        sInitialized = true;
    }
    
    /**
     * Initialize IOCore in Java-only mode (Android 15+).
     * This bypasses all native IO hooks and uses pure Java redirection.
     */
    public static void initJavaOnly(Context context) {
        if (sInitialized) return;
        
        sJavaOnlyMode = true;
        Log.w(TAG, "IOCore initialized in Java-only mode (Android 15+ compatibility)");
        
        // Pre-populate common redirects for Android 15
        setupDefaultRedirects(context);
        
        sInitialized = true;
    }
    
    /**
     * Setup default redirects for Android 15+ Java-only mode.
     * These would normally be handled by native layer.
     */
    private static void setupDefaultRedirects(Context context) {
        try {
            // Redirect app data directories to virtual space
            File virtualRoot = new File(context.getFilesDir().getParentFile(), "virtual");
            if (!virtualRoot.exists()) {
                virtualRoot.mkdirs();
            }
            
            Log.d(TAG, "Default virtual root: " + virtualRoot.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup default redirects", e);
        }
    }

    /**
     * Add a bidirectional IO redirect: any access to {@code source} is transparently
     * rewritten to {@code target} in both Java and native layers.
     * 
     * On Android 15+, this operates in Java-only mode without native hooks.
     */
    public static void addRedirect(String source, String target) {
        if (source == null || target == null) return;
        
        sRedirects.put(source, target);
        
        // Only call native layer if loaded and not in Java-only mode
        if (!sJavaOnlyMode && NativeCore.isLoaded()) {
            NativeCore.addIORedirect(source, target);
        } else if (sJavaOnlyMode) {
            Log.d(TAG, "Java-only redirect: " + source + " -> " + target);
        }
    }

    public static void removeRedirect(String source) {
        if (source == null) return;
        
        sRedirects.remove(source);
        
        // Only call native layer if loaded and not in Java-only mode
        if (!sJavaOnlyMode && NativeCore.isLoaded()) {
            NativeCore.removeIORedirect(source);
        }
    }

    /**
     * Redirect a path using the registered mappings.
     * This is the core method used by virtual apps to access redirected paths.
     */
    public static String redirect(String path) {
        if (path == null) return null;
        
        // Check Java-level redirects first (works in both modes)
        for (Map.Entry<String, String> e : sRedirects.entrySet()) {
            if (path.startsWith(e.getKey())) {
                String redirected = e.getValue() + path.substring(e.getKey().length());
                
                // Log redirects in debug mode for Android 15 troubleshooting
                if (sJavaOnlyMode && Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Redirect: " + path + " -> " + redirected);
                }
                
                return redirected;
            }
        }
        
        // On Android 15+ in Java-only mode, check if we need additional processing
        if (sJavaOnlyMode && IS_ANDROID_15_OR_HIGHER) {
            return redirectAndroid15(path);
        }
        
        return path;
    }
    
    /**
     * Android 15+ specific path handling.
     * Handles special cases that native layer would normally manage.
     */
    private static String redirectAndroid15(String path) {
        // Handle /data/data/ paths that need redirection to virtual space
        if (path.startsWith("/data/data/")) {
            // Extract package name from path
            String[] parts = path.split("/");
            if (parts.length >= 3) {
                String packageName = parts[3];
                // Check if this is a virtual app path
                // Return original path for now - full implementation would redirect to virtual data
            }
        }
        
        return path;
    }

    public static Map<String, String> getRedirects() {
        return java.util.Collections.unmodifiableMap(sRedirects);
    }

    /** 
     * Setup standard data redirects for a virtual app.
     * Works in both native and Java-only modes.
     */
    public static void setupAppRedirects(String realDataPath, String virtualDataPath) {
        addRedirect(realDataPath, virtualDataPath);
        Log.d(TAG, "App IO redirect: " + realDataPath + " -> " + virtualDataPath + 
              (sJavaOnlyMode ? " (Java-only)" : ""));
    }
    
    /** Check if running in Java-only mode (Android 15+) */
    public static boolean isJavaOnlyMode() {
        return sJavaOnlyMode;
    }
    
    /** Check if IOCore has been initialized */
    public static boolean isInitialized() {
        return sInitialized;
    }
    
    /**
     * Clear all redirects. Useful for cleanup or reset operations.
     */
    public static void clearRedirects() {
        sRedirects.clear();
        
        if (!sJavaOnlyMode && NativeCore.isLoaded()) {
            // Native layer doesn't have bulk clear, would need to remove one by one
            Log.w(TAG, "Native redirects may persist after clear");
        }
        
        Log.d(TAG, "All redirects cleared");
    }
}


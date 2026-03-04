package top.niunaijun.blackbox.core;

import android.util.Log;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Java-side IO path redirection registry, synchronized with the native layer.
 */
public class IOCore {
    private static final String TAG = "IOCore";
    private static final Map<String, String> sRedirects = new ConcurrentHashMap<>();

    private IOCore() {}

    /**
     * Add a bidirectional IO redirect: any access to {@code source} is transparently
     * rewritten to {@code target} in both Java and native layers.
     */
    public static void addRedirect(String source, String target) {
        if (source == null || target == null) return;
        sRedirects.put(source, target);
        if (NativeCore.isLoaded()) {
            NativeCore.addIORedirect(source, target);
        }
    }

    public static void removeRedirect(String source) {
        sRedirects.remove(source);
        if (NativeCore.isLoaded()) {
            NativeCore.removeIORedirect(source);
        }
    }

    public static String redirect(String path) {
        if (path == null) return null;
        for (Map.Entry<String, String> e : sRedirects.entrySet()) {
            if (path.startsWith(e.getKey())) {
                return e.getValue() + path.substring(e.getKey().length());
            }
        }
        return path;
    }

    public static Map<String, String> getRedirects() {
        return java.util.Collections.unmodifiableMap(sRedirects);
    }

    /** Setup standard data redirects for a virtual app. */
    public static void setupAppRedirects(String realDataPath, String virtualDataPath) {
        addRedirect(realDataPath, virtualDataPath);
        Log.d(TAG, "IO redirect: " + realDataPath + " -> " + virtualDataPath);
    }
}

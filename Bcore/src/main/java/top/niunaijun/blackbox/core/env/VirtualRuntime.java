package top.niunaijun.blackbox.core.env;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Manages virtual runtime state: patched Build fields, process identity, etc.
 */
public class VirtualRuntime {
    private static final String TAG = "VirtualRuntime";

    private static int sVirtualUid = -1;
    private static int sVirtualPid = -1;
    private static String sVirtualPackageName;

    public static void init(int uid, int pid, String packageName) {
        sVirtualUid = uid;
        sVirtualPid = pid;
        sVirtualPackageName = packageName;
    }

    public static int getVirtualUid()            { return sVirtualUid; }
    public static int getVirtualPid()            { return sVirtualPid; }
    public static String getVirtualPackageName() { return sVirtualPackageName; }

    /** Patch android.os.Build fields to spoof device identity. */
    public static void spoofDevice(String manufacturer, String model,
                                    String brand, String fingerprint) {
        try {
            patchBuildField("MANUFACTURER", manufacturer);
            patchBuildField("MODEL", model);
            patchBuildField("BRAND", brand);
            patchBuildField("FINGERPRINT", fingerprint);
        } catch (Exception e) {
            Log.e(TAG, "Failed to spoof device", e);
        }
    }

    private static void patchBuildField(String name, String value) throws Exception {
        Field f = Build.class.getDeclaredField(name);
        f.setAccessible(true);
        // Remove final modifier
        Field modifiers = Field.class.getDeclaredField("accessFlags");
        modifiers.setAccessible(true);
        modifiers.setInt(f, f.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        f.set(null, value);
    }
}

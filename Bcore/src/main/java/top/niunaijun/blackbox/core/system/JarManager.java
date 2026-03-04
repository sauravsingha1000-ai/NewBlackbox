package top.niunaijun.blackbox.core.system;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import top.niunaijun.blackbox.core.env.BEnvironment;

/**
 * Manages extraction and lifecycle of bundled JAR assets.
 */
public class JarManager {
    private static final String TAG = "JarManager";

    public static void extractAll(Context context) {
        extract(context, JarConfig.EMPTY_JAR);
        extract(context, JarConfig.JUNIT_JAR);
    }

    public static File getJarFile(String name) {
        return new File(BEnvironment.getBaseDir(), "jars/" + name);
    }

    public static void extract(Context context, String assetName) {
        File dest = getJarFile(assetName);
        if (dest.exists() && dest.length() > 0) {
            Log.d(TAG, "JAR already extracted: " + assetName);
            return;
        }
        dest.getParentFile().mkdirs();
        try (InputStream in = context.getAssets().open(assetName);
             FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            Log.d(TAG, "Extracted JAR: " + assetName + " -> " + dest);
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract JAR: " + assetName, e);
        }
    }
}

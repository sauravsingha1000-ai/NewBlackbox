package top.niunaijun.blackbox.core.env;

import android.content.Context;
import android.os.Environment;
import java.io.File;

/**
 * Manages virtual filesystem paths for each user/app.
 */
public class BEnvironment {
    private static Context sContext;
    private static File sBaseDir;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
        sBaseDir = new File(sContext.getFilesDir(), "BlackBox");
        sBaseDir.mkdirs();
    }

    public static File getBaseDir() {
        return sBaseDir;
    }

    /** Root dir for a virtual user */
    public static File getUserDir(int userId) {
        File dir = new File(sBaseDir, "users/" + userId);
        dir.mkdirs();
        return dir;
    }

    /** Data dir for a virtual package */
    public static File getDataDir(String packageName, int userId) {
        File dir = new File(getUserDir(userId), "data/" + packageName);
        dir.mkdirs();
        return dir;
    }

    /** APK storage dir */
    public static File getApkDir(String packageName, int userId) {
        File dir = new File(getUserDir(userId), "apk/" + packageName);
        dir.mkdirs();
        return dir;
    }

    /** Lib dir for extracted native libs */
    public static File getLibDir(String packageName, int userId) {
        File dir = new File(getUserDir(userId), "lib/" + packageName);
        dir.mkdirs();
        return dir;
    }

    /** Dex cache dir */
    public static File getDalvikCacheDir(String packageName, int userId) {
        File dir = new File(getUserDir(userId), "dalvik/" + packageName);
        dir.mkdirs();
        return dir;
    }

    /** Virtual /sdcard path for a user */
    public static File getSdcardDir(int userId) {
        File dir = new File(getUserDir(userId), "sdcard");
        dir.mkdirs();
        return dir;
    }

    /** Settings file */
    public static File getSettingsFile() {
        return new File(sBaseDir, "settings.json");
    }

    /** Package settings dir */
    public static File getPackageSettingsDir() {
        File dir = new File(sBaseDir, "packages");
        dir.mkdirs();
        return dir;
    }

    public static File getPackageSettingsFile(String packageName) {
        return new File(getPackageSettingsDir(), packageName + ".json");
    }
}

package top.niunaijun.blackbox.core;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Global uncaught exception handler for virtual processes.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "BBlackbox/Crash";
    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private final File mCrashDir;

    public CrashHandler(Thread.UncaughtExceptionHandler defaultHandler, File crashDir) {
        this.mDefaultHandler = defaultHandler;
        this.mCrashDir = crashDir;
        mCrashDir.mkdirs();
    }

    public static void install(File crashDir) {
        Thread.UncaughtExceptionHandler existing = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(existing, crashDir));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "Uncaught exception in thread " + thread.getName(), ex);
        writeCrashLog(thread, ex);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    private void writeCrashLog(Thread thread, Throwable ex) {
        try {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File logFile = new File(mCrashDir, "crash_" + ts + ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
                pw.println("Thread: " + thread.getName());
                pw.println("Time: " + ts);
                ex.printStackTrace(pw);
            }
        } catch (Exception ignored) {}
    }
}

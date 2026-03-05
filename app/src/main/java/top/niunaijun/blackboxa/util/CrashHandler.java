package top.niunaijun.blackbox.util;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance = new CrashHandler();
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Context context;

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        try {
            File dir = new File(context.getExternalFilesDir(null), "crash");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "crash.txt");

            PrintWriter pw = new PrintWriter(new FileWriter(file, true));
            pw.println("===== Crash =====");
            throwable.printStackTrace(pw);
            pw.close();

        } catch (Exception ignored) {}

        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        }
    }
}

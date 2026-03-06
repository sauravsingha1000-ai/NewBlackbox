package top.niunaijun.blackboxa.app

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.util.CrashHandler
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun attachBaseContext(base: Context) {
        // MUST be first
        super.attachBaseContext(base)

        // Setup early crash handler
        setupEarlyCrashHandler(base)

        // FIX: pass base directly (NOT applicationContext)
        BlackBoxCore.get().doAttachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        // Enable main crash logger
        CrashHandler.get().init(this)

        // Initialize BlackBox
        BlackBoxCore.get().doCreate(this)

        // App manager init
        AppManager.init(this)
    }

    private fun setupEarlyCrashHandler(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrashLog(context, thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun saveCrashLog(context: Context, thread: Thread, throwable: Throwable) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        try {
            // safer path for Android 11+
            val crashDir = File("/storage/emulated/0/Android/data/top.niunaijun.blackboxa/crash")
            crashDir.mkdirs()

            val crashFile = File(crashDir, "crash_$timestamp.txt")
            val report = buildCrashReport(thread, throwable)

            FileWriter(crashFile).use { it.write(report) }

            Log.e("EarlyCrash", "Crash log saved: ${crashFile.absolutePath}")

        } catch (e: Exception) {
            try {
                File("/sdcard/blackboxa_crash_$timestamp.txt")
                    .writeText(buildCrashReport(thread, throwable))
            } catch (_: Exception) {
            }
        }
    }

    private fun buildCrashReport(thread: Thread, throwable: Throwable): String {
        return buildString {
            appendLine("========= EARLY CRASH =========")
            appendLine("Time: ${Date()}")
            appendLine("Thread: ${thread.name}")
            appendLine("Exception: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message}")
            appendLine()
            appendLine(throwable.stackTraceToString())

            var cause = throwable.cause
            while (cause != null) {
                appendLine()
                appendLine("Caused by: ${cause.javaClass.name}")
                appendLine(cause.stackTraceToString())
                cause = cause.cause
            }

            appendLine("========= END =========")
        }
    }
}

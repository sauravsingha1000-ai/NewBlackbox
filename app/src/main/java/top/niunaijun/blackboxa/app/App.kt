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
        // Set crash handler BEFORE BlackBox attaches (catches early native crashes)
        setupEarlyCrashHandler(base)
        
        super.attachBaseContext(base)
        BlackBoxCore.get().doAttachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Enable crash logger
        CrashHandler.get().init(this)

        BlackBoxCore.get().doCreate(this)
        AppManager.init(this)
    }

    private fun setupEarlyCrashHandler(context: Context) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            
            // Save to external files dir for easy Termux access
            val crashDir = File(context.getExternalFilesDir(null), "early_crash")
            crashDir.mkdirs()
            
            val crashFile = File(crashDir, "crash_$timestamp.txt")
            
            val report = buildString {
                appendLine("========= EARLY CRASH =========")
                appendLine("Package: top.niunaijun.blackboxa")
                appendLine("Time: ${Date()}")
                appendLine("Thread: ${thread.name} (ID: ${thread.id})")
                appendLine("Exception: ${throwable.javaClass.name}")
                appendLine("Message: ${throwable.message}")
                appendLine()
                appendLine("--------- STACK TRACE ---------")
                appendLine(throwable.stackTraceToString())
                
                // Cause chain
                var cause = throwable.cause
                while (cause != null) {
                    appendLine()
                    appendLine("Caused by: ${cause.javaClass.name}")
                    appendLine(cause.stackTraceToString())
                    cause = cause.cause
                }
                appendLine("========= END =========")
            }
            
            // Write to file
            try {
                FileWriter(crashFile).use { it.write(report) }
                Log.e("EarlyCrash", "Saved to: ${crashFile.absolutePath}")
            } catch (e: Exception) {
                // Fallback to sdcard root
                try {
                    val fallback = File("/sdcard/blackboxa_crash_$timestamp.txt")
                    FileWriter(fallback).use { it.write(report) }
                    Log.e("EarlyCrash", "Fallback saved to: ${fallback.absolutePath}")
                } catch (e2: Exception) {
                    Log.e("EarlyCrash", "Failed to save crash log", e2)
                }
            }
            
            // Call default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}

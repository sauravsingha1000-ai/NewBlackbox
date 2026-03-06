package top.niunaijun.blackboxa.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*

class CrashHandler private constructor() : UncaughtExceptionHandler {

    private var defaultHandler: UncaughtExceptionHandler? = null
    private lateinit var logDir: File

    companion object {
        private val instance = CrashHandler()
        private const val TAG = "CrashHandler"

        fun get(): CrashHandler = instance
    }

    fun init(context: Context) {

        // Safer path for Android 11+
        logDir = File("/storage/emulated/0/Android/data/top.niunaijun.blackboxa/crash_logs")

        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)

        Log.d(TAG, "CrashHandler initialized. Log dir: ${logDir.absolutePath}")
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val logFile = saveCrashLog(thread, throwable)
        Log.e(TAG, "App crashed! Log saved to: $logFile", throwable)

        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun saveCrashLog(thread: Thread, throwable: Throwable): File {

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "crash_$timestamp.log"

        val logFile = File(logDir, fileName)

        val crashReport = buildString {
            appendLine("========= CRASH LOG =========")
            appendLine("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
            appendLine("Thread: ${thread.name} (ID: ${thread.id})")
            appendLine("Exception: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message}")
            appendLine()
            appendLine("--------- STACK TRACE ---------")
            appendLine(throwable.stackTraceToString())
            appendLine("========= END =========")
        }

        try {
            FileWriter(logFile).use { it.write(crashReport) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log", e)
        }

        return logFile
    }

    fun getLogFiles(): List<File> {
        return logDir.listFiles()?.filter { it.extension == "log" }?.toList() ?: emptyList()
    }

    fun getLatestLog(): File? {
        return getLogFiles().maxByOrNull { it.lastModified() }
    }
}

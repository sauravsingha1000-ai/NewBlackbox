package top.niunaijun.blackboxa.util

import android.content.Context
import android.util.Log
import java.lang.Thread.UncaughtExceptionHandler

class CrashHandler private constructor() : UncaughtExceptionHandler {

private var defaultHandler: UncaughtExceptionHandler? = null

companion object {
    private val instance = CrashHandler()

    fun get(): CrashHandler {
        return instance
    }
}

fun init(context: Context) {
    defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler(this)
}

override fun uncaughtException(thread: Thread, throwable: Throwable) {
    Log.e("CrashHandler", "App crashed!", throwable)
    defaultHandler?.uncaughtException(thread, throwable)
}

}

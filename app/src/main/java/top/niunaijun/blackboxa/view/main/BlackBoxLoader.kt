package top.niunaijun.blackboxa.view.main

import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore

/**
 * Handles BlackBox initialization and loading state for the UI.
 */
object BlackBoxLoader {
    fun isReady(): Boolean = BlackBoxCore.get().isInitialized
    fun init(context: Context) = BlackBoxCore.get().doCreate(
        context.applicationContext as android.app.Application
    )
}

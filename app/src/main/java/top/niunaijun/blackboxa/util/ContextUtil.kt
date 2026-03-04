package top.niunaijun.blackboxa.util

import android.content.Context
import top.niunaijun.blackboxa.app.App

object ContextUtil {
    val context: Context get() = App.instance.applicationContext
}

package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val userId: Int = 0,
    val apkPath: String = "",
    val versionName: String = "",
    val versionCode: Int = 0,
    val isRunning: Boolean = false
)

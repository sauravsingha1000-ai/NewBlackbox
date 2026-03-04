package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

data class InstalledAppBean(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val userId: Int = 0,
    val versionName: String = "",
    val versionCode: Int = 0
)

package top.niunaijun.blackboxa.bean

data class GmsBean(
    val packageName: String,
    val appName: String,
    val gmsEnabled: Boolean = false,
    val userId: Int = 0
)

package top.niunaijun.blackboxa.bean

data class XpModuleInfo(
    val packageName: String,
    val name: String,
    val description: String = "",
    val minVersion: Int = 0,
    val version: Int = 0,
    val enabled: Boolean = false
)

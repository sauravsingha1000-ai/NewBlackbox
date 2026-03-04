package top.niunaijun.blackboxa.bean

data class FakeLocationBean(
    val packageName: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val enabled: Boolean = true,
    val description: String = ""
)

package top.niunaijun.blackboxa.data

import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.bean.GmsBean

class GmsRepository(private val context: Context) {

    fun getGmsEnabledApps(userId: Int = 0): List<GmsBean> {
        return BlackBoxCore.get().getInstalledApps(userId).map { pkg ->
            GmsBean(
                packageName = pkg.packageName,
                appName = pkg.packageName,
                gmsEnabled = false,
                userId = userId
            )
        }
    }

    fun setGmsEnabled(packageName: String, enabled: Boolean) {
        // Store GMS pref per package
    }
}

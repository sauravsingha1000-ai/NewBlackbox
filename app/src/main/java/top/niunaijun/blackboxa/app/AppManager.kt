package top.niunaijun.blackboxa.app

import android.content.Context
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.entity.pm.InstalledPackage

/**
 * Convenience wrapper over BlackBoxCore for the UI layer.
 */
object AppManager {

    fun init(context: Context) {
        // Any app-level initialization
    }

    fun getInstalledApps(userId: Int = 0): List<InstalledPackage> =
        BlackBoxCore.get().getInstalledApps(userId)

    fun installApp(apkPath: String, userId: Int = 0) =
        BlackBoxCore.get().installPackageAsUser(apkPath, userId)

    fun uninstallApp(packageName: String, userId: Int = 0) =
        BlackBoxCore.get().uninstallPackageAsUser(packageName, userId)

    fun launchApp(packageName: String, userId: Int = 0): Boolean =
        BlackBoxCore.get().launchApk(packageName, userId)

    fun isInstalled(packageName: String, userId: Int = 0): Boolean =
        BlackBoxCore.get().isInstalled(packageName, userId)
}

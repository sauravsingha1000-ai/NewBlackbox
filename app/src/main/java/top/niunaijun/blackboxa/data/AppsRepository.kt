package top.niunaijun.blackboxa.data

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.bean.AppInfo

class AppsRepository(private val context: Context) {

    /**
     * Get installed virtual apps
     */
    suspend fun getInstalledApps(userId: Int = 0): List<AppInfo> =
        withContext(Dispatchers.IO) {

            val pm = context.packageManager

            BlackBoxCore.get().getInstalledApps(userId).mapNotNull { pkg ->

                try {

                    val label = try {
                        pm.getApplicationLabel(
                            pm.getApplicationInfo(pkg.packageName, 0)
                        ).toString()
                    } catch (_: Exception) {
                        pkg.packageName
                    }

                    val icon: Drawable? = try {
                        pm.getApplicationIcon(pkg.packageName)
                    } catch (_: Exception) {
                        null
                    }

                    AppInfo(
                        packageName = pkg.packageName,
                        appName = label,
                        icon = icon,
                        userId = userId,
                        apkPath = pkg.apkPath ?: "",
                        versionName = pkg.versionName ?: "",
                        versionCode = pkg.versionCode
                    )

                } catch (_: Exception) {
                    null
                }
            }
        }

    /**
     * Install APK into virtual space
     * Prevent duplicate installs
     */
    suspend fun installApp(apkPath: String, userId: Int = 0) =
        withContext(Dispatchers.IO) {

            try {

                val core = BlackBoxCore.get()

                val result = core.installPackageAsUser(apkPath, userId)

                if (!result.success) {
                    throw RuntimeException(result.message ?: "Install failed")
                }

                result

            } catch (e: Exception) {

                throw RuntimeException(e.message ?: "Installation error")
            }
        }

    /**
     * Install from device app
     */
    suspend fun installFromDevice(packageName: String, userId: Int = 0) =
        withContext(Dispatchers.IO) {

            val pm = context.packageManager

            val info = pm.getApplicationInfo(packageName, 0)

            installApp(info.sourceDir, userId)
        }

    /**
     * Uninstall virtual app
     */
    suspend fun uninstallApp(packageName: String, userId: Int = 0) =
        withContext(Dispatchers.IO) {

            try {

                BlackBoxCore.get().uninstallPackageAsUser(packageName, userId)

            } catch (e: Exception) {

                throw RuntimeException(e.message ?: "Uninstall failed")
            }
        }

    /**
     * Launch virtual app
     */
    suspend fun launchApp(packageName: String, userId: Int = 0): Boolean =
        withContext(Dispatchers.Main) {

            try {

                BlackBoxCore.get().launchApk(packageName, userId)

            } catch (_: Exception) {

                false
            }
        }
}

package top.niunaijun.blackboxa.data

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.bean.AppInfo

class AppsRepository(private val context: Context) {

    suspend fun getInstalledApps(userId: Int = 0): List<AppInfo> =
        withContext(Dispatchers.IO) {
            BlackBoxCore.get().getInstalledApps(userId).mapNotNull { pkg ->
                try {
                    val pm = context.packageManager
                    val label = try {
                        pm.getApplicationLabel(
                            pm.getApplicationInfo(pkg.packageName, 0)
                        ).toString()
                    } catch (e: PackageManager.NameNotFoundException) {
                        pkg.packageName
                    }
                    val icon: Drawable? = try {
                        pm.getApplicationIcon(pkg.packageName)
                    } catch (_: Exception) { null }

                    AppInfo(
                        packageName = pkg.packageName,
                        appName = label,
                        icon = icon,
                        userId = userId,
                        apkPath = pkg.apkPath ?: "",
                        versionName = pkg.versionName ?: "",
                        versionCode = pkg.versionCode
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }

    suspend fun installApp(apkPath: String, userId: Int = 0) =
        withContext(Dispatchers.IO) {
            BlackBoxCore.get().installPackageAsUser(apkPath, userId)
        }

    suspend fun uninstallApp(packageName: String, userId: Int = 0) =
        withContext(Dispatchers.IO) {
            BlackBoxCore.get().uninstallPackageAsUser(packageName, userId)
        }

    suspend fun launchApp(packageName: String, userId: Int = 0): Boolean =
        withContext(Dispatchers.Main) {
            BlackBoxCore.get().launchApk(packageName, userId)
        }
}

package top.niunaijun.blackboxa.view.apps

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.data.AppsRepository

class AppsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppsRepository(application)

    val apps = MutableLiveData<List<AppInfo>>()
    val deviceApps = MutableLiveData<List<ApplicationInfo>>()

    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val installResult = MutableLiveData<String>()

    fun loadApps(userId: Int = 0) {

        viewModelScope.launch {

            loading.postValue(true)

            try {

                val list = repo.getInstalledApps(userId)

                apps.postValue(list)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Failed to load apps")

            } finally {

                loading.postValue(false)
            }
        }
    }

    fun loadDeviceApps() {

        viewModelScope.launch {

            try {

                val pm = getApplication<Application>().packageManager

                val list = withContext(Dispatchers.IO) {

                    pm.getInstalledApplications(PackageManager.GET_META_DATA)

                        .filter {
                            it.flags and ApplicationInfo.FLAG_SYSTEM == 0
                        }

                        .filter {
                            pm.getLaunchIntentForPackage(it.packageName) != null
                        }

                        .sortedBy {
                            it.loadLabel(pm).toString().lowercase()
                        }
                }

                deviceApps.postValue(list)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Failed to load device apps")
            }
        }
    }

    fun installApp(apkPath: String, userId: Int = 0) {

        viewModelScope.launch {

            loading.postValue(true)

            try {

                val pm = getApplication<Application>().packageManager

                val pkgInfo = pm.getPackageArchiveInfo(apkPath, 0)

                val packageName = pkgInfo?.packageName

                if (packageName == null) {

                    error.postValue("Invalid APK file")
                    return@launch
                }

                val installedApps = repo.getInstalledApps(userId)

                if (installedApps.any { it.packageName == packageName }) {

                    error.postValue("App already installed in TeristaSpace")
                    return@launch
                }

                val result = repo.installApp(apkPath, userId)

                if (result.success) {

                    installResult.postValue("Installed: ${result.packageName}")

                    loadApps(userId)

                } else {

                    error.postValue("Install failed: ${result.message}")
                }

            } catch (e: Exception) {

                error.postValue(e.message ?: "Installation error")

            } finally {

                loading.postValue(false)
            }
        }
    }

    fun installFromDevice(packageName: String, userId: Int = 0) {

        viewModelScope.launch {

            try {

                val pm = getApplication<Application>().packageManager

                val info = pm.getApplicationInfo(packageName, 0)

                installApp(info.sourceDir, userId)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Install from device failed")
            }
        }
    }

    fun uninstallApp(packageName: String, userId: Int = 0) {

        viewModelScope.launch {

            try {

                repo.uninstallApp(packageName, userId)

                loadApps(userId)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Uninstall failed")
            }
        }
    }

    fun launchApp(packageName: String, userId: Int = 0) {

        viewModelScope.launch {

            try {

                repo.launchApp(packageName, userId)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Launch failed")
            }
        }
    }
}

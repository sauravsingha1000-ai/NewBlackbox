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

    /**
     * Load apps installed inside TeristaSpace
     */
    fun loadApps(userId: Int = 0) {

        viewModelScope.launch {

            loading.postValue(true)

            try {

                val list = withContext(Dispatchers.IO) {
                    repo.getInstalledApps(userId)
                }

                apps.postValue(list)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Failed to load apps")

            } finally {

                loading.postValue(false)
            }
        }
    }

    /**
     * Load installed apps from the real device
     */
    fun loadDeviceApps() {

        viewModelScope.launch {

            try {

                val pm = getApplication<Application>().packageManager

                val list = withContext(Dispatchers.IO) {

                    pm.getInstalledApplications(PackageManager.GET_META_DATA)

                        // hide system apps
                        .filter {
                            it.flags and ApplicationInfo.FLAG_SYSTEM == 0
                        }

                        // only launchable apps
                        .filter {
                            pm.getLaunchIntentForPackage(it.packageName) != null
                        }

                        // sort alphabetically
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

    /**
     * Install APK from storage
     */
    fun installApp(apkPath: String, userId: Int = 0) {

        viewModelScope.launch {

            loading.postValue(true)

            try {

                val pm = getApplication<Application>().packageManager

                val pkgInfo = withContext(Dispatchers.IO) {
                    pm.getPackageArchiveInfo(apkPath, 0)
                }

                val packageName = pkgInfo?.packageName

                if (packageName == null) {

                    error.postValue("Invalid APK file")
                    return@launch
                }

                // prevent duplicate installs
                val installedApps = withContext(Dispatchers.IO) {
                    repo.getInstalledApps(userId)
                }

                if (installedApps.any { it.packageName == packageName }) {

                    error.postValue("App already installed in TeristaSpace")
                    return@launch
                }

                val result = withContext(Dispatchers.IO) {
                    repo.installApp(apkPath, userId)
                }

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

    /**
     * Install app from real device
     */
    fun installFromDevice(packageName: String, userId: Int = 0) {

        viewModelScope.launch {

            try {

                val pm = getApplication<Application>().packageManager

                val info = withContext(Dispatchers.IO) {
                    pm.getApplicationInfo(packageName, 0)
                }

                installApp(info.sourceDir, userId)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Install from device failed")
            }
        }
    }

    /**
     * Uninstall virtual app
     */
    fun uninstallApp(packageName: String, userId: Int = 0) {

        viewModelScope.launch {

            try {

                withContext(Dispatchers.IO) {
                    repo.uninstallApp(packageName, userId)
                }

                loadApps(userId)

            } catch (e: Exception) {

                error.postValue(e.message ?: "Uninstall failed")
            }
        }
    }

    /**
     * Launch virtual app
     */
    fun launchApp(packageName: String, userId: Int = 0) {

        viewModelScope.launch {

            try {

                withContext(Dispatchers.IO) {
                    repo.launchApp(packageName, userId)
                }

            } catch (e: Exception) {

                error.postValue(e.message ?: "Launch failed")
            }
        }
    }
}

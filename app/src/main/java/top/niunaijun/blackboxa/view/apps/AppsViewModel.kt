package top.niunaijun.blackboxa.view.apps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.data.AppsRepository

class AppsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AppsRepository(application)

    val apps = MutableLiveData<List<AppInfo>>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val installResult = MutableLiveData<String>()

    fun loadApps(userId: Int = 0) {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                apps.postValue(repo.getInstalledApps(userId))
            } catch (e: Exception) {
                error.postValue(e.message)
            } finally {
                loading.postValue(false)
            }
        }
    }

    fun installApp(apkPath: String, userId: Int = 0) {
        viewModelScope.launch {
            loading.postValue(true)
            try {
                val result = repo.installApp(apkPath, userId)
                if (result.success) {
                    installResult.postValue("Installed: ${result.packageName}")
                    loadApps(userId)
                } else {
                    error.postValue("Install failed: ${result.message}")
                }
            } catch (e: Exception) {
                error.postValue(e.message)
            } finally {
                loading.postValue(false)
            }
        }
    }

    fun uninstallApp(packageName: String, userId: Int = 0) {
        viewModelScope.launch {
            try {
                repo.uninstallApp(packageName, userId)
                loadApps(userId)
            } catch (e: Exception) {
                error.postValue(e.message)
            }
        }
    }

    fun launchApp(packageName: String, userId: Int = 0) {
        viewModelScope.launch {
            repo.launchApp(packageName, userId)
        }
    }
}

package top.niunaijun.blackboxa.view.list

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.niunaijun.blackboxa.bean.InstalledAppBean

/**
 * ViewModel for the "install from device" picker — lists all device apps.
 */
class ListViewModel(application: Application) : AndroidViewModel(application) {
    val apps = MutableLiveData<List<InstalledAppBean>>()
    val loading = MutableLiveData<Boolean>()

    fun loadDeviceApps() {
        viewModelScope.launch {
            loading.postValue(true)
            val pm = getApplication<Application>().packageManager
            val result = withContext(Dispatchers.IO) {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                    .map { ai ->
                        InstalledAppBean(
                            packageName = ai.packageName,
                            appName = pm.getApplicationLabel(ai).toString(),
                            icon = try { pm.getApplicationIcon(ai.packageName) } catch (_: Exception) { null },
                            versionName = try {
                                pm.getPackageInfo(ai.packageName, 0).versionName ?: ""
                            } catch (_: Exception) { "" }
                        )
                    }
                    .sortedBy { it.appName.lowercase() }
            }
            apps.postValue(result)
            loading.postValue(false)
        }
    }
}

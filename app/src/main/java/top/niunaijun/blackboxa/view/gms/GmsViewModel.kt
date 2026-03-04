package top.niunaijun.blackboxa.view.gms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.data.GmsRepository

class GmsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = GmsRepository(application)
    val apps = MutableLiveData<List<GmsBean>>()

    fun loadApps(userId: Int = 0) { apps.postValue(repo.getGmsEnabledApps(userId)) }
    fun setGmsEnabled(packageName: String, enabled: Boolean) {
        repo.setGmsEnabled(packageName, enabled)
    }
}

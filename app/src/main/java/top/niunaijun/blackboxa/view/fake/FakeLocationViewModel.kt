package top.niunaijun.blackboxa.view.fake

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.core.system.location.BLocationManagerService
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.data.FakeLocationRepository

class FakeLocationViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = FakeLocationRepository(
        application.getSharedPreferences("fake_location", Context.MODE_PRIVATE)
    )

    val locations = MutableLiveData<List<FakeLocationBean>>()

    fun loadLocations() { locations.postValue(repo.getAll()) }

    fun setFakeLocation(packageName: String, lat: Double, lng: Double, userId: Int = 0) {
        val bean = FakeLocationBean(packageName, lat, lng)
        repo.save(bean)
        BLocationManagerService.get().setFakeLocation(BLocation(lat, lng), userId)
        loadLocations()
    }

    fun clearFakeLocation(packageName: String, userId: Int = 0) {
        repo.remove(packageName)
        BLocationManagerService.get().clearFakeLocation(userId)
        loadLocations()
    }
}

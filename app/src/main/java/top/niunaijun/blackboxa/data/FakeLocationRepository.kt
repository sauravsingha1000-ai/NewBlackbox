package top.niunaijun.blackboxa.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import top.niunaijun.blackboxa.bean.FakeLocationBean

class FakeLocationRepository(private val prefs: SharedPreferences) {
    private val gson = Gson()
    private val KEY = "fake_locations"

    fun getAll(): List<FakeLocationBean> {
        val json = prefs.getString(KEY, "[]") ?: "[]"
        val type = object : TypeToken<List<FakeLocationBean>>() {}.type
        return gson.fromJson(json, type)
    }

    fun save(bean: FakeLocationBean) {
        val list = getAll().toMutableList()
        list.removeAll { it.packageName == bean.packageName }
        list.add(bean)
        prefs.edit().putString(KEY, gson.toJson(list)).apply()
    }

    fun remove(packageName: String) {
        val list = getAll().toMutableList()
        list.removeAll { it.packageName == packageName }
        prefs.edit().putString(KEY, gson.toJson(list)).apply()
    }

    fun get(packageName: String): FakeLocationBean? =
        getAll().firstOrNull { it.packageName == packageName }
}

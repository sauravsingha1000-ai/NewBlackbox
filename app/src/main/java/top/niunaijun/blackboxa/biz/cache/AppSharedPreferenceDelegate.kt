package top.niunaijun.blackboxa.biz.cache

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AppSharedPreferenceDelegate<T>(
    private val context: Context,
    private val defaultValue: T,
    private val prefName: String = "blackboxa_prefs"
) : ReadWriteProperty<Any?, T> {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        when (defaultValue) {
            is Boolean -> prefs.getBoolean(property.name, defaultValue) as T
            is Int -> prefs.getInt(property.name, defaultValue) as T
            is Long -> prefs.getLong(property.name, defaultValue) as T
            is Float -> prefs.getFloat(property.name, defaultValue) as T
            is String -> prefs.getString(property.name, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        prefs.edit().apply {
            when (value) {
                is Boolean -> putBoolean(property.name, value)
                is Int -> putInt(property.name, value)
                is Long -> putLong(property.name, value)
                is Float -> putFloat(property.name, value)
                is String -> putString(property.name, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }.apply()
}

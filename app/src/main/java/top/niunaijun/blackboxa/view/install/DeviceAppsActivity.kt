package top.niunaijun.blackboxa.view.install

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.databinding.ActivityDeviceAppsBinding

class DeviceAppsActivity : AppCompatActivity() {

private lateinit var binding: ActivityDeviceAppsBinding

private val allApps = mutableListOf<DeviceApp>()
private val filteredApps = mutableListOf<DeviceApp>()

private lateinit var adapter: DeviceAppsAdapter

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityDeviceAppsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    loadApps()

    adapter = DeviceAppsAdapter(filteredApps) { packageName ->
        BlackBoxCore.get().installPackageAsUser(packageName, 0)
    }

    binding.recyclerView.layoutManager = LinearLayoutManager(this)
    binding.recyclerView.adapter = adapter

    setupSearch()
}

private fun loadApps() {

    val pm = packageManager
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    packages.forEach { app ->

        // Hide system apps
        if ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0) return@forEach

        val label = pm.getApplicationLabel(app).toString()
        val icon = pm.getApplicationIcon(app)

        val deviceApp = DeviceApp(label, app.packageName, icon)

        allApps.add(deviceApp)
    }

    filteredApps.addAll(allApps)
}

private fun setupSearch() {

    binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean = false

        override fun onQueryTextChange(query: String?): Boolean {

            val text = query?.lowercase() ?: ""

            filteredApps.clear()

            filteredApps.addAll(
                allApps.filter {
                    it.name.lowercase().contains(text)
                }
            )

            adapter.notifyDataSetChanged()

            return true
        }
    })
}

}

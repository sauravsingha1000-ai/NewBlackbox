package top.niunaijun.blackboxa.view.install

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadApps()

        val installedPackages = BlackBoxCore.get()
            .getInstalledApps(0)
            .map { it.packageName }
            .toMutableSet()

        adapter = DeviceAppsAdapter(filteredApps, installedPackages) { packageName ->
            installApp(packageName)
        }

        binding.recyclerView.adapter = adapter

        setupSearch()
    }

    /**
     * Install selected app
     */
    private fun installApp(packageName: String) {

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {

            try {

                val apkPath = withContext(Dispatchers.IO) {

                    val pm = packageManager
                    val info = pm.getApplicationInfo(packageName, 0)

                    info.sourceDir
                }

                val result = withContext(Dispatchers.IO) {

                    BlackBoxCore.get()
                        .installPackageAsUser(apkPath, 0)
                }

                binding.progressBar.visibility = View.GONE

                if (result.success) {

                    Toast.makeText(
                        this@DeviceAppsActivity,
                        "Installed successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    adapter.markInstalled(packageName)

                } else {

                    Toast.makeText(
                        this@DeviceAppsActivity,
                        "Install failed: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this@DeviceAppsActivity,
                    "Install error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Load installed apps from device
     */
    private fun loadApps() {

        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        packages.forEach { app ->

            if ((app.flags and ApplicationInfo.FLAG_SYSTEM) != 0) return@forEach

            val label = pm.getApplicationLabel(app).toString()
            val icon = pm.getApplicationIcon(app)

            val deviceApp = DeviceApp(label, app.packageName, icon)

            allApps.add(deviceApp)
        }

        allApps.sortBy { it.name.lowercase() }

        filteredApps.addAll(allApps)
    }

    /**
     * Search apps
     */
    private fun setupSearch() {

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = false

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

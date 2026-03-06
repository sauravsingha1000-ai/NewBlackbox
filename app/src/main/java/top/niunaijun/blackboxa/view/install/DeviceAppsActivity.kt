package top.niunaijun.blackboxa.view.install

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.databinding.ActivityDeviceAppsBinding

class DeviceAppsActivity : AppCompatActivity() {

private lateinit var binding: ActivityDeviceAppsBinding
private val apps = mutableListOf<DeviceApp>()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityDeviceAppsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    loadApps()

    binding.recyclerView.layoutManager = LinearLayoutManager(this)

    binding.recyclerView.adapter = DeviceAppsAdapter(apps) { packageName ->
        BlackBoxCore.get().installPackageAsUser(packageName, 0)
    }
}

private fun loadApps() {

    val pm = packageManager
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    packages.forEach {

        val label = pm.getApplicationLabel(it).toString()
        val icon = pm.getApplicationIcon(it)

        apps.add(DeviceApp(label, it.packageName, icon))
    }
}

}

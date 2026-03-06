package top.niunaijun.blackboxa.view.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.databinding.ItemDeviceAppBinding

class DeviceAppsAdapter(
    private val apps: List<ApplicationInfo>,
    private val pm: PackageManager,
    private val onInstall: (String) -> Unit
) : RecyclerView.Adapter<DeviceAppsAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemDeviceAppBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(app: ApplicationInfo) {

            val label = app.loadLabel(pm).toString()
            val icon = app.loadIcon(pm)

            binding.tvName.text = label
            binding.tvPackage.text = app.packageName
            binding.ivIcon.setImageDrawable(icon)

            // Install when clicked
            binding.root.setOnClickListener {
                onInstall(app.packageName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeviceAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(apps[position])
    }
}

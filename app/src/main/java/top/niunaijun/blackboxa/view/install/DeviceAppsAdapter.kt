package top.niunaijun.blackboxa.view.install

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.databinding.ItemDeviceAppBinding

class DeviceAppsAdapter(
    private val list: List<DeviceApp>,
    private val installedPackages: MutableSet<String>,
    private val install: (String) -> Unit
) : RecyclerView.Adapter<DeviceAppsAdapter.Holder>() {

    inner class Holder(val binding: ItemDeviceAppBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val binding = ItemDeviceAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return Holder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val app = list[position]

        holder.binding.ivIcon.setImageDrawable(app.icon)
        holder.binding.tvName.text = app.name

        val alreadyInstalled = installedPackages.contains(app.packageName)

        if (alreadyInstalled) {

            holder.binding.tvPackage.text =
                "${app.packageName} • Installed"

            holder.itemView.alpha = 0.5f
            holder.itemView.isClickable = false
            holder.itemView.setOnClickListener(null)

        } else {

            holder.binding.tvPackage.text = app.packageName

            holder.itemView.alpha = 1f
            holder.itemView.isClickable = true

            holder.itemView.setOnClickListener {

                install(app.packageName)
            }
        }
    }

    /**
     * Mark app as installed after installation
     */
    fun markInstalled(packageName: String) {

        installedPackages.add(packageName)

        notifyDataSetChanged()
    }
}

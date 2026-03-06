package top.niunaijun.blackboxa.view.install

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.databinding.ItemDeviceAppBinding

class DeviceAppsAdapter(
private val list: List<DeviceApp>,
private val install: (String) -> Unit
) : RecyclerView.Adapter<DeviceAppsAdapter.Holder>() {

class Holder(val binding: ItemDeviceAppBinding)
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

    holder.binding.icon.setImageDrawable(app.icon)
    holder.binding.name.text = app.name
    holder.binding.pkg.text = app.packageName

    holder.itemView.setOnClickListener {
        install(app.packageName)
    }
}

}

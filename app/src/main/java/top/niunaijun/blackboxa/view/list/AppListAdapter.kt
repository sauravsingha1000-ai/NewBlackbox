package top.niunaijun.blackboxa.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.databinding.ItemAppBinding

class AppListAdapter(private val onSelect: (InstalledAppBean) -> Unit)
    : ListAdapter<InstalledAppBean, AppListAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<InstalledAppBean>() {
            override fun areItemsTheSame(a: InstalledAppBean, b: InstalledAppBean) =
                a.packageName == b.packageName

            override fun areContentsTheSame(a: InstalledAppBean, b: InstalledAppBean) =
                a == b
        }
    }

    inner class VH(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InstalledAppBean) {
            binding.tvName.text = item.appName
            binding.tvPackage.text = item.packageName
            binding.tvVersion.text = item.versionName
            binding.ivIcon.setImageDrawable(item.icon)

            binding.root.setOnClickListener { onSelect(item) }

            binding.btnLaunch.visibility = android.view.View.GONE
            binding.btnUninstall.visibility = android.view.View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}

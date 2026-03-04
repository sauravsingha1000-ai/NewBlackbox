package top.niunaijun.blackboxa.view.gms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.databinding.ItemGmsBinding

class GmsAdapter(
    private val onToggle: (GmsBean, Boolean) -> Unit
) : ListAdapter<GmsBean, GmsAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<GmsBean>() {
            override fun areItemsTheSame(a: GmsBean, b: GmsBean) = a.packageName == b.packageName
            override fun areContentsTheSame(a: GmsBean, b: GmsBean) = a == b
        }
    }

    inner class VH(val b: ItemGmsBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: GmsBean) {
            b.tvName.text = item.appName
            b.tvPackage.text = item.packageName
            b.switchGms.isChecked = item.gmsEnabled
            b.switchGms.setOnCheckedChangeListener { _, checked -> onToggle(item, checked) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemGmsBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))
}

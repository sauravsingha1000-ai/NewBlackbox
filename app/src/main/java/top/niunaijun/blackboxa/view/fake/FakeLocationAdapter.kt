package top.niunaijun.blackboxa.view.fake

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.databinding.ItemFakeLocationBinding

class FakeLocationAdapter(
    private val onDelete: (FakeLocationBean) -> Unit
) : ListAdapter<FakeLocationBean, FakeLocationAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<FakeLocationBean>() {
            override fun areItemsTheSame(a: FakeLocationBean, b: FakeLocationBean) =
                a.packageName == b.packageName
            override fun areContentsTheSame(a: FakeLocationBean, b: FakeLocationBean) = a == b
        }
    }

    inner class VH(val b: ItemFakeLocationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: FakeLocationBean) {
            b.tvPackage.text = item.packageName
            b.tvLatLng.text = "${item.latitude}, ${item.longitude}"
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemFakeLocationBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))
}

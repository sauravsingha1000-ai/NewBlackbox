package top.niunaijun.blackboxa.view.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.ItemAppBinding

class AppsAdapter(
    private val onLaunch: (AppInfo) -> Unit,
    private val onUninstall: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppsAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AppInfo>() {
            override fun areItemsTheSame(a: AppInfo, b: AppInfo) =
                a.packageName == b.packageName && a.userId == b.userId

            override fun areContentsTheSame(a: AppInfo, b: AppInfo) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(app: AppInfo) {

            binding.tvName.text = app.appName
            binding.tvPackage.text = app.packageName
            binding.tvVersion.text = app.versionName

            if (app.icon != null) {
                binding.ivIcon.setImageDrawable(app.icon)
            } else {
                binding.ivIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }

            // Launch app when clicking icon
            binding.ivIcon.setOnClickListener {
                onLaunch(app)
            }

            // Launch app when clicking name
            binding.tvName.setOnClickListener {
                onLaunch(app)
            }

            // Buttons (optional in grid)
            binding.btnLaunch.setOnClickListener { onLaunch(app) }
            binding.btnUninstall.setOnClickListener { onUninstall(app) }

            // Hide package/version if you want cleaner grid
            if (bindingAdapterPosition >= 0) {
                binding.tvPackage.visibility = View.GONE
                binding.tvVersion.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

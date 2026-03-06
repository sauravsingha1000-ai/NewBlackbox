package top.niunaijun.blackboxa.view.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.bean.AppInfo
import com.google.android.material.snackbar.Snackbar
import top.niunaijun.blackboxa.databinding.ItemAppBinding

class AppsAdapter(
private val onLaunch: (AppInfo) -> Unit,
private val onUninstall: (AppInfo) -> Unit,
private val onClone: (AppInfo) -> Unit = {},
private val onClearData: (AppInfo) -> Unit = {},
private val onAppInfo: (AppInfo) -> Unit = {}
) : ListAdapter<AppInfo, AppsAdapter.ViewHolder>(DIFF) {

companion object {
    private val DIFF = object : DiffUtil.ItemCallback<AppInfo>() {

        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName &&
                    oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}

inner class ViewHolder(private val binding: ItemAppBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(app: AppInfo) {

        binding.tvName.text = app.appName
        binding.tvPackage.text = app.packageName
        binding.tvVersion.text = app.versionName

        if (app.icon != null) {
            binding.ivIcon.setImageDrawable(app.icon)
        } else {
            binding.ivIcon.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        binding.btnLaunch.setOnClickListener {
            onLaunch(app)
        }

        binding.btnUninstall.setOnClickListener {
            onUninstall(app)
        }

        // Long press menu
        binding.root.setOnLongClickListener {
            showPopupMenu(it, app)
            true
        }
    }

    private fun showPopupMenu(view: View, app: AppInfo) {
        val popup = PopupMenu(view.context, view)

        popup.menu.add(0, 1, 0, "Launch")
        popup.menu.add(0, 2, 1, "Clone")
        popup.menu.add(0, 3, 2, "Clear Data")
        popup.menu.add(0, 4, 3, "App Info")
        popup.menu.add(0, 5, 4, "Uninstall")

        popup.setOnMenuItemClickListener {

            when (it.itemId) {

                1 -> onLaunch(app)

                2 -> onClone(app)

                3 -> onClearData(app)

                4 -> onAppInfo(app)

                5 -> onUninstall(app)
            }

            true
        }

        popup.show()
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

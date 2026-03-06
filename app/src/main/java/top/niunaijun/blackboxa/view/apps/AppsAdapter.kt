package top.niunaijun.blackboxa.view.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
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
        override fun areItemsTheSame(a: AppInfo, b: AppInfo) =
            a.packageName == b.packageName && a.userId == b.userId

        override fun areContentsTheSame(a: AppInfo, b: AppInfo) = a == b
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

        binding.btnLaunch.setOnClickListener { onLaunch(app) }
        binding.btnUninstall.setOnClickListener { onUninstall(app) }

        // ⭐ LONG PRESS MENU
        binding.root.setOnLongClickListener {
            showMenu(it, app)
            true
        }
    }

    private fun showMenu(view: View, app: AppInfo) {
        val popup = PopupMenu(view.context, view)

        popup.menu.add("Launch")
        popup.menu.add("Clone")
        popup.menu.add("Clear Data")
        popup.menu.add("App Info")
        popup.menu.add("Uninstall")

        popup.setOnMenuItemClickListener {

            when (it.title) {

                "Launch" -> onLaunch(app)

                "Clone" -> onClone(app)

                "Clear Data" -> onClearData(app)

                "App Info" -> onAppInfo(app)

                "Uninstall" -> onUninstall(app)
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

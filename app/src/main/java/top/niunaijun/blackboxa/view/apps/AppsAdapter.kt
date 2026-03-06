package top.niunaijun.blackboxa.view.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

            // Launch button
            binding.btnLaunch.setOnClickListener {
                onLaunch(app)
            }

            // Uninstall button
            binding.btnUninstall.setOnClickListener {
                confirmUninstall(app)
            }

            // Long press popup menu
            binding.root.setOnLongClickListener {
                showPopupMenu(it, app)
                true
            }
        }

        private fun confirmUninstall(app: AppInfo) {

            AlertDialog.Builder(binding.root.context)
                .setTitle("Uninstall App")
                .setMessage("Remove ${app.appName} from virtual space?")
                .setPositiveButton("Uninstall") { _, _ ->
                    onUninstall(app)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun showPopupMenu(view: View, app: AppInfo) {

            val popup = PopupMenu(view.context, view)

            popup.menu.add(0, 1, 0, "Launch")
            popup.menu.add(0, 2, 1, "Clone App")
            popup.menu.add(0, 3, 2, "Clear Data")
            popup.menu.add(0, 4, 3, "App Info")
            popup.menu.add(0, 5, 4, "Uninstall")

            popup.setOnMenuItemClickListener {

                when (it.itemId) {

                    1 -> onLaunch(app)

                    2 -> onClone(app)

                    3 -> onClearData(app)

                    4 -> onAppInfo(app)

                    5 -> confirmUninstall(app)
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

package top.niunaijun.blackboxa.view.apps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import top.niunaijun.blackboxa.databinding.FragmentAppsBinding
import top.niunaijun.blackboxa.view.install.DeviceAppsActivity

class AppsFragment : Fragment() {

    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppsViewModel by viewModels {
        AppsFactory(requireActivity().application)
    }

    private lateinit var adapter: AppsAdapter

    // Storage picker (APK/APKS/XAPK/APKM)
    private val pickApk =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { installFromUri(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        adapter = AppsAdapter(

            onLaunch = { app ->
                viewModel.launchApp(app.packageName)
            },

            onUninstall = { app ->
                viewModel.uninstallApp(app.packageName)
            },

            onClone = { app ->
                Snackbar.make(binding.root, "Cloning ${app.appName}", Snackbar.LENGTH_SHORT).show()
            },

            onClearData = { app ->
                Snackbar.make(binding.root, "Clearing data for ${app.appName}", Snackbar.LENGTH_SHORT).show()
            },

            onAppInfo = { app ->
                Snackbar.make(binding.root, app.packageName, Snackbar.LENGTH_LONG).show()
            }
        )

        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 4)

        binding.recyclerView.adapter = adapter

        enableDragAndDrop()

        binding.fabAdd.setOnClickListener {
            showInstallOptions()
        }

        viewModel.apps.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility =
                if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrEmpty()) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.loadApps()
    }

    private fun showInstallOptions() {

        val options = arrayOf(
            "Install from Installed Apps",
            "Install APK / APKS / XAPK / APKM"
        )

        AlertDialog.Builder(requireContext())
            .setTitle("Install App")
            .setItems(options) { _, which ->

                when (which) {

                    0 -> {
                        val intent =
                            Intent(requireContext(), DeviceAppsActivity::class.java)
                        startActivity(intent)
                    }

                    1 -> {
                        pickApk.launch("*/*")
                    }
                }
            }
            .show()
    }

    private fun installFromUri(uri: Uri) {

        val path = try {

            requireContext()
                .contentResolver
                .openFileDescriptor(uri, "r")
                ?.use { "/proc/self/fd/${it.fd}" }

        } catch (e: Exception) {
            null
        }

        if (path != null) {
            viewModel.installApp(path)
        } else {
            Snackbar.make(binding.root, "Install failed", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun enableDragAndDrop() {

        val callback = object : ItemTouchHelper.SimpleCallback(

            ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or
                    ItemTouchHelper.RIGHT,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                val list = adapter.currentList.toMutableList()

                val item = list.removeAt(from)
                list.add(to, item)

                adapter.submitList(list)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }
}

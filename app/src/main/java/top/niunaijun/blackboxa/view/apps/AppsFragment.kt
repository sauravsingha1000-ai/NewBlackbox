package top.niunaijun.blackboxa.view.apps

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.FragmentAppsBinding

class AppsFragment : Fragment() {

    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppsViewModel by viewModels {
        AppsFactory(requireActivity().application)
    }

    private lateinit var adapter: AppsAdapter

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
            onLaunch = { app -> viewModel.launchApp(app.packageName) },
            onUninstall = { app -> viewModel.uninstallApp(app.packageName) }
        )

        // ⭐ Grid launcher for virtual apps
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerView.adapter = adapter

        // ⭐ Install button opens device apps list
        binding.fabAdd.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.viewPager, // Activity container
                    DeviceAppsFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        viewModel.apps.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility =
                if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.loadApps()
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

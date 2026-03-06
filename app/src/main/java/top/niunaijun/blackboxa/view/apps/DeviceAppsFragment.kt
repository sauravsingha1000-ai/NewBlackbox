package top.niunaijun.blackboxa.view.apps

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import top.niunaijun.blackboxa.databinding.FragmentDeviceAppsBinding

class DeviceAppsFragment : Fragment() {

    private var _binding: FragmentDeviceAppsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppsViewModel by viewModels {
        AppsFactory(requireActivity().application)
    }

    private lateinit var adapter: DeviceAppsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDeviceAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DeviceAppsAdapter(
            apps = emptyList(),
            pm = requireContext().packageManager
        ) { packageName ->

            viewModel.installFromDevice(packageName)

            Snackbar.make(
                binding.root,
                "Installing...",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.deviceApps.observe(viewLifecycleOwner) { apps ->
            adapter = DeviceAppsAdapter(
                apps,
                requireContext().packageManager
            ) { packageName ->

                viewModel.installFromDevice(packageName)

                Snackbar.make(
                    binding.root,
                    "Installing...",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            binding.recyclerView.adapter = adapter
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.loadDeviceApps()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package top.niunaijun.blackboxa.view.gms

import android.os.Bundle
import androidx.activity.viewModels
import top.niunaijun.blackboxa.databinding.ActivityGmsBinding
import top.niunaijun.blackboxa.view.base.BaseActivity

class GmsManagerActivity : BaseActivity<ActivityGmsBinding>() {
    private val viewModel: GmsViewModel by viewModels { GmsFactory(application) }
    private lateinit var adapter: GmsAdapter

    override fun getViewBinding() = ActivityGmsBinding.inflate(layoutInflater)

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GMS Manager"
        adapter = GmsAdapter { gms, enabled -> viewModel.setGmsEnabled(gms.packageName, enabled) }
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        viewModel.apps.observe(this) { adapter.submitList(it) }
        viewModel.loadApps()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}

package top.niunaijun.blackboxa.view.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.view.base.BaseActivity

class ListActivity : BaseActivity<ActivityListBinding>() {
    private val viewModel: ListViewModel by viewModels { ListFactory(application) }
    private lateinit var adapter: AppListAdapter

    override fun getViewBinding() = ActivityListBinding.inflate(layoutInflater)

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = AppListAdapter { app ->
            val result = Intent().apply { putExtra("apkPath", app.packageName) }
            setResult(Activity.RESULT_OK, result)
            finish()
        }
        binding.recyclerView.adapter = adapter
    }

    override fun initData() {
        viewModel.apps.observe(this) { adapter.submitList(it) }
        viewModel.loading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.loadDeviceApps()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}

package top.niunaijun.blackboxa.view.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityMainBinding
import top.niunaijun.blackboxa.view.apps.AppsFragment
import top.niunaijun.blackboxa.view.base.BaseActivity
import top.niunaijun.blackboxa.view.setting.SettingActivity
import java.io.File
import java.io.FileOutputStream

class MainActivity : BaseActivity<ActivityMainBinding>() {

private val requestPermission =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

// File picker result
private val pickApk =
    registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { installApk(it) }
    }

override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

override fun initView() {

    setSupportActionBar(binding.toolbar)
    supportActionBar?.title = "TeristaSpace"

    supportFragmentManager.beginTransaction()
        .replace(R.id.container, AppsFragment())
        .commit()
}

override fun initData() {
    requestPermissions()
}

private fun requestPermissions() {

    val perms = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        perms.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        perms.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    requestPermission.launch(perms.toTypedArray())
}

override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {

    return when (item.itemId) {

        R.id.action_settings -> {
            startActivity(Intent(this, SettingActivity::class.java))
            true
        }

        // Storage install
        R.id.action_storage -> {

            pickApk.launch("*/*")

            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}

// Install APK inside TeristaSpace
private fun installApk(uri: Uri) {

    try {

        val input = contentResolver.openInputStream(uri) ?: return

        val file = File(cacheDir, "install.apk")
        val output = FileOutputStream(file)

        input.copyTo(output)

        input.close()
        output.close()

        // Install inside virtual engine
        BlackBoxCore.get().installPackageAsUser(file.absolutePath, 0)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

}

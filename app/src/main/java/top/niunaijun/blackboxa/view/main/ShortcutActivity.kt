package top.niunaijun.blackboxa.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import top.niunaijun.blackboxa.app.AppManager

class ShortcutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pkg = intent.getStringExtra("packageName")
        if (pkg != null) AppManager.launchApp(pkg)
        finish()
    }
}

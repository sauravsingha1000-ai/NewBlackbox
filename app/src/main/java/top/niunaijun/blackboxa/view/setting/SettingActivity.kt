package top.niunaijun.blackboxa.view.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import top.niunaijun.blackboxa.R

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingFragment())
            .commit()
    }
    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}

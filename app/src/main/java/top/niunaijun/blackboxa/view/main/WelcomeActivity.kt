package top.niunaijun.blackboxa.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Navigate straight to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

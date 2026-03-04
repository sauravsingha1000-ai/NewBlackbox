package top.niunaijun.blackboxa.util

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import top.niunaijun.blackboxa.view.main.ShortcutActivity

object ShortcutUtil {

    fun createShortcut(context: Context, packageName: String, label: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return
        val sm = context.getSystemService(ShortcutManager::class.java) ?: return
        val intent = Intent(context, ShortcutActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("packageName", packageName)
        }
        val shortcut = ShortcutInfo.Builder(context, packageName)
            .setShortLabel(label)
            .setLongLabel(label)
            .setIntent(intent)
            .setIcon(Icon.createWithResource(context, android.R.drawable.ic_menu_manage))
            .build()
        sm.requestPinShortcut(shortcut, null)
    }
}

package top.niunaijun.blackboxa.view.install

import android.content.Context
import androidx.appcompat.app.AlertDialog

object InstallerDialog {

    fun show(context: Context, fileName: String, onInstall: () -> Unit) {

        val options = arrayOf(
            "Install",
            "Cancel"
        )

        AlertDialog.Builder(context)
            .setTitle("Install Package")
            .setMessage(fileName)
            .setItems(options) { dialog, which ->

                when (which) {

                    0 -> onInstall()

                    else -> dialog.dismiss()
                }
            }
            .show()
    }
}

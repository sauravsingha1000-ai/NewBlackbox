package top.niunaijun.blackboxa.view.install

import android.content.Context
import androidx.appcompat.app.AlertDialog

object InstallerDialog {

    fun show(
        context: Context,
        fileName: String,
        onInstall: () -> Unit
    ) {

        val options = arrayOf(
            "Install",
            "Cancel"
        )

        AlertDialog.Builder(context)
            .setTitle("Install Package")
            .setMessage("Do you want to install:\n\n$fileName\n\ninside TeristaSpace?")
            .setItems(options) { dialog, which ->

                when (which) {

                    0 -> {
                        onInstall()
                    }

                    else -> {
                        dialog.dismiss()
                    }
                }
            }
            .setCancelable(true)
            .show()
    }
}

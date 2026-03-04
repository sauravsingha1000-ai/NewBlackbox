package top.niunaijun.blackboxa.view.base

import android.app.ProgressDialog
import androidx.viewbinding.ViewBinding

@Suppress("DEPRECATION")
abstract class LoadingActivity<VB : ViewBinding> : BaseActivity<VB>() {
    private var progressDialog: ProgressDialog? = null

    fun showLoading(message: String = "Loading...") {
        progressDialog?.dismiss()
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}

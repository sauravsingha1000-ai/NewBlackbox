package top.niunaijun.blackboxa.widget

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * Floating overlay window for in-virtual-app controls.
 */
class EnFloatView(context: Context) : FrameLayout(context) {
    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var attached = false

    private val params = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.TOP or Gravity.START
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
    }

    fun show() {
        if (!attached) {
            wm.addView(this, params)
            attached = true
        }
    }

    fun hide() {
        if (attached) {
            wm.removeView(this)
            attached = false
        }
    }
}

package top.niunaijun.blackboxa.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object ResUtil {
    fun getDrawable(context: Context, resId: Int): Drawable? =
        ContextCompat.getDrawable(context, resId)

    fun getColor(context: Context, resId: Int): Int =
        ContextCompat.getColor(context, resId)

    fun getColorStateList(context: Context, resId: Int): ColorStateList? =
        ContextCompat.getColorStateList(context, resId)
}

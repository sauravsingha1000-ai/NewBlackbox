package top.niunaijun.blackboxa.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

inline fun <reified VB : ViewBinding> ViewGroup.inflate(
    inflateMethod: (LayoutInflater, ViewGroup, Boolean) -> VB
): VB = inflateMethod(LayoutInflater.from(context), this, false)

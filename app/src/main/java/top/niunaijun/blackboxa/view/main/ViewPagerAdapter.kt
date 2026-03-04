package top.niunaijun.blackboxa.view.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import top.niunaijun.blackboxa.view.apps.AppsFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val tabs = listOf(
        "Apps" to AppsFragment()
    )

    override fun getItemCount() = tabs.size
    override fun createFragment(position: Int): Fragment = tabs[position].second
    fun getTitle(position: Int): String = tabs[position].first
}

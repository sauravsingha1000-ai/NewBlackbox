package top.niunaijun.blackboxa.data

import top.niunaijun.blackboxa.bean.AppInfo

object AppsSortCompon {
    fun sortByName(list: List<AppInfo>): List<AppInfo> =
        list.sortedBy { it.appName.lowercase() }

    fun sortByInstallTime(list: List<AppInfo>): List<AppInfo> = list
}

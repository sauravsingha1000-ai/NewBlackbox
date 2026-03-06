package top.niunaijun.blackboxa.core

import android.content.Context
import java.io.File

object VirtualStorageManager {

    private const val ROOT = "virtual"

    fun getRoot(context: Context): File {

        val root = File(context.getExternalFilesDir(null), ROOT)

        if (!root.exists()) {
            root.mkdirs()
        }

        return root
    }

    fun getAppsDir(context: Context): File {

        val dir = File(getRoot(context), "apps")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun getAppDir(context: Context, packageName: String): File {

        val dir = File(getAppsDir(context), packageName)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun getDataDir(context: Context): File {

        val dir = File(getRoot(context), "data")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun getAppDataDir(context: Context, packageName: String): File {

        val dir = File(getDataDir(context), packageName)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun getVirtualStorage(context: Context): File {

        val dir = File(getRoot(context), "storage/emulated/0")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun createDefaultFolders(context: Context) {

        val storage = getVirtualStorage(context)

        listOf(
            "Download",
            "DCIM",
            "Pictures",
            "Movies",
            "Music",
            "Documents"
        ).forEach {

            val folder = File(storage, it)

            if (!folder.exists()) {
                folder.mkdirs()
            }
        }
    }
}

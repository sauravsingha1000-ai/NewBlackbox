package top.niunaijun.blackboxa.core

import android.content.Context
import java.io.File

object VirtualStorage {

    fun getVirtualRoot(context: Context): File {

        val root = File(
            context.getExternalFilesDir(null),
            "virtual"
        )

        if (!root.exists()) {
            root.mkdirs()
        }

        return root
    }

    fun getUserDir(context: Context): File {

        val user = File(
            getVirtualRoot(context),
            "data/user/0"
        )

        if (!user.exists()) {
            user.mkdirs()
        }

        return user
    }

    fun getVirtualStorage(context: Context): File {

        val storage = File(
            getVirtualRoot(context),
            "storage/emulated/0"
        )

        if (!storage.exists()) {
            storage.mkdirs()
        }

        return storage
    }
}

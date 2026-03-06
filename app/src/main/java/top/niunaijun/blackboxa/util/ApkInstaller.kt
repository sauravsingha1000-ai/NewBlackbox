package top.niunaijun.blackboxa.util

import java.io.File
import java.util.zip.ZipFile

object ApkInstaller {

    fun getType(path: String): String {

        return when {

            path.endsWith(".apk", true) -> "APK"

            path.endsWith(".apks", true) -> "APKS"

            path.endsWith(".xapk", true) -> "XAPK"

            path.endsWith(".apkm", true) -> "APKM"

            else -> "UNKNOWN"
        }
    }

    fun extractBundle(path: String, output: File) {

        val zip = ZipFile(path)

        zip.entries().asSequence().forEach { entry ->

            val file = File(output, entry.name)

            if (entry.isDirectory) {

                file.mkdirs()

            } else {

                file.parentFile?.mkdirs()

                zip.getInputStream(entry).use { input ->

                    file.outputStream().use { outputStream ->

                        input.copyTo(outputStream)
                    }
                }
            }
        }
    }
}

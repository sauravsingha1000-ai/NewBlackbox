package top.niunaijun.blackboxa.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

object BundleInstaller {

    fun detectType(path: String): String {

        return when {

            path.endsWith(".apk", true) -> "APK"

            path.endsWith(".apks", true) -> "APKS"

            path.endsWith(".xapk", true) -> "XAPK"

            path.endsWith(".apkm", true) -> "APKM"

            else -> "UNKNOWN"
        }
    }

    fun extractBundle(context: Context, path: String): File? {

        return try {

            val outputDir = File(
                context.getExternalFilesDir(null),
                "bundle_temp"
            )

            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val zip = ZipFile(path)

            zip.entries().asSequence().forEach { entry ->

                val file = File(outputDir, entry.name)

                if (entry.isDirectory) {

                    file.mkdirs()

                } else {

                    file.parentFile?.mkdirs()

                    zip.getInputStream(entry).use { input ->

                        FileOutputStream(file).use { output ->

                            input.copyTo(output)
                        }
                    }
                }
            }

            outputDir

        } catch (e: Exception) {

            Log.e("BundleInstaller", "Extraction failed", e)

            null
        }
    }
}

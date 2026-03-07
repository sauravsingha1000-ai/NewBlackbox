package top.niunaijun.blackboxa.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

object BundleInstaller {

    private const val TAG = "BundleInstaller"

    /**
     * Detect bundle type
     */
    fun detectType(path: String): String {

        return when {

            path.endsWith(".apk", true) -> "APK"

            path.endsWith(".apks", true) -> "APKS"

            path.endsWith(".xapk", true) -> "XAPK"

            path.endsWith(".apkm", true) -> "APKM"

            else -> "UNKNOWN"
        }
    }

    /**
     * Extract bundle packages (APKS / XAPK / APKM)
     */
    fun extractBundle(context: Context, path: String): File? {

        return try {

            val outputDir = File(
                context.getExternalFilesDir(null),
                "bundle_temp"
            )

            // Clean previous extraction
            if (outputDir.exists()) {
                outputDir.deleteRecursively()
            }

            outputDir.mkdirs()

            val zip = ZipFile(path)

            zip.entries().asSequence().forEach { entry ->

                val file = File(outputDir, entry.name)

                // Security check (Zip Slip protection)
                if (!file.canonicalPath.startsWith(outputDir.canonicalPath)) {
                    throw SecurityException("Zip path traversal detected")
                }

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

            zip.close()

            Log.d(TAG, "Bundle extracted to: ${outputDir.absolutePath}")

            outputDir

        } catch (e: Exception) {

            Log.e(TAG, "Extraction failed", e)

            null
        }
    }
}

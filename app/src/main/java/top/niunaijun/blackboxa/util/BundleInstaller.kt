package top.niunaijun.blackboxa.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile

object BundleInstaller {

    private const val TAG = "BundleInstaller"

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
     * Extract bundle safely
     */
    fun extractBundle(context: Context, path: String): File? {

        return try {

            val outputDir = File(context.cacheDir, "bundle_temp")

            if (outputDir.exists()) {
                outputDir.deleteRecursively()
            }

            outputDir.mkdirs()

            val zip = ZipFile(path)

            zip.entries().asSequence().forEach { entry ->

                val file = File(outputDir, entry.name)

                if (!file.canonicalPath.startsWith(outputDir.canonicalPath)) {
                    throw SecurityException("Zip traversal detected")
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

            outputDir

        } catch (e: Exception) {

            Log.e(TAG, "Bundle extraction failed", e)

            null
        }
    }

    /**
     * Find all APK files (base + splits)
     */
    fun findApkFiles(folder: File): List<File> {

        val list = mutableListOf<File>()

        folder.walkTopDown().forEach {

            if (it.isFile && it.extension.equals("apk", true)) {

                list.add(it)
            }
        }

        return list
    }

    /**
     * Auto detect base APK
     */
    fun findBaseApk(apkFiles: List<File>): File? {

        apkFiles.forEach {

            if (it.name.equals("base.apk", true)) {

                return it
            }
        }

        return apkFiles.firstOrNull()
    }

    /**
     * Detect split APK files
     */
    fun findSplitApks(apkFiles: List<File>): List<File> {

        return apkFiles.filter {

            it.name.startsWith("split") ||
            it.name.contains("config", true)
        }
    }

    /**
     * Extract OBB files (XAPK games)
     */
    fun extractObb(context: Context, bundleDir: File) {

        try {

            val obbFiles = bundleDir.walkTopDown().filter {

                it.extension.equals("obb", true)

            }.toList()

            if (obbFiles.isEmpty()) return

            val obbRoot = File(context.getExternalFilesDir(null), "obb")

            if (!obbRoot.exists()) {
                obbRoot.mkdirs()
            }

            obbFiles.forEach { obb ->

                val target = File(obbRoot, obb.name)

                obb.copyTo(target, overwrite = true)

                Log.d(TAG, "OBB copied: ${target.absolutePath}")
            }

        } catch (e: Exception) {

            Log.e(TAG, "OBB extraction failed", e)
        }
    }
}

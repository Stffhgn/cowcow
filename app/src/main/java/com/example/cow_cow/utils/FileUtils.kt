package com.example.cow_cow.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL

object FileUtils {

    /**
     * Download a file from a given URL and save it locally.
     */
    @Throws(IOException::class)
    fun downloadFile(context: Context, fileUrl: String, fileName: String): File {
        val url = URL(fileUrl)
        val connection = url.openConnection()
        connection.connect()

        val input: InputStream = connection.getInputStream()
        val file = File(context.cacheDir, fileName)
        val output: FileOutputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } != -1) {
            output.write(buffer, 0, length)
        }

        output.flush()
        output.close()
        input.close()

        return file
    }

    /**
     * Get a file from the app’s resources.
     */
    @Throws(IOException::class)
    fun getFileFromResource(context: Context, resId: Int): File {
        val inputStream = context.resources.openRawResource(resId)
        val file = File(context.cacheDir, context.resources.getResourceEntryName(resId))
        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        return file
    }

    /**
     * Clear the local cache directory.
     */
    fun clearLocalCache(context: Context) {
        val cacheDir = context.cacheDir
        if (cacheDir.isDirectory) {
            cacheDir.listFiles()?.forEach { it.delete() }
        }
    }
}

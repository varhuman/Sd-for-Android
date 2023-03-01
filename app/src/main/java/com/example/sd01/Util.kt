package com.example.sd01

import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


object Util {
    public fun saveToInternalStorage(bitmapImage: Bitmap, imageName:String, context:Context): String? {
//        val cw = ContextWrapper(getApplicationContext())
//        .getDir()
        // path to /data/data/yourapp/app_data/imageDir
        val directory: File = context.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val imgPath = File(directory, "${imageName}.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imgPath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.getAbsolutePath()
    }

    fun saveToGallery(bitmap: Bitmap,albumName: String , context: Context) {
        val filename = "${System.currentTimeMillis()}.png"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/$albumName")
            }

            context.contentResolver.let {
                it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                    it.openOutputStream(uri)?.let(write)
                }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, filename)
            write(FileOutputStream(image))
        }
    }
}
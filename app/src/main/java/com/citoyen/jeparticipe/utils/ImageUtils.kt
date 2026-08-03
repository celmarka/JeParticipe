package com.citoyen.jeparticipe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {

    // Convertir File en Base64
    fun fileToBase64(file: File): String {
        val bytes = file.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // Compresser l'image et retourner en Base64
    fun compressAndEncode(file: File, maxWidth: Int = 1024, quality: Int = 80): String {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        var sampleSize = 1
        while (options.outWidth / sampleSize > maxWidth) {
            sampleSize *= 2
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = sampleSize

        val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

        // ✅ Vérifier que bitmap n'est pas null
        if (bitmap == null) {
            return fileToBase64(file) // Retourner l'image non compressée si erreur
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val bytes = outputStream.toByteArray()

        bitmap.recycle()

        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // Compresser depuis Uri
    fun compressAndEncodeFromUri(context: Context, uri: Uri, maxWidth: Int = 1024, quality: Int = 80): String? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        // Si l'image est trop petite, on la lit directement
        if (options.outWidth <= maxWidth) {
            val directStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(directStream)
            directStream.close()

            if (bitmap == null) {
                return null
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val bytes = outputStream.toByteArray()
            bitmap.recycle()
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        var sampleSize = 1
        while (options.outWidth / sampleSize > maxWidth) {
            sampleSize *= 2
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = sampleSize

        val newInputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(newInputStream, null, options)
        newInputStream.close()

        // ✅ Vérifier que bitmap n'est pas null
        if (bitmap == null) {
            return null
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val bytes = outputStream.toByteArray()

        bitmap.recycle()

        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
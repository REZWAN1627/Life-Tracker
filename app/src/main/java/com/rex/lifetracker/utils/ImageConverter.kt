package com.rex.lifetracker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.ByteArrayOutputStream

object ImageConverter {
    fun getByte(image: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        val bitmap = image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()

    }

    suspend fun getBitmap(imageUri: String, requireContext: Context): Bitmap {
        Log.d(Constant.TAG, "getBitmap: $imageUri")
        val loading = ImageLoader(requireContext)
        val request = ImageRequest.Builder(requireContext)
            .data(imageUri)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }
}
package com.travelmeet.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream

object ImageCompressionUtils {
    private const val TAG = "ImageCompressionUtils"

    /**
     * Compresses an image from URI to bytes with optimized sampling and quality reduction
     * @param context Context for content resolver
     * @param uri Image URI to compress
     * @param maxWidth Maximum width for sampling (default 1080)
     * @param maxHeight Maximum height for sampling (default 1080)
     * @param maxSizeKb Maximum size in KB (default 500)
     * @param quality Initial JPEG quality (default 80)
     * @return Compressed image bytes
     */
    fun compressImage(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1080,
        maxHeight: Int = 1080,
        maxSizeKb: Int = Constants.MAX_IMAGE_SIZE_KB,
        quality: Int = Constants.IMAGE_QUALITY
    ): ByteArray {
        try {
            // First pass: decode bounds to determine sample size
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Calculate optimal sample size
            val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight, maxWidth, maxHeight)

            // Second pass: decode with sample size
            val inputStream2 = context.contentResolver.openInputStream(uri)
            val options2 = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            var bitmap = BitmapFactory.decodeStream(inputStream2, null, options2)
            inputStream2?.close()

            if (bitmap == null) {
                Log.e(TAG, "Failed to decode image, falling back to raw bytes")
                return readRawBytes(context, uri)
            }

            // Compress with quality adjustment
            val outputStream = ByteArrayOutputStream()
            var currentQuality = quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, outputStream)

            // Reduce quality if still too large
            while (outputStream.toByteArray().size > maxSizeKb * 1024 && currentQuality > 10) {
                outputStream.reset()
                currentQuality -= 10
                bitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, outputStream)
            }

            bitmap.recycle()
            Log.d(TAG, "Image compressed: original size unknown, final size: ${outputStream.toByteArray().size / 1024}KB")
            return outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e(TAG, "Error during image compression: ${e.message}", e)
            return readRawBytes(context, uri)
        }
    }

    /**
     * Calculates optimal sample size for bitmap decoding
     */
    private fun calculateInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Reads raw bytes from URI without compression (fallback)
     */
    private fun readRawBytes(context: Context, uri: Uri): ByteArray {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading raw bytes: ${e.message}", e)
            ByteArray(0)
        }
    }
}


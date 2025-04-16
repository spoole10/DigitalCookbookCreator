package com.saralynpoole.digitalcookbookcreator.application

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.ScriptIntrinsicConvolve3x3
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Manager for text recognition using the Google ML Kit.
 */
class TextRecognitionManager(private val context: Context) {

    companion object {
        private const val TAG = "TextRecognitionManager"
        private const val VALID_IMAGE_EXTENSIONS = "jpg|jpeg|png|bmp"

        private const val TEXT_EXTRACTION_TAG = "ExtractedText"
    }

    // Initialize the text recognizer
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Recognizes text from an image URI.
    suspend fun recognizeText(imageUri: Uri): Result<Text> = withContext(Dispatchers.IO) {
        try {
            // Check if the file format is valid
            val path = imageUri.path ?: ""
            if (!path.matches(".*\\.($VALID_IMAGE_EXTENSIONS)$".toRegex(RegexOption.IGNORE_CASE))) {
                return@withContext Result.failure(
                    FileFormatException("Invalid file format. Only JPG, PNG, and BMP are supported.")
                )
            }

            // Prepare the image for text recognition
            val inputImage = getInputImage(imageUri)
                ?: return@withContext Result.failure(IOException("Failed to create input image"))

            // Perform text recognition
            val result = suspendCancellableCoroutine<Text> { continuation ->
                recognizer.process(inputImage)
                    .addOnSuccessListener { text ->
                        // Log the extracted text
                        logExtractedText(text)
                        continuation.resume(text)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Text recognition failed", exception)
                        continuation.resumeWithException(exception)
                    }
            }

            // Check if any text was recognized
            if (result.text.isEmpty()) {
                Log.w(TAG, "No text recognized in the image")
            }

            Result.success(result)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Image file not found", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e(TAG, "Error processing image", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Text recognition failed", e)
            Result.failure(e)
        }
    }

    // Creates an InputImage from URI
    private fun getInputImage(uri: Uri): InputImage? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Apply preprocessing to improve text recognition
                val processedBitmap = preprocessImage(bitmap)

                InputImage.fromBitmap(processedBitmap, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create input image", e)
            null
        }
    }

    private fun preprocessImage(original: Bitmap): Bitmap {
        try {
            // Create a mutable copy of the bitmap
            val processed = original.copy(Bitmap.Config.ARGB_8888, true)

            // Apply grayscale for better text detection
            val colorMatrix = ColorMatrix().apply {
                setSaturation(0f) // Convert to grayscale
            }

            // Adjust contrast to make text stand out more
            colorMatrix.postConcat(ColorMatrix().apply {
                // Increase contrast
                setScale(1.2f, 1.2f, 1.2f, 1f)
            })

            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(colorMatrix)
            }

            val canvas = Canvas(processed)
            canvas.drawBitmap(processed, 0f, 0f, paint)

            // Apply a slight Gaussian blur followed by sharpening for noise reduction
            val rs = RenderScript.create(context)
            val input = Allocation.createFromBitmap(rs, processed)
            val output = Allocation.createTyped(rs, input.type)

            // First blur slightly
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setInput(input)
            // Very slight blur
            blurScript.setRadius(1.0f)
            blurScript.forEach(output)

            // Then apply sharpening
            val matrix = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
            matrix.setInput(output)
            // Sharpening kernel
            matrix.setCoefficients(floatArrayOf(
                0f, -1f, 0f,
                -1f, 5f, -1f,
                0f, -1f, 0f
            ))
            matrix.forEach(input)

            output.copyTo(processed)

            // Clean up
            rs.destroy()

            return processed
        } catch (e: Exception) {
            Log.e(TAG, "Image preprocessing failed", e)
            // Return original if processing fails
            return original
        }
    }


    // Logs the extracted text with detailed structure
    private fun logExtractedText(text: Text) {
        // First log the complete text
        Log.d(TEXT_EXTRACTION_TAG, "======= COMPLETE EXTRACTED TEXT =======")
        Log.d(TEXT_EXTRACTION_TAG, text.text)
        Log.d(TEXT_EXTRACTION_TAG, "=======================================")

        // Then log the structured text (blocks, lines, elements)
        Log.d(TEXT_EXTRACTION_TAG, "======= STRUCTURED TEXT BLOCKS =======")
        text.textBlocks.forEachIndexed { blockIdx, block ->
            Log.d(TEXT_EXTRACTION_TAG, "BLOCK #$blockIdx: ${block.text}")

            block.lines.forEachIndexed { lineIdx, line ->
                Log.d(TEXT_EXTRACTION_TAG, "  LINE #$lineIdx: ${line.text}")

                line.elements.forEachIndexed { elementIdx, element ->
                    Log.d(TEXT_EXTRACTION_TAG, "    ELEMENT #$elementIdx: ${element.text}")
                }
            }
        }
        Log.d(TEXT_EXTRACTION_TAG, "=======================================")
    }

    // Exception thrown when the file format is invalid
    class FileFormatException(message: String) : Exception(message)
}
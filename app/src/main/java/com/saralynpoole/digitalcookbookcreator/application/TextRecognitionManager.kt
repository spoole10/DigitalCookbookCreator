package com.saralynpoole.digitalcookbookcreator.application

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
                InputImage.fromBitmap(bitmap, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create input image", e)
            null
        }
    }

    // Exception thrown when the file format is invalid
    class FileFormatException(message: String) : Exception(message)
}
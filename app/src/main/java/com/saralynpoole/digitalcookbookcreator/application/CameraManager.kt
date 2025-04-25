package com.saralynpoole.digitalcookbookcreator.application

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Manager for camera operations using CameraX API.
 */
class CameraManager(private val context: Context) {

    private var imageCapture: ImageCapture? = null
    private val executor = ContextCompat.getMainExecutor(context)

    companion object {
        private const val TAG = "CameraManager"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val MAX_FILE_SIZE_MB = 10 // 10MB max file size
        // Uncomment to test file size limit exception
        //private const val MAX_FILE_SIZE_MB = 0.01 // 100 KB max file size
    }

    // Initializes the camera and binds the camera preview and capture use cases to the provided
    // PreviewView and LifeCycleOwner.
    suspend fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: androidx.camera.view.PreviewView
    ): Result<Unit> = withContext(Dispatchers.Main) {
        return@withContext try {
            // Request a camera provider asynchronously
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val cameraProvider = suspendCoroutine<ProcessCameraProvider> { continuation ->
                cameraProviderFuture.addListener({
                    continuation.resume(cameraProviderFuture.get())
                }, executor)
            }

            // Build the camera preview use case
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Build the image capture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(android.util.Size(1600, 1920))
                .build()
           /* imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
            */
            // Select back camera as default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Clear any previous use cases to prevent conflicts
            cameraProvider.unbindAll()

            // Bind the lifecycle, camera selector, preview, and image capture to the camera provider
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            Result.success(Unit)
        } catch (exc: Exception) {
            // Log and return failure if binding failed
            Log.e(TAG, "Use case binding failed", exc)
            Result.failure(exc)
        }
    }

    // Captures a photo and returns the file URI.
    suspend fun takePhoto(): Result<Uri> = withContext(Dispatchers.IO) {
        val imageCapture = imageCapture ?: return@withContext Result.failure(
            IllegalStateException("Camera not initialized")
        )

        // Create a temporary file with a timestamp-based name
        val photoFile = File(
            context.cacheDir,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Set up output options for where to save the photo
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Take the picture asynchronously
        return@withContext suspendCoroutine { continuation ->
            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)

                        // Check file size
                        val fileSize = photoFile.length().toDouble() / (1024 * 1024) // Size in MB
                        // If the file size exceeds the limit, delete the file and return an error
                        if (fileSize > MAX_FILE_SIZE_MB) {
                            continuation.resume(Result.failure(
                                FileSizeLimitExceededException("File size exceeds $MAX_FILE_SIZE_MB MB")
                            ))
                            // Delete the oversized file
                            photoFile.delete()
                            return
                        }

                        // Otherwise, return success with the file URI
                        continuation.resume(Result.success(savedUri))
                        Log.d(TAG, "Photo saved: $savedUri")
                        Log.d("CameraManager", "Captured file size = ${photoFile.length() / 1024} KB")

                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Return an error if photo capture fails
                        Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                        continuation.resume(Result.failure(exception))
                    }
                }
            )
        }
    }

    // Exception thrown when file size exceeds the limit.
    class FileSizeLimitExceededException(message: String) : Exception(message)

    // Exception thrown when file format is invalid.
    class FileFormatException(message: String) : Exception(message)
}
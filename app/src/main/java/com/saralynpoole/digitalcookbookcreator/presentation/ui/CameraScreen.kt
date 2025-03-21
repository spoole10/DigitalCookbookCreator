package com.saralynpoole.digitalcookbookcreator.presentation.ui

import android.util.Log
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.saralynpoole.digitalcookbookcreator.application.CameraManager
import com.saralynpoole.digitalcookbookcreator.application.RecipeFormatter
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel
import com.saralynpoole.digitalcookbookcreator.application.TextRecognitionManager
import kotlinx.coroutines.launch

private const val TAG = "CameraScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: RecipeViewModel,
    onPhotoTaken: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember { CameraManager(context) }
    val textRecognitionManager = remember { TextRecognitionManager(context) }
    val recipeFormatter = remember { RecipeFormatter() }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isCameraInitialized by remember { mutableStateOf(false) }

    // Request camera permission
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    // Check if camera permission is granted
    LaunchedEffect(cameraPermissionState) {
        if (!cameraPermissionState.status.isGranted &&
            !cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cameraPermissionState.status.isGranted) {
                // Camera preview
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }

                        // Start camera when the view is created
                        scope.launch {
                            val result = cameraManager.startCamera(lifecycleOwner, previewView)
                            result.onSuccess {
                                isCameraInitialized = true
                            }.onFailure { error ->
                                Log.e(TAG, "Failed to start camera", error)
                                snackbarHostState.showSnackbar("Failed to start camera: ${error.message}")
                            }
                        }

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Back button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                // Capture button
                FloatingActionButton(
                    onClick = {
                        if (isCameraInitialized) {
                            scope.launch {
                                // Take photo
                                val photoResult = cameraManager.takePhoto()
                                photoResult.onSuccess { uri ->
                                    try {
                                        // Recognize text
                                        val textResult = textRecognitionManager.recognizeText(uri)
                                        textResult.onSuccess { text ->
                                            // Format into recipe
                                            val formattedRecipe = recipeFormatter.formatRecipe(text)

                                            // Update viewModel
                                            viewModel.updateTitle(formattedRecipe.title)
                                            viewModel.updateDescription(formattedRecipe.description)

                                            // Clear existing ingredients/steps
                                            val currentIngredients = viewModel.ingredients.value
                                            val currentSteps = viewModel.steps.value

                                            // Add new ingredients from formatted recipe
                                            formattedRecipe.ingredients.forEachIndexed { index, ingredient ->
                                                viewModel.updateIngredient(
                                                    index,
                                                    ingredient.name,
                                                    ingredient.quantity
                                                )
                                            }

                                            // Add new steps from formatted recipe
                                            formattedRecipe.steps.forEachIndexed { index, step ->
                                                viewModel.updateStep(index, step)
                                            }

                                            onPhotoTaken()
                                        }.onFailure { error ->
                                            snackbarHostState.showSnackbar(
                                                when (error) {
                                                    is TextRecognitionManager.FileFormatException ->
                                                        "File format error: ${error.message}"
                                                    else -> "Failed to recognize text: ${error.message}"
                                                }
                                            )
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error processing image", e)
                                        snackbarHostState.showSnackbar("Error processing image: ${e.message}")
                                    }
                                }.onFailure { error ->
                                    snackbarHostState.showSnackbar(
                                        when (error) {
                                            is CameraManager.FileSizeLimitExceededException ->
                                                "File size too large: ${error.message}"
                                            is CameraManager.FileFormatException ->
                                                "File format error: ${error.message}"
                                            else -> "Failed to take photo: ${error.message}"
                                        }
                                    )
                                }
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Camera is not ready yet")
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Take photo",
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                // Camera permission not granted
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Camera permission is required to take photos of recipes",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
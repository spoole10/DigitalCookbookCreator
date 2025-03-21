package com.saralynpoole.digitalcookbookcreator.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Create a new recipe screen.
 */
@Composable
fun CreateNewRecipeScreen(
    // Navigation functions
    navigateToManuallyInputRecipe: () -> Unit,
    navigateToFormatRecipe: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCamera: () -> Unit
) {
    // Background for the screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title text at the top of the screen
            Text(
                text = "Create a new recipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Button to manually input a recipe
                Button(
                    onClick = navigateToManuallyInputRecipe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    Text(
                        text = "Manually input recipe",
                        fontSize = 16.sp
                    )
                }

                // Button to navigate to the camera.
                Button(
                    onClick = navigateToCamera,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Camera, contentDescription = "Camera")
                    Text("Take a picture of a recipe")
                }

                // Button to navigate back to the home screen
                Button(
                    onClick = navigateToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    Text(
                        text = "Home",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
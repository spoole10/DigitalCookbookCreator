package com.saralynpoole.digitalcookbookcreator.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel

/**
 * Delete a recipe screen.
 */
@Composable
fun DeleteRecipeScreen(
    // View model for the recipe
    viewModel: RecipeViewModel,

    // Event handlers
    onConfirmDelete: () -> Unit,
    onCancel: () -> Unit
) {
    // Collect states from the viewModel
    val recipeTitle by viewModel.recipeTitle.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
                text = "Delete recipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )
            // Show error message if it exists
            errorMessage?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            // Confirmation message text
            Text(
                text = "Are you sure you want to delete the recipe '$recipeTitle'?",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            // Adds some vertical space
            Spacer(modifier = Modifier.height(32.dp))

            // Row to arrange the buttons horizontally
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Button to confirm that the user wants to delete a recipe
                Button(
                    onClick = onConfirmDelete,
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("Yes")
                }
                // Button to cancel deleting a recipe
                Button(
                    onClick = onCancel,
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("No")
                }
            }
        }
    }
}
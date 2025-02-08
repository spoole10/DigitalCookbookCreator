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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Format a recipe screen.
 */
@Composable
fun FormatRecipeScreen(
    // Recipe properties
    title: String,
    description: String,
    ingredients: List<String>,
    steps: List<String>,
    // Event handlers
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIngredientChange: (Int, String) -> Unit,
    onStepChange: (Int, String) -> Unit,
    // Navigation functions
    onSaveRecipe: () -> Unit,
    onRetakeRecipePhoto: () -> Unit,
    onViewAllRecipes: () -> Unit
) {
    // Background for the screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Outer column to stack elements vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title text at the top of the screen
            Text(
                text = "Format recipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )

            // Column to contain input fields
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TextFields for the recipe title and description
                TextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Label for the ingredients section
                Text("Ingredients:")

                // Lists ingredient input fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ingredients.forEachIndexed { index, ingredient ->
                        // Text field for each ingredient
                        TextField(
                            value = ingredient,
                            onValueChange = { onIngredientChange(index, it) },
                            label = { Text("Ingredient ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Label for the steps section
                Text("Steps:")

                // Column to list step input fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.forEachIndexed { index, step ->
                        // Text field for each step
                        TextField(
                            value = step,
                            onValueChange = { onStepChange(index, it) },
                            label = { Text("Step ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Adds vertical space
            Spacer(modifier = Modifier.height(16.dp))

            // Column for the action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row to arrange the save and view all recipes buttons horizontally
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Button to save the recipe
                    Button(
                        onClick = onSaveRecipe,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp)
                            .padding(horizontal = 8.dp)  // Add padding between buttons
                    ) {
                        Text("Save recipe")
                    }
                    // Button to navigate to the view all recipes screen
                    Button(
                        onClick = onViewAllRecipes,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp)
                            .padding(horizontal = 8.dp)  // Add padding between buttons
                    ) {
                        Text("View all recipes")
                    }
                }
                // Button to retake the recipe photo
                // In a future release, this will be updated to open the camera
                Button(
                    onClick = onRetakeRecipePhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    Text("Retake recipe photo")
                }
            }
        }
    }
}

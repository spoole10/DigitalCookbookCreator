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
 * Manually input a recipe screen.
 */
@Composable
fun ManuallyInputRecipeScreen(
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
    // Nav functions
    onSaveRecipe: () -> Unit,
    onViewAllRecipes: () -> Unit
) {
    // Background for the screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Column to arrange the elements vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Displays the screen title
            Text(
                text = "Manually input a recipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )

            // Column to arrange the form fields with spacing
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
                Text("Ingredients:")

                // Arranges the ingredient fields with spacing
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ingredients.forEachIndexed { index, ingredient ->
                        // TextField for each ingredient
                        TextField(
                            value = ingredient,
                            onValueChange = { onIngredientChange(index, it) },
                            label = { Text("Ingredient ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Text("Steps:")

                // Column to arrange the step fields with spacing
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.forEachIndexed { index, step ->
                        // TextField for each step
                        TextField(
                            value = step,
                            onValueChange = { onStepChange(index, it) },
                            label = { Text("Step ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Adds some space between the form and the buttons
            Spacer(modifier = Modifier.height(16.dp))

            // Arranges the buttons horizontally
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Save recipe button
                Button(
                    onClick = onSaveRecipe,
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("Save recipe")
                }
                // View all recipes button
                Button(
                    onClick = onViewAllRecipes,
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("View all recipes")
                }
            }
        }
    }
}
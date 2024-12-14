package com.saralynpoole.digitalcookbookcreator.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * View a single recipe screen.
 */
@Composable
fun ViewSingleRecipeScreen(
    // Recipe properties
    title: String,
    description: String,
    ingredients: List<String>,
    steps: List<String>,
    // Nav function
    navigateToViewAllRecipes: () -> Unit
) {
    // Background for the screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Arranges the elements vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Displays the screen title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )
            // Displays the recipe description
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            // Adds some space between the description and the ingredients
            Spacer(modifier = Modifier.height(32.dp))

            // Displays the recipe ingredients
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            // Arranges the ingredients with spacing
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                ingredients.forEach { ingredient ->
                    // Displays each ingredient
                    Text(
                        text = "- $ingredient",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            // Adds some space between the ingredients and the steps
            Spacer(modifier = Modifier.height(32.dp))

            // Displays the recipe steps
            Text(
                text = "Steps:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            // Arranges the steps with spacing
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    // Displays each step
                    Text(
                        text = "${index + 1}. $step",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
            // Adds some space between the steps and the button
            Spacer(modifier = Modifier.height(32.dp))

            // Button to navigate back to the view all recipes screen
            Button(
                onClick = navigateToViewAllRecipes,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = "View all recipes",
                    fontSize = 16.sp
                )
            }
        }
    }
}
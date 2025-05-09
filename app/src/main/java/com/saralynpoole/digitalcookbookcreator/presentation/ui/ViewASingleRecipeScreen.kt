package com.saralynpoole.digitalcookbookcreator.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
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
 * View a single recipe screen.
 */
@Composable
fun ViewSingleRecipeScreen(
    viewModel: RecipeViewModel,
    recipeId: Int,
    navigateToViewAllRecipes: () -> Unit
) {
    // Collect the current recipe and loading state from the view model
    val currentRecipe by viewModel.currentRecipe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Load the recipe when the screen is first displayed
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            isLoading -> {
                // Display a loading indicator while the recipe is being loaded
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            currentRecipe == null -> {
                // Display a message when the recipe is not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Recipe not found",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = navigateToViewAllRecipes) {
                            Text("Return to recipes")
                        }
                    }
                }
            }
            else -> {
                // Display the recipe details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        // Display the recipe title
                        text = currentRecipe!!.recipe.recipeTitle,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp, top = 32.dp)
                    )

                    if (currentRecipe!!.recipe.recipeDescription.isNotBlank()) {
                        // Display the recipe description if available
                        Text(
                            text = currentRecipe!!.recipe.recipeDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    if (currentRecipe!!.ingredients.isNotEmpty()) {
                        // Display the ingredients list if available
                        Text(
                            text = "Ingredients:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            // Sort the ingredients by name
                            currentRecipe!!.ingredients.forEach { ingredient ->
                                Text(
                                    text = "- ${ingredient.quantity} ${ingredient.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    if (currentRecipe!!.steps.isNotEmpty()) {
                        // Display the steps if available
                        Text(
                            text = "Steps:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            // Sort the steps by step number
                            currentRecipe!!.steps
                                .sortedBy { it.stepNumber }
                                .forEach { step ->
                                    Text(
                                        text = "${step.stepNumber}. ${step.description}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 16.sp
                                        ),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Button to navigate back to the list of all recipes
                    Button(
                        onClick = navigateToViewAllRecipes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text(
                            text = "Back to all recipes",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
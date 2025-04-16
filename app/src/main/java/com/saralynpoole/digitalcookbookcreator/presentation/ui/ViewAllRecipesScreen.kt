package com.saralynpoole.digitalcookbookcreator.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeWithRelations
import com.saralynpoole.digitalcookbookcreator.presentation.theme.CardBackground

/**
 * View all recipes screen.
 */
@Composable
fun ViewAllRecipesScreen(
    viewModel: RecipeViewModel,
    onUpdateRecipe: (Int) -> Unit,
    onDeleteRecipe: (Int) -> Unit,
    onViewRecipe: (Int) -> Unit,
    navigateToHome: () -> Unit
) {
    // Collect states from the ViewModel
    val recipes by viewModel.allRecipes.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
                text = "View All Recipes",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )
            // Show error message if any
            errorMessage?.let { error ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Display message if no recipes are found
            if (recipes.isEmpty()) {
                Text(
                    text = "No recipes found. Create a new recipe to get started!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )
            } else {
                // Arranges the list of recipes with spacing
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(recipes) { recipeWithRelations ->
                        // Displays each recipe
                        RecipeItem(
                            recipe = recipeWithRelations,
                            onUpdateRecipe = onUpdateRecipe,
                            onDeleteRecipe = onDeleteRecipe,
                            onViewRecipe = onViewRecipe
                        )
                    }
                }
            }

            // Button to navigate back to the home screen
            Button(
                onClick = navigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(bottom = 50.dp)
            ) {
                Text(
                    text = "Home",
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Recipe item for the view all recipes screen.
 */
@Composable
fun RecipeItem(
    recipe: RecipeWithRelations,
    onUpdateRecipe: (Int) -> Unit,
    onDeleteRecipe: (Int) -> Unit,
    onViewRecipe: (Int) -> Unit
) {
    // Card for each recipe
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clickable { onViewRecipe(recipe.recipe.recipeId) },
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display the recipe title
            Text(
                text = recipe.recipe.recipeTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Display the recipe description
            Text(
                text = recipe.recipe.recipeDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Display the number of ingredients and steps
            Text(
                text = "${recipe.ingredients.size} ingredients • ${recipe.steps.size} steps",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                // Update button
                Button(
                    onClick = { onUpdateRecipe(recipe.recipe.recipeId) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Update")
                }
                // Delete button
                Button(
                    onClick = { onDeleteRecipe(recipe.recipe.recipeId) }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
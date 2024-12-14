package com.saralynpoole.digitalcookbookcreator.presentation.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
 * View all recipes screen.
 */
@Composable
fun ViewAllRecipesScreen(
    recipes: List<String>,
    onUpdateRecipe: (String) -> Unit,
    onDeleteRecipe: (String) -> Unit,
    onViewRecipe: (String) -> Unit,
    navigateToHome: () -> Unit
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
                text = "View all recipes",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )

            // Arranges the list of recipes with spacing
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(recipes) { recipe ->
                    // Displays each recipe
                    RecipeItem(
                        recipeTitle = recipe,
                        onUpdateRecipe = onUpdateRecipe,
                        onDeleteRecipe = onDeleteRecipe,
                        onViewRecipe = onViewRecipe
                    )
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
// Displays each recipe
@Composable
fun RecipeItem(
    recipeTitle: String,
    onUpdateRecipe: (String) -> Unit,
    onDeleteRecipe: (String) -> Unit,
    onViewRecipe: (String) -> Unit
) {
    // Card for each recipe
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clickable { onViewRecipe(recipeTitle) },
        elevation = CardDefaults.cardElevation()
    ) {
        // Arranges the elements horizontally
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Displays the recipe title
            Text(
                text = recipeTitle,
                modifier = Modifier.weight(1f)
            )
            // Arranges the update and delete buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Update button
                Button(
                    onClick = { onUpdateRecipe(recipeTitle) },
                    modifier = Modifier.heightIn(min = 40.dp)
                ) {
                    Text("Update")
                }
                // Delete button
                Button(
                    onClick = { onDeleteRecipe(recipeTitle) },
                    modifier = Modifier.heightIn(min = 40.dp)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
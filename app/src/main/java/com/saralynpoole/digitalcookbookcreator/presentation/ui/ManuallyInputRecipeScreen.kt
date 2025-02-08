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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel

/**
 * Manually input a recipe screen.
 */
@Composable
fun ManuallyInputRecipeScreen(
    viewModel: RecipeViewModel,
    onViewAllRecipes: () -> Unit
) {
    // Collect states from the ViewModel
    val title by viewModel.recipeTitle.collectAsState()
    val description by viewModel.recipeDescription.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val steps by viewModel.steps.collectAsState()

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
                text = "Manually input a recipe",
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
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Text field for each ingredient
                            TextField(
                                value = ingredient.name,
                                onValueChange = {
                                    viewModel.updateIngredient(
                                        index,
                                        it,
                                        ingredient.quantity
                                    )
                                },
                                label = { Text("Ingredient name") },
                                modifier = Modifier.weight(2f)
                            )
                            // Text field for ingredient quantity
                            TextField(
                                value = ingredient.quantity,
                                onValueChange = {

                                    viewModel.updateIngredient(
                                        index,
                                        ingredient.name,
                                        it
                                    )
                                },
                                label = { Text("Qty") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    // Add ingredient button
                    Button(
                        onClick = {
                            viewModel.updateIngredient(
                                ingredients.size,
                                "",
                                ""
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Ingredient")
                    }
                }

                // Label for the steps section
                Text("Steps:")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.forEachIndexed { index, step ->
                        // Text field for each step
                        TextField(
                            value = step,
                            onValueChange = { viewModel.updateStep(index, it) },
                            label = { Text("Step ${index + 1}") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Add step button
                    Button(
                        onClick = { viewModel.updateStep(steps.size, "") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Step")
                    }
                }
            }

            // Adds vertical space
            Spacer(modifier = Modifier.height(16.dp))

            // Row to arrange the save and view all recipes buttons horizontally
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Button to save a recipe
                Button(
                    onClick = { viewModel.saveRecipe() },
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("Save recipe")
                }
                // Button to navigate to the view all recipes screen
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
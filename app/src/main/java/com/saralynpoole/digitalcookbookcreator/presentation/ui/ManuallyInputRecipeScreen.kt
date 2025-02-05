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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Manually input a recipe",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 32.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Description field
                TextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Ingredients:")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ingredients.forEachIndexed { index, ingredient ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                            TextField(
                                // Convert quantity to empty string if 0, otherwise show the number
                                value = if (ingredient.quantity == 0) "" else ingredient.quantity.toString(),
                                onValueChange = {
                                    // Only parse to Int if the input is not empty
                                    val quantity = if (it.isEmpty()) 0 else it.toIntOrNull() ?: return@TextField
                                    viewModel.updateIngredient(
                                        index,
                                        ingredient.name,
                                        quantity
                                    )
                                },
                                label = { Text("Qty") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                0
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Ingredient")
                    }
                }

                Text("Steps:")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.forEachIndexed { index, step ->
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.saveRecipe() },
                    modifier = Modifier.heightIn(min = 56.dp)
                ) {
                    Text("Save recipe")
                }
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
package com.saralynpoole.digitalcookbookcreator.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel

/**
 * Format a recipe screen.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FormatRecipeScreen(
    viewModel: RecipeViewModel,
    onSaveComplete: () -> Unit,
    onRetakeRecipePhoto: () -> Unit,
    onViewAllRecipes: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Collect StateFlow values
    val title by viewModel.recipeTitle.collectAsState()
    val description by viewModel.recipeDescription.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val titleError by viewModel.titleError.collectAsState()
    val ingredientsError by viewModel.ingredientsError.collectAsState()
    val stepsError by viewModel.stepsError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Error messages from ViewModel
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Format Recipe",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = { Text("Title") },
                        isError = titleError,
                        supportingText = {
                            if (titleError) {
                                Text("Title is required")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    )

                    // Ingredients section
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Ingredient list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ingredients.forEachIndexed { index, ingredient ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Ingredient name
                                OutlinedTextField(
                                    value = ingredient.name,
                                    onValueChange = { viewModel.updateIngredient(index, it, ingredient.quantity) },
                                    label = { Text("Ingredient") },
                                    modifier = Modifier.weight(1f),
                                    enabled = true
                                )

                                // Quantity
                                OutlinedTextField(
                                    value = ingredient.quantity,
                                    onValueChange = { viewModel.updateIngredient(index, ingredient.name, it) },
                                    label = { Text("Qty") },
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .padding(start = 8.dp),
                                    enabled = true
                                )

                                // Delete button
                                IconButton(onClick = { viewModel.removeIngredient(index) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove ingredient"
                                    )
                                }
                            }
                        }

                        // Add ingredient button
                        Button(
                            onClick = { viewModel.updateIngredient(ingredients.size, "", "") },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add ingredient"
                            )
                            Text("Add Ingredient")
                        }

                        // Show ingredients error if needed
                        if (ingredientsError) {
                            Text(
                                "At least one ingredient is required",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Steps section
                    Text(
                        text = "Steps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Steps list
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        steps.forEachIndexed { index, step ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Step number
                                Text(
                                    "${index + 1}.",
                                    modifier = Modifier.padding(top = 20.dp, end = 8.dp)
                                )

                                // Step description
                                OutlinedTextField(
                                    value = step,
                                    onValueChange = { viewModel.updateStep(index, it) },
                                    label = { Text("Step ${index + 1}") },
                                    minLines = 2,
                                    modifier = Modifier.weight(1f),
                                    enabled = true
                                )

                                // Delete button
                                IconButton(onClick = { viewModel.removeStep(index) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove step"
                                    )
                                }
                            }
                        }

                        // Add step button
                        Button(
                            onClick = { viewModel.updateStep(steps.size, "") },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add step"
                            )
                            Text("Add Step")
                        }

                        // Show steps error if needed
                        if (stepsError) {
                            Text(
                                "At least one step is required",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 16.dp)
                    ) {
                        Button(
                            onClick = onRetakeRecipePhoto,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retake Photo")
                        }

                        Button(
                            onClick = {
                                viewModel.saveRecipe()
                                onSaveComplete()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Recipe")
                        }
                    }

                    // View all recipes button
                    TextButton(
                        onClick = onViewAllRecipes,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View All Recipes")
                    }
                }
            }
        }
    }
}
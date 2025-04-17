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

    // Error states
    val titleError by viewModel.titleError.collectAsState()
    val ingredientsError by viewModel.ingredientsError.collectAsState()
    val stepsError by viewModel.stepsError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val titleLengthError by viewModel.titleLengthError.collectAsState()
    val descriptionLengthError by viewModel.descriptionLengthError.collectAsState()
    val ingredientNameLengthError by viewModel.ingredientNameLengthError.collectAsState()
    val ingredientQuantityLengthError by viewModel.ingredientQuantityLengthError.collectAsState()
    val stepDescriptionLengthError by viewModel.stepDescriptionLengthError.collectAsState()

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
                        isError = titleError || titleLengthError,
                        supportingText = {
                            if (titleError) {
                                Text("Title is required")
                            } else if (titleLengthError) {
                                Text("Title exceeds maximum length of ${RecipeViewModel.MAX_TITLE_LENGTH} characters")
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
                        enabled = true,
                        isError = descriptionLengthError,
                        supportingText = {
                            if (descriptionLengthError) {
                                Text("Description exceeds maximum length of ${RecipeViewModel.MAX_DESCRIPTION_LENGTH} characters")
                            }
                        }
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
                                Column(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = ingredient.name,
                                        onValueChange = { viewModel.updateIngredient(index, it, ingredient.quantity) },
                                        label = { Text("Ingredient") },
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = ingredientNameLengthError.getOrNull(index) == true
                                    )
                                    if (ingredientNameLengthError.getOrNull(index) == true) {
                                        Text(
                                            text = "Ingredient name exceeds maximum length of ${RecipeViewModel.MAX_INGREDIENT_NAME_LENGTH} characters",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

                                // Quantity
                                Column(
                                    modifier = Modifier
                                        .weight(0.5f)
                                        .padding(start = 8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = ingredient.quantity,
                                        onValueChange = { viewModel.updateIngredient(index, ingredient.name, it) },
                                        label = { Text("Qty") },
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = ingredientQuantityLengthError.getOrNull(index) == true
                                    )
                                    if (ingredientQuantityLengthError.getOrNull(index) == true) {
                                        Text(
                                            text = "Quantity exceeds maximum length of ${RecipeViewModel.MAX_INGREDIENT_QUANTITY_LENGTH} characters",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

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
                                Column(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = step,
                                        onValueChange = { viewModel.updateStep(index, it) },
                                        label = { Text("Step ${index + 1}") },
                                        minLines = 2,
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = stepDescriptionLengthError.getOrNull(index) == true
                                    )
                                    if (stepDescriptionLengthError.getOrNull(index) == true) {
                                        Text(
                                            text = "Step description exceeds maximum length of ${RecipeViewModel.MAX_STEP_DESCRIPTION_LENGTH} characters",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }

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
package com.saralynpoole.digitalcookbookcreator.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel

/**
 * Update a recipe screen.
 */
@Composable
fun UpdateRecipeScreen(
    recipeId: Int,
    viewModel: RecipeViewModel,
    onNavigateBack: () -> Unit
) {
    // Scroll state for scrollable content
    val scrollState = rememberScrollState()

    // Collect states from the RecipeViewModel
    val currentRecipe by viewModel.currentRecipe.collectAsState()
    val title by viewModel.recipeTitle.collectAsState()
    val description by viewModel.recipeDescription.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Validation states
    val titleError by viewModel.titleError.collectAsState()
    val titleLengthError by viewModel.titleLengthError.collectAsState()
    val descriptionLengthError by viewModel.descriptionLengthError.collectAsState()
    val ingredientsError by viewModel.ingredientsError.collectAsState()
    val ingredientNameLengthError by viewModel.ingredientNameLengthError.collectAsState()
    val ingredientQuantityLengthError by viewModel.ingredientQuantityLengthError.collectAsState()
    val stepsError by viewModel.stepsError.collectAsState()
    val stepDescriptionLengthError by viewModel.stepDescriptionLengthError.collectAsState()

    // State for success dialog
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Load the recipe when the screen is first composed
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    // Error dialog
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(errorMessage ?: "An unknown error occurred") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    // Success dialog shown after a successful update
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onNavigateBack()
            },
            title = { Text("Success") },
            text = { Text("Recipe updated successfully!") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Show loading indicator while loading or updating
        if (isLoading || isUpdating) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(if (isUpdating) "Updating recipe..." else "Loading recipe...")
            }
        }
        else {
            // Main form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Title at the top
                Text(
                    text = "Update Recipe",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 32.dp)
                )

                // Recipe input fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Title field
                    Column {
                        Text("Title:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { viewModel.updateTitle(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Recipe Title") },
                            isError = titleError || titleLengthError,
                            singleLine = true
                        )
                        // Error messages for title validation
                        if (titleError) {
                            Text(
                                text = "Title is required",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                        if (titleLengthError) {
                            Text(
                                text = "Title exceeds maximum length of ${RecipeViewModel.MAX_TITLE_LENGTH} characters",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // Description field with validation
                    Column {
                        Text("Description:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { viewModel.updateDescription(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Description") },
                            isError = descriptionLengthError
                        )
                        // Error message for description validation
                        if (descriptionLengthError) {
                            Text(
                                text = "Description exceeds maximum length of ${RecipeViewModel.MAX_DESCRIPTION_LENGTH} characters",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // Ingredients section with an add button and validation
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ingredients:", fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { viewModel.updateIngredient(ingredients.size, "", "") }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add ingredient")
                                Text("Add Ingredient")
                            }
                        }

                        // Error message if there are no valid ingredients
                        if (ingredientsError) {
                            Text(
                                text = "At least one ingredient is required",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)
                            )
                        }

                        // List of ingredient input fields
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ingredients.forEachIndexed { index, ingredient ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Ingredient name field with validation
                                    OutlinedTextField(
                                        value = ingredient.name,
                                        onValueChange = {
                                            viewModel.updateIngredient(
                                                index,
                                                it,
                                                ingredient.quantity
                                            )
                                        },
                                        label = { Text("Ingredient") },
                                        modifier = Modifier.weight(2f),
                                        singleLine = true,
                                        isError = ingredientNameLengthError.getOrNull(index) == true
                                    )
                                    // Ingredient quantity field with validation
                                    OutlinedTextField(
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
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        isError = ingredientQuantityLengthError.getOrNull(index) == true
                                    )
                                    // Delete button for removing an ingredient
                                    IconButton(onClick = { viewModel.removeIngredient(index) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove ingredient",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                // Error messages for ingredient fields
                                if (ingredientNameLengthError.getOrNull(index) == true) {
                                    Text(
                                        text = "Ingredient name exceeds maximum length of ${RecipeViewModel.MAX_INGREDIENT_NAME_LENGTH} characters",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                    )
                                }
                                if (ingredientQuantityLengthError.getOrNull(index) == true) {
                                    Text(
                                        text = "Quantity exceeds maximum length of ${RecipeViewModel.MAX_INGREDIENT_QUANTITY_LENGTH} characters",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Steps section
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Recipe Steps:", fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { viewModel.updateStep(steps.size, "") }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add step")
                                Text("Add Step")
                            }
                        }

                        // Error message if there are no valid steps
                        if (stepsError) {
                            Text(
                                text = "At least one step is required",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 8.dp)
                            )
                        }

                        // List of step input fields
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            steps.forEachIndexed { index, step ->
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Step number label
                                        Text("${index + 1}.", modifier = Modifier.padding(8.dp))
                                        // Step description field with validation
                                        OutlinedTextField(
                                            value = step,
                                            onValueChange = { viewModel.updateStep(index, it) },
                                            label = { Text("Step Description") },
                                            modifier = Modifier.weight(1f),
                                            isError = stepDescriptionLengthError.getOrNull(index) == true
                                        )
                                        // Delete button for removing a step
                                        IconButton(onClick = { viewModel.removeStep(index) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove step",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                    // Error message for step description
                                    if (stepDescriptionLengthError.getOrNull(index) == true) {
                                        Text(
                                            text = "Step description exceeds maximum length of ${RecipeViewModel.MAX_STEP_DESCRIPTION_LENGTH} characters",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action buttons at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save button (only enabled when there are no validation errors)
                    Button(
                        onClick = {
                            viewModel.updateExistingRecipe(recipeId)
                            if (errorMessage == null) {
                                showSuccessDialog = true
                            }
                        },
                        // Validation check to enable/disable the save button
                        modifier = Modifier.weight(1f),
                        enabled = !titleError && !titleLengthError &&
                                !descriptionLengthError &&
                                !ingredientsError &&
                                ingredientNameLengthError.none { it } &&
                                ingredientQuantityLengthError.none { it } &&
                                !stepsError &&
                                stepDescriptionLengthError.none { it }
                    ) {
                        Text("Save changes")
                    }
                    // Cancel button (returns to the previous screen without saving)
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
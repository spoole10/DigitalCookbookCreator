package com.saralynpoole.digitalcookbookcreator.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel
import com.saralynpoole.digitalcookbookcreator.presentation.ui.ManuallyInputRecipeScreen


@Composable
fun RecipeCreationRoute(
    onNavigateToAllRecipes: () -> Unit
) {
    val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory())

    ManuallyInputRecipeScreen(
        viewModel = viewModel,
        onViewAllRecipes = onNavigateToAllRecipes
    )
}
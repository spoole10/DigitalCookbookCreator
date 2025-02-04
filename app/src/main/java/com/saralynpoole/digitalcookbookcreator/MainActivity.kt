package com.saralynpoole.digitalcookbookcreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saralynpoole.digitalcookbookcreator.application.RecipeViewModel
import com.saralynpoole.digitalcookbookcreator.presentation.theme.DigitalCookbookCreatorTheme
import com.saralynpoole.digitalcookbookcreator.presentation.ui.CreateNewRecipeScreen
import com.saralynpoole.digitalcookbookcreator.presentation.ui.DeleteRecipeScreen
import com.saralynpoole.digitalcookbookcreator.presentation.ui.FormatRecipeScreen
import HomeScreen
import android.annotation.SuppressLint
import com.saralynpoole.digitalcookbookcreator.presentation.ui.ManuallyInputRecipeScreen
import com.saralynpoole.digitalcookbookcreator.presentation.ui.UpdateRecipeScreen
import com.saralynpoole.digitalcookbookcreator.presentation.ui.ViewAllRecipesScreen
import com.saralynpoole.digitalcookbookcreator.presentation.ui.ViewSingleRecipeScreen

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets the content of the activity to the DigitalCookbookCreatorTheme
        setContent {
            DigitalCookbookCreatorTheme {
                // Sets up the navigation graph for the app
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    // Composable for the home screen
                    composable("home") {
                        HomeScreen(
                            // Navigates to the create recipe screen
                            navigateToCreateRecipe = {
                                navController.navigate("create_recipe")
                            },
                            // Navigates to the view recipes screen
                            navigateToViewRecipes = {
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the create recipe screen
                    composable("create_recipe") {
                        CreateNewRecipeScreen(
                            // Navigates to the manually input recipe screen
                            navigateToManuallyInputRecipe = {
                                navController.navigate("manually_input_recipe")
                            },
                            // Navigates to the format recipe screen
                            navigateToFormatRecipe = {
                                navController.navigate("format_recipe")
                            },
                            // Navigates back to the home screen
                            navigateToHome = {
                                navController.navigate("home")
                            }
                        )
                    }
                    // Composable for the manually input recipe screen
                    composable("manually_input_recipe") {
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        ManuallyInputRecipeScreen(
                            viewModel = viewModel,
                            onViewAllRecipes = {
                                // Navigates to the view recipes screen
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the view recipes screen
                    composable("view_recipes") {
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        ViewAllRecipesScreen(
                            // Sample recipe data
                            recipes = listOf("Recipe 1", "Recipe 2"),
                            // Navigates to the update recipe screen
                            onUpdateRecipe = { recipeTitle ->
                                navController.navigate("update_recipe/$recipeTitle")
                            },
                            // Navigates to the delete recipe screen
                            onDeleteRecipe = { recipeTitle ->
                                navController.navigate("delete_recipe/$recipeTitle")
                            },
                            // Navigates to the view single recipe screen
                            onViewRecipe = { recipeTitle ->
                                navController.navigate("view_recipe/$recipeTitle")
                            },
                            // Navigates back to the home screen
                            navigateToHome = {
                                navController.navigate("home")
                            }
                        )
                    }
                    // Composable for the update recipe screen
                    composable(
                        "update_recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        UpdateRecipeScreen(
                            title = viewModel.recipeTitle.value,
                            description = viewModel.recipeDescription.value,
                            ingredients = viewModel.ingredients.value.map { it.name },
                            steps = viewModel.steps.value,
                            onTitleChange = { viewModel.updateTitle(it) },
                            onDescriptionChange = { viewModel.updateDescription(it) },
                            onIngredientChange = { index, value ->
                                viewModel.updateIngredient(
                                    index,
                                    value,
                                    viewModel.ingredients.value.getOrNull(index)?.quantity ?: 0
                                )
                            },
                            onStepChange = { index, value -> viewModel.updateStep(index, value) },
                            onSaveChanges = { viewModel.saveRecipe() },
                            onViewAllRecipes = {
                                // Navigates back to the view recipes screen
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the delete recipe screen
                    composable(
                        "delete_recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        DeleteRecipeScreen(
                            recipeTitle = viewModel.recipeTitle.value,
                            onConfirmDelete = { /* Confirm deletion */ },
                            onCancel = {
                                // Navigates back to the view recipes screen
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the format recipe screen
                    composable("format_recipe") {
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        FormatRecipeScreen(
                            title = viewModel.recipeTitle.value,
                            description = viewModel.recipeDescription.value,
                            ingredients = viewModel.ingredients.value.map { it.name },
                            steps = viewModel.steps.value,
                            onTitleChange = { viewModel.updateTitle(it) },
                            onDescriptionChange = { viewModel.updateDescription(it) },
                            onIngredientChange = { index, value ->
                                viewModel.updateIngredient(
                                    index,
                                    value,
                                    viewModel.ingredients.value.getOrNull(index)?.quantity ?: 0
                                )
                            },
                            onStepChange = { index, value -> viewModel.updateStep(index, value) },
                            onSaveRecipe = { viewModel.saveRecipe() },
                            onRetakeRecipePhoto = { /* Retake recipe photo */ },
                            onViewAllRecipes = {
                                // Navigates back to the view recipes screen
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the view single recipe screen
                    composable(
                        "view_recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        ViewSingleRecipeScreen(
                            title = viewModel.recipeTitle.value,
                            description = viewModel.recipeDescription.value,
                            ingredients = viewModel.ingredients.value.map { it.name },
                            steps = viewModel.steps.value,
                            navigateToViewAllRecipes = {
                                // Navigates back to the view recipes screen
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                }
            }
        }
    }
}
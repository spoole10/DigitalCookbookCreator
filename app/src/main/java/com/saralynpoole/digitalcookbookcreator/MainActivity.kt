package com.saralynpoole.digitalcookbookcreator

import HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saralynpoole.digitalcookbookcreator.presentation.theme.DigitalCookbookCreatorTheme
import com.saralynpoole.digitalcookbookcreator.presentation.ui.*

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity() {
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
                        // Sample recipe data
                        val title = "Recipe Title"
                        val description = "Recipe Description"
                        val ingredients = listOf("Ingredient 1", "Ingredient 2")
                        val steps = listOf("Step 1", "Step 2")
                        ManuallyInputRecipeScreen(
                            title = title,
                            description = description,
                            ingredients = ingredients,
                            steps = steps,
                            onTitleChange = { /* Update title */ },
                            onDescriptionChange = { /* Update description */ },
                            onIngredientChange = { index, value -> /* Update ingredient */ },
                            onStepChange = { index, value -> /* Update step */ },
                            onSaveRecipe = { /* Save recipe */ },
                            onViewAllRecipes = {
                                // Navigates back to the view recipes screen
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the view recipes screen
                    composable("view_recipes") {
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
                        // Sample recipe data
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        val title = "Sample Recipe Title"
                        val description = "Sample Recipe Description"
                        val ingredients = listOf("Ingredient 1", "Ingredient 2")
                        val steps = listOf("Step 1", "Step 2")
                        UpdateRecipeScreen(
                            title = title,
                            description = description,
                            ingredients = ingredients,
                            steps = steps,
                            onTitleChange = { /* Update title */ },
                            onDescriptionChange = { /* Update description */ },
                            onIngredientChange = { index, value -> /* Update ingredient */ },
                            onStepChange = { index, value -> /* Update step */ },
                            onSaveChanges = { /* Save changes */ },
                            // Navigates back to the view recipes screen
                            onViewAllRecipes = {
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the delete recipe screen
                    composable(
                        "delete_recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        // Sample recipe data
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        val recipeTitle = "Sample Recipe Title"
                        DeleteRecipeScreen(
                            recipeTitle = recipeTitle,
                            onConfirmDelete = { /* Confirm deletion */ },
                            // Navigates back to the view recipes screen
                            onCancel = {
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the format recipe screen
                    composable("format_recipe") {
                        // Sample recipe data
                        val title = "Formatted Recipe Title"
                        val description = "Formatted Recipe Description"
                        val ingredients = listOf("Formatted Ingredient 1", "Formatted Ingredient 2")
                        val steps = listOf("Formatted Step 1", "Formatted Step 2")
                        FormatRecipeScreen(
                            title = title,
                            description = description,
                            ingredients = ingredients,
                            steps = steps,
                            onTitleChange = { /* Update title */ },
                            onDescriptionChange = { /* Update description */ },
                            onIngredientChange = { index, value -> /* Update ingredient */ },
                            onStepChange = { index, value -> /* Update step */ },
                            onSaveRecipe = { /* Save recipe */ },
                            onRetakeRecipePhoto = { /* Retake recipe photo */ },
                            // Navigates back to the view recipes screen
                            onViewAllRecipes = {
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the view single recipe screen
                    composable("view_recipe/{recipeId}") { backStackEntry ->
                        // Sample recipe data
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        val title = "Sample Recipe Title"
                        val description = "Sample Recipe Description"
                        val ingredients = listOf("Ingredient 1", "Ingredient 2")
                        val steps = listOf("Step 1", "Step 2")
                        ViewSingleRecipeScreen(
                            title = title,
                            description = description,
                            ingredients = ingredients,
                            steps = steps,
                            // Navigates back to the view recipes screen
                            navigateToViewAllRecipes = {
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                }
            }
        }
    }
}

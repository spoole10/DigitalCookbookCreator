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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.saralynpoole.digitalcookbookcreator.presentation.ui.CameraScreen
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
                val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory(LocalContext.current))

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
                            navigateToManuallyInputRecipe = { navController.navigate("manually_input_recipe") },
                            navigateToFormatRecipe = { navController.navigate("format_recipe") },
                            navigateToHome = { navController.navigate("home") },
                            navigateToCamera = { navController.navigate("camera_screen") }
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
                    // Composable for the view all recipes screen
                    composable("view_recipes") {
                        // Initializes the view model
                        val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory())
                        ViewAllRecipesScreen(
                            // Navigates to the update recipe screen
                            viewModel = viewModel,
                            onUpdateRecipe = { recipeId ->
                                navController.navigate("update_recipe/$recipeId")
                            },
                            // Navigates to the delete recipe screen
                            onDeleteRecipe = { recipeId ->
                                navController.navigate("delete_recipe/$recipeId")
                            },
                            // Navigates to the view a single recipe screen
                            onViewRecipe = { recipeId ->
                                navController.navigate("view_recipe/$recipeId")
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
                        arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val viewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory())
                        val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable

                        UpdateRecipeScreen(
                            recipeId = recipeId,
                            viewModel = viewModel,
                            onNavigateBack = { navController.navigateUp() }
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
                        val recipeId = backStackEntry.arguments?.getString("recipeId")?.toIntOrNull() ?: 0

                        // Load the recipe when the screen is shown
                        LaunchedEffect(recipeId) {
                            viewModel.loadRecipe(recipeId)
                        }

                        DeleteRecipeScreen(
                            viewModel = viewModel,
                            onConfirmDelete = {
                                viewModel.deleteRecipe(recipeId)
                                // Only navigate back if there are no errors
                                if (viewModel.errorMessage.value == null) {
                                    navController.navigate("view_recipes")
                                }
                            },
                            onCancel = {
                                navController.navigate("view_recipes")
                            }
                        )
                    }
                    // Composable for the format recipe screen
                    composable("format_recipe") {
                        FormatRecipeScreen(
                            viewModel = viewModel,
                            onSaveComplete = { navController.navigate("view_recipes") },
                            onRetakeRecipePhoto = { navController.navigate("camera_screen") },
                            onViewAllRecipes = { navController.navigate("view_recipes") }
                        )
                    }
                    // Composable for the view a single recipe screen
                    composable(
                        "view_recipe/{recipeId}",
                        // Passes the recipeId as an argument
                        arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        // Initializes the view model
                        val viewModel: RecipeViewModel = viewModel(
                            factory = RecipeViewModel.Factory()
                        )
                        // Retrieves the recipeId from the back stack entry
                        val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: return@composable

                        // Displays the view a single recipe screen
                        ViewSingleRecipeScreen(
                            viewModel = viewModel,
                            recipeId = recipeId,
                            // Navigates back to the view recipes screen
                            navigateToViewAllRecipes = {
                                navController.navigate("view_recipes") {
                                    popUpTo("view_recipes") { inclusive = true }
                                }
                            }
                        )
                    }
                    // Composable for the camera screen
                    composable("camera_screen") {
                        CameraScreen(
                            viewModel = viewModel,
                            onPhotoTaken = { navController.navigate("format_recipe") },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
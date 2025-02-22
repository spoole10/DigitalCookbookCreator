package com.saralynpoole.digitalcookbookcreator.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.saralynpoole.digitalcookbookcreator.di.DependencyContainer
import com.saralynpoole.digitalcookbookcreator.domain.entity.IngredientEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeWithRelations
import com.saralynpoole.digitalcookbookcreator.domain.entity.StepEntity
import com.saralynpoole.digitalcookbookcreator.domain.usecase.IngredientUseCase
import com.saralynpoole.digitalcookbookcreator.domain.usecase.RecipeUseCase
import com.saralynpoole.digitalcookbookcreator.domain.usecase.StepUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * View model for a recipe
 */
class RecipeViewModel(
    // Use cases for recipes, ingredients, and steps
    private val recipeUseCase: RecipeUseCase,
    private val ingredientUseCase: IngredientUseCase,
    private val stepUseCase: StepUseCase
) : ViewModel() {

    // Mutable states
    private val _recipeTitle = MutableStateFlow("")
    val recipeTitle = _recipeTitle.asStateFlow()

    private val _recipeDescription = MutableStateFlow("")
    val recipeDescription = _recipeDescription.asStateFlow()

    private val _ingredients = MutableStateFlow<List<IngredientState>>(emptyList())
    val ingredients = _ingredients.asStateFlow()

    private val _steps = MutableStateFlow<List<String>>(emptyList())
    val steps = _steps.asStateFlow()

    private val _allRecipes = MutableStateFlow<List<RecipeWithRelations>>(emptyList())
    val allRecipes = _allRecipes.asStateFlow()

    private val _currentRecipe = MutableStateFlow<RecipeWithRelations?>(null)
    val currentRecipe = _currentRecipe.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Function initialize to load all recipes when the ViewModel is created
    init {
        loadAllRecipes()
    }

    // Function to load all recipes
    private fun loadAllRecipes() {
        viewModelScope.launch {
            recipeUseCase.getAllRecipes().collect { recipes ->
                _allRecipes.value = recipes
            }
        }
    }

    // Function to load a specific recipe by its ID
    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            recipeUseCase.getRecipe(recipeId).collect { recipes ->
                _currentRecipe.value = recipes.firstOrNull()
                _isLoading.value = false
            }
        }
    }

    // Function to delete a recipe by its ID
    fun deleteRecipe(recipeId: Int) {
        // Find the recipe to delete and delete it
        viewModelScope.launch {
            val recipeToDelete = _allRecipes.value.find { it.recipe.recipeId == recipeId }
            recipeToDelete?.let {
                recipeUseCase.deleteRecipe(it.recipe)
            }
        }
    }

    // Data class to represent the state of an ingredient
    data class IngredientState(
        val name: String = "",
        val quantity: String = ""
    )

    // Methods to update the title, description, ingredients, and steps
    fun updateTitle(newTitle: String) {
        _recipeTitle.value = newTitle
    }

    fun updateDescription(newDescription: String) {
        _recipeDescription.value = newDescription
    }

    fun updateIngredient(index: Int, name: String, quantity: String) {
        // Create a mutable list of ingredients
        val currentList = _ingredients.value.toMutableList()
        // Create a new ingredient state
        val newIngredient = IngredientState(name, quantity)
        if (index < currentList.size) {
            // Update the existing ingredient
            currentList[index] = newIngredient
        } else {
            // Add a new ingredient
            currentList.add(newIngredient)
        }
        // Update the ingredients state
        _ingredients.value = currentList
    }

    fun updateStep(index: Int, description: String) {
        // Create a mutable list of steps
        val currentList = _steps.value.toMutableList()
        // Update the step
        if (index < currentList.size) {
            currentList[index] = description
        } else {
            // Add a new step
            currentList.add(description)
        }
        // Update the steps state
        _steps.value = currentList
    }

    // Method to save the recipe
    fun saveRecipe() {
        viewModelScope.launch {
            // Create a recipe entity
            val recipeEntity = RecipeEntity(
                recipeTitle = _recipeTitle.value,
                recipeDescription = _recipeDescription.value
            )
            // Call the insertRecipe function from the RecipeUseCase to save the recipe
            val recipeId = recipeUseCase.insertRecipe(recipeEntity).toInt()

            // Save ingredients
            _ingredients.value.forEach { ingredientState ->
                // Create an ingredient entity
                val ingredientEntity = IngredientEntity(
                    recipeId = recipeId,
                    name = ingredientState.name,
                    quantity = ingredientState.quantity
                )
                // Call the addIngredient function from the IngredientUseCase to save each ingredient
                ingredientUseCase.addIngredient(ingredientEntity)
            }

            // Save steps
            _steps.value.forEachIndexed { index, stepDescription ->
                // Create a step entity
                val stepEntity = StepEntity(
                    recipeId = recipeId,
                    description = stepDescription,
                    stepNumber = index + 1
                )
                // Call the insertStep function from the StepUseCase to save each step
                stepUseCase.insertStep(stepEntity)
            }
        }
    }

    // Factory class for creating instances of RecipeViewModel
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeViewModel(
                DependencyContainer.getRecipeUseCase(),
                DependencyContainer.getIngredientUseCase(),
                DependencyContainer.getStepUseCase()
            ) as T
        }
    }
}
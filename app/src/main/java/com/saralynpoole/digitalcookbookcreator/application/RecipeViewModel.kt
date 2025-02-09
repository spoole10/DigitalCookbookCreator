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
        val currentList = _ingredients.value.toMutableList()
        val newIngredient = IngredientState(name, quantity)
        if (index < currentList.size) {
            currentList[index] = newIngredient
        } else {
            currentList.add(newIngredient)
        }
        _ingredients.value = currentList
    }

    fun updateStep(index: Int, description: String) {
        val currentList = _steps.value.toMutableList()
        if (index < currentList.size) {
            currentList[index] = description
        } else {
            currentList.add(description)
        }
        _steps.value = currentList
    }

    // Method to save the recipe
    fun saveRecipe() {
        viewModelScope.launch {
            val recipeEntity = RecipeEntity(
                recipeTitle = _recipeTitle.value,
                recipeDescription = _recipeDescription.value
            )
            val recipeId = recipeUseCase.insertRecipe(recipeEntity).toInt()

            _ingredients.value.forEach { ingredientState ->
                val ingredientEntity = IngredientEntity(
                    recipeId = recipeId,
                    name = ingredientState.name,
                    quantity = ingredientState.quantity
                )
                ingredientUseCase.addIngredient(ingredientEntity)
            }

            _steps.value.forEachIndexed { index, stepDescription ->
                val stepEntity = StepEntity(
                    recipeId = recipeId,
                    description = stepDescription,
                    stepNumber = index + 1
                )
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
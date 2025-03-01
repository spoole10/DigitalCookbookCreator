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

    // States for update functionality
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Validation states
    private val _titleError = MutableStateFlow(false)
    val titleError = _titleError.asStateFlow()

    private val _ingredientsError = MutableStateFlow(false)
    val ingredientsError = _ingredientsError.asStateFlow()

    private val _stepsError = MutableStateFlow(false)
    val stepsError = _stepsError.asStateFlow()

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
            _errorMessage.value = null
            try {
                recipeUseCase.getRecipe(recipeId).collect { recipes ->
                    val recipe = recipes.firstOrNull()
                    recipe?.let {
                        _recipeTitle.value = it.recipe.recipeTitle
                        _recipeDescription.value = it.recipe.recipeDescription
                        _ingredients.value = it.ingredients.map { ingredient ->
                            IngredientState(ingredient.name, ingredient.quantity)
                        }
                        _steps.value = it.steps
                            .sortedBy { step -> step.stepNumber }
                            .map { step -> step.description }
                    }
                    _currentRecipe.value = recipe
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recipe: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    // Validate all fields
    private fun validateFields(): Boolean {
        var isValid = true

        // Validate title
        if (_recipeTitle.value.isBlank()) {
            _titleError.value = true
            isValid = false
        } else {
            _titleError.value = false
        }

        // Validate ingredients
        // There must be at least one ingredient in the recipe
        val validIngredients = _ingredients.value.filter { it.name.isNotBlank() }
        if (validIngredients.isEmpty()) {
            _ingredientsError.value = true
            isValid = false
        } else {
            _ingredientsError.value = false
        }

        // Validate steps
        // There must be at least one step in the recipe
        val validSteps = _steps.value.filter { it.isNotBlank() }
        if (validSteps.isEmpty()) {
            _stepsError.value = true
            isValid = false
        } else {
            _stepsError.value = false
        }

        return isValid
    }

    // Function to update an existing recipe
    fun updateExistingRecipe(recipeId: Int): Boolean {
        // Validate the recipe data
        if (!validateFields()) {
            _errorMessage.value = "Please fix the errors in the form"
            return false
        }

        viewModelScope.launch {
            try {
                _isUpdating.value = true
                _errorMessage.value = null

                // Filter out empty ingredients and steps before saving
                val validIngredients = _ingredients.value.filter { it.name.isNotBlank() }
                val validSteps = _steps.value.filter { it.isNotBlank() }

                // Update recipe entity
                val updatedRecipe = RecipeEntity(
                    recipeId = recipeId,
                    recipeTitle = _recipeTitle.value,
                    recipeDescription = _recipeDescription.value
                )
                recipeUseCase.updateRecipe(updatedRecipe)

                // Delete existing ingredients and steps
                _currentRecipe.value?.let { currentRecipe ->
                    currentRecipe.ingredients.forEach { ingredient ->
                        ingredientUseCase.deleteIngredient(ingredient)
                    }
                    currentRecipe.steps.forEach { step ->
                        stepUseCase.deleteStep(step)
                    }
                }

                // Insert new ingredients
                validIngredients.forEach { ingredientState ->
                    val ingredientEntity = IngredientEntity(
                        recipeId = recipeId,
                        name = ingredientState.name,
                        quantity = ingredientState.quantity
                    )
                    ingredientUseCase.addIngredient(ingredientEntity)
                }

                // Insert new steps
                validSteps.forEachIndexed { index, stepDescription ->
                    val stepEntity = StepEntity(
                        recipeId = recipeId,
                        description = stepDescription,
                        stepNumber = index + 1
                    )
                    stepUseCase.insertStep(stepEntity)
                }

                // Reload the recipe to refresh UI
                loadRecipe(recipeId)

                _isUpdating.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update recipe: ${e.localizedMessage}"
                _isUpdating.value = false
                return@launch
            }
        }
        return true
    }

    // Function to delete a recipe by its ID
    fun deleteRecipe(recipeId: Int) {
        // Find the recipe to delete and delete it
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recipeToDelete = _allRecipes.value.find { it.recipe.recipeId == recipeId }
                recipeToDelete?.let {
                    recipeUseCase.deleteRecipe(it.recipe)
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete recipe: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    // Function to clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Data class to represent the state of an ingredient
    data class IngredientState(
        val name: String = "",
        val quantity: String = ""
    )

    // Methods to update the title, description, ingredients, and steps
    fun updateTitle(newTitle: String) {
        _recipeTitle.value = newTitle
        // Validate title as user types
        _titleError.value = newTitle.isBlank()
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

        // Validate ingredients after update
        _ingredientsError.value = currentList.none { it.name.isNotBlank() }
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

        // Validate steps after update
        _stepsError.value = currentList.none { it.isNotBlank() }
    }

    // Functions to remove ingredients and steps
    fun removeIngredient(index: Int) {
        val currentList = _ingredients.value.toMutableList()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _ingredients.value = currentList

            // Validate ingredients after removal
            _ingredientsError.value = currentList.none { it.name.isNotBlank() }
        }
    }

    fun removeStep(index: Int) {
        val currentList = _steps.value.toMutableList()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _steps.value = currentList

            // Validate steps after removal
            _stepsError.value = currentList.none { it.isNotBlank() }
        }
    }

    // Method to save the recipe
    fun saveRecipe() {
        // Validate the recipe data
        if (!validateFields()) {
            _errorMessage.value = "Please fix the errors in the form"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Filter out empty ingredients and steps
                val validIngredients = _ingredients.value.filter { it.name.isNotBlank() }
                val validSteps = _steps.value.filter { it.isNotBlank() }

                // Create a recipe entity
                val recipeEntity = RecipeEntity(
                    recipeTitle = _recipeTitle.value,
                    recipeDescription = _recipeDescription.value
                )
                // Call the insertRecipe function from the RecipeUseCase to save the recipe
                val recipeId = recipeUseCase.insertRecipe(recipeEntity).toInt()

                // Save ingredients
                validIngredients.forEach { ingredientState ->
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
                validSteps.forEachIndexed { index, stepDescription ->
                    // Create a step entity
                    val stepEntity = StepEntity(
                        recipeId = recipeId,
                        description = stepDescription,
                        stepNumber = index + 1
                    )
                    // Call the insertStep function from the StepUseCase to save each step
                    stepUseCase.insertStep(stepEntity)
                }

                // Reset the form after successful save
                resetForm()
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save recipe: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    // Function to reset form values
    private fun resetForm() {
        _recipeTitle.value = ""
        _recipeDescription.value = ""
        _ingredients.value = emptyList()
        _steps.value = emptyList()
        _titleError.value = false
        _ingredientsError.value = false
        _stepsError.value = false
    }

    // Factory class for creating instances of the RecipeViewModel
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
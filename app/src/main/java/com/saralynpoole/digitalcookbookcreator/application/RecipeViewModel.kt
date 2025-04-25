package com.saralynpoole.digitalcookbookcreator.application

import android.content.Context
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
    private val stepUseCase: StepUseCase,
    private val appContext: Context? = null
) : ViewModel() {

    companion object {
        // Constants for validation
        const val MAX_TITLE_LENGTH = 100
        const val MAX_DESCRIPTION_LENGTH = 500
        const val MAX_INGREDIENT_NAME_LENGTH = 100
        const val MAX_INGREDIENT_QUANTITY_LENGTH = 100
        const val MAX_STEP_DESCRIPTION_LENGTH = 300
    }

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

    // Loading state flag
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

    private val _titleLengthError = MutableStateFlow(false)
    val titleLengthError = _titleLengthError.asStateFlow()

    private val _descriptionLengthError = MutableStateFlow(false)
    val descriptionLengthError = _descriptionLengthError.asStateFlow()

    private val _ingredientNameLengthError = MutableStateFlow<List<Boolean>>(emptyList())
    val ingredientNameLengthError = _ingredientNameLengthError.asStateFlow()

    private val _ingredientQuantityLengthError = MutableStateFlow<List<Boolean>>(emptyList())
    val ingredientQuantityLengthError = _ingredientQuantityLengthError.asStateFlow()

    private val _stepDescriptionLengthError = MutableStateFlow<List<Boolean>>(emptyList())
    val stepDescriptionLengthError = _stepDescriptionLengthError.asStateFlow()

    // Load all recipes when the ViewModel is created
    init {
        loadAllRecipes()
    }


    // Function to load all recipes from the database into the view model state
    private fun loadAllRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Uncomment to simulate error when loading recipes
                //throw Exception("Simulated error")
                recipeUseCase.getAllRecipes().collect { recipes ->
                    _allRecipes.value = recipes
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recipes: ${e.localizedMessage}"
                _isLoading.value = false
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
                        // Update state with recipe details
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

    // Validates all fields before saving or updating a recipe
    private fun validateFields(): Boolean {
        var isValid = true

        // Validate title (required field)
        if (_recipeTitle.value.isBlank()) {
            _titleError.value = true
            isValid = false
        } else {
            _titleError.value = false
        }

        // Validate title length
        if (_recipeTitle.value.length > MAX_TITLE_LENGTH) {
            _titleLengthError.value = true
            isValid = false
        } else {
            _titleLengthError.value = false
        }

        // Validate description length
        if (_recipeDescription.value.length > MAX_DESCRIPTION_LENGTH) {
            _descriptionLengthError.value = true
            isValid = false
        } else {
            _descriptionLengthError.value = false
        }

        // Validate ingredients (at least one valid ingredient is required)
        val validIngredients = _ingredients.value.filter { it.name.isNotBlank() }
        if (validIngredients.isEmpty()) {
            _ingredientsError.value = true
            isValid = false
        } else {
            _ingredientsError.value = false
        }

        // Validate ingredient name and quantity lengths
        val nameErrors = _ingredients.value.map { it.name.length > MAX_INGREDIENT_NAME_LENGTH }
        val quantityErrors = _ingredients.value.map { it.quantity.length > MAX_INGREDIENT_QUANTITY_LENGTH }
        _ingredientNameLengthError.value = nameErrors
        _ingredientQuantityLengthError.value = quantityErrors

        if (nameErrors.any { it } || quantityErrors.any { it }) {
            isValid = false
        }

        // Validate steps
        val validSteps = _steps.value.filter { it.isNotBlank() }
        if (validSteps.isEmpty()) {
            _stepsError.value = true
            isValid = false
        } else {
            _stepsError.value = false
        }

        // Validate step description lengths
        val stepErrors = _steps.value.map { it.length > MAX_STEP_DESCRIPTION_LENGTH }
        _stepDescriptionLengthError.value = stepErrors
        if (stepErrors.any { it }) {
            isValid = false
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
                /*// Uncomment to simulate an exception
                _errorMessage.value = "Simulated error";
                throw Exception("Simulated error")*/

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
                /*// Uncomment to simulate an exception
                _errorMessage.value = "Simulated error";
                throw Exception("Simulated error")*/
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
        // Validate title as the user types
        _titleError.value = newTitle.isBlank()
        _titleLengthError.value = newTitle.length > MAX_TITLE_LENGTH
    }

    // Updates the recipe description and validates it
    fun updateDescription(newDescription: String) {
        _recipeDescription.value = newDescription
        _descriptionLengthError.value = newDescription.length > MAX_DESCRIPTION_LENGTH
    }

    // Updates an ingredient at the given index and validates it
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

        // Validate ingredient name and quantity lengths
        val nameErrors = currentList.map { it.name.length > MAX_INGREDIENT_NAME_LENGTH }
        val quantityErrors = currentList.map { it.quantity.length > MAX_INGREDIENT_QUANTITY_LENGTH }
        _ingredientNameLengthError.value = nameErrors
        _ingredientQuantityLengthError.value = quantityErrors
    }

    // Updates a step at the given index and validates it
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

        // Validate step description lengths
        val stepErrors = currentList.map { it.length > MAX_STEP_DESCRIPTION_LENGTH }
        _stepDescriptionLengthError.value = stepErrors
    }

    // Function to remove an ingredient
    fun removeIngredient(index: Int) {
        val currentList = _ingredients.value.toMutableList()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _ingredients.value = currentList

            // Validate ingredients after removal
            _ingredientsError.value = currentList.none { it.name.isNotBlank() }
        }
    }


    // Function to remove a step
    fun removeStep(index: Int) {
        val currentList = _steps.value.toMutableList()
        if (index < currentList.size) {
            currentList.removeAt(index)
            _steps.value = currentList

            // Validate steps after removal
            _stepsError.value = currentList.none { it.isNotBlank() }
        }
    }

    // Validates all recipe fields, then saves it to the database
    // After a successful save, the form is reset
    fun saveRecipe() {
        // Validate the recipe data
        if (!validateFields()) {
            _errorMessage.value = "Please fix the errors in the form"
            return
        }

        viewModelScope.launch {
            try {
              /*  // Uncomment to simulate an exception
                _errorMessage.value = "Simulated error";
                throw Exception("Simulated error")*/

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


    // Function to reset form values and validation states
    private fun resetForm() {
        _recipeTitle.value = ""
        _recipeDescription.value = ""
        _ingredients.value = emptyList()
        _steps.value = emptyList()
        _titleError.value = false
        _titleLengthError.value = false
        _descriptionLengthError.value = false
        _ingredientsError.value = false
        _ingredientNameLengthError.value = emptyList()
        _ingredientQuantityLengthError.value = emptyList()
        _stepsError.value = false
        _stepDescriptionLengthError.value = emptyList()
    }

    // Factory class for creating instances of the RecipeViewModel
    class Factory(private val context: Context? = null) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecipeViewModel(
                DependencyContainer.getRecipeUseCase(),
                DependencyContainer.getIngredientUseCase(),
                DependencyContainer.getStepUseCase(),
                context
            ) as T
        }
    }
}
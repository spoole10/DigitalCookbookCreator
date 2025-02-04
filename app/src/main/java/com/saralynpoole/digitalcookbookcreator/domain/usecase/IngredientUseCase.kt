package com.saralynpoole.digitalcookbookcreator.domain.usecase

import com.saralynpoole.digitalcookbookcreator.data.repository.IngredientRepository
import com.saralynpoole.digitalcookbookcreator.domain.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow


/*
 * Use case for ingredients.
 */
class IngredientUseCase (
    // Repository for ingredients
    private val repository: IngredientRepository
) {
    // Adds an ingredient
    suspend fun addIngredient(ingredient: IngredientEntity): Long =
        repository.insertIngredient(ingredient)

    // Updates an ingredient
    suspend fun updateIngredient(ingredient: IngredientEntity) =
        repository.updateIngredient(ingredient)

    // Deletes an ingredient
    suspend fun deleteIngredient(ingredient: IngredientEntity) =
        repository.deleteIngredient(ingredient)

    // Gets ingredients for a recipe
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientEntity>> =
        repository.getIngredientsForRecipe(recipeId)

    // Gets all ingredients
    fun getAllIngredients(): Flow<List<IngredientEntity>> =
        repository.getAllIngredients()
}
package com.saralynpoole.digitalcookbookcreator.data.repository

import com.saralynpoole.digitalcookbookcreator.data.dao.IngredientDAO
import com.saralynpoole.digitalcookbookcreator.domain.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that provides an abstraction layer for Ingredient data access.
 * Delegates operations to the IngredientDAO to interact with the local Room database.
 */
class IngredientRepository (
    private val ingredientDAO: IngredientDAO
) {
    // CRUD operations for ingredients

    // Insert a new ingredient
    suspend fun insertIngredient(ingredient: IngredientEntity): Long =
        ingredientDAO.insertIngredient(ingredient)

    // Delete an ingredient
    suspend fun deleteIngredient(ingredient: IngredientEntity) =
        ingredientDAO.deleteIngredient(ingredient)

    // Update an ingredient
    suspend fun updateIngredient(ingredient: IngredientEntity) =
        ingredientDAO.updateIngredient(ingredient)

    // Get ingredients for a specific recipe
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientEntity>> =
        ingredientDAO.getIngredientsForRecipe(recipeId)

    // Get all ingredients
    fun getAllIngredients(): Flow<List<IngredientEntity>> =
        ingredientDAO.getAllIngredients()
}
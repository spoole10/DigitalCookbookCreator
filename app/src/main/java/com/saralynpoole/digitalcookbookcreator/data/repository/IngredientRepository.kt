package com.saralynpoole.digitalcookbookcreator.data.repository

import com.saralynpoole.digitalcookbookcreator.data.dao.IngredientDAO
import com.saralynpoole.digitalcookbookcreator.domain.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

class IngredientRepository (
    private val ingredientDAO: IngredientDAO
) {
    suspend fun insertIngredient(ingredient: IngredientEntity): Long =
        ingredientDAO.insertIngredient(ingredient)

    suspend fun deleteIngredient(ingredient: IngredientEntity) =
        ingredientDAO.deleteIngredient(ingredient)

    suspend fun updateIngredient(ingredient: IngredientEntity) =
        ingredientDAO.updateIngredient(ingredient)

    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientEntity>> =
        ingredientDAO.getIngredientsForRecipe(recipeId)

    fun getAllIngredients(): Flow<List<IngredientEntity>> =
        ingredientDAO.getAllIngredients()
}
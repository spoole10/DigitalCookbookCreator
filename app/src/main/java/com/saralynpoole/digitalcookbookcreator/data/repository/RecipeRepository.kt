package com.saralynpoole.digitalcookbookcreator.data.repository

import com.saralynpoole.digitalcookbookcreator.data.dao.RecipeDAO
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeWithRelations
import kotlinx.coroutines.flow.Flow

class RecipeRepository (
    private val recipeDAO: RecipeDAO
) {
    suspend fun insertRecipe(recipe: RecipeEntity): Long =
        recipeDAO.insertRecipe(recipe)

    suspend fun deleteRecipe(recipe: RecipeEntity) =
        recipeDAO.deleteRecipe(recipe)

    suspend fun updateRecipe(recipe: RecipeEntity) =
        recipeDAO.updateRecipe(recipe)

    fun getAllRecipes(): Flow<List<RecipeWithRelations>> =
        recipeDAO.getAllRecipes()

    fun getRecipe(id: Int): Flow<List<RecipeWithRelations>> =
        recipeDAO.getRecipe(id)
}
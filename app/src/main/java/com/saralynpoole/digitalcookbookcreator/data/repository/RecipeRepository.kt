package com.saralynpoole.digitalcookbookcreator.data.repository

import com.saralynpoole.digitalcookbookcreator.data.dao.RecipeDAO
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeWithRelations
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that provides an abstraction layer for Recipe data access.
 * Delegates operations to the RecipeDAO to interact with the local Room database.
 */
class RecipeRepository (
    private val recipeDAO: RecipeDAO
) {
    // CRUD operations for recipes

    // Insert a new recipe
    suspend fun insertRecipe(recipe: RecipeEntity): Long =
        recipeDAO.insertRecipe(recipe)

    // Delete a recipe
    suspend fun deleteRecipe(recipe: RecipeEntity) =
        recipeDAO.deleteRecipe(recipe)

    // Update a recipe
    suspend fun updateRecipe(recipe: RecipeEntity) =
        recipeDAO.updateRecipe(recipe)

    // Get all recipes
    fun getAllRecipes(): Flow<List<RecipeWithRelations>> =
        recipeDAO.getAllRecipes()

    // Get a specific recipe by ID
    fun getRecipe(id: Int): Flow<List<RecipeWithRelations>> =
        recipeDAO.getRecipe(id)
}
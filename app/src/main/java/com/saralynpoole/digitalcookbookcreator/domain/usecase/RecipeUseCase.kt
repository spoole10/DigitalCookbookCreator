package com.saralynpoole.digitalcookbookcreator.domain.usecase

import com.saralynpoole.digitalcookbookcreator.data.repository.RecipeRepository
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeWithRelations
import kotlinx.coroutines.flow.Flow

/**
 * Use case for recipes.
 */
class RecipeUseCase(
    // Repository for recipes
    private val repository: RecipeRepository
) {
    // Adds a recipe
    suspend fun insertRecipe(recipe: RecipeEntity): Long =
        repository.insertRecipe(recipe)

    // Deletes a recipe
    suspend fun deleteRecipe(recipe: RecipeEntity) =
        repository.deleteRecipe(recipe)

    // Updates a recipe
    suspend fun updateRecipe(recipe: RecipeEntity) =
        repository.updateRecipe(recipe)

    // Gets all recipes
    fun getAllRecipes(): Flow<List<RecipeWithRelations>> =
        repository.getAllRecipes()

    // Gets a single recipe
    fun getRecipe(id: Int): Flow<List<RecipeWithRelations>> =
        repository.getRecipe(id)
}
package com.saralynpoole.digitalcookbookcreator.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.saralynpoole.digitalcookbookcreator.domain.entity.IngredientEntity

/**
 * Data Access Object for ingredients
 */
@Dao
interface IngredientDAO {
    // CRUD operations for ingredients

    // Insert a new ingredient
    @Insert
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    // Delete an ingredient
    @Delete
    suspend fun deleteIngredient(ingredient: IngredientEntity)

    // Update an ingredient
    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity)

    // Query to get ingredients for a recipe
    @Query("SELECT * FROM Ingredients WHERE RecipeID = :recipeId")
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientEntity>>

    // Query to get all ingredients
    @Query("SELECT * FROM Ingredients")
    fun getAllIngredients(): Flow<List<IngredientEntity>>
}
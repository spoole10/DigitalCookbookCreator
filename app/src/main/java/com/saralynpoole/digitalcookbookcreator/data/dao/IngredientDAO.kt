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
    @Insert
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    @Delete
    suspend fun deleteIngredient(ingredient: IngredientEntity)

    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity)

    // Query to get ingredients for a recipe
    @Query("SELECT * FROM Ingredients WHERE RecipeID = :recipeId")
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientEntity>>

    // Query to get all ingredients
    @Query("SELECT * FROM Ingredients")
    fun getAllIngredients(): Flow<List<IngredientEntity>>
}
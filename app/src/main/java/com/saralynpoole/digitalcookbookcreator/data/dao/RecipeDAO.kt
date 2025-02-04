package com.saralynpoole.digitalcookbookcreator.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeWithRelations

/*
   Data Access Object for recipes
 */
@Dao
interface RecipeDAO {
    // CRUD operations for recipes
    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    // Query to get all recipes
    @Query("SELECT * FROM Recipes")
    fun getAllRecipes(): Flow<List<RecipeWithRelations>>

    // Query to get a specific recipe
    @Query("SELECT * FROM Recipes WHERE RecipeID = :id")
    fun getRecipe(id: Int): Flow<List<RecipeWithRelations>>
}
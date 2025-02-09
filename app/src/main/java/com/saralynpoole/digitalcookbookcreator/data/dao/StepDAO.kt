package com.saralynpoole.digitalcookbookcreator.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.saralynpoole.digitalcookbookcreator.domain.entity.StepEntity

/**
 * Data Access Object for steps
 */
@Dao
interface StepDAO {
    // CRUD operations for steps
    @Insert
    suspend fun insertStep(step: StepEntity): Long

    @Delete
    suspend fun deleteStep(step: StepEntity)

    @Update
    suspend fun updateStep(step: StepEntity)

    // Query to get steps for a recipe
    @Query("SELECT * FROM Steps WHERE RecipeID = :recipeId ORDER BY StepNumber")
    fun getStepsForRecipe(recipeId: Int): Flow<List<StepEntity>>
}
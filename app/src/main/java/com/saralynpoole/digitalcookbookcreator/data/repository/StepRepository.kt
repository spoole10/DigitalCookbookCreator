package com.saralynpoole.digitalcookbookcreator.data.repository

import com.saralynpoole.digitalcookbookcreator.data.dao.StepDAO
import com.saralynpoole.digitalcookbookcreator.domain.entity.StepEntity
import kotlinx.coroutines.flow.Flow


/**
 * Repository class that provides an abstraction layer for Step data access.
 * Delegates operations to the StepDAO to interact with the local Room database.
 */
class StepRepository (
    private val stepDAO: StepDAO
) {
    // CRUD operations for steps

    // Insert a new step
    suspend fun insertStep(step: StepEntity): Long =
        stepDAO.insertStep(step)

    // Delete a step
    suspend fun deleteStep(step: StepEntity) =
        stepDAO.deleteStep(step)

    // Update a step
    suspend fun updateStep(step: StepEntity) =
        stepDAO.updateStep(step)

    // Get steps for a specific recipe
    fun getStepsForRecipe(recipeId: Int): Flow<List<StepEntity>> =
        stepDAO.getStepsForRecipe(recipeId)
}
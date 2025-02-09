package com.saralynpoole.digitalcookbookcreator.domain.usecase

import com.saralynpoole.digitalcookbookcreator.data.repository.StepRepository
import com.saralynpoole.digitalcookbookcreator.domain.entity.StepEntity
import kotlinx.coroutines.flow.Flow


/**
  Use case for steps.
 */

class StepUseCase (
    // Repository for steps
    private val repository: StepRepository
) {
    // Adds a step
    suspend fun insertStep(step: StepEntity): Long =
        repository.insertStep(step)

    // Deletes a step
    suspend fun deleteStep(step: StepEntity) =
        repository.deleteStep(step)

    // Updates a step
    suspend fun updateStep(step: StepEntity) =
        repository.updateStep(step)

    // Gets steps for a recipe
    fun getStepsForRecipe(recipeId: Int): Flow<List<StepEntity>> =
        repository.getStepsForRecipe(recipeId)
}
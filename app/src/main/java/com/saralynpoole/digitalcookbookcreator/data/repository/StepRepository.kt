package com.saralynpoole.digitalcookbookcreator.data.repository

import com.saralynpoole.digitalcookbookcreator.data.dao.StepDAO
import com.saralynpoole.digitalcookbookcreator.domain.entity.StepEntity
import kotlinx.coroutines.flow.Flow


/*
 * Repository for steps.
 */
class StepRepository (
    private val stepDAO: StepDAO
) {
    suspend fun insertStep(step: StepEntity): Long =
        stepDAO.insertStep(step)

    suspend fun deleteStep(step: StepEntity) =
        stepDAO.deleteStep(step)

    suspend fun updateStep(step: StepEntity) =
        stepDAO.updateStep(step)

    fun getStepsForRecipe(recipeId: Int): Flow<List<StepEntity>> =
        stepDAO.getStepsForRecipe(recipeId)
}
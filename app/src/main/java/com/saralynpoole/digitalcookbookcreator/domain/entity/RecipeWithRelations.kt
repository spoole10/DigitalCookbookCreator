package com.saralynpoole.digitalcookbookcreator.domain.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
    Recipe entity with the associated ingredients and steps
 */
data class RecipeWithRelations(
    // Embedded recipe entity
    @Embedded
    val recipe: RecipeEntity,

    @Relation(
        parentColumn = "RecipeID",
        entityColumn = "RecipeID"
    )
    // List of ingredients associated with the recipe
    val ingredients: List<IngredientEntity>,

    @Relation(
        parentColumn = "RecipeID",
        entityColumn = "RecipeID"
    )
    // List of steps associated with the recipe
    val steps: List<StepEntity>
)

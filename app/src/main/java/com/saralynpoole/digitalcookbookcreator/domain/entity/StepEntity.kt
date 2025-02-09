package com.saralynpoole.digitalcookbookcreator.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
    Entity to represent a step
 */
@Entity(
    tableName = "Steps",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["RecipeID"],
            childColumns = ["RecipeID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StepEntity(
    // Primary key for the step entity
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "StepID")
    val stepId: Int = 0,

    // Foreign key referencing the RecipeID
    @ColumnInfo(name = "RecipeID")
    val recipeId: Int,

    // Description of the step
    @ColumnInfo(name = "Description", typeAffinity = ColumnInfo.TEXT)
    val description: String,

    // The step number
    @ColumnInfo(name = "StepNumber")
    val stepNumber: Int
)

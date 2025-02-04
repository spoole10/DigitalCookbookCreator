package com.saralynpoole.digitalcookbookcreator.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/*
    Entity to represent an ingredient
 */
@Entity(
    tableName = "Ingredients",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["RecipeID"],
            childColumns = ["RecipeID"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientEntity(
    // Primary key for the ingredient entity
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "IngredientID")
    val ingredientId: Int = 0,

    // Foreign key referencing the RecipeID
    @ColumnInfo(name = "RecipeID")
    val recipeId: Int,

    // Name of the ingredient
    @ColumnInfo(name = "Name", typeAffinity = ColumnInfo.TEXT)
    val name: String,

    // Quantity of the ingredient
    @ColumnInfo(name = "Quantity")
    val quantity: Int
)


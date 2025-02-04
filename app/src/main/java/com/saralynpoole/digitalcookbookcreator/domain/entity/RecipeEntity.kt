package com.saralynpoole.digitalcookbookcreator.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/*
    Entity to represent a recipe
 */
@Entity(tableName = "Recipes")
data class RecipeEntity(
    // Primary key for the recipe entity
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "RecipeID")
    val recipeId: Int = 0,

    // Title of the recipe
    @ColumnInfo(name = "RecipeTitle", typeAffinity = ColumnInfo.TEXT)
    val recipeTitle: String,

    // Description of the entity
    @ColumnInfo(name = "RecipeDescription", typeAffinity = ColumnInfo.TEXT)
    val recipeDescription: String
)

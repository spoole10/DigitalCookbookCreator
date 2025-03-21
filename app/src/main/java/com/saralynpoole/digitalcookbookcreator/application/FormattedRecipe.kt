package com.saralynpoole.digitalcookbookcreator.application

/**
 * Represents a formatted recipe extracted from text recognition.
 */
data class FormattedRecipe(
    val title: String = "",
    val description: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList()
) {
    // Represents an ingredient with name and quantity.
    data class Ingredient(
        val name: String,
        val quantity: String
    )
}
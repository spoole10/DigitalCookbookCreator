package com.saralynpoole.digitalcookbookcreator.application

import android.util.Log
import com.google.mlkit.vision.text.Text

/**
 * Formats recognized text into a structured recipe.
 */
class RecipeFormatter {

    companion object {
        private const val TAG = "RecipeFormatter"

        // Keywords that might indicate the start of ingredient or step sections
        private val INGREDIENT_SECTION_KEYWORDS = listOf(
            "ingredients", "ingredients:", "you'll need", "you need", "what you need"
        )
        private val STEP_SECTION_KEYWORDS = listOf(
            "directions", "directions:", "instructions", "instructions:", "steps", "steps:",
            "method", "method:", "preparation", "preparation:"
        )
    }

    // Formats recognized text into a structured recipe.
    fun formatRecipe(recognizedText: Text): FormattedRecipe {
        val fullText = recognizedText.text

        Log.d(TAG, "Formatting recipe from text: $fullText")

        // Split the text into lines
        val lines = fullText.split("\n").filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            return FormattedRecipe()
        }

        // Get the title (usually the first line)
        val title = extractTitle(lines)

        // Find ingredient and step sections
        val (ingredientSection, stepSection) = identifySections(lines)

        // Extract description
        val description = extractDescription(lines, ingredientSection)

        // Extract ingredients
        val ingredients = extractIngredients(ingredientSection)

        // Extract steps
        val steps = extractSteps(stepSection)

        return FormattedRecipe(title, description, ingredients, steps)
    }

    private fun extractTitle(lines: List<String>): String {
        // Title is usually the first line or the first non-short line
        return lines.firstOrNull { it.length > 3 } ?: ""
    }

    private fun identifySections(lines: List<String>): Pair<List<String>, List<String>> {
        var ingredientSectionStart = -1
        var stepSectionStart = -1

        // Look for section markers
        lines.forEachIndexed { index, line ->
            val lowerLine = line.lowercase()

            // Check for ingredient section
            if (ingredientSectionStart == -1 &&
                INGREDIENT_SECTION_KEYWORDS.any { keyword -> lowerLine.contains(keyword) }) {
                ingredientSectionStart = index
            }

            // Check for step section
            if (stepSectionStart == -1 &&
                STEP_SECTION_KEYWORDS.any { keyword -> lowerLine.contains(keyword) }) {
                stepSectionStart = index
            }
        }

        // Calculate sections
        val ingredientSection: List<String>
        val stepSection: List<String>

        when {
            // Both sections found
            ingredientSectionStart >= 0 && stepSectionStart >= 0 -> {
                if (ingredientSectionStart < stepSectionStart) {
                    // Ingredients come before steps
                    ingredientSection = lines.subList(ingredientSectionStart + 1, stepSectionStart)
                    stepSection = lines.subList(stepSectionStart + 1, lines.size)
                } else {
                    // Steps come before ingredients
                    stepSection = lines.subList(stepSectionStart + 1, ingredientSectionStart)
                    ingredientSection = lines.subList(ingredientSectionStart + 1, lines.size)
                }
            }
            // Only ingredients found
            ingredientSectionStart >= 0 -> {
                ingredientSection = lines.subList(ingredientSectionStart + 1, lines.size)
                stepSection = emptyList()
            }
            // Only steps found
            stepSectionStart >= 0 -> {
                ingredientSection = emptyList()
                stepSection = lines.subList(stepSectionStart + 1, lines.size)
            }
            // No sections found, use heuristics
            else -> {
                // Assume first half is ingredients, second half is steps
                val midpoint = lines.size / 2
                ingredientSection = lines.subList(1, midpoint) // Skip title
                stepSection = lines.subList(midpoint, lines.size)
            }
        }

        return Pair(ingredientSection, stepSection)
    }

    private fun extractDescription(lines: List<String>, ingredientSection: List<String>): String {
        // Description is usually the text between title and ingredients
        val titleIndex = 0
        val ingredientStartIndex = lines.indexOfFirst { line ->
            INGREDIENT_SECTION_KEYWORDS.any {
                line.lowercase().contains(it)
            }
        }

        return if (ingredientStartIndex > titleIndex + 1) {
            lines.subList(titleIndex + 1, ingredientStartIndex).joinToString("\n")
        } else {
            // No clear description section
            ""
        }
    }

    private fun extractIngredients(ingredientLines: List<String>): List<FormattedRecipe.Ingredient> {
        return ingredientLines.mapNotNull { line ->
            // Try to separate quantity from ingredient name
            val result = parseIngredient(line)
            if (result.first.isNotBlank()) {
                FormattedRecipe.Ingredient(result.first, result.second)
            } else {
                null
            }
        }
    }

    private fun parseIngredient(line: String): Pair<String, String> {
        // Common patterns in ingredients:
        // "1 cup flour"
        // "1/2 teaspoon salt"
        // "2-3 tablespoons olive oil"

        // Look for numbers at the beginning
        val quantityPattern = "^\\s*(\\d+[\\d./\\s-]*)\\s*([a-zA-Z]+)?".toRegex()
        val match = quantityPattern.find(line)

        return if (match != null) {
            val quantity = match.groupValues[1].trim() +
                    (match.groupValues[2].trim().let { if (it.isNotEmpty()) " $it" else "" })
            val name = line.substring(match.range.last + 1).trim()
            Pair(name, quantity)
        } else {
            // No quantity found, treat the whole line as the ingredient name
            Pair(line.trim(), "")
        }
    }

    private fun extractSteps(stepLines: List<String>): List<String> {
        // Process step lines to combine multi-line steps
        val processedSteps = mutableListOf<String>()
        var currentStep = ""

        for (line in stepLines) {
            // Check if line starts with a number or bullet point
            if (line.trim().matches("^[\\d.)+\\-*•]+.*$".toRegex())) {
                // New step detected, save the previous one if exists
                if (currentStep.isNotBlank()) {
                    processedSteps.add(currentStep.trim())
                }
                // Start a new step, removing the number/bullet
                currentStep = line.trim().replace("^[\\d.)+\\-*•]+\\s*".toRegex(), "")
            } else {
                // Continue the current step
                currentStep += if (currentStep.isBlank()) line.trim() else " ${line.trim()}"
            }
        }

        // Add the last step if exists
        if (currentStep.isNotBlank()) {
            processedSteps.add(currentStep.trim())
        }

        return processedSteps
    }
}
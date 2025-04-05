package com.saralynpoole.digitalcookbookcreator.application

import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.text.Text

/**
 * Formats recognized text into a structured recipe with improved parsing.
 */
class RecipeFormatter {

    companion object {
        private const val TAG = "RecipeFormatter"

        // Keywords that might indicate the start of ingredient or step sections
        private val INGREDIENT_SECTION_KEYWORDS = listOf(
            "ingredients", "ingredients:", "you'll need", "you need", "what you need",
            "shopping list", "grocery list", "items needed", "items required"
        )

        private val STEP_SECTION_KEYWORDS = listOf(
            "directions", "directions:", "instructions", "instructions:", "steps", "steps:",
            "method", "method:", "preparation", "preparation:", "procedure", "procedure:",
            "how to prepare", "how to make", "how to cook"
        )

        // Common measurement units to help identify ingredients
        private val MEASUREMENT_UNITS = listOf(
            "cup", "cups", "tablespoon", "tablespoons", "tbsp", "tsp", "teaspoon", "teaspoons",
            "ounce", "ounces", "oz", "pound", "pounds", "lb", "lbs", "gram", "grams", "g",
            "kilogram", "kilograms", "kg", "ml", "milliliter", "milliliters", "liter", "liters",
            "l", "pinch", "dash", "handful", "slice", "slices", "piece", "pieces", "clove", "cloves"
        )
    }

    // Formats recognized text into a structured recipe.
    fun formatRecipe(recognizedText: Text): FormattedRecipe {
        val fullText = correctCommonErrors(recognizedText.text)
        Log.d(TAG, "Formatting recipe from text: $fullText")

        // Process text blocks for better structural understanding
        val structuredBlocks = processTextBlocks(recognizedText)

        // Split the text into lines for traditional processing
        val lines = fullText.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) {
            return FormattedRecipe()
        }

        // Get the title
        val title = extractTitle(lines)

        // Find ingredient and step sections
        val (ingredientSection, stepSection, otherText) = identifySections(lines, structuredBlocks)

        // Extract description
        val description = extractDescription(lines, otherText)

        // Extract ingredients
        val ingredients = extractIngredients(ingredientSection, structuredBlocks)

        // Extract steps
        val steps = extractSteps(stepSection)

        Log.d(TAG, "Extraction results - Title: $title, Ingredients: ${ingredients.size}, Steps: ${steps.size}")

        return FormattedRecipe(title, description, ingredients, steps)
    }

    // Function to process ML Kit's text blocks
    private fun processTextBlocks(recognizedText: Text): Map<String, List<Pair<String, Rect>>> {
        val blockMap = mutableMapOf<String, List<Pair<String, Rect>>>()

        // Extract lines with their bounding boxes
        val lines = mutableListOf<Pair<String, Rect>>()
        for (block in recognizedText.textBlocks) {
            for (line in block.lines) {
                val lineText = correctCommonErrors(line.text)
                val lineFrame = line.boundingBox
                if (lineFrame != null) {
                    lines.add(Pair(lineText, lineFrame))
                }
            }
        }

        // Store lines in the map
        blockMap["lines"] = lines

        return blockMap
    }

    private fun extractTitle(lines: List<String>): String {
        // Title is usually the first non-short line that's not a section header
        for (line in lines) {
            // Skip very short lines or lines that look like section headers
            if (line.length > 3 &&
                !isSectionHeader(line) &&
                !line.endsWith(":") &&
                !line.matches("^\\d+\\..*$".toRegex())) { // Skip lines that start with numbers
                return line
            }
        }
        return ""
    }

    private fun isSectionHeader(line: String): Boolean {
        val lowerLine = line.lowercase()
        return INGREDIENT_SECTION_KEYWORDS.any { lowerLine.contains(it) } ||
                STEP_SECTION_KEYWORDS.any { lowerLine.contains(it) }
    }

    private fun identifySections(
        lines: List<String>,
        structuredBlocks: Map<String, List<Pair<String, Rect>>>
    ): Triple<List<String>, List<String>, List<String>> {
        var ingredientSectionStart = -1
        var ingredientSectionEnd = -1
        var stepSectionStart = -1
        var stepSectionEnd = -1

        // First pass: Look for explicit section markers
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

        // Determine section endings
        if (ingredientSectionStart >= 0 && stepSectionStart >= 0) {
            if (ingredientSectionStart < stepSectionStart) {
                ingredientSectionEnd = stepSectionStart
                stepSectionEnd = lines.size
            } else {
                stepSectionEnd = ingredientSectionStart
                ingredientSectionEnd = lines.size
            }
        } else if (ingredientSectionStart >= 0) {
            ingredientSectionEnd = lines.size
        } else if (stepSectionStart >= 0) {
            stepSectionEnd = lines.size
        }

        // Second pass: If no explicit sections, try to detect ingredients and steps patterns
        if (ingredientSectionStart == -1 && stepSectionStart == -1) {
            // Detect ingredient-like lines (quantities followed by ingredients)
            val likelyIngredientLines = mutableListOf<Int>()
            val likelyStepLines = mutableListOf<Int>()

            lines.forEachIndexed { index, line ->
                // Skip title
                if (index == 0) return@forEachIndexed

                // Check for ingredient patterns: measurements, quantities, etc.
                if (looksLikeIngredient(line)) {
                    likelyIngredientLines.add(index)
                }

                // Check for step patterns: numbered steps, bullet points
                if (looksLikeStep(line)) {
                    likelyStepLines.add(index)
                }
            }

            // If consecutive likely ingredient lines were found, mark as ingredient section
            if (likelyIngredientLines.isNotEmpty() && areConsecutive(likelyIngredientLines)) {
                ingredientSectionStart = likelyIngredientLines.first()
                ingredientSectionEnd = likelyIngredientLines.last() + 1
            }

            // If consecutive likely step lines were found, mark as step section
            if (likelyStepLines.isNotEmpty() && areConsecutive(likelyStepLines)) {
                stepSectionStart = likelyStepLines.first()
                stepSectionEnd = likelyStepLines.last() + 1
            }
        }

        // Calculate final sections
        val ingredientSection: List<String> = if (ingredientSectionStart >= 0) {
            // Skip the section header
            lines.subList(ingredientSectionStart + 1, ingredientSectionEnd)
        } else {
            emptyList()
        }

        val stepSection: List<String> = if (stepSectionStart >= 0) {
            // Skip the section header
            lines.subList(stepSectionStart + 1, stepSectionEnd)
        } else {
            emptyList()
        }

        // Collect remaining text (not in ingredients or steps)
        val otherTextIndices = (0 until lines.size).filter { index ->
            val isInIngredients = index >= ingredientSectionStart && index < ingredientSectionEnd
            val isInSteps = index >= stepSectionStart && index < stepSectionEnd
            !isInIngredients && !isInSteps
        }

        val otherText = otherTextIndices.map { lines[it] }

        return Triple(ingredientSection, stepSection, otherText)
    }

    private fun areConsecutive(indices: List<Int>): Boolean {
        if (indices.isEmpty()) return false
        var consecutiveCount = 1
        for (i in 1 until indices.size) {
            if (indices[i] == indices[i-1] + 1) {
                consecutiveCount++
                // At least 3 consecutive lines
                if (consecutiveCount >= 3) return true
            } else {
                consecutiveCount = 1
            }
        }
        return false
    }

    private fun looksLikeIngredient(line: String): Boolean {
        // Check for patterns like quantities and measurements
        val hasNumbers = line.matches(".*\\d+.*".toRegex())
        val hasFractions = line.contains("½") || line.contains("¼") || line.contains("¾") ||
                line.contains("⅓") || line.contains("⅔") || line.contains("/")
        val hasMeasurementUnit = MEASUREMENT_UNITS.any { unit ->
            line.lowercase().contains(" $unit ") || line.lowercase().contains(" ${unit}s ")
        }

        return (hasNumbers || hasFractions) && (hasMeasurementUnit ||
                line.matches("^\\s*[\\d½¼¾⅓⅔]+.*$".toRegex()))
    }

    private fun looksLikeStep(line: String): Boolean {
        // Check for numbered steps or bullet points
        return line.matches("^\\s*\\d+\\..*$".toRegex()) || // "1. Do something"
                line.matches("^\\s*\\d+\\).*$".toRegex()) || // "1) Do something"
                line.matches("^\\s*[•\\-\\*]+.*$".toRegex()) // "• Do something" or "- Do something"
    }

    private fun extractDescription(lines: List<String>, otherText: List<String>): String {
        // Use other text not in ingredients or steps sections
        // Skip the first line (title) and any section headers
        return otherText.drop(1)
            .filter { !isSectionHeader(it) }
            .joinToString("\n")
    }

    // Ingredient extraction using the block structure
    private fun extractIngredients(ingredientLines: List<String>, structuredBlocks: Map<String, List<Pair<String, Rect>>>): List<FormattedRecipe.Ingredient> {
        // Use spatial layout for ingredient parsing when possible
        val spatialIngredients = extractIngredientsFromSpatialLayout(structuredBlocks)
        if (spatialIngredients.isNotEmpty()) {
            return spatialIngredients
        }

        // Fall back to text-based parsing if spatial layout doesn't have results
        val parsedIngredients = ingredientLines.mapNotNull { line ->
            parseIngredient(line)
        }

        // Combine fragmented ingredients
        return combineFragmentedIngredients(parsedIngredients)
    }

    // Function to extract ingredients based on spatial layout
    private fun extractIngredientsFromSpatialLayout(structuredBlocks: Map<String, List<Pair<String, Rect>>>): List<FormattedRecipe.Ingredient> {
        val result = mutableListOf<FormattedRecipe.Ingredient>()
        val lines = structuredBlocks["lines"] ?: return emptyList()

        // Get ingredient line candidates (lines that look like ingredients)
        val ingredientLines = lines.filter { (text, _) ->
            looksLikeIngredient(text)
        }

        for (line in ingredientLines) {
            val lineText = line.first

            // Try to identify quantity and name based on spatial position and regex patterns
            val parsedIngredient = parseIngredientWithPosition(lineText)
            if (parsedIngredient != null) {
                result.add(parsedIngredient)
            }
        }

        return result
    }

    // Parse ingredient
    private fun parseIngredientWithPosition(line: String): FormattedRecipe.Ingredient? {
        // First correct common OCR errors in this line
        val correctedLine = correctCommonErrors(line)

        // Regex patterns
        val quantityPattern = "^\\s*(\\d+[\\d.,/\\s-]*\\s*(?:[a-zA-Z]+)?(?:\\s+[a-zA-Z]+)?)\\s+(.+)$".toRegex()
        val fractionPattern = "^\\s*([0-9]\\s*/\\s*[0-9]|½|¼|¾|⅓|⅔)\\s+(.+)$".toRegex()
        val measurementPattern = "(?i)(cup|cups|tablespoon|tablespoons|tbsp|tsp|teaspoon|teaspoons|ounce|ounces|oz|pound|pounds|lb|lbs|gram|grams|g|kg|ml|l)s?\\s+of\\s+(.+)$".toRegex()

        // Process fractions and special characters
        val processedLine = processFractions(correctedLine)

        // Try each pattern in order
        quantityPattern.find(processedLine)?.let {
            val quantity = it.groupValues[1].trim()
            val name = it.groupValues[2].trim()
            return FormattedRecipe.Ingredient(name, quantity)
        }

        fractionPattern.find(processedLine)?.let {
            val quantity = it.groupValues[1].trim()
            val name = it.groupValues[2].trim()
            return FormattedRecipe.Ingredient(name, quantity)
        }

        measurementPattern.find(processedLine)?.let {
            val unit = it.groupValues[1].trim()
            val name = it.groupValues[2].trim()

            // Look for a number before the unit
            val numberMatch = "^\\s*(\\d+[\\d.,/\\s-]*)\\s*$unit".toRegex(RegexOption.IGNORE_CASE).find(processedLine)
            val quantity = if (numberMatch != null) {
                "${numberMatch.groupValues[1].trim()} $unit"
            } else {
                unit
            }

            return FormattedRecipe.Ingredient(name, quantity)
        }

        // Last resort: check if it contains any measurement unit
        for (unit in MEASUREMENT_UNITS) {
            val unitPattern = "(?i)(\\d+[\\d.,/\\s-]*)\\s*$unit\\s+(.+)$".toRegex()
            unitPattern.find(processedLine)?.let {
                val quantity = "${it.groupValues[1].trim()} $unit"
                val name = it.groupValues[2].trim()
                return FormattedRecipe.Ingredient(name, quantity)
            }
        }

        // If all else fails and it looks like an ingredient, put the whole line as the name
        return if (looksLikeIngredient(correctedLine)) {
            FormattedRecipe.Ingredient(correctedLine, "")
        } else {
            null
        }
    }
    // Process fractions and special characters in text
    private fun processFractions(text: String): String {
        return text
            .replace("½", "1/2")
            .replace("¼", "1/4")
            .replace("¾", "3/4")
            .replace("⅓", "1/3")
            .replace("⅔", "2/3")
            // Handle digit+slash+digit pattern carefully
            .replace("(\\d+)\\s*/\\s*(\\d+)".toRegex()) { matchResult ->
                "${matchResult.groupValues[1]}/${matchResult.groupValues[2]}"
            }
    }


    private fun parseIngredient(line: String): FormattedRecipe.Ingredient? {
        val parsedIngredient = parseIngredientWithPosition(line)
        if (parsedIngredient != null) {
            return parsedIngredient
        }

        // If structured parsing fails, fall back to the simplistic approach
        val correctedLine = correctCommonErrors(line)

        // If no structure is detected but it looks like an ingredient, use the whole line
        return if (looksLikeIngredient(correctedLine)) {
            FormattedRecipe.Ingredient(correctedLine, "")
        } else {
            null
        }
    }

    private fun combineFragmentedIngredients(ingredients: List<FormattedRecipe.Ingredient>): List<FormattedRecipe.Ingredient> {
        val combinedIngredients = mutableListOf<FormattedRecipe.Ingredient>()
        var currentIngredient: FormattedRecipe.Ingredient? = null

        for (ingredient in ingredients) {
            // Check if this looks like a continued fragment
            if (currentIngredient != null &&
                (ingredient.name.length < 3 ||
                        ingredient.quantity.contains("cup") && currentIngredient.name.contains("sugar") ||
                        ingredient.name.startsWith("powder") ||
                        ingredient.name.startsWith("sugar"))) {

                // Combine with previous ingredient
                currentIngredient = FormattedRecipe.Ingredient(
                    "${currentIngredient.name} ${ingredient.name}".trim(),
                    if (ingredient.quantity.isNotBlank()) ingredient.quantity else currentIngredient.quantity
                )
            } else {
                // Add the previous combined ingredient if it exists
                currentIngredient?.let { combinedIngredients.add(it) }
                // Start a new ingredient
                currentIngredient = ingredient
            }
        }

        // Add the last ingredient if it exists
        currentIngredient?.let { combinedIngredients.add(it) }

        return combinedIngredients
    }

    private fun extractSteps(stepLines: List<String>): List<String> {
        // Process step lines to combine multi-line steps with improved handling
        val processedSteps = mutableListOf<String>()
        var currentStep = ""
        var inStep = false

        for (line in stepLines) {
            // Regex for step identification
            val isNewStep = line.trim().matches("^\\s*(?:\\d+[.)\\s]+|[•\\-\\*]\\s*|[ivxIVX]+[.)\\s]+).*$".toRegex())

            if (isNewStep) {
                // New step detected, save the previous one if exists
                if (currentStep.isNotBlank()) {
                    processedSteps.add(currentStep.trim())
                }
                // Start a new step, removing the number/bullet
                currentStep = line.trim().replace("^\\s*(?:\\d+[.)\\s]+|[•\\-\\*]\\s*|[ivxIVX]+[.)\\s]+)\\s*".toRegex(), "")
                inStep = true
            } else if (inStep) {
                // Continue the current step
                currentStep += if (currentStep.isBlank()) line.trim() else " ${line.trim()}"
            } else {
                // Not in a step yet, but might be a step without numbering
                if (currentStep.isBlank()) {
                    currentStep = line.trim()
                    inStep = true
                } else {
                    // Add as a new step if it's substantive enough
                    if (line.length > 10) {
                        processedSteps.add(currentStep.trim())
                        currentStep = line.trim()
                    } else {
                        // Append to current step if it's short
                        currentStep += " ${line.trim()}"
                    }
                }
            }
        }

        // Add the last step if exists
        if (currentStep.isNotBlank()) {
            processedSteps.add(currentStep.trim())
        }

        // Post-process steps to merge too-short steps
        return mergeShortSteps(processedSteps)
    }

    private fun mergeShortSteps(steps: List<String>): List<String> {
        // Merge very short steps that might be part of the same instruction
        val mergedSteps = mutableListOf<String>()
        var currentStep = ""

        for (step in steps) {
            if (step.length < 20 && currentStep.isNotBlank()) {
                // This step is very short, merge with previous
                currentStep += " $step"
            } else {
                // Add previous step if exists
                if (currentStep.isNotBlank()) {
                    mergedSteps.add(currentStep)
                }
                currentStep = step
            }
        }

        // Add the last step
        if (currentStep.isNotBlank()) {
            mergedSteps.add(currentStep)
        }

        return mergedSteps
    }

    // Dictionary to help correct common OCR errors
    private val wordCorrections = mapOf(
        "bronies" to "brownies",
        "brosnies" to "brownies",
        "udy" to "fudgy",
        "ated" to "granulated",
        "posuder" to "powdered",
        "cuo" to "cup",
        "posder" to "powdered",
        "eb" to "of",
        "cocoa pouler" to "cocoa powder",
        "dlive" to "olive",
        "perpos" to "purpose",
        "tsp" to "tsp",
        "tbsp" to "tbsp",
        "tbsps" to "tbsps"
    )

    // Helper function to correct common errors
    private fun correctCommonErrors(text: String): String {
        var correctedText = text
        wordCorrections.forEach { (error, correction) ->
            correctedText = correctedText.replace(error, correction, ignoreCase = true)
        }
        return correctedText
    }
}
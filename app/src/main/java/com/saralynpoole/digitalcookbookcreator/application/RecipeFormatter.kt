package com.saralynpoole.digitalcookbookcreator.application

import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.text.Text

/**
 * Formats recognized text into a structured recipe format.
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
            "tbsps" to "tbsps",
            "prehet" to "preheat",
            "flow" to "flour",
            "3ranuated" to "granulated",
            "upanulated" to "granulated",
            "floar" to "flour",
            "upanulated" to "granulated",
            "peaheat" to "preheat",
            "ven" to "oven",
            "ranulated" to "granulated",
            "porpae" to "purpose",
            "flor" to "flour",
            "flo" to "flour",
            "flourur" to "flour",
            "Instrvctions" to "Instructions",
            "V2" to "1/2"
        )
    }

    // Formats recognized text into a structured FormattedRecipe object.
    fun formatRecipe(recognizedText: Text): FormattedRecipe {
        val fullText = correctCommonErrors(recognizedText.text)
        Log.d(TAG, "Formatting recipe from text: $fullText")

        // Process text blocks for better structural understanding
        val structuredBlocks = processTextBlocks(recognizedText)

        // Split the text into lines for traditional processing
        val lines = fullText.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // Return an empty recipe if no valid lines were found
        if (lines.isEmpty()) {
            return FormattedRecipe()
        }

        // Extract the recipe title (usually the first non-header meaningful line)
        val title = extractTitle(lines)

        // Find ingredient and step sections
        val (ingredientSection, stepSection, otherText) = identifySections(lines, structuredBlocks)

        // Extract ingredients from the identified section
        val ingredients = extractIngredients(ingredientSection, structuredBlocks)

        // Extract steps from the identified section
        val steps = extractSteps(stepSection, lines)

        // Extract description from leftover text
        val description = extractDescription(lines, otherText, steps)

        Log.d(TAG, "Extraction results - Title: $title, Ingredients: ${ingredients.size}, Steps: ${steps.size}")
        Log.d(TAG, "Steps: $steps")

        val cleanedIngredients = ingredients.distinctBy { it.name.lowercase().trim() }
        val cleanedSteps = steps.distinctBy { it.lowercase().trim() }

        // Return the fully structured recipe
        return FormattedRecipe(title, description, cleanedIngredients, cleanedSteps)
    }


    // Function to process ML Kit's text blocks into a structured map of lines and bounding boxes
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

    // Identifies ingredient section, step section, and other text sections.
    // Uses both explicit keywords and heuristic pattern matching if needed
    private fun identifySections(
        lines: List<String>,
        structuredBlocks: Map<String, List<Pair<String, Rect>>>
    ): Triple<List<String>, List<String>, List<String>> {
        var ingredientSectionStart = -1
        var ingredientSectionEnd = -1
        var stepSectionStart = -1
        var stepSectionEnd = -1

        // Patterns that help identify steps even without keywords
        val stepPatterns = listOf(
            "^\\s*(?:\\d+[.):\\s]+).*$".toRegex(),
            "^\\s*(?:step\\s*\\d+).*$".toRegex(RegexOption.IGNORE_CASE),
            "^\\s*(?:preheat|mix|stir|add|combine|beat|fold|bake|cook).*$".toRegex(RegexOption.IGNORE_CASE)
        )

        // First pass: Look for explicit section markers
        lines.forEachIndexed { index, line ->
            val lowerLine = line.lowercase()

            // Check for ingredient section
            if (INGREDIENT_SECTION_KEYWORDS.any { keyword -> lowerLine.contains(keyword) }) {
                // If we already found ingredients section, but found another one
                // (like "additional ingredients"), keep the first one
                if (ingredientSectionStart == -1) {
                    ingredientSectionStart = index
                }
            }

            // Check for step section
            if (STEP_SECTION_KEYWORDS.any { keyword -> lowerLine.contains(keyword) }) {
                // If we already found steps section but found another one
                // (like "additional steps"), keep the first one
                if (stepSectionStart == -1) {
                    stepSectionStart = index
                }
            }
        }

        // Determine section boundaries
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
        if (ingredientSectionStart == -1 || stepSectionStart == -1) {
            // Detect ingredient-like lines (quantities followed by ingredients)
            val likelyIngredientLines = mutableListOf<Int>()
            val likelyStepLines = mutableListOf<Int>()

            lines.forEachIndexed { index, line ->
                // Skip very first lines which might be title
                if (index <= 1) return@forEachIndexed

                // Check for ingredient patterns: measurements, quantities, etc.
                if (looksLikeIngredient(line)) {
                    likelyIngredientLines.add(index)
                }

                // Check for step patterns: numbered steps, bullet points
                if (looksLikeStep(line)) {
                    likelyStepLines.add(index)
                }
            }

            // If consecutive likely ingredient lines were found and we don't have an ingredients section yet
            if (likelyIngredientLines.isNotEmpty() && ingredientSectionStart == -1 && hasConsecutiveRuns(likelyIngredientLines, 3)) {
                // Find the start of the longest consecutive run
                val (start, end) = findLongestConsecutiveRun(likelyIngredientLines)
                ingredientSectionStart = start
                ingredientSectionEnd = end + 1 // +1 because end is inclusive
            }

            // If consecutive likely step lines were found and we don't have a steps section yet
            if (likelyStepLines.isNotEmpty() && stepSectionStart == -1 && hasConsecutiveRuns(likelyStepLines, 2)) {
                // Find the start of the longest consecutive run
                val (start, end) = findLongestConsecutiveRun(likelyStepLines)
                stepSectionStart = start
                stepSectionEnd = end + 1 // +1 because end is inclusive
            }
        }

        // Calculate final sections
        val ingredientSection: List<String> = if (ingredientSectionStart >= 0) {
            // Skip the section header if it looks like a header
            val startIdx = if (isSectionHeader(lines[ingredientSectionStart]))
                ingredientSectionStart + 1 else ingredientSectionStart
            lines.subList(startIdx, ingredientSectionEnd)
        } else {
            emptyList()
        }

        val stepSection: List<String> = if (stepSectionStart >= 0) {
            // Skip the section header if it looks like a header
            val startIdx = if (isSectionHeader(lines[stepSectionStart]))
                stepSectionStart + 1 else stepSectionStart
            lines.subList(startIdx, stepSectionEnd)
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

        // Enhanced detection for common ingredients that might be in description
        if (otherText.isNotEmpty()) {
            val ingredientLikeText = mutableListOf<Int>()

            otherText.forEachIndexed { index, line ->
                // Check for common ingredient patterns
                if (line.matches(".*\\b(cup|cups|tbsp|tsp|teaspoon|tablespoon|sugar|flour|butter)\\b.*".toRegex(RegexOption.IGNORE_CASE)) ||
                    line.matches(".*\\d+\\s*[/\\d].*".toRegex()) ||  // Has numbers with fractions
                    line.contains("granulated") ||
                    line.contains("flour") ||
                    line.contains("purpose") ||
                    line.matches(".*\\b[\\d½¼¾⅓⅔]+\\s*\\w+.*".toRegex())) {  // Quantity followed by word

                    ingredientLikeText.add(index)
                }
            }

            // If we have ingredient-like text in other sections, move it to ingredients
            if (ingredientLikeText.isNotEmpty()) {
                val newIngredients = ingredientLikeText.map { otherText[it] }
                val newOtherText = otherText.filterIndexed { index, _ -> index !in ingredientLikeText }

                return Triple(
                    ingredientSection + newIngredients,
                    stepSection,
                    newOtherText
                )
            }
        }

        return Triple(ingredientSection, stepSection, otherText)
    }

    // Determines if a line looks like a section header (e.g, "Ingredient")
    private fun isSectionHeader(line: String): Boolean {
        val lowerLine = line.lowercase()
        return INGREDIENT_SECTION_KEYWORDS.any { lowerLine.contains(it) } ||
                STEP_SECTION_KEYWORDS.any { lowerLine.contains(it) }
    }

    // Extracts the recipe title (usually the first meaningful line)
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

    // Extracts the recipe description
    private fun extractDescription(lines: List<String>, otherText: List<String>, steps: List<String>): String {
        // Filter out lines that look like ingredients or steps
        val filteredText = otherText.drop(1)
            .filter { line ->
                !isSectionHeader(line) &&
                        !looksLikeIngredient(line) &&
                        !steps.any { step -> line.contains(step, ignoreCase = true) } &&
                        !line.matches(".*\\b(cup|cups|tbsp|tsp|teaspoon|tablespoon)\\b.*".toRegex(RegexOption.IGNORE_CASE)) &&
                        !line.matches(".*\\b(granulated|flour|sugar|butter|oil)\\b.*".toRegex(RegexOption.IGNORE_CASE)) &&
                        !line.matches(".*\\b\\d+[/\\d].*".toRegex()) &&  // Has numbers with fractions
                        !line.matches("^\\s*(?:step\\s*\\d+|\\d+\\.\\s*|\\d+\\)|preheat|mix|stir|add|combine|beat|fold|bake|cook).*$".toRegex(RegexOption.IGNORE_CASE))
            }
            .joinToString("\n")
            .trim()

        return filteredText
    }

    // Ingredient extraction using the block structure
    private fun extractIngredients(ingredientLines: List<String>, structuredBlocks: Map<String, List<Pair<String, Rect>>>): List<FormattedRecipe.Ingredient> {
        // Use spatial layout for ingredient parsing when possible
        val spatialIngredients = extractIngredientsFromSpatialLayout(structuredBlocks)

        // Fallback to extract based on text line patterns
        val textIngredients = ingredientLines.mapNotNull { parseIngredient(it) }

        // Combine both lists and remove duplicates by normalized name
        val allIngredients = (spatialIngredients + textIngredients).distinctBy {
            it.name.lowercase().trim()
        }

        return consolidateIngredients(allIngredients)
    }

    // Function to extract ingredients based on spatial layout (bounding boxes)
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

    // Parses an ingredient from a single line
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

    // Parse ingredient using spatial patterns and regex
    private fun parseIngredientWithPosition(line: String): FormattedRecipe.Ingredient? {
        // First correct common OCR errors in this line
        val correctedLine = correctCommonErrors(line)

        // Handle single-word ingredient fragments that might be quantities or units
        if (correctedLine.matches("^\\s*(cup|cups|tbsp|tsp|l\\.?\\d*)\\s*$".toRegex(RegexOption.IGNORE_CASE))) {
            return FormattedRecipe.Ingredient("", correctedLine.trim())
        }

        // Handle single words that look like ingredients
        if (correctedLine.matches("^\\s*(sugar|flour|butter|eggs|milk|salt|pepper)\\s*$".toRegex(RegexOption.IGNORE_CASE))) {
            return FormattedRecipe.Ingredient(correctedLine.trim(), "")
        }

        // Handle fragments like "granulated" or "all purpose flour"
        if (correctedLine.contains("granulated") ||
            correctedLine.contains("purpose") ||
            correctedLine.contains("flour")) {
            return FormattedRecipe.Ingredient(correctedLine.trim(), "")
        }

        // Process fractions and special characters
        val processedLine = processFractions(correctedLine)

        // Regex patterns
        val quantityPattern = "^\\s*(\\d+[\\d.,/\\s-]*\\s*(?:[a-zA-Z]+)?(?:\\s+[a-zA-Z]+)?)\\s+(.+)$".toRegex()
        val fractionPattern = "^\\s*([0-9]\\s*/\\s*[0-9]|½|¼|¾|⅓|⅔)\\s+(.+)$".toRegex()
        val measurementPattern = "(?i)(cup|cups|tablespoon|tablespoons|tbsp|tsp|teaspoon|teaspoons|ounce|ounces|oz|pound|pounds|lb|lbs|gram|grams|g|kg|ml|l)s?\\s+of\\s+(.+)$".toRegex()

        // Extract cases like "3/4 cup sugar" or "1 cup flour"
        val combinedPattern = "^\\s*(\\d+[\\d.,/\\s-]*\\s*(?:cup|cups|tbsp|tsp|teaspoon|tablespoon)s?)\\s+(.+)$".toRegex(RegexOption.IGNORE_CASE)
        combinedPattern.find(processedLine)?.let {
            val quantity = it.groupValues[1].trim()
            val name = it.groupValues[2].trim()
            return FormattedRecipe.Ingredient(name, quantity)
        }

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

    // Consolidates duplicate or fragmented ingredients into cleaner entries
    private fun consolidateIngredients(ingredients: List<FormattedRecipe.Ingredient>): List<FormattedRecipe.Ingredient> {
        val result = mutableListOf<FormattedRecipe.Ingredient>()

        // Group related ingredient fragments
        val fragments = mutableMapOf<String, MutableList<FormattedRecipe.Ingredient>>()

        // Group by common keywords
        ingredients.forEach { ingredient ->
            val key = when {
                ingredient.name.contains("flour") || ingredient.name.contains("purpose") -> "flour"
                ingredient.name.contains("sugar") || ingredient.name.contains("granulated") -> "sugar"
                ingredient.name.contains("butter") -> "butter"
                ingredient.name.contains("egg") -> "egg"
                ingredient.name.contains("chocolate") -> "chocolate"
                ingredient.name.contains("vanilla") -> "vanilla"
                ingredient.name.contains("baking") -> "baking"
                ingredient.name.contains("salt") -> "salt"
                ingredient.name.contains("oil") -> "oil"
                else -> ingredient.name.take(3).lowercase().trim() // Group by first 3 chars for anything else
            }

            if (!fragments.containsKey(key)) {
                fragments[key] = mutableListOf()
            }
            fragments[key]!!.add(ingredient)
        }

        // Consolidate each group
        fragments.values.forEach { group ->
            if (group.size == 1) {
                result.add(group[0])
            } else {
                // Combine name and quantity from fragments
                val combinedName = group.joinToString(" ") { it.name }.trim()
                val combinedQuantity = group.find { it.quantity.isNotBlank() }?.quantity ?: ""

                result.add(FormattedRecipe.Ingredient(combinedName, combinedQuantity))
            }
        }

        return result
    }

    // Combines fragmented ingredient entries that likely belong together.
    // Useful when OCR splits a quantity from its corresponding ingredient name
    // (e.g., "1/2" on one line, "sugar" on the next).
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

    // Extracts steps
    private fun extractSteps(stepLines: List<String>, allLines: List<String>): List<String> {
        val processedSteps = mutableListOf<String>()

        // First try to extract from designated step section
        if (stepLines.isNotEmpty()) {
            var currentStep = ""

            for (line in stepLines) {
                val trimmedLine = line.trim()
                // Skip empty lines
                if (trimmedLine.isBlank()) continue

                // Check for numbered step patterns (very common in recipes)
                val isNumberedStep = trimmedLine.matches("^\\s*(?:\\d+[.):\\s]+).*$".toRegex())

                if (isNumberedStep || currentStep.isBlank()) {
                    // Save previous step if exists
                    if (currentStep.isNotBlank()) {
                        processedSteps.add(currentStep.trim())
                    }

                    // Start new step, removing any numbering
                    currentStep = trimmedLine.replace("^\\s*(?:\\d+[.):\\s]+)\\s*".toRegex(), "")
                } else {
                    // Continue current step
                    currentStep += " " + trimmedLine
                }
            }

            // Add final step
            if (currentStep.isNotBlank()) {
                processedSteps.add(currentStep.trim())
            }
        }

        // If no steps extracted from step section, scan all lines for step-like content
        if (processedSteps.isEmpty()) {
            // Find lines that look like steps across the entire recipe
            for (line in allLines) {
                val trimmedLine = line.trim()

                // Check for common step patterns
                if (trimmedLine.matches("^\\s*(?:step\\s*\\d+|\\d+\\.\\s*|\\d+\\)|preheat|mix|stir|add|combine|beat|fold|bake|cook).*$".toRegex(RegexOption.IGNORE_CASE))) {
                    // Remove step numbering if present
                    val cleanedStep = trimmedLine.replace("^\\s*(?:step\\s*\\d+[.:)]\\s*|\\d+[.):]\\s*)".toRegex(RegexOption.IGNORE_CASE), "")
                    processedSteps.add(cleanedStep)
                }
            }
        }

        return processedSteps
    }

    // Merges very short steps that most likely belong the the previous one
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

    // Determines if a line looks like an ingredient based on content patterns.
    private fun looksLikeIngredient(line: String): Boolean {
        if (looksLikeStep(line)) return false
        val trimmedLine = line.trim()
        // Skip very short lines
        if (trimmedLine.length < 3) return false

        // Check for common fragments that indicate ingredients
        if (trimmedLine.matches(".*\\b(granulated|purpose|flour|sugar|cups?)\\b.*".toRegex(RegexOption.IGNORE_CASE))) {
            return true
        }

        // Check for single letter 'q' pattern (likely OCR mistake for quantity)
        if (trimmedLine == "q" || trimmedLine.startsWith("q ")) {
            return true
        }

        // Check for patterns like quantities and measurements
        val hasNumbers = trimmedLine.matches(".*\\d+.*".toRegex())
        val hasFractions = trimmedLine.contains("½") || trimmedLine.contains("¼") ||
                trimmedLine.contains("¾") || trimmedLine.contains("⅓") ||
                trimmedLine.contains("⅔") || trimmedLine.contains("/")

        // Expand measurement unit detection
        val hasMeasurementUnit = MEASUREMENT_UNITS.any { unit ->
            trimmedLine.lowercase().contains(" $unit ") ||
                    trimmedLine.lowercase().contains(" ${unit}s ") ||
                    trimmedLine.lowercase().contains(" $unit") ||  // Unit at end
                    trimmedLine.lowercase().contains(" ${unit}s") ||  // Plural unit at end
                    trimmedLine.lowercase().matches(".*\\d+\\s*$unit.*".toRegex()) ||  // Number followed by unit
                    trimmedLine.lowercase().matches(".*\\d+\\s*${unit}s.*".toRegex()) ||  // Number followed by plural unit
                    trimmedLine.lowercase() == unit ||  // Just the unit by itself
                    trimmedLine.lowercase() == "${unit}s"  // Just the plural unit by itself
        }

        // Expanded list of common ingredients
        val commonIngredients = listOf(
            "salt", "pepper", "oil", "water", "sugar", "flour", "granulated", "purpose",
            "butter", "egg", "eggs", "garlic", "onion", "vanilla", "chocolate", "milk",
            "cream", "baking", "powder", "soda", "cinnamon"
        )

        val hasCommonIngredient = commonIngredients.any {
            trimmedLine.lowercase().contains(it)
        }

        return (hasNumbers || hasFractions || hasMeasurementUnit || hasCommonIngredient ||
                trimmedLine.matches("^\\s*[\\d½¼¾⅓⅔]+.*$".toRegex()))
    }

    // Determines if a line looks like a step instruction based on patterns like numbering or common cooking verbs
    private fun looksLikeStep(line: String): Boolean {
        // Check for numbered steps or bullet points
        return line.matches("^\\s*\\d+\\..*$".toRegex()) || // "1. Do something"
                line.matches("^\\s*\\d+\\).*$".toRegex()) || // "1) Do something"
                line.matches("^\\s*[•\\-\\*]+.*$".toRegex()) // "• Do something" or "- Do something"
    }

    // Helper function to find the longest consecutive run in a list of indices
    private fun findLongestConsecutiveRun(indices: List<Int>): Pair<Int, Int> {
        if (indices.isEmpty()) return Pair(-1, -1)

        var longestRunStart = indices[0]
        var longestRunEnd = indices[0]
        var currentRunStart = indices[0]
        var currentRunEnd = indices[0]

        for (i in 1 until indices.size) {
            if (indices[i] == indices[i-1] + 1) {
                // Continue the current run
                currentRunEnd = indices[i]
            } else {
                // Start a new run
                if (currentRunEnd - currentRunStart > longestRunEnd - longestRunStart) {
                    longestRunStart = currentRunStart
                    longestRunEnd = currentRunEnd
                }
                currentRunStart = indices[i]
                currentRunEnd = indices[i]
            }
        }

        // Check if the last run is the longest
        if (currentRunEnd - currentRunStart > longestRunEnd - longestRunStart) {
            longestRunStart = currentRunStart
            longestRunEnd = currentRunEnd
        }

        return Pair(longestRunStart, longestRunEnd)
    }

    // Helper function to check if there are consecutive runs of at least minLength
    private fun hasConsecutiveRuns(indices: List<Int>, minLength: Int): Boolean {
        if (indices.size < minLength) return false

        var consecutiveCount = 1
        for (i in 1 until indices.size) {
            if (indices[i] == indices[i-1] + 1) {
                consecutiveCount++
                if (consecutiveCount >= minLength) return true
            } else {
                consecutiveCount = 1
            }
        }

        return false
    }

    // Checks if a list of indices are consecutive
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

    // Process fractions and special characters in text
    private fun processFractions(text: String): String {
        return text
            .replace("½", "1/2")
            .replace("¼", "1/4")
            .replace("¾", "3/4")
            .replace("⅓", "1/3")
            .replace("⅔", "2/3")
            // Handle digit+slash+digit pattern 
            .replace("(\\d+)\\s*/\\s*(\\d+)".toRegex()) { matchResult ->
                "${matchResult.groupValues[1]}/${matchResult.groupValues[2]}"
            }
    }

    // Helper function to correct common OCR errors in a block of text
    private fun correctCommonErrors(text: String): String {
        var correctedText = text
        wordCorrections.forEach { (error, correction) ->
            correctedText = correctedText.replace(error, correction, ignoreCase = true)
        }
        return correctedText
    }
}
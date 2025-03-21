package com.saralynpoole.digitalcookbookcreator.di

import android.content.Context
import com.saralynpoole.digitalcookbookcreator.data.database.AppDatabase
import com.saralynpoole.digitalcookbookcreator.data.repository.IngredientRepository
import com.saralynpoole.digitalcookbookcreator.data.repository.RecipeRepository
import com.saralynpoole.digitalcookbookcreator.data.repository.StepRepository
import com.saralynpoole.digitalcookbookcreator.domain.usecase.IngredientUseCase
import com.saralynpoole.digitalcookbookcreator.domain.usecase.RecipeUseCase
import com.saralynpoole.digitalcookbookcreator.domain.usecase.StepUseCase

/**
 * Dependency container for the application.
 * Holds references to all of the dependencies used in the app and initializes
 * them when the app starts.
 */
object DependencyContainer {
    private var database: AppDatabase? = null
    private var recipeRepository: RecipeRepository? = null
    private var ingredientRepository: IngredientRepository? = null
    private var stepRepository: StepRepository? = null
    private var recipeUseCase: RecipeUseCase? = null
    private var ingredientUseCase: IngredientUseCase? = null
    private var stepUseCase: StepUseCase? = null

    /**
     * Initializes the dependency container.
     */
    fun initialize(context: Context) {
        // Initialize the database instance
        database = AppDatabase.getDatabase(context)

        // Initialize the repositories with their corresponding DAOs from the database
        recipeRepository = RecipeRepository(database!!.recipeDAO())
        ingredientRepository = IngredientRepository(database!!.ingredientDAO())
        stepRepository = StepRepository(database!!.stepDAO())

        // Initialize the use cases with their corresponding repositories
        recipeUseCase = RecipeUseCase(recipeRepository!!)
        ingredientUseCase = IngredientUseCase(ingredientRepository!!)
        stepUseCase = StepUseCase(stepRepository!!)
    }

    // Getter methods for the use cases
    fun getRecipeUseCase(): RecipeUseCase = recipeUseCase!!
    fun getIngredientUseCase(): IngredientUseCase = ingredientUseCase!!
    fun getStepUseCase(): StepUseCase = stepUseCase!!
}

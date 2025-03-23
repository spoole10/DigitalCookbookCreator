package com.saralynpoole.digitalcookbookcreator.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saralynpoole.digitalcookbookcreator.data.dao.RecipeDAO
import com.saralynpoole.digitalcookbookcreator.data.dao.IngredientDAO
import com.saralynpoole.digitalcookbookcreator.data.dao.StepDAO
import com.saralynpoole.digitalcookbookcreator.data.database.exceptions.DatabaseConnectionException
import com.saralynpoole.digitalcookbookcreator.domain.entity.RecipeEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.IngredientEntity
import com.saralynpoole.digitalcookbookcreator.domain.entity.StepEntity

/**
 * Database class for the application
 */
@Database(
    entities = [
        RecipeEntity::class,
        IngredientEntity::class,
        StepEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    // DAOs for each entity
    abstract fun recipeDAO(): RecipeDAO
    abstract fun ingredientDAO(): IngredientDAO
    abstract fun stepDAO(): StepDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Returns the singleton instance of the database.
        // If the instance is null, it creates a new one using Room.databaseBuilder.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Creates a new instance of the database if it doesn't exist
                val instance = try {
                    // Uncomment to simulate a database connection error.
                    //throw DatabaseConnectionException("Cannot connect to the database.")
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "recipe_database"
                    ).build()
                } catch (e: Exception) {
                    Log.e("AppDatabase", "Error creating database: ${e.message}")
                    throw DatabaseConnectionException("Unable to connect to database.", e)
                }
                // Sets the newly created instance to INSTANCE
                INSTANCE = instance
                // Returns the instance
                instance
            }
        }
    }
}
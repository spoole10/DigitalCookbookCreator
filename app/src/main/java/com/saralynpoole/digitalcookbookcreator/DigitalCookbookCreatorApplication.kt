package com.saralynpoole.digitalcookbookcreator

import android.app.Application
import android.widget.Toast
import com.saralynpoole.digitalcookbookcreator.data.database.exceptions.DatabaseConnectionException
import com.saralynpoole.digitalcookbookcreator.di.DependencyContainer

/**
 * Custom Application class for the DigitalCookbookCreator application.
 */
class DigitalCookbookCreatorApplication : Application() {
    // Initializes the dependency container when the application is created
    override fun onCreate() {
        super.onCreate()
        try {
            DependencyContainer.initialize(this)
        } catch (e: DatabaseConnectionException) {
            // Notify the user about the database connection issue
            Toast.makeText(this, e.message ?: "Error connecting to the database", Toast.LENGTH_LONG)
                .show()

        }
    }
}
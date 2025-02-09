package com.saralynpoole.digitalcookbookcreator

import android.app.Application
import com.saralynpoole.digitalcookbookcreator.di.DependencyContainer

/**
 * Custom Application class for the DigitalCookbookCreator application.
 */
class DigitalCookbookCreatorApplication : Application() {
    // Initializes the dependency container when the application is created
    override fun onCreate() {
        super.onCreate()
        DependencyContainer.initialize(this)
    }
}
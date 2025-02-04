package com.saralynpoole.digitalcookbookcreator

import android.app.Application
import com.saralynpoole.digitalcookbookcreator.di.DependencyContainer

class DigitalCookbookCreatorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyContainer.initialize(this)
    }
}
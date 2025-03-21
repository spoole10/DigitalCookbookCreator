plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    //id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.saralynpoole.digitalcookbookcreator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.saralynpoole.digitalcookbookcreator"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.text.recognition)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:2.5.0")
    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.4.0")
    implementation ("androidx.camera:camera-camera2:1.4.0")
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("androidx.camera:camera-view:1.4.0")

    // ML Kit for text recognition
    implementation ("com.google.mlkit:text-recognition:16.0.0")

    // For handling permissions
    implementation ("com.google.accompanist:accompanist-permissions:0.28.0")

    // For handling image loading and manipulation
    implementation ("io.coil-kt:coil-compose:2.4.0")

    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.4.0")
    implementation ("androidx.camera:camera-camera2:1.4.0")
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("androidx.camera:camera-view:1.4.0")
    implementation ("androidx.compose.material:material-icons-extended:1.5.0")

    // ML Kit Text Recognition
    implementation ("com.google.mlkit:text-recognition:16.0.0")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Permission handling
    implementation ("com.google.accompanist:accompanist-permissions:0.30.1")

    //val hilt_version = "2.44"
   // implementation ("com.google.dagger:hilt-android:$hilt_version")
   // ksp("com.google.dagger:hilt-android-compiler:$hilt_version")


   // implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

}
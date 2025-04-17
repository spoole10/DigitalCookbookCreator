# Technologies Used
This page outlines the technologies and tools used to build the Digital Cookbook Creator Android application, as well as the rationale behind each technology decision.

---

## Technologies Chosen

The following table describes each technology used in the application as well as the purpose for each technology.

| Technology | Purpose |
|------------|---------|
| **Kotlin 2.0** | Programming Language |
| **Android Studio 2024.2.1** | IDE |
| **Jetpack Compose 1.7.3** | UI Framework |
| **CameraX 1.4.0** | Camera Integration |
| **Google ML Kit Text Recognition v2.0** | Optical Character Recognition (OCR) |
| **SQLite 3.46.1** | Embedded Database Engine |
| **Room 2.6.1** | ORM & DAO Abstraction Layer |
| **Gradle 8.9** | Build System |
| **GitHub** | Version Control |


---

## Why These Technologies?

This section describes the rationale behind the decision to use each technology.

- **Kotlin 2.0**  
  Google's recommended language for Android development. Its null safety, concise syntax, and Jetpack-first support made it the best choice for building the app cleanly and safely.

- **Android Studio 2024.2.1**  
  Provides advanced tools like the emulator, performance profiler, and testing tools. This makes it the most productive environment for Kotlin projects.

- **Jetpack Compose 1.7.3**  
  Android’s modern toolkit for native UI development. It was chosen because of its ability to simplify and accelerate the development of Android UI implementation.

- **CameraX 1.4.0**  
  Offers lifecycle-aware camera handling and wide device compatibility. Perfect for the OCR feature that requires in-app image capture.

- **Google ML Kit Text Recognition v2.0**  
  Provides fast and private on-device OCR. This is ideal for converting handwritten recipes without needing cloud connectivity.

- **SQLite 3.46.1**  
  A lightweight embedded database that allows users to store all recipe data locally and access it offline.

- **Room 2.6.1**  
  Abstracts SQLite access using DAOs and entities. Supports type safety and relationships with minimal boilerplate code.

- **Gradle 8.9**  
  Used for builds, version control, and dependency management. Supports modular, reproducible project builds.

- **GitHub**  
  Used for version control.

---

## Is the App Deployed in the Cloud?

No. The app is currently offline and runs fully on the user's device.  
All recipe data is stored locally using Room/SQLite, and ML Kit runs on-device for OCR.

---

## Best Practices

The following table describes best practices that were used in the development of Digital Cookbook Creator.

| Practice | Description |
|---------|-------------|
| **Error Logging** | Android Logcat was used for structured logging and debugging throughout development |
| **Modular Structure** | The project is organized into clean architecture layers: `application`, `domain`, `data`, and `presentation`. This separation improves maintainability, testability, and scalability. |
| **Test Cases** | Manual test cases were created for each feature. These included form validation, OCR accuracy, and recipe data persistence. See the [Architecture And Design](ArchitectureAndDesign.md) page for a full list of test cases. |

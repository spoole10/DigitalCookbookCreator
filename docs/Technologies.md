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

## Technologies Learned During This Project

Throughout the development of Digital Cookbook Creator, I intentionally chose to learn several new technologies to expand my skill set and better align with modern Android development practices. These tools weren't just useful for the app but also represented meaningful learning opportunities that helped me grow as a developer.

- **Kotlin 2.0**  
  I had never used Kotlin before this project, but I chose to learn it because it's the official language for Android development. Learning Kotlin gave me experience with null safety, concise syntax, and better integration with Jetpack libraries—all of which helped me write clean, maintainable code.

- **Jetpack Compose**  
  While I was more familiar with XML-based layouts, I wanted to learn Jetpack Compose because it represents the future of Android UI development. It also offered a more modern, declarative way to build user interfaces. By learning Compose, I was able to create dynamic, state-aware UI components much more efficiently.

- **CameraX**  
  Integrating camera functionality was new to me, and I chose CameraX because it simplifies a traditionally complex task. Learning CameraX allowed me to implement photo capture in a way that was lifecycle-aware and device-compatible, which was important for the recipe-scanning feature.

- **Google ML Kit (Text Recognition)**  
  This was my first time working with machine learning in a mobile app. I chose ML Kit because it offered on-device OCR, and I was excited to learn how to process images and extract handwritten text. It pushed me to think about edge cases, image quality, and user fallback workflows.

- **SQLite and Room**  
  Although I was already familiar with SQL, I had never worked with SQLite in a mobile environment or used Room as an abstraction layer. Learning how to define entities, manage relationships, and create DAOs using Room helped me build a reliable offline-first database structure.

- **Android Studio**  
  I had no experience with Android Studio before this project, so I used this opportunity to explore its advanced features like the emulator, layout inspector, and Logcat. Becoming more comfortable with Android Studio significantly improved my development workflow.

- **Gradle**  
  Gradle was new to me at the start of this project. I learned how it handles build automation, dependency management, and project structuring in Android. Understanding Gradle gave me more control over the build process and helped me manage third-party libraries effectively.

This project gave me the chance to apply my existing knowledge while also diving into new technologies that strengthened both the app and my development experience overall.

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

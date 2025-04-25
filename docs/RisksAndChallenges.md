# Risks and Challenges

This page describes the risks encountered during the planning and development of the Digital Cookbook Creator application, how they were addressed, and the strategies used to manage them effectively.

---

## Risk: Learning New Technologies

**Challenge:**  
At the beginning of the project, I had no prior experience with Kotlin, Jetpack Compose, ML Kit, or Android Studio.

**How It Was Overcome:**  
I committed to learning each of these new technologies by reading official documentation, completing online tutorials, and creating practice apps to explore the language and Android Studio environment.

**Risk Management Approach:**  
This risk was identified during the planning phase. Time was specifically allocated to learn and experiment with each new technology before development of Digital Cookbook Creator began. This early mitigation kept the project on schedule.

---

## Risk: Database Setup

**Challenge:**  
I had limited experience with setting up a SQLite database with Room.

**How It Was Overcome:**  
A proof-of-concept project was created to test SQLite and Room configuration, entity relationships, and DAO queries. This allowed the me to confidently design and implement the database during development.

**Risk Management Approach:**  
This risk was identified during the planning phase. As such, time was allocated during the planning phase to learn and experiement with Room. As a result, the database was implemented efficiently and successfully.

---

## Risk: ML Kit OCR Accuracy

**Challenge:**  
The app uses Google ML Kit’s Text Recognition API to convert handwritten recipe photos into digital text. However, after integration, it became clear that ML Kit struggled to accurately extract structured content from real handwritten recipes.

**Limitation:**  
The initial proof-of-concept focused on simple, paragraph-style sentences and did not reflect the complexity of real handwritten recipe formats. As a result, it failed to reveal limitations in ML Kit’s performance. Once integrated into the app and tested with real recipe samples, it became evident that the OCR engine struggled with cluttered layouts, inconsistent handwriting, and structured lists common in recipes. These challenges impacted the accuracy and reliability of the extracted text.

**How It Was Managed:**  
To preserve usability despite OCR limitations, the application includes a built-in format recipe screen that allows users to manually correct or rewrite the extracted recipe before saving it. Additionally, the OCR functionality was isolated within a dedicated class (TextRecognitionManager.kt), allowing me to improve or replace the text recognition logic in future iterations without affecting the rest of the application.

**What Was Learned:**  
A proof-of-concept must accurately reflect real-world usage to be effective. Testing ML Kit on simple, paragraph-style text did not reveal its limitations with handwritten recipe formats. In future projects, I will  validate machine learning components using more thorough proof-of-concepts designed to mimic real world data and edge cases. This experience also emphasized the importance of having fallback workflows when working with AI-driven features, as perfect accuracy is not always guaranteed.

**Risk Management Approach:**  
Although the OCR limitation was not fully discovered until after integration, the risk was mitigated by designing the text recognition feature as a self-contained module (TextRecognitionManager.kt). This modular approach allows for future improvements or replacement of the OCR engine without impacting other parts of the application. Manual editing was included as a fallback to ensure the app remained functional and user-friendly, even when OCR output was inaccurate. This combination of isolation, flexibility, and user control helped maintain overall project stability.

# 🌱 Agriculture Assistant

An AI-powered Android application designed to support farmers with crop health analysis, disease classification, weather information, crop records, farming tips, and multilingual audio guidance.

## ✨ Features

- **User Authentication:** Registration, login, and password recovery using Firebase Authentication.
- **Crop Health Analysis:** Analyze crop-leaf images using TensorFlow Lite models.
- **Disease Classification:** Predict supported crop disease classes and display top predictions.
- **Crop Diary:** Maintain crop records using SQLite.
- **Weather Information:** Retrieve weather data using Retrofit and the OpenWeather API.
- **Farming Tips:** Access useful farming guidance.
- **Multilingual Audio Guides:** Listen to farming guidance in English and Hindi.
- **Saved Crops and Profile:** Access saved crop information and profile screens.
- **On-device Machine Learning:** Run crop analysis directly in the Android application.

## 🧰 Technology Stack

| Component | Technology |
|---|---|
| Android development | Kotlin, Android Studio |
| User interface | XML layouts, Android Activities |
| Authentication | Firebase Authentication |
| Cloud services | Firebase / Firestore integration |
| Local database | SQLite |
| API communication | Retrofit |
| Weather data | OpenWeather API |
| Machine learning | TensorFlow Lite |
| Model architecture | MobileNetV2 |
| Image input | 224 × 224 RGB images |

## 🧠 Machine Learning Models

Two TensorFlow Lite models are included in `app/src/main/assets/`.

| Model | File | Purpose | Reported evaluation |
|---|---|---|---|
| Healthy vs. Stressed | `Agriculture_Assistant_Healthy_vs_Stressed_MobileNetV2.tflite` | Binary crop-health classification | 98% accuracy |
| Crop Disease Classification | `Agriculture_Assistant_Disease_MobileNetV2.tflite` | Classification across 15 supported classes | 94.05% accuracy |

The development evaluation also reported 98.43% precision and 97.56% recall for the health model, and 93.54% macro F1 and 99.52% Top-3 accuracy for the disease model.

### Supported Disease Model Classes

1. Pepper bell — bacterial spot
2. Pepper bell — healthy
3. Potato — early blight
4. Potato — late blight
5. Potato — healthy
6. Tomato — bacterial spot
7. Tomato — early blight
8. Tomato — late blight
9. Tomato — leaf mold
10. Tomato — Septoria leaf spot
11. Tomato — two-spotted spider mites
12. Tomato — target spot
13. Tomato — yellow leaf curl virus
14. Tomato — mosaic virus
15. Tomato — healthy

### Model Workflow

1. Capture a crop-leaf image.
2. Resize the image to 224 × 224 pixels.
3. Preprocess the RGB image.
4. Run inference using TensorFlow Lite.
5. Display the predicted crop-health status and disease classes.

**Important:** These metrics come from development dataset evaluation. Real-world accuracy may vary due to lighting, camera quality, crop varieties, and field conditions. Predictions should be treated as decision-support information, not a substitute for expert agricultural advice.

## 🖼️ Screenshots

Add actual screenshots from your Android app to a folder named `screenshots/`.

| Dashboard | Crop Health |
|---|---|
| ![Dashboard](screenshots/dashboard.png) | ![Crop Health](screenshots/crop-health.png) |

| Crop Diary | Weather |
|---|---|
| ![Crop Diary](screenshots/crop-diary.png) | ![Weather](screenshots/weather.png) |

| Farming Tips | Audio Guide |
|---|---|
| ![Farming Tips](screenshots/farming-tips.png) | ![Audio Guide](screenshots/audio-guide.png) |

*The screenshot paths are placeholders. Add the corresponding image files to display them on GitHub.*

## 📁 Project Structure

```text
AgricultureAssistant/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   ├── Agriculture_Assistant_Disease_MobileNetV2.tflite
│   │   │   └── Agriculture_Assistant_Healthy_vs_Stressed_MobileNetV2.tflite
│   │   ├── java/com/example/agricultureassistant/
│   │   │   ├── ml/CropHealthML.kt
│   │   │   ├── CropHealthActivity.kt
│   │   │   ├── CropDiaryActivity.kt
│   │   │   └── WeatherActivity.kt
│   │   └── res/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Android Studio
- A compatible JDK and Android SDK
- An Android emulator or physical Android device
- A Firebase project configured for the application
- An OpenWeather API key for weather functionality

### 1. Clone the Repository

```bash
git clone https://github.com/RASIKA2004/AgricultureAssistant.git
cd AgricultureAssistant
```

### 2. Open in Android Studio

Open the cloned project folder in Android Studio and allow Gradle synchronization to complete.

### 3. Configure Firebase

1. Open the Firebase Console.
2. Select or create your Firebase project.
3. Register the Android application using the application's configured package ID.
4. Download the matching `google-services.json`.
5. Place it inside the `app/` directory.
6. Enable the Firebase services required by the application and configure appropriate security rules.

The Firebase configuration file is intentionally excluded from this repository. Obtain it from your authorized Firebase project.

### 4. Configure the Weather API

Configure your OpenWeather API key using the local Gradle property setup expected by the project's Gradle configuration. Do not hard-code secrets in source code or commit API keys to GitHub.

If a key has been exposed, rotate it with the provider and use the replacement key locally.

### 5. Build and Run

1. Open the project in Android Studio.
2. Complete Gradle synchronization.
3. Connect an Android device or start an emulator.
4. Run the application.
5. Grant the permissions needed for camera or location features when prompted.

## 🔐 Security Notes

- Keep API keys, passwords, signing keys, and service-account credentials out of Git.
- The repository's `.gitignore` excludes local configuration files, including `local.properties`, `gradle.properties`, and `app/google-services.json`.
- Restrict API keys through the relevant provider and monitor their usage.
- Configure Firebase Authentication and Firestore security rules appropriately.
- Review changes to `.gitignore` whenever adding new configuration files.

## 🛣️ Future Improvements

- Expand support for additional crops and diseases.
- Validate the models with more real-world field images.
- Improve confidence handling and uncertainty messaging.
- Add automated tests for crop analysis, weather, and database features.
- Add verified treatment recommendations from trusted agricultural sources.
- Add real screenshots and a demonstration video.

## 🔗 Repository

[View Agriculture Assistant on GitHub](https://github.com/RASIKA2004/AgricultureAssistant)

## 📄 License

No license is specified yet. Add a license file if you intend to define how others may use, modify, and distribute this project.
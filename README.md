# 🌱 AI Calories & Meal Tracker
[![Android SDK](https://img.shields.io/badge/SDK-36%2B-brightgreen.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9%20%2F%202.x-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![Gemini](https://img.shields.io/badge/AI-Gemini%203.5%20%2F%203.1-orange.svg)](https://deepmind.google/technologies/gemini)
[![Firebase](https://img.shields.io/badge/Firebase-Auth%20%26%20Firestore-yellow.svg)](https://firebase.google.com)

An advanced, AI-powered native Android application built using Kotlin, Jetpack Compose (Material 3), Room DB, Firebase, and Gemini API models. The application delivers carbon-powered metabolic intelligence, optical food image auditing, grounded recipe planning, and personalized nutrition advice.

---

## 📖 Table of Contents
- [✨ Core Capabilities](#-core-capabilities)
- [🛠️ Tech Stack & Architecture](#%EF%B8%8F-tech-stack--architecture)
- [🎨 UI & Navigation (Tabs)](#-ui--navigation-tabs)
- [📜 Project Constitution (Spec-Kit SDD Principles)](#-project-constitution-spec-kit-sdd-principles)
- [📝 Feature Specification (Prioritized User Journeys)](#-feature-specification-prioritized-user-journeys)
- [🚀 Setup & Running Locally](#-setup--running-locally)
- [🔬 Verification Plan & Test Automation](#-verification-plan--test-automation)
- [📋 Approval checklist: What Needs to be Configured to Approve the App](#-approval-checklist-what-needs-to-be-configured-to-approve-the-app)
- [📂 File Structure](#-file-structure)

---

## ✨ Core Capabilities
*   **AI Optical Camera Meal Scanner**: Points the camera at a dish, snaps a photo, or imports a gallery image. The app converts the image to base64 and invokes `gemini-3.5-flash` to identify the dish, calculate macros (calories, protein, carbs, fat), and write a concise health/fitness description returned in structured JSON.
*   **AI Diet Planner & Recipe Builder**: Integrates with `gemini-3.5-flash` utilizing **Google Search Grounding** to search current culinary trends and seasonal recipes (e.g. as of mid-2026), providing complete portion sizing guides, ingredient lists, and metabolic consumption strategies.
*   **AI Expert Clinical Nutrition Coach**: Integrates with `gemini-3.1-pro-preview` with **High Thinking Mode** enabled, conducting sports-science evaluations of biometric weight progress, water logged, steps synced, and daily food records to optimize muscle synthesis and metabolism.
*   **Biochemical BMI & TDEE Calculator**: Computes physiological Body Mass Index (BMI) and Total Daily Energy Expenditure (TDEE) based on biological gender, age, height, weight, and exercise multipliers, locking outcomes directly into user profile goals.
*   **Biometric Weight Timeline**: Logs daily weights to local SharedPreferences database, tracking net weight gain/loss trends over time.
*   **Offline Data Portability Engine**: Generates a single human-readable JSON block containing the user profile, weight journals, and meal logs, facilitating offline backup, migration, and restoration.
*   **Firebase Cloud Sync**: Authenticates users anonymously and synchronizes Room database entries to/from Firestore, falling back gracefully to local storage when Firebase is offline or unconfigured.

---

## 🛠️ Tech Stack & Architecture
The application is structured around a modern, reactive MVVM (Model-View-ViewModel) architecture:
*   **Frontend**: Kotlin, Jetpack Compose (Material 3), Android Jetpack Lifecycle (ViewModel, StateFlow, Compose-Navigation).
*   **Local Persistence**: Room Database (with SQLite backend) for caching logs, User profiles, and activities; SharedPreferences for lighter configurations and weight histories.
*   **Network & REST API**: Retrofit 2, OkHttp 3 (with custom network timeout limits), Moshi JSON Converters.
*   **Cloud Backend**: Firebase Authentication (Anonymous Login), Google Sign-in client integrations, and Firebase Cloud Firestore.
*   **AI Foundations**: Google Generative AI (Gemini Beta endpoints) invoking:
    *   `gemini-3.5-flash` (for fast JSON OCR-visual scanning and Google Search-grounded meal planning).
    *   `gemini-3.1-pro-preview` (with `thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH")` for deep reasoning clinical advice).

---

## 🎨 UI & Navigation (Tabs)
The application features a dark, carbon-matte aesthetic optimized for high-performance fitness telemetry. The bottom navigation bar contains five distinct operational nodes:
1.  **Calorie Tracker (Home)**: Displays the **Metabolic Compass** (a circular progress gauge tracking remaining calories against goals, adjusted dynamically for active calories burned), macronutrient ratios, step counters, and manual meal log diary cards.
2.  **Diet Planner**: Hosts pre-configured standard portion diets (e.g., Avocado Toast, Quinoa Seared Salmon, Tender Beef Pho) with direct one-click logging, alongside the Grounded Recipe Builder.
3.  **AI Scanner**: Integrates camera and gallery selective launchers, opening a futuristic viewfinder hud layout to analyze meal pictures and view raw AI payloads.
4.  **AI Coach**: Houses the Biochemical BMI & TDEE Calculator and the expert reasoning clinical advisor input.
5.  **Settings**: Configures Google Sandbox/Firebase sign-ins, smartwatch wearable sync options (Fitbit, Garmin, Apple Health, Samsung), profile target edits, the custom prompt/system instruction templates for Gemini scanner tuning, and the data backup importer.

---

## 📜 Project Constitution (Spec-Kit SDD Principles)
Aligned with the **[github/spec-kit](https://github.com/github/spec-kit)** methodology, this project is governed by a set of strict architectural articles to ensure quality, modularity, and testability.

| Article | Principle | Local Project Enforcement |
| :--- | :--- | :--- |
| **I** | **Library-First** | Data API and local databases are separated from UI screens. Room entity mappings, DAO implementations, and API clients reside inside separate namespaces (`com.example.data.*`). |
| **II** | **Observability Mandate** | The AI scanner supports a "Debug mode" toggle to directly display the raw JSON payload returned by Gemini. Input prompts and system instructions are fully configurable on-screen. |
| **III** | **Test-First Imperative** | No logic modifications without validating unit, Robolectric, and Roborazzi screenshot tests. The app ensures layout alignment and key values remain strictly correct via assertions. |
| **VII** | **Simplicity (YAGNI)** | Muted abstractions: Uses Compose features directly rather than building complex UI frameworks. Room and Firebase instances are accessed via standard singletons. |
| **VIII** | **Framework Trust** | Direct usage of Android SDK, Room ORM, and Firebase Firestore without custom abstraction layers, lowering the overhead for developer maintenance. |
| **IX** | **Integration-First Testing** | Uses Room local databases for unit tests and local network caching protocols. Mock endpoints are configured to fall back safely during offline test executions. |

---

## 📝 Feature Specification (Prioritized User Journeys)
Based on Spec-Kit's prioritized delivery framework, the core features map to these prioritized user stories:

### 👤 User Story 1: Manual Tracking & Vital Logs (Priority: P1 - MVP)
*   **Journey**: As a user, I can manually log my food (name, calories, protein, carbs, fat) and water, view a metabolic tracker that calculates my remaining calories (`Goal - Intake + Burned`), and update my biometric profile.
*   **Test**: Create a manual entry, click Save, and verify it updates the remaining calorie count and Room database state.
*   **Acceptance Criteria**:
    *   *Given* a daily goal of 2000 kcal, *When* I log a meal with 500 kcal, *Then* remaining calories MUST display 1500 kcal.
    *   *Given* no network connection, *When* I log a meal, *Then* it MUST persist to local Room DB and show in the diary.

### 📷 User Story 2: AI Optical Camera Scanning (Priority: P2 - Critical AI Core)
*   **Journey**: As a user, I can capture a meal picture or choose a preset sample. The app identifies the dish and automatically populates macro values using Gemini.
*   **Test**: Select the "Salmon Bowl" preset sample, click to scan, and assert that the dish is correctly parsed to a JSON macro object and added to the logged list.
*   **Acceptance Criteria**:
    *   *Given* a base64 image, *When* Gemini returns valid JSON, *Then* the app MUST display estimated protein, carbs, and fat.
    *   *Given* malformed JSON from the model, *When* parsing, *Then* the app MUST handle it gracefully and show the raw text payload.

### 🥗 User Story 3: Grounded Recipe Planning & Wearable Sync (Priority: P3 - Advanced features)
*   **Journey**: As a user, I can generate custom recipes using Google Search grounding, consult an AI coach for detailed clinical advice with thinking metrics, and sync steps from a smartwatch brand.
*   **Test**: Input "Vegan pho", click search, verify grounding results, and trigger the Wearable Sync simulator.
*   **Acceptance Criteria**:
    *   *Given* a user question, *When* Coach is queried, *Then* it MUST trigger a Pro Model call with "HIGH" thinking level.
    *   *Given* a synced brand, *When* syncing, *Then* it MUST append simulated steps and calories burned to the daily activity log.

---

## 🚀 Setup & Running Locally

### 1. Prerequisites
*   **Android Studio** (Ladybug or newer)
*   **Android SDK 36** (Target SDK `36`, Minimum SDK `24`)
*   **Gradle 8.x** and **Java 11 (JDK 11)** configured in Android Studio compiler settings.

### 2. Secrets Configuration
The app uses the **Secrets Gradle Plugin** to load API keys securely.
1.  In the project root, duplicate `.env.example` and rename it to `.env`:
    ```bash
    cp .env.example .env
    ```
2.  Open `.env` and replace `MY_GEMINI_API_KEY` with your actual Google AI Studio API key:
    ```ini
    GEMINI_API_KEY=AIzaSyYourActualAPIKeyHere
    ```

### 3. Build & Run
Open the project in Android Studio, wait for the Gradle sync to complete, and run the project:
*   **Run on Emulator/Device**: Click the **Run** button (green play icon) or press `Shift + F10`.
*   **Build APK**: Go to `Build > Build Bundle(s) / APK(s) > Build APK(s)`.

---

## 🔬 Verification Plan & Test Automation
The repository includes pre-configured unit, integration, and screenshot tests.

### Automated Tests Execution
Run tests from your terminal using Gradle:
```bash
# Run unit and Robolectric tests
./gradlew testDebugUnitTest

# Generate Roborazzi visual screenshot tests
./gradlew recordRoborazziDebug
```

### Manual Verification Path
1.  **AI Studio Scanner Verification**: Go to the AI Scanner tab, select a food preset, and verify it successfully extracts macros via the API and lists it in the Debug mode payload.
2.  **Offline Database Persistence**: Log food or log a weight timeline entry. Kill the app, toggle Airplane Mode, launch the app, and verify all data persists locally in Room DB and SharedPreferences.
3.  **Data Portability Audit**: Go to Settings, tap Export JSON, copy it to your clipboard. Modify a field, paste it into the Import field, and tap Validate & Hydrate to confirm local database updating.

---

## 📋 Approval Checklist: What Needs to be Configured to Approve the App
To ensure the application compiles, connects, and functions correctly for store/repository approval, complete the following items:

- [ ] **1. Gemini API Key Configuration**
  - Ensure a valid `GEMINI_API_KEY` is loaded. If left as `MY_GEMINI_API_KEY`, the app will display a configuration error on the Settings/Scanner tabs.
- [ ] **2. Firebase Credentials Configuration**
  - Currently, the app includes a placeholder `google-services.json`.
  - For full Cloud Sync and Sign-in functionality:
    1. Create a project in the Firebase Console (https://console.firebase.google.com).
    2. Register the package name `com.aistudio.nutritiontracker.pvwqzy` (configured in app/build.gradle.kts).
    3. Download the generated `google-services.json` and replace the one in `app/google-services.json`.
    4. Enable "Anonymous Authentication" and "Cloud Firestore" database in your Firebase console.
- [ ] **3. Android SDK 36 Environment Alignment**
  - Confirm Android SDK 36 (including API level 36, minor 1) is installed via the Android SDK Manager.
  - Set JDK 11 as the Gradle JVM in Android Studio settings.
- [ ] **4. Test Suite Approval**
  - Run `./gradlew testDebugUnitTest` and ensure all unit tests and Robolectric mock contexts pass.
  - Verify Roborazzi screenshot reference (`src/test/screenshots/greeting.png`) generates correctly.

---

## 📂 File Structure
```text
AI-calories-trackin-demo/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt        # Shutter Activity mounting Compose
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   └── GeminiClient.kt # Retrofit endpoints for Flash & Pro
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── AppDatabase.kt  # Room database declaration
│   │   │   │   │   │   └── *Dao.kt         # Dao interfaces (Meal, User, Activity)
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── *Log.kt         # Room Entity Schemas
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── AuthRepository.kt       # Firebase Auth / Offline sandbox helper
│   │   │   │   │       └── NutritionRepository.kt  # DB flow broker
│   │   │   │   └── ui/
│   │   │   │       ├── screens/
│   │   │   │       │   └── MainScreen.kt   # App screen tabs and Composable components
│   │   │   │       └── theme/              # Color, Type, Theme mappings
│   │   │   └── res/
│   │   │       └── values/strings.xml      # App resource strings
│   │   └── test/
│   │       └── java/com/example/
│   │           ├── ExampleUnitTest.kt      # Core calculations testing
│   │           ├── ExampleRobolectricTest.kt # Android context string test
│   │           └── GreetingScreenshotTest.kt # Roborazzi layout visual tester
│   ├── build.gradle.kts                    # App dependencies, Compile targets, Secrets mappings
│   └── google-services.json                # Firebase configuration placeholder
├── .env.example                            # Secrets placeholder mapping
├── build.gradle.kts                        # Root Gradle plugin definitions
├── settings.gradle.kts                     # Gradle repository bindings
└── metadata.json                           # AI Studio Project metadata definitions
```

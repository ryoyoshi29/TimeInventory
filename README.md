# TimeInventory

**Close the gap between your Ideal Day and Actual Day with AI-powered insights**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.9.3-4285F4?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## About the Project

### Motivation

How much of your day goes according to plan?

Most productivity apps focus on planning or tracking, but not comparing. TimeInventory bridges this
gap by visualizing the difference between your Planned Events (Ideal Day) and Log Events (Actual
Day), helping you understand where your time really goes.

For busy professionals, project managers, and students struggling with time management,
TimeInventory provides:

- **Visual comparison** of planned vs. actual time usage
- **AI-powered feedback** using Google's Gemini API to generate actionable KPT (Keep-Problem-Try)
  insights

### Key Features

**Timeline Visualization**

- Side-by-side comparison of planned events and actual logs
- Color-coded categories for quick recognition
- Timeline view showing time blocks and gaps

**AI-Powered Feedback**

- Analyzes your day's performance
- Generates structured KPT feedback:
    - **Keep**: What worked well
    - **Problem**: Areas needing improvement
    - **Try**: Actionable next steps

### Demo
https://github.com/user-attachments/assets/1b5baf52-c40c-411a-adbc-797424b6f742

---

## Tech Stack

### Core Technologies

- **Kotlin Multiplatform (KMP)**
- **Compose Multiplatform**
- **Material 3 Design System**

### Libraries & Frameworks

| Category          | Technology                                                               | Purpose                                                           |
|-------------------|--------------------------------------------------------------------------|-------------------------------------------------------------------|
| **Network**       | [Ktor Client](https://ktor.io/)                                          | HTTP communication with platform-specific engines (OkHttp/Darwin) |
| **Serialization** | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | Type-safe JSON parsing                                            |
| **Database**      | [Room](https://developer.android.com/jetpack/androidx/releases/room)     | Local persistence (Single Source of Truth)                        |
| **Async**         | Kotlin Coroutines & Flow                                                 | Reactive data streams with unidirectional data flow               |
| **DI**            | [Koin](https://insert-koin.io/)                                          | Dependency injection for KMP                                      |
| **Config**        | [BuildKonfig](https://github.com/yshrsmz/BuildKonfig)                    | Secure API key management                                         |
| **DateTime**      | [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)           | Multiplatform date/time handling                                  |

### AI Integration

**Gemini 2.5 Flash Lite** via REST API

- Unified implementation in `commonMain` using Ktor HTTP Client
- Platform-specific engines:
    - Android: OkHttp
    - iOS: Darwin (native URLSession)
- Structured JSON output using Gemini's Response Schema feature
- Custom error handling with sealed exception hierarchy

---

## Architecture

TimeInventory follows **clean architecture** principles with a **reactive, unidirectional data
flow (UDF)** model based
on [official Android architecture guidance](https://developer.android.com/topic/architecture).

**Detailed documentation:** [Architecture.md](docs/Architecture.md)

---

## Modularization

TimeInventory uses a **multi-module architecture** to improve build times, enforce separation of
concerns.

**Detailed documentation:** [Modularization.md](docs/Modularization.md)

---

## Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2.1) or later
- **Xcode** 15.3 or later (for iOS development)
- **JDK** 21 or later
- **Gemini API Key** ([Get yours here](https://aistudio.google.com/app/apikey))

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/TimeInventory.git
   cd TimeInventory
   ```

2. **Configure API Key**

   Copy the template file and add your Gemini API key:
   ```bash
   cp local.properties.template local.properties
   ```

   Edit `local.properties`:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   GEMINI_API_KEY=your_api_key_here
   ```

3. **Run the app**

   #### Android
   ```bash
   ./gradlew :composeApp:assembleDebug
   ./gradlew :composeApp:installDebug
   ```
   Or use the **Run** configuration in Android Studio.

   #### iOS
    - Open `iosApp/iosApp.xcodeproj` in Xcode
    - Select your target device/simulator
    - Press **Run** (⌘R)

   Or from terminal:
   ```bash
   ./gradlew :composeApp:iosSimulatorArm64Run
   ```

---

## Feature Walkthrough

Follow these steps to experience the core features:

### 1. **Create Planned Events (Your Ideal Day)**

- Navigate to the **Timeline** screen
- Tap the **"+"** button
- Select **"Planned Event"** tab
- Add events like:
    - 09:00 - 10:00: Team Meeting (Category: Work)
    - 10:00 - 12:00: Focus Time (Category: Work)
    - 12:00 - 13:00: Lunch Break (Category: Personal)

### 2. **Log Your Actual Day**

- Use the **Timer** button to track real-time activities
- Or manually add **"Log Events"** via the bottom sheet
- Enter actual time spent:
    - 09:00 - 10:30: Team Meeting (ran 30min over!)
    - 10:30 - 11:00: Email triage (unexpected)
    - 11:00 - 12:00: Focus Time (only got 1 hour)

### 3. **Generate AI Feedback**

- Navigate to the **Report** screen
- Tap **"Generate AI Feedback"** button
- Watch as Gemini analyzes your day and generates:
    - **Keep**: "Successfully completed team meeting"
    - **Problem**: "Meeting overrun reduced focus time by 1 hour"
    - **Try**: "Set stricter meeting time limits tomorrow"

### 4. **View Analytics**

- Scroll down to see category-wise time distribution
- Compare planned vs actual time in visual charts
- Identify patterns and improve planning

---

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Google Gemini API](https://ai.google.dev/)
- [Now in Android](https://github.com/android/nowinandroid) - Architecture inspiration

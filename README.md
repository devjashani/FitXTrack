# FitXTrack 🏋️‍♂️📈

FitXTrack is an Android fitness and wellness application built using **Kotlin**, **Jetpack Compose**, and **Firebase**. The app helps users manage workouts, track fitness progress, monitor health metrics, create workout plans, schedule training sessions, and maintain a healthier lifestyle through an intuitive and modern user interface.

---

## ✨ Features

### 🔐 Authentication

* User Sign In / Sign Up
* Firebase Authentication integration
* Secure user session management

### 🏠 Home Dashboard

* Personalized fitness dashboard
* Quick access to workouts and progress
* Health and activity overview

### 💪 Workout Management

* Push Day (Prime & Elite)
* Pull Day
* Leg Day (Prime & Elite)
* Arms Day
* Full Body Workout
* Abs & Core Workout
* Custom Workout Plan Creation

### 📅 Planning & Scheduling

* Weekly Workout Planner
* Calendar View
* Workout Scheduling
* Personal Training Booking

### 📊 Fitness Tracking

* Fitness Progress Monitoring
* Workout History Tracking
* Progress Cards & Statistics
* Goal-Based Fitness Tracking

### 🧘 Wellness Features

* Meditation Sessions
* Health Monitoring
* Activity Tracking

### 👤 Profile Management

* User Profile Editing
* Personal Information Management
* Settings & Preferences

---

## 🏗️ Tech Stack

### Frontend

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose

### Architecture

* MVVM (Model–View–ViewModel)
* Repository Pattern
* State Management with ViewModels

### Backend & Services

* Firebase Authentication
* Firebase Firestore (if configured)
* Android Health & Sensor APIs

### Android Features

* Activity Recognition
* Step Counter Support
* Accelerometer Support
* Body Sensor Integration

---

## 📂 Project Structure

```text
app/
├── src/main/java/
│   ├── com.yourorg.fitxtrackdemo/
│   │   ├── MainActivity.kt
│   │   ├── ui/theme/
│   │   │   ├── Screens
│   │   │   ├── ViewModels
│   │   │   ├── Navigation
│   │   │   ├── Repositories
│   │   │   └── UI Components
│   ├── data/
│   │   ├── HealthData.kt
│   │   ├── WorkoutData.kt
│   │   └── UserProfile.kt
│   └── manager/
│       ├── HealthManager.kt
│       ├── WorkoutHistoryManager.kt
│       └── SimpleHealthManager.kt
```

---

## 📱 Main Screens

* Authentication Screen
* Home Screen
* Workout Main Screen
* Weekly Planner Screen
* Calendar Screen
* Fitness Progress Screen
* Workout History Screen
* Meditation Screen
* Personal Training Screen
* Pricing Screen
* Profile Edit Screen
* Settings Screen

### Workout Screens

* Push Day Prime
* Push Day Elite
* Pull Day
* Leg Day Prime
* Leg Day Elite
* Arms Day
* Full Body
* Abs & Core

---

## ⚙️ Requirements

* Android Studio Hedgehog or newer
* JDK 17
* Android SDK 34
* Kotlin 1.9+
* Firebase Project

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/FitXTrack.git
cd FitXTrack
```

### 2. Open in Android Studio

Open the project folder in Android Studio.

### 3. Configure Firebase

1. Create a Firebase project.
2. Enable Authentication.
3. Download `google-services.json`.
4. Place the file inside:

```text
app/google-services.json
```

### 4. Sync Gradle

Allow Android Studio to sync all dependencies.

### 5. Run the App

Connect an Android device or start an emulator and run:

```bash
Run > Run 'app'
```

---

## 🔑 Permissions Used

```xml
android.permission.ACTIVITY_RECOGNITION
android.permission.BODY_SENSORS
```

### Optional Hardware Features

```xml
android.hardware.sensor.stepcounter
android.hardware.sensor.accelerometer
```

---

## 🧠 Architecture Overview

```text
UI (Jetpack Compose)
        ↓
ViewModels
        ↓
Repositories
        ↓
Firebase / Local Data / Health Managers
```

This structure keeps business logic separated from UI components and improves maintainability and scalability.

---

## 📸 Future Improvements

* Google Fit / Health Connect integration
* Nutrition & calorie tracking
* Wear OS support
* Social fitness challenges
* Cloud synchronization
* AI-powered workout recommendations
* Advanced analytics dashboard

---

## 🤝 Contributing

Contributions, feature requests, and bug reports are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Open a Pull Request

---

## 📄 License

This project is intended for educational and personal development purposes. Add your preferred open-source license before production use.

---

## 👨‍💻 Author

Developed as a modern Android fitness tracking and workout management application using Kotlin, Jetpack Compose, and Firebase.

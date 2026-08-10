# ⚡ Crackhead — Digital Wellbeing & Cooldown Enforcer

<p align="center">
  <img src="app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" width="100" height="100" alt="Crackhead Logo" />
</p>

<p align="center">
  <b>Break addictive app loops, master your focus, and reclaim your digital time.</b>
</p>

<p align="center">
  <a href="https://developer.android.com"><img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" /></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Language" /></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white" alt="UI Framework" /></a>
  <a href="https://developer.android.com/training/data-storage/room"><img src="https://img.shields.io/badge/Database-Room-00897B?style=for-the-badge&logo=sqlite&logoColor=white" alt="Database" /></a>
  <a href="https://m3.material.io"><img src="https://img.shields.io/badge/Design-Material%203-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3" /></a>
</p>

---

## 📌 Overview

**Crackhead** is a modern, privacy-focused Android application designed to combat doomscrolling and digital fatigue. Built natively with **Kotlin** and **Jetpack Compose**, Crackhead combines real-time application monitoring with intelligent cooldown enforcement to help users maintain healthy screen habits.

Unlike passive screen-time trackers, Crackhead actively monitors foreground app transitions using an **Accessibility Service** and **Usage Access API**, automatically triggering temporary cooldown locks when daily limits or continuous usage session thresholds are reached.

---

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| 🛡️ **Accessibility Service Enforcer** | High-precision background detector that instantly detects launched apps and enforces active cooldown screens. |
| ⏱️ **Smart Session Limits** | Set maximum allowed session durations (e.g. 15 mins) followed by forced cooldown periods (e.g. 30 mins lock). |
| 🔒 **Immersive Cooldown Overlay** | Full-screen blocking UI featuring a live countdown timer, motivational reminders, and strict bypass options. |
| 📊 **Real-Time Usage Tracking** | Direct integration with Android's `UsageStatsManager` for precise, tamper-resistant time logging. |
| 📈 **Insights & Analytics** | Detailed screen-time metrics, daily/weekly trend comparisons, block counts, and top app breakdown. |
| 🎨 **Material 3 UI & Theming** | Fully responsive, light/dark theme support with auto-generated color palettes for all installed apps. |
| 🔒 **100% Offline & Private** | All data stays strictly on-device inside an encrypted SQLite database managed by Room. |

---

## 📱 Screen Breakdown & Features

### 1. 🏠 Home Screen (Dashboard)
The primary command center providing an immediate overview of daily usage, monitored apps, and active restrictions.
* **Hero Screen Time Header**: Displays total active screen time versus cumulative daily limits with a dynamic progress indicator.
* **Stat Cards**: Quick indicators for total blocks triggered today and currently monitored application count.
* **Live App Cards**: Color-coded cards for each monitored app featuring animated progress bars, remaining session minutes, and quick toggle actions.

```
+-------------------------------------------------------+
|  📊 Crackhead                              [Active 🟢] |
|                                                       |
|  +-------------------------------------------------+  |
|  |  TOTAL SCREEN TIME                              |  |
|  |  2h 15m / 4h limit                [ 56% Used ]  |  |
|  +-------------------------------------------------+  |
|                                                       |
|  [ 🛑 3 Blocks Today ]       [ 📱 5 Apps Monitored ]  |
|                                                       |
|  Monitored Applications                               |
|  +-------------------------------------------------+  |
|  | (IG) Instagram                  42m / 30m limit |  |
|  | [========================......] [ COOLDOWN ]   |  |
|  +-------------------------------------------------+  |
|  | (YT) YouTube                    18m / 60m limit |  |
|  | [========......................] [ Active   ]   |  |
|  +-------------------------------------------------+  |
+-------------------------------------------------------+
```

---

### 2. 🎯 Select Apps Screen
An intuitive app catalog allowing users to choose which system or user applications to manage.
* **Search & Category Filters**: Search by app name or filter by categories (Social, Video, Games, Utilities).
* **Multi-Select Workflow**: Quickly select or deselect multiple apps in a batch.
* **Auto Color & Initials**: Automatically derives visual branding (initials and background color palette) from app package names.

---

### 3. 🛡️ Rules & Enforcement Screen
Manage active digital boundary rules and customize enforcement strictness.
* **Active Rules List**: Overview of all custom enforcement rules, including rule type, target apps, and activation triggers.
* **Strict Mode Toggle**: Enable strict mode to prevent rule editing or app unmonitoring during active cooldown periods.
* **Rule Controls**: Easily enable, edit, or remove enforcement rules.

---

### 4. ➕ Rule Builder Screen
A comprehensive step-by-step wizard for building tailored focus rules.
* **Rule Types**:
  * **Session Limit**: Enforce mandatory cooldown break after X continuous minutes of usage.
  * **Daily Time Cap**: Set maximum daily usage limit per application.
  * **Time Window Lockout**: Block specific apps during work hours, study time, or bedtime.
* **App Scope**: Target single applications or apply rules across groups of apps.

---

### 5. 📈 Insights & Analytics Screen
Visual statistics and historical trends to help analyze phone usage patterns over time.
* **Usage Charts**: Weekly comparison charts illustrating screen time spikes and focus improvements.
* **Block Frequency**: Total cooldown interventions triggered over days/weeks.
* **Top Distractions**: Ranked list of applications consuming the most attention.

---

### 6. ⚙️ Settings & System Status Screen
System integration management, permissions check, and app preferences.
* **Permission Diagnostics**: Real-time status indicators for **Usage Access** and **Accessibility Service** enablement.
* **Theme Options**: System default, AMOLED Dark Mode, and Light Mode preferences.
* **Data & Diagnostics**: Local database backup, data reset, and test cooldown trigger actions for validation.

---

### 7. 🔒 Cooldown Enforcement Screen (Overlay)
A full-screen activity (`CooldownActivity`) launched when an application violates an active rule or limit.
* **Live Countdown**: Animated timer showing remaining lock duration down to the second.
* **Motivational Reminders**: Mindful prompts highlighting the benefits of taking a break.
* **Emergency Bypass**: Controlled exit flow for urgent situations.

---

## 🛠️ Tech Stack & Architecture

Crackhead is engineered following modern Android architecture standards (MVVM + Clean Architecture):

* **Language**: [Kotlin](https://kotlinlang.org/) (100%)
* **UI Engine**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
* **State Management**: `ViewModel`, `StateFlow`, `collectAsStateWithLifecycle`
* **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) with KSP
* **Preferences**: DataStore Key-Value store (`ThemePreferences`)
* **Background Enforcer**: `AccessibilityService` + `ForegroundService` + `UsageStatsManager`
* **Concurrency**: Kotlin Coroutines & Asynchronous Flows

### Directory Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                  # Main entry point & navigation host
├── CooldownActivity.kt              # Full-screen cooldown enforcement overlay
├── data/
│   ├── Entities.kt                  # Room entities (MonitoredApp, Rule, DailySummary)
│   ├── Daos.kt                      # Data Access Objects
│   ├── CrackheadDatabase.kt         # Room Database configuration
│   ├── CrackheadRepository.kt       # Repository layer combining DB & System Services
│   └── ThemePreferences.kt          # Theme and user settings storage
├── service/
│   ├── CrackheadAccessibilityService.kt # Foreground app change detector
│   └── CrackheadMonitoringService.kt    # Background usage statistics collector
├── ui/
│   ├── MainViewModel.kt             # UI state holder & business logic dispatcher
│   ├── theme/                       # Color, Typography, and M3 Theme definitions
│   └── screens/
│       ├── HomeScreen.kt            # Usage dashboard & active app monitor
│       ├── SelectAppsScreen.kt      # App catalog & selection list
│       ├── RulesScreen.kt           # Rules list & strict mode controls
│       ├── NewRuleScreen.kt         # Custom rule creation wizard
│       ├── InsightsScreen.kt        # Analytics & screen time charts
│       └── SettingsScreen.kt        # Accessibility & preference configuration
```

---

## 🚀 Getting Started (Clone & Build Guide)

This section provides a beginner-friendly, step-by-step guide for developers testing or building the app themselves.

---

### 📋 Prerequisites

Before you start, make sure you have:
* **Git**: Installed on your system ([git-scm.com](https://git-scm.com/))
* **JDK 17+**: Java Development Kit 17 or higher
* **Android SDK**: Installed via Android Studio or standalone Command Line Tools
* **Min Android Device OS**: Android 8.0 (API Level 26) or newer

---

### 📦 1. Clone the Repository

Open your terminal or command prompt and clone the project:

```bash
git clone https://github.com/your-username/crackhead-android.git
cd crackhead-android
```

---

### 🛠️ 2. Build Universal APKs (CLI & Android Studio)

#### Method A: Using Command Line (Terminal / CMD)

You can build universal APK files directly without opening Android Studio.

* **Build Universal Debug APK (Recommended for Testing):**
  ```bash
  # Linux / macOS
  gradle :app:assembleDebug

  # Or if using Gradle Wrapper (if present)
  ./gradlew :app:assembleDebug

  # Windows (Command Prompt)
  gradlew :app:assembleDebug
  ```

* **Build Universal Release APK:**
  ```bash
  gradle :app:assembleRelease
  ```

#### Method B: Using Android Studio

1. Launch **Android Studio**.
2. Select **Open** and select the `crackhead-android` folder.
3. Wait for Gradle sync to complete.
4. From the top menu, go to **Build ➔ Build Bundle(s) / APK(s) ➔ Build APK(s)**.

---

### 📂 3. Locate Your Built APK Output

Once the build finishes, your compiled APK files are saved in the project output directories:

* **Universal Debug APK Location**:
  `app/build/outputs/apk/debug/app-debug.apk`
* **Universal Release APK Location**:
  `app/build/outputs/apk/release/app-release.apk`

---

### 📲 4. Install & Test on Your Android Device

#### Option 1: Via ADB (Android Debug Bridge)
Connect your phone with **USB Debugging** enabled and run:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### Option 2: Direct File Transfer
1. Copy `app-debug.apk` to your phone via USB cable, Google Drive, or messaging apps.
2. On your Android phone, open your **File Manager** app.
3. Tap `app-debug.apk` and approve **Install Unknown Apps** permissions if prompted.

---

### ⚠️ Troubleshooting: Why GitHub Release APKs Might Not Reflect Latest Changes

If you download a newly generated release APK from GitHub Releases or GitHub Actions and it **fails to install** or **does not reflect the latest UI changes**, check these common causes:

1. 🔐 **App Signature Mismatch (Most Common)**:
   * **Cause**: Automatic GitHub Actions releases generate temporary signing keystores per workflow run. If you previously installed an older build, Android OS strictly blocks updating over it due to signature mismatch.
   * **Fix**: **Uninstall the existing Crackhead app** from your Android phone completely, then install the newly downloaded release APK.

2. 🔢 **Version Code Not Incremented**:
   * **Cause**: Android OS requires a higher `versionCode` in `app/build.gradle.kts` to acknowledge a newer version over an older one.
   * **Fix**: Ensure `versionCode` is incremented (e.g., from `1` to `2`) when pushing new releases.

---

---

## 🔑 Required System Permissions

To function as a real-time screen enforcer, Crackhead requires two core Android system permissions:

1. **Usage Access Permission** (`android.permission.PACKAGE_USAGE_STATS`):
   * *Purpose*: Allows Crackhead to read accurate daily foreground app time from the OS.
   * *Enable*: Settings ➔ Security & Privacy ➔ Special App Access ➔ Usage Access ➔ Crackhead.

2. **Accessibility Service** (`CrackheadAccessibilityService`):
   * *Purpose*: Detects immediate foreground package switches to display the cooldown lock screen without delay.
   * *Enable*: Settings ➔ Accessibility ➔ Installed Apps / Downloaded Apps ➔ Crackhead.

---

## 🔒 Privacy & Security

Crackhead respects your privacy:
* 🛑 **Zero Telemetry**: No user analytics, trackings, or external API calls.
* 💾 **100% Local**: All app usage logs and custom rules are saved locally in `CrackheadDatabase`.
* 🔐 **No Cloud Dependencies**: Operates completely offline without accounts or login requirements.

---

## 📄 License

```
Copyright 2026 Crackhead Digital Wellbeing Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

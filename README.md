This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…


# KIT (Keep In Touch)

## Overview

KIT (Keep In Touch) is a Kotlin Multiplatform mobile application designed to help users maintain relationships by tracking contacts, scheduling reminders, and receiving notifications when it is time to reconnect with someone.

The goal of the application is to prevent important relationships from being forgotten by providing an easy-to-use contact management and reminder system.

---

## Team Members

* Joshua Newberry
* Evan Logan
* [Add Third Team Member]

---

## Technologies Used

### Frontend

* Kotlin Multiplatform (KMP)
* Compose Multiplatform
* SwiftUI (iOS Integration)

### Backend / Data

* Room Database
* SQLite
* Firebase Firestore

### Dependency Injection

* Koin

### Networking

* Ktor

### Development Tools

* GitHub
* Jira
* Lucidchart
* Android Studio
* Xcode

---

## Project Architecture

The application follows the MVVM (Model-View-ViewModel) architecture.

### Components

#### UI Layer

* Home Screen
* Contacts Screen
* Individual Contact Screen
* Calendar Screen
* Reminder Screen
* Settings Screen

#### ViewModels

* HomeViewModel
* ContactsViewModel
* CalendarViewModel

#### Data Layer

* AppDAO
* Room Database
* Repository Pattern

---

## Features

### Contact Management

* Create contacts
* View contacts
* Edit contact information

### Reminder Management

* Create reminders
* Track upcoming reminders
* Mark reminders as completed

### Notifications

* Local notification support
* Reminder alerts
* Daily reminder notifications

### Settings

* Notification preferences
* Application settings

---

## UML Diagrams

The project includes the following UML diagrams:

* Use Case Diagram
* Class Diagram
* Communication Diagram
* Sequence Diagram

Created using Lucidchart.

---

## Installation

### Android

1. Clone the repository

```bash
git clone https://github.com/joshuamnewberry/KIT.git
```

2. Open the project in Android Studio

3. Build and run the Android application

### iOS

1. Clone the repository

```bash
git clone https://github.com/joshuamnewberry/KIT.git
```

2. Build the shared framework

```bash
./gradlew :shared:assemble
```

3. Open:

```text
iosApp/iosApp.xcodeproj
```

4. Run on an iOS Simulator

---

## Testing

Testing includes:

* ViewModel unit testing
* Reminder scheduling verification
* Notification testing
* Android testing
* iOS testing

---

## Risk Analysis

### Risks Encountered

* Learning Kotlin Multiplatform development
* iOS framework configuration issues
* Team merge conflicts
* Firebase integration challenges
* Time constraints near project deadlines

### Mitigation Strategies

* Frequent GitHub commits
* Jira sprint tracking
* Team communication
* Scope reduction when necessary
* Incremental testing throughout development

---

## Retrospective

### What Went Well

* Successful Kotlin Multiplatform setup
* Cross-platform code sharing
* Effective use of GitHub and Jira
* Functional notification system

### What Could Be Improved

* Earlier testing implementation
* More frequent integration testing
* Better feature prioritization early in development

---

## Future Improvements

* Full calendar integration
* Cloud synchronization
* Contact import/export support
* Advanced notification scheduling
* Enhanced analytics and relationship tracking

---

## Repository

GitHub Repository:

https://github.com/joshuamnewberry/KIT


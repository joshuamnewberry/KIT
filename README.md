# KIT (Keep In Touch)

## Overview

KIT (Keep In Touch) is a Kotlin Multiplatform mobile application that helps users maintain relationships by tracking contacts, scheduling reminders, and sending notifications when it is time to check in with someone.

The project was developed as part of CIS 350 Software Engineering at Grand Valley State University.

---

## Team Members

* Joshua Newberry
* Evan Logan
* Korbin TenBrink

---

## Technologies Used

* Kotlin Multiplatform (KMP)
* Compose Multiplatform
* Room Database
* SQLite
* Firebase Firestore
* Koin Dependency Injection
* Ktor Networking
* Android Studio
* Xcode
* GitHub
* Jira
* Lucidchart

---

## Project Architecture

The application follows the MVVM (Model-View-ViewModel) architecture.

### Main Components

#### UI

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

* Room Database
* DAO Pattern
* Repository Pattern

---

## Features

### Contact Management

* View contacts
* Manage contact information
* Track relationship interactions

### Reminder System

* Create reminders
* Track upcoming reminders
* Mark reminders as completed

### Notifications

* Local notifications
* Reminder alerts
* Check-in reminders

### Settings

* Notification preferences
* User settings

---

## UML Diagrams

The project includes:

* Use Case Diagram
* Class Diagram
* Communication Diagram
* Sequence Diagram

Created using Lucidchart.

---

## Running the Project

### Android

```bash
./gradlew :androidApp:assembleDebug
```

### iOS

Build the shared framework:

```bash
./gradlew :shared:assemble
```

Open:

```text
iosApp/iosApp.xcodeproj
```

Run using Xcode.

---

## Testing

### Android Tests

```bash
./gradlew :shared:testAndroidHostTest
```

### iOS Tests

```bash
./gradlew :shared:iosSimulatorArm64Test
```

### Manual Testing

* Navigation testing
* Contact management testing
* Reminder testing
* Notification testing
* iOS simulator testing

---

## Risk Analysis

A full risk analysis — including risks encountered, mitigation strategies, and what could have been done better — is documented in [RiskAnalysis.md](RiskAnalysis.md).

---

## Retrospective

A full retrospective covering what went well, what could be improved, and lessons learned is documented in [Retrospective.md](Retrospective.md).

---


## Project Management (Jira)

The team used Jira for sprint planning and task tracking throughout the project.
A screenshot of the Jira board is included here:
[Jira Board Screenshots](https://github.com/joshuamnewberry/KIT/tree/main/Jira%20Screenshots)

---

## Repository

https://github.com/joshuamnewberry/KIT


## Documentation

- [Risk Analysis](RiskAnalysis.md)
- [Retrospective](Retrospective.md)
- [Testing Documentation](Testing.md)

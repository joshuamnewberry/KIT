# Testing

This document describes the testing strategy for KIT (Keep In Touch), covering automated unit and integration tests, manual testing, and continuous integration.

## Testing Strategy

KIT separates its logic into a shared Kotlin Multiplatform module, a data layer (DAO + repository), ViewModels, and platform UI. Testing focuses on the shared data layer, where the core application logic lives, because it is platform-independent and the highest-value code to verify automatically. UI and platform-specific behavior (notifications, navigation) are verified through manual testing on Android and iOS.

## Frameworks and Tools

- **kotlin.test** — assertions and test annotations, runs on both Android and iOS targets
- **kotlinx-coroutines-test** (`runTest`) — for testing `suspend` functions deterministically
- **GitHub Actions** — runs the full test suite automatically on every push and pull request

## Automated Tests

### Unit and Integration Tests — Data Layer (`AppDaoTest`)

The data access layer is verified using a `FakeAppDAO`, an in-memory implementation of the `AppDAO` interface. This allows the DAO contract and the relationships between entities to be tested without a live database, while still exercising real logic (filtering, sorting, and cross-reference joins). Because these tests verify multiple entities working together through their relationships (contacts linked to reminders and events), they function as integration tests of the data layer in addition to unit-testing individual operations.

Coverage includes:

**Contacts**
- Insert and retrieve a contact by ID
- Retrieving an unknown ID returns null
- Contacts are returned sorted by name
- Inserting with an existing ID replaces rather than duplicates
- Update changes stored values
- Delete removes the contact

**Reminders**
- Insert a reminder and link it to a contact via a cross-reference
- Retrieve all contacts associated with a reminder
- Retrieve due reminders at or before a given time (boundary verified)
- Update and delete reminders
- Removing a cross-reference unlinks a contact from a reminder

**Events**
- Insert an event and link it to a contact
- Delete an event
- Remove an event–contact cross-reference

**Important Dates**
- Insert and retrieve dates for a contact
- Dates are filtered by contact
- Upcoming dates are returned at or before a threshold (boundary verified)
- Update and delete important dates

### Platform Smoke Tests

`SharedLogicAndroidHostTest` and `SharedLogicIOSTest` are minimal tests that confirm the test runner and shared module build and execute correctly on each platform target. They do not test application logic.

## Running Tests Locally

Android:

```
./gradlew :shared:testAndroidHostTest
```

iOS:

```
./gradlew :shared:iosSimulatorArm64Test
```

## Continuous Integration

All automated tests run through GitHub Actions on every push and pull request. A passing run is required, which ensures changes to the shared data layer do not introduce regressions. The current build is passing.

## Manual Testing

Platform-specific behavior that is not covered by automated tests is verified manually on Android and iOS.

**Home Screen**
- Application launches successfully
- Weekly summary displays
- Navigation buttons function properly

**Contacts**
- Contact list loads
- Contact details can be viewed

**Notifications**
- Notification permissions are requested
- Notifications are delivered on iOS

**Settings**
- Settings screen loads
- Notification preferences can be changed

## Known Gaps and Future Work

- `getWeeklyInteractionCount` currently returns a placeholder value and is not yet backed by a real query; it is intentionally excluded from the automated suite until implemented. The Home screen weekly summary depends on this method.
- ViewModel and repository layers are not yet covered by automated tests and are currently verified manually.
- Integration testing against a real (non-fake) Room database and Firebase Firestore is planned but not yet implemented.

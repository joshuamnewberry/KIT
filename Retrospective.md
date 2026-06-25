# Retrospective

## What Went Well

- Successfully developed a Kotlin Multiplatform application with shared business logic between Android and iOS.
- Established a functional notification system.
- Set up continuous integration with GitHub Actions early and kept it green, so regressions in the shared logic were caught before reaching the main branch.
- Built an automated test suite for the data layer covering contacts, reminders, events, important dates, and interaction counting.
- Used Jira and GitHub effectively throughout development for sprint planning, task tracking, and version control.

## What Could Be Improved

- Begin testing earlier in the development cycle. Automated tests were added relatively late, which left the ViewModel and repository layers verified only manually.
- Reduce merge conflicts through smaller, more frequent commits and clearer division of work.
- Improve project planning for advanced features so they don't need to be simplified near the deadline.
- Build and run the full application on both platforms earlier, rather than relying on CI compilation checks, so platform-specific build issues surface while there is still time to address them.

## Lessons Learned

The team gained experience with Kotlin Multiplatform, mobile development, collaborative software engineering, version control, and agile project management practices.

A key lesson came near the end of the project, when we discovered that the full iOS application build was blocked by a Room/KSP code-generation incompatibility on the Kotlin/Native target — an issue that our continuous integration had not caught, because the iOS CI job compiles the shared module without running Room's KSP step. Rather than spending our remaining time on an uncertain deep fix that risked destabilizing a working build, we made a deliberate trade-off: we kept the shared logic, tests, and Android application fully working, demonstrated and tested on Android, and documented the iOS build as a known limitation. This taught us the value of validating the real end-to-end build on every target platform throughout development, not just at the end, and of recognizing when the responsible engineering decision is to scope around a problem and document it rather than chase it under deadline pressure.

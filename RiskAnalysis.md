# Risk Analysis

## Risks Encountered

### Kotlin Multiplatform Learning Curve
The team had limited experience with Kotlin Multiplatform development. This increased development time while learning platform-specific configuration and shared code architecture.

### Team Coordination
Multiple developers working on the same repository occasionally caused merge conflicts and overlapping work.

### iOS Build Configuration
Configuring the iOS application and integrating shared KMP code required additional troubleshooting and setup.

### iOS Room/KSP Build Incompatibility
Late in development, while attempting to build and run the full application on iOS, we encountered a build failure in Room's KSP (Kotlin Symbol Processing) annotation processor on the Kotlin/Native target. The error, `The @ConstructedBy definition must be an 'expect' declaration`, originates from the interaction between Room's database-builder code generation and the `expect`/`actual` declarations required for Kotlin Multiplatform. This is a known friction point between Room's multiplatform support and the Kotlin/Native toolchain, and it is sensitive to the exact versions of Room, KSP, and the Kotlin Gradle plugin in use.

Notably, this issue did not surface in continuous integration, because the CI iOS job compiles the shared module while excluding the Room KSP task for the Native target. The Android build, which exercises the same Room code generation, builds and runs without issue.

### Scope Management
Some planned features required simplification to ensure the project could be completed before the deadline.

## Mitigation Strategies

- Used GitHub for version control and GitHub Actions for continuous integration on every push
- Used Jira for sprint planning and task tracking
- Held regular team discussions to coordinate work and reduce merge conflicts
- Prioritized core functionality over advanced features
- For the iOS Room/KSP build issue, after confirming that a quick declaration-level fix did not resolve the error, we made a deliberate decision not to pursue a deeper fix close to the deadline, since resolving it would likely require coordinated version upgrades of Room, KSP, and the Kotlin plugin with a real risk of cascading breakage. Instead, we kept the shared module, all automated tests, and the Android application fully working and passing in CI, and selected the Android emulator as the platform for demonstration and final manual testing, since it exercises the identical shared logic. The iOS full-app build was documented as a known limitation rather than hidden.

## What Was Done Well

- The shared Kotlin Multiplatform module, containing the data layer and core application logic, was kept clean and fully testable, with an automated test suite covering contacts, reminders, events, important dates, and interaction counting.
- Continuous integration was set up early and kept green, catching regressions in the shared logic before they reached the main branch.
- Version control and sprint tracking were used consistently throughout the project.

## What Could Have Been Done Better

- Building and running the full application on both platforms earlier in the project, rather than relying on CI compilation checks alone, would have surfaced the iOS Room/KSP incompatibility sooner and left time to resolve it properly. In future work, pinning and validating a known-good combination of Room, KSP, and Kotlin versions for Kotlin/Native at project setup would prevent this class of late-stage build failure.
- Automated testing was introduced relatively late. Writing tests alongside each feature from the start would have improved coverage of the ViewModel and repository layers, which are currently verified manually.
- Earlier and more granular task division would have further reduced the merge conflicts encountered when multiple developers worked in the same areas of the codebase.

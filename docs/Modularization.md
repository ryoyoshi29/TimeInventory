# Modularization

This document explains the modularization strategy and the role of each module in the "TimeInventory" application.

## Overview

Modularization is the practice of breaking down a monolithic, single-module codebase into loosely coupled, self-contained modules.

### Benefits of Modularization

Modularization offers many benefits, including:

- **Scalability**
    - In a tightly coupled codebase, a single change can trigger a cascade of alterations. A properly modularized project embraces the [separation of concerns](https://en.wikipedia.org/wiki/Separation_of_concerns) principle. This empowers developers with greater autonomy while enforcing architectural patterns.

- **Ownership**
    - A module can have a dedicated owner who is responsible for maintaining the code and tests, fixing bugs, and reviewing changes.

- **Encapsulation**
    - Isolated code is easier to read, understand, test, and maintain.

- **Reduced Build Time**
    - Leveraging Gradle's parallel and incremental builds can reduce build times.

- **Reusability**
    - Proper modularization enables opportunities for code sharing and building multiple apps for different platforms from the same foundation.

## Modularization Strategy

It's important to note that there is no single modularization strategy that fits all projects. However, there are general guidelines that can be followed to maximize benefits and minimize downsides.

A basic module is simply a directory with a Gradle build script inside. Usually, a module consists of one or more source sets and possibly a collection of resources or assets. Modules can be built and tested independently. Due to Gradle's flexibility, there are few constraints on how you can organize your project. In general, you should strive for low coupling and high cohesion.

* **Low Coupling** - Modules should be as independent as possible from one another, so that changes to one module have zero or minimal impact on other modules. They should not possess knowledge of the inner workings of other modules.

* **High Cohesion** - A module should comprise a collection of code that acts as a system. It should have clearly defined responsibilities and stay within the boundaries of certain domain knowledge. For example, the `core:database` module in the TimeInventory app is responsible for accessing the local database, executing queries, and managing entities.

## Types of Modules in TimeInventory

The TimeInventory app contains the following types of modules:

* `app` module - Contains app-level classes and scaffolding classes that bind the rest of the codebase, such as `MainActivity`, `TimeInventoryApp`, and app-level controlled navigation. The `app` module depends on all `feature` modules and required `core` modules.

* `feature:` modules - Feature-specific modules that are scoped to handle a single responsibility in the app. If a class is needed only by one `feature` module, it should remain within that module. If not, it should be extracted into an appropriate `core` module. `feature` modules should not depend on other feature modules. They only depend on the `core` modules they require.

* `core:` modules - Common library modules containing auxiliary code and specific dependencies that need to be shared between other modules in the app. These modules can depend on other core modules, but they shouldn't depend on feature or app modules.

## Module Structure

Using the above modularization strategy, the TimeInventory app has the following module structure:

## Module Details

| Name | Responsibilities | Key Classes and Examples                                                                  |
|------|-----------------|-------------------------------------------------------------------------------------------|
| `app` | Integrates everything required for the app to function correctly. Includes UI scaffolding and navigation. | `TimeInventoryApp`<br>`MainActivity`<br>App-level navigation control                      |
| `feature:timeline` | Provides functionality for the timeline screen. Displays the ideal day and actual day side by side, and handles timer start/stop and history editing. All UI components specific to this screen are included. | `TimelineScreen`<br>`TimelineViewModel`<br>`TimeLogBlock`<br>`RoutineBlock`               |
| `feature:report` | Provides functionality for the report screen. Displays graphs comparing TimeLog and Routine, visualizes goal achievement rates, and shows AI feedback. All UI components specific to this screen are included. | `ReportScreen`<br>`ReportViewModel`<br>`PieChart`<br>`BarChart`<br>`FeedbackCard`         |
| `core:data` | Fetches app data from multiple sources and shares it across different features. Implements the Repository pattern and provides a single point of access to data. | `TimeLogRepository`<br>`RoutineRepository`<br>`CategoryRepository`<br>`FeedbackRepository` |
| `core:designsystem` | Provides the foundation of the design system. Includes customized Material 3 components, app theme, and icons. Does not depend on the data layer and provides only pure UI components that can be reused across all features. | `TimeInventoryTheme`<br>`PrimaryButton`<br>`Card`<br>`Icons`|
| `core:model` | Defines model classes used throughout the app. Has no dependencies on other modules and is the most foundational module. | `TimeLog`<br>`Routine`<br>`Category`<br>`Feedback`                                        |
| `core:database` | Provides local database storage using SQLDelight. Responsible for persisting TimeLog, Routine, and Category. | `TimeInventoryDatabase`<br>`TimeLogDao`<br>`RoutineDao`<br>`CategoryDao`                  |
| `core:network` | Responsible for making network requests and handling responses from remote data sources. Communicates with the Gemini API. | `GeminiApiClient`<br>`FeedbackNetworkDataSource`                                          |

## Module Dependency Rules

The module structure of TimeInventory follows these dependency rules:

### Basic Rules

1. **Feature modules do not depend on other feature modules**
    - `feature:timeline` and `feature:report` are independent of each other
    - Both indirectly share data through `core:data` and `core:designsystem`

2. **Core modules do not depend on feature modules**
    - `core:data` is unaware of `feature:timeline` or `feature:report`
    - This maintains the reusability of core modules

3. **App module depends on all feature modules**
    - Responsible for navigation and app-level integration
    - Does not directly depend on core modules (indirectly depends through features)

### Data Flow Direction
```
UI Layer (feature) 
    ↓ (data request)
Data Layer (core:data)
    ↓ (data retrieval)
Data Sources (core:database, core:network)
    ↓ (model usage)
Models (core:model)
```

## Modularization Approach in TimeInventory

The modularization approach for this app was defined taking into account the TimeInventory project roadmap, upcoming work, and new features. Additionally, we aimed to find the right balance between over-modularizing a relatively small app and showcasing a modularization pattern suitable for much larger codebases closer to real-world production environments.

### Module Division Criteria

TimeInventory divides modules based on the following criteria:

**Feature Division**
- Divided by screens that users can recognize
- `timeline` (timeline screen) and `report` (report screen) have clearly distinct responsibilities

**Data Layer Granularity**
- Currently, `core:data` as a single module is sufficient
- If Repositories and data sources increase in the future, further division will be considered

**UI Component Sharing**
- `core:design` provides common UI components used by multiple feature modules
- Feature-specific UI components are placed within each feature module

### Future Extensibility

This module structure anticipates the following future extensions:

- **Adding new feature modules**: For example, `feature:settings` (settings screen)
- **Adding core:datastore**: When user preference persistence becomes necessary
- **Adding core:common**: When common utility classes or extension functions increase

## References

- [Now in Android - Modularization](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)
- [Android Developers - App Architecture Guide](https://developer.android.com/topic/architecture)
- [Gradle - Multi-Project Builds](https://docs.gradle.org/current/userguide/multi_project_builds.html)
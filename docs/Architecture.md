# Architecture

## Overview

This document describes the architecture of the "TimeInventory" application, including its layer composition, key classes, and their interactions.

## Design Goals

The goals of the app architecture are as follows:

- Adhere to the [official architecture guidance](https://developer.android.com/jetpack/guide) as much as possible
- Improve development velocity by making the architecture easy to understand
- Make changes easier
- Make testing easier
- Reduce build times

## Architecture Overview

The app architecture consists of two layers: [Data Layer](https://developer.android.com/jetpack/guide/data-layer) and [UI Layer](https://developer.android.com/jetpack/guide/ui-layer).

> **Note**
> The Domain layer is omitted in this app. The official Android architecture considers the Domain layer as optional, and we judged it unnecessary at this point since the app does not have complex business logic. For more details, please refer to [this documentation](https://developer.android.com/topic/architecture?hl=en#domain-layer).

The architecture follows a reactive programming model with [unidirectional data flow](https://developer.android.com/jetpack/guide/ui-layer#udf). With the Data Layer as the foundation, the key concepts are:

- Higher layers react to changes in lower layers
- Events flow downward
- Data flows upward

Data flow is implemented using streams with [Kotlin Flows](https://developer.android.com/kotlin/flow).

## Data Flow Example: Displaying Graphs Comparing TimeLog and Routine, and AI Feedback on the Report Screen

When the app launches, it loads TimeLog (time records) and Routine (ideal day) from the local database. This data is combined and displayed to the user.

When the user presses the button to receive AI feedback, a prompt is constructed using TimeLog (time records) and Routine (ideal day) and posted to the Gemini API. The returned response is received and displayed on the screen.

The following diagram illustrates how events occur and data flows through related objects.

## Data Layer

The Data Layer contains app data and business logic. It is the single source of truth for all data in the app.

The Data Layer is composed of Repositories, and each Repository has its own model. For example, TimeLogRepository has TimeLogModel, and RoutineRepository has RoutineModel.

Repositories are the public API to other layers and provide the only way to access app data. Repositories typically provide one or more methods for reading and writing data.

### Reading Data

Reading of the application's primary data (such as TimeLog and Routine) is exposed as data streams. This means that each client of the Repository must be prepared to respond to data changes.

Reading is performed from local storage (such as a database) as the single source of truth (SSOT). Therefore, data integrity errors themselves are minimized. However, technical errors such as IO errors may still occur.

AI feedback data (FeedbackRepository) is temporary data retrieved in real-time from the external Gemini API and is outside the scope of SSOT principles for reading. This data is processed with the assumption of network communication errors and API-side errors.

**Example: Reading a List of TimeLogs**

A list of TimeLogs can be obtained by subscribing to the TimeLogRepository::getTimeLogsStream flow, which emits List<TimeLog>.

Whenever the list of TimeLogs changes (for example, when a new record is added), an updated List<TimeLog> is emitted to the stream.

### Writing Data

To write data, Repositories provide suspend functions. The caller is responsible for ensuring they are executed in the appropriate scope.

**Example: Starting a Timer**

Simply calling TimeLogRepository.startTimer with a category ID saves a new TimeLog record to the local database.

### Data Sources

Repositories may depend on one or more data sources. For example, RoutineRepository depends on the following data sources:

| Name | Implementation Technology | Purpose |
|------|--------------------------|---------|
| RoutineDao | SQLDelight | Persistent relational data related to Routine |

### Data Synchronization

Repositories are responsible for coordinating data between local storage and remote sources. When data is fetched from a remote data source, it is immediately written to local storage. Updated data is sent from local storage to the associated data stream and received by all listening clients.

This approach separates the app's reading and writing concerns so they don't interfere with each other.

## UI Layer

The [UI Layer](https://developer.android.com/topic/architecture/ui-layer) consists of:

- UI elements built using [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel) (KMP compatible)

ViewModels receive streams of data from Repositories and transform them into UI state. UI elements reflect this state and provide ways for users to interact with the app. These interactions are passed to ViewModels as events and processed.

### Modeling UI State

UI state is modeled as a hierarchy using sealed interfaces and their implementations as immutable data classes. State objects are only emitted through the transformation of data streams. This approach ensures:

- UI state represents the underlying app data (SSOT data provided by Repositories) formatted for UI display.
- UI elements exhaustively handle all possible states (Loading, Success, Error) defined in this sealed hierarchy.

**Example: Timeline Screen UI State**

The display of TimeLog and Routine on the timeline screen is modeled using TimelineUiState. This is a sealed interface that creates the following state hierarchy:

- Loading: Indicates data is being loaded
- Success: Indicates data has been loaded successfully. The Success state includes lists of TimeLog and Routine
- Error: Indicates an error has occurred

uiState is passed to the TimelineScreen composable, which handles all of these states.

### Transforming Streams to UI State

ViewModels receive streams of data as cold flows from one or more Repositories. These are either combined using combine or simply mapped to produce a single Flow of UI state. This single Flow is converted to a hot flow using stateIn. The conversion to StateFlow allows UI elements to read the last known state from the Flow.

**Example: Displaying TimeLog and Routine**

TimelineViewModel exposes uiState as StateFlow<TimelineUiState>. This hot flow is created by combining cold flows provided by TimeLogRepository and RoutineRepository. Whenever new data is emitted, it is converted to a TimelineUiState.Success state and exposed to the UI.

### Handling User Interactions

User actions are communicated from UI elements to ViewModels using regular method calls. These methods are passed to UI elements as lambda expressions.

**Example: Starting a Timer**

TimelineScreen receives a lambda expression called startTimer provided by TimelineViewModel.onStartTimer. Each time the user taps the timer start button, this method is called. The ViewModel processes this action and notifies TimeLogRepository.

## References

- [App Architecture Guide](https://developer.android.com/topic/architecture)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Now in Android - Architecture Discussion](https://github.com/android/nowinandroid/discussions/1273)

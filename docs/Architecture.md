# アーキテクチャ

## 概要

このドキュメントでは、「TimeInventory」アプリケーションのアーキテクチャについて、そのレイヤー構成、主要なクラス、およびそれらの相互作用を含めて説明します。

## 設計目標

アプリ・アーキテクチャの目標は以下の通りです：

* 可能な限り[公式のアーキテクチャガイド](https://developer.android.com/jetpack/guide)に準拠する
* アーキテクチャを理解しやすくすることで、開発速度（ベロシティ）を向上させる
* 変更を容易にする
* テストを容易にする
* ビルド時間を短縮する

## アーキテクチャの概要

アプリのアーキテクチャは、[データレイヤー](https://developer.android.com/jetpack/guide/data-layer)
と[UIレイヤー](https://developer.android.com/jetpack/guide/ui-layer)の2つのレイヤーで構成されています。

> **注意**
>
このアプリではドメインレイヤーを省略しています。Androidの公式アーキテクチャではドメインレイヤーはオプションとされており、現状このアプリには複雑なビジネスロジックがないため、不要と判断しています。詳細は[こちらのドキュメント](https://developer.android.com/topic/architecture?hl=en#domain-layer)
を参照してください。

アーキテクチャは、[単方向データフロー](https://developer.android.com/jetpack/guide/ui-layer#udf)
を持つリアクティブプログラミングモデルに従っています。データレイヤーを基盤とし、主な概念は以下の通りです：

* 上位レイヤーは下位レイヤーの変更に反応する
* イベントは下方向に流れる
* データは上方向に流れる

データフローは、[Kotlin Flows](https://developer.android.com/kotlin/flow)を使用したストリームで実装されています。

### アーキテクチャ図

## データフローの例：レポート画面でのAIフィードバック生成

この例では、ユーザーがレポート画面でAIによるフィードバック生成をリクエストした際のデータフローを示します。

### イベントフロー（下方向 - ユーザーアクション）

1. ユーザーが`ReportScreen`の **「フィードバックを生成 (Generate Feedback)」ボタン** をタップする。
2. `ReportScreen`は、コルーチンスコープ内で`getFormattedLogEvents()`および
   `getFormattedPlannedEvents()`サスペンド関数を呼び出すことで、このユーザー操作イベントを
   `ReportViewModel`に伝達する。
3. `ReportViewModel`は、データレイヤーの`LogEventRepository`および`PlannedEventRepository`
   に対してデータ取得操作をリクエストする。
4. 各リポジトリは、対応するDAO（`LogEventDao`, `PlannedEventDao`）に対してDB操作をリクエストする。
5. DAOは、**Roomデータベース**に対して具体的なSQL操作を実行する。

### データフロー（上方向 - データレスポンス）

6. Roomデータベースは、DB操作の結果としてのデータを各DAOに返す。
7. DAOは、そのデータをそれぞれのリポジトリに返す。
8. リポジトリはデータを整形し、`ReportViewModel`に返す。
9. `ReportViewModel`は最終的なプロンプト文字列を作成し、`generateFeedback(targetDate, prompt)`
   を呼び出すことで`AiFeedbackRepository`にイベントを伝達する。
10. `AiFeedbackRepository`は、`GeminiApiClient`（`core:network`由来）に対し、**Gemini API**
    エンドポイントへのHTTPS POSTリクエスト送信をリクエストする。
11. Gemini APIはリクエストを処理し、生成されたフィードバックを含むJSONレスポンスを返す。
12. `GeminiApiClient`は生のJSONレスポンスを`AiFeedbackRepository`に返す。
13. `AiFeedbackRepository`はJSONをDTOにパースし、ドメインモデル（`AiFeedback`）にマッピングして、
    `AiFeedbackDao`経由でローカルのRoomデータベースに保存する。
14. `AiFeedbackRepository`は`AiFeedback`オブジェクトを`ReportViewModel`に返す。
15. `ReportViewModel`は自身の`uiState`を`AiFeedbackState.Success(feedback)`に更新する。
16. `uiState`フローを収集（collect）している`ReportScreen`は、自動的に再コンポーズ（再描画）され、生成されたフィードバックをユーザーに表示する。

## データレイヤー

データレイヤーには、アプリのデータとビジネスロジックが含まれます。これはアプリ内のすべてのデータにおける「信頼できる唯一の情報源（Single
Source of Truth）」です。

データレイヤーはリポジトリ（Repository）で構成され、各リポジトリは1つまたは複数のデータソースに依存する可能性があります。リポジトリは他のレイヤーに対するパブリックAPIであり、アプリのデータにアクセスする唯一の方法を提供します。

リポジトリは通常、データの読み取りと書き込みのためのメソッドを1つ以上提供します。

### データの読み取り

アプリケーションの主要データ（LogEventやPlannedEventなど）の読み取りは、データストリームとして公開されます。これは、リポジトリの各クライアントがデータの変更に対応する準備をしておく必要があることを意味します。

読み取りは、「信頼できる唯一の情報源（SSOT）」としてのローカルデータベースから行われます。これによりデータ整合性のエラーは最小限に抑えられますが、I/O例外などの技術的なエラーは依然として発生する可能性があります。

**例：LogEventリストの読み取り**

LogEventのリストは、`getLogEventsByDateStream(date)`や
`getLogEventsByPeriodStream(startDate, endDate)`といった`LogEventRepository`
のフロー（Flow）メソッドを購読することで取得できます。これらは`Flow<List<LogEvent>>`を排出（emit）します。

LogEventのリストが変更されるたび（例：新しいレコードが追加された場合）、更新された`List<LogEvent>`
がストリームに排出されます。

### データの書き込み

データを書き込むために、リポジトリはサスペンド（suspend）関数を提供します。呼び出し元は、適切なスコープでこれらが実行されることを保証する責任があります。

**例：新しいPlannedEventの作成**

ユーザーが`EventBottomSheetContent`から新しいイベントを保存すると、`TimelineViewModel`はリポジトリのメソッド（例：
`plannedEventRepository.upsertPlannedEvent(...)`）を呼び出します。この`suspend`
関数は、DAOを介してローカルデータベースにレコードを挿入または更新します。

### データソース

リポジトリは1つ以上のデータソースに依存する場合があります。このアプリにおける主なデータソースは以下の通りです：

| 名前                     | モジュール           | 実装技術     | 目的                                                             |
|------------------------|-----------------|----------|----------------------------------------------------------------|
| DAO (例: `LogEventDao`) | `core:database` | **Room** | `LogEvent`, `PlannedEvent`, `AiFeedback`などに関連する永続的なリレーショナルデータ。 |
| Gemini API Client      | `core:network`  | **Ktor** | REST APIを介してAIベースのフィードバックを生成するための外部ソース。                        |

### データ同期

リポジトリは、ローカルストレージとリモートソース間のデータの調整を担当します。リモートデータソースからデータが取得されると、即座にローカルストレージに書き込まれます。更新されたデータはローカルストレージから関連するデータストリームに送られ、リッスンしているすべてのクライアントによって受信されます。

このアプローチにより、アプリの読み取りと書き込みの関心事が分離され、互いに干渉しなくなります。

## UIレイヤー

[UIレイヤー](https://developer.android.com/topic/architecture/ui-layer)は以下で構成されます：

* [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)を使用して構築されたUI要素
* [ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel) (KMP互換)

ViewModelはリポジトリからデータのストリームを受け取り、それをUI状態（UI
State）に変換します。UI要素はこの状態を反映し、ユーザーがアプリと対話する方法を提供します。これらの対話（インタラクション）はイベントとしてViewModelに渡され、処理されます。

### UI状態のモデリング

UI状態は、`sealed interface`とその実装である不変（immutable）な`data class`または`data object`
を使用して階層としてモデル化されます。状態オブジェクトは、データレイヤーからのデータストリームの変換、またはユーザーイベントへの応答としてのみ排出されます。このアプローチにより、以下が保証されます：

* UI状態は、UI表示用に整形された基盤となるアプリデータ（リポジトリによって提供されるSSOTデータ）を表す。
* UI要素は、`when`式を使用して、シール階層で定義されたすべての可能な状態（例：`Initial`, `Loading`,
  `Success`, `Error`）を網羅的に処理する。

**例：UI状態のモデリング**

各画面のUI状態は、この階層的アプローチを使用してモデル化されています。

* `TimelineScreen`の場合、`TimelineUiState`はデータレイヤーからの複数の`Flow`
  ストリームを組み合わせることでリアクティブに構築されます。基盤となるデータが変更されると、自動的に
  `uiState`の更新がトリガーされます。
* `ReportScreen`の場合、`ReportUiState`はより命令的（imperative）に更新されます。`ReportViewModel`
  は、ボタンクリックなどのユーザートリガーイベントに応じて、手動で状態を`Loading`、`Success`、または
  `Error`に設定します。

### ストリームからUI状態への変換

ViewModelはUI状態を`StateFlow`として公開します。この状態は、主に2つの方法で構築できます：

1. **リアクティブ**：データの変更を継続的に監視する必要がある画面（例：`TimelineScreen`
   ）の場合、ViewModelはリポジトリからデータストリーム（`Flow`）を受け取ります。これらは`combine`や
   `flatMapLatest`などの演算子を使用して単一のUI状態の`Flow`に結合され、その後`stateIn`を使用して
   `StateFlow`に変換されます。これにより、UIは常に信頼できる情報源からの最新データを反映します。
2. **命令的**：単発のユーザーアクションによって駆動される画面（例：`ReportScreen`）の場合、ViewModelは
   `MutableStateFlow`を保持します。ユーザーイベントが発生すると、ViewModelは必要なビジネスロジック（リポジトリの
   `suspend`関数の呼び出しなど）を実行し、その結果を用いて手動で`StateFlow`の値（例：
   `_uiState.value = UiState.Success(data)`）を更新します。

**例：`TimelineViewModel`のリアクティブアプローチ**

`TimelineViewModel`は`uiState`を`StateFlow<TimelineUiState>`として公開します。このホットフロー（Hot
Flow）は、`LogEventRepository`と`PlannedEventRepository`によって提供されるコールドフロー（Cold
Flows）を組み合わせることで作成されます。新しいデータが排出されると、それは`TimelineUiState.Success`
状態に変換され、UIに公開されます。

### ユーザーインタラクションの処理

ユーザーのアクションは、通常のメソッド呼び出しを使用してUI要素からViewModelに伝達されます。これらのメソッドは、ラムダ式としてUI要素に渡されます。

**例：AIフィードバックの生成**

`ReportScreen`は`rememberCoroutineScope()`を使用して`CoroutineScope`を取得します。ユーザーが「Generate
Feedback」ボタンをタップすると、`onClick`ラムダがコルーチンを起動します。このコルーチン内で、
`ReportViewModel`の`suspend`関数を呼び出して整形されたテキストを取得し、最終的なプロンプトを構築した後、
`viewModel.generateFeedback(prompt)`を呼び出します。ViewModelはこのアクションを処理し、
`AiFeedbackRepository`に通知します。

## 参考文献

* [アプリ アーキテクチャ ガイド](https://developer.android.com/topic/architecture)
* [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
* [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
* [Now in Android - Architecture Discussion](https://github.com/android/nowinandroid/discussions/1273)
# モジュール化

このドキュメントでは、「TimeInventory」アプリケーションにおけるモジュール化戦略と各モジュールの役割について説明します。

## 概要

モジュール化とは、モノリシックな（単一の）コードベースを、疎結合で自己完結した複数のモジュールに分割する手法です。

### モジュール化のメリット

モジュール化には、以下のような多くのメリットがあります：

* **拡張性（スケーラビリティ）**
*
密結合なコードベースでは、たった一つの変更が連鎖的な修正を引き起こす可能性があります。適切にモジュール化されたプロジェクトは、[関心の分離（Separation of Concerns）](https://ja.wikipedia.org/wiki/%E9%96%A2%E5%BF%83%E3%81%AE%E5%88%86%E9%9B%A2)
の原則を採用しています。これにより、アーキテクチャパターンを強制しつつ、開発者により大きな自律性を与えることができます。


* **オーナーシップ**
* 各モジュールに対して、コードやテストの保守、バグ修正、変更のレビューに責任を持つ専任のオーナーを割り当てることができます。


* **カプセル化**
* 隔離されたコードは、読みやすく、理解しやすく、テストや保守も容易になります。


* **ビルド時間の短縮**
* Gradleの並列ビルドやインクリメンタル（増分）ビルドを活用することで、ビルド時間を短縮できます。


* **再利用性**
* 適切なモジュール化により、コード共有の機会が生まれ、同じ基盤から異なるプラットフォーム向けに複数のアプリを構築することが可能になります。

## モジュール化戦略

すべてのプロジェクトに適合する唯一のモジュール化戦略は存在しない点に注意が必要です。しかし、メリットを最大化し、デメリットを最小限に抑えるための一般的なガイドラインは存在します。

基本的なモジュールは、単にGradleビルドスクリプトを含むディレクトリに過ぎません。通常、モジュールは1つ以上のソースセットと、場合によってはリソースやアセットの集合で構成されます。モジュールは独立してビルドおよびテストが可能です。Gradleの柔軟性により、プロジェクト構成の制約はほとんどありませんが、一般的には「低結合・高凝集」を目指すべきです。

* **疎結合（Low Coupling）** -
  モジュール同士は可能な限り独立しているべきです。そうすることで、あるモジュールへの変更が他のモジュールに与える影響をゼロ、または最小限に抑えられます。モジュールは、他のモジュールの内部動作に関する知識を持つべきではありません。
* **高凝集（High Cohesion）** -
  モジュールは、一つのシステムとして動作するコードの集合体であるべきです。明確に定義された責任を持ち、特定のドメイン知識の範囲内に留まる必要があります。例えば、TimeInventoryアプリの
  `core:database` モジュールは、ローカルデータベースへのアクセス、クエリの実行、エンティティの管理のみを担当しています。

## TimeInventoryにおけるモジュールの種類

TimeInventoryアプリには、以下の種類のモジュールが含まれています：

* `composeApp` モジュール - `MainActivity`、`TimeInventoryApp`
  、アプリレベルのナビゲーションなど、コードベースの残りの部分を束ねるアプリレベルのクラスや土台となるクラスを含みます。
  `composeApp` モジュールは、すべての `feature` モジュールと必要な `core` モジュールに依存します。
* `feature:` モジュール - アプリ内の単一の責任を処理するようにスコープされた機能特化型モジュールです。あるクラスが1つの
  `feature` モジュールでのみ必要な場合は、そのモジュール内に留めるべきです。そうでない場合は、適切な
  `core` モジュールに抽出する必要があります。`feature` モジュールは他の `feature`
  モジュールに依存してはいけません。必要な `core` モジュールにのみ依存します。
* `core:` モジュール - 補助的なコードや、アプリ内の他のモジュール間で共有する必要がある特定の依存関係を含む共通ライブラリモジュールです。これらのモジュールは他の
  `core` モジュールに依存することはできますが、`feature` モジュールや `composeApp` モジュールに依存してはいけません。

## モジュール構成

上記のモジュール化戦略に基づき、TimeInventoryアプリは以下のモジュール構成になっています：

## モジュールの詳細

| 名前           | 責務                                                                     | 主要クラスと例                |
|--------------|------------------------------------------------------------------------|------------------------|
| `composeApp` | アプリが正しく機能するために必要なすべてを統合します。UIの土台、DI（依存性注入）のセットアップ、アプリレベルのナビゲーションを含みます。 | `App` (Composable)<br> 

<br>`MainActivity`<br>

<br>`TimeInventoryApplication`<br>

<br>DIモジュール: `dataModule`, `networkModule`, `viewModelModule` |
| `feature:timeline` |
タイムライン画面の機能を提供します。イベントを表示し、ボトムシート経由でのイベント作成・編集を処理します。 |
`TimelineScreen`<br>

<br>`TimelineViewModel`<br>

<br>`EventBottomSheetContent` |
| `feature:report` |
レポート画面の機能を提供します。ユーザーの日々の活動に対するAIによるフィードバックを生成・表示します。 |
`ReportScreen`<br>

<br>`ReportViewModel`<br>

<br>`AiFeedbackContent` |
| `core:data` |
リポジトリ（Repository）パターンを実装します。ローカル（データベース）およびリモート（ネットワーク）ソースからアプリデータを取得し、機能モジュールに対してデータへの単一アクセスポイントを提供します。 |
`LogEventRepository`<br>

<br>`PlannedEventRepository`<br>

<br>`AiFeedbackRepository`<br>

<br>`CategoryRepository`<br>

<br>`PreferencesRepository` |
| `core:designsystem` | デザインシステムの基盤を提供します。カスタマイズされたMaterial
3コンポーネント、テーマカラー、および全機能で再利用可能なUI要素を含みます。 | `PrimaryButton`<br>

<br>`DestructiveButton`<br>

<br>`TimePickerDialog`<br>

<br>`DropdownMenu`<br>

<br>`OutlinedTextField` |
| `core:model` |
アプリ全体で使用される純粋なデータモデルクラスを定義します。これは他のモジュールへの依存を持たない、最も基礎的なモジュールです。 |
`LogEvent`<br>

<br>`PlannedEvent`<br>

<br>`Category`<br>

<br>`AiFeedback`<br>

<br>`AiFeedbackMode` |
| `core:database` |
Roomを使用したローカルデータベースストレージを提供します。LogEvent、PlannedEvent、Category、AiFeedbackの永続化を担当します。 |
`TimeInventoryDatabase`<br>

<br>`LogEventDao`<br>

<br>`PlannedEventDao`<br>

<br>`AiFeedbackDao`<br>

<br>`CategoryDao` |
| `core:network` | ネットワークリクエストの作成と、リモートデータソースからのレスポンス処理を担当します。Gemini
APIと通信します。 | `GeminiApiClient` |

## モジュール依存ルール

TimeInventoryのモジュール構造は、以下の依存ルールに従っています：

### 基本ルール

1. **機能（Feature）モジュールは他の機能モジュールに依存しない**

* `feature:timeline` と `feature:report` は互いに独立しています。
* 両者は `core:data` と `core:designsystem` を通じて間接的にデータを共有します。


2. **コア（Core）モジュールは機能モジュールに依存しない**

* `core:data` は `feature:timeline` や `feature:report` を関知しません。
* これにより、コアモジュールの再利用性が維持されます。


3. **`composeApp` モジュールはすべての `feature` モジュールに依存する**

* ナビゲーションとアプリレベルの統合を担当するためです。

### データフローの方向

```
UIレイヤー (feature) 
    ↓ (データ要求)
データレイヤー (core:data)
    ↓ (データ取得)
データソース (core:database, core:network)
    ↓ (モデル使用)
モデル (core:model)

```

## TimeInventoryにおけるモジュール化のアプローチ

このアプリのモジュール化アプローチは、TimeInventoryプロジェクトのロードマップ、今後の作業、および新機能を考慮して定義されました。また、比較的小規模なアプリを過度にモジュール化することと、実際の開発環境に近い大規模なコードベースに適したモジュール化パターンを示すことの適切なバランスを目指しました。

### モジュール分割の基準

TimeInventoryでは、以下の基準に基づいてモジュールを分割しています：

**機能による分割**

* ユーザーが認識できる画面単位で分割しています。
* `timeline`（タイムライン画面）と `report`（レポート画面）は、明確に異なる責務を持っています。

**データレイヤーの粒度**

* 現状では、`core:data` は単一のモジュールで十分です。
* 将来的にリポジトリやデータソースが増えた場合は、さらなる分割を検討します。

**UIコンポーネントの共有**

* `core:designsystem` は、複数の機能モジュールで使用される共通のUIコンポーネントを提供します。
* 機能固有のUIコンポーネントは、各機能モジュール内に配置されます。

### 将来の拡張性

このモジュール構造は、以下の将来的な拡張を見込んでいます：

* **新しい機能モジュールの追加**: 例：`feature:settings`（設定画面）など。
* **core:commonの追加**: 共通のユーティリティクラスや拡張関数が増えた場合。

## 参考文献

* [Now in Android - Modularization](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)
* [Android Developers - アプリ アーキテクチャ ガイド](https://developer.android.com/topic/architecture)
* [Gradle - マルチプロジェクト ビルド](https://docs.gradle.org/current/userguide/multi_project_builds.html)
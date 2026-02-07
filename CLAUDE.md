# CLAUDE.md

このファイルは、AIアシスタントがプロジェクトの構造、設計指針、および開発ルールを迅速に理解するためのガイドです。

## 1. プロジェクト概要

TimeInventoryは、「理想の1日（計画）」と「現実の1日（実績）」のギャップを可視化し、AIによる分析を通じて時間管理を改善するためのKotlin
Multiplatform (KMP) アプリケーションです。

### コア機能

* **タイムライン可視化**: 計画（Planned）と実績（Actual）を左右に並べて視覚的に比較。
* **AIフィードバック**: LLMを用いて、その日の行動をKPT（Keep-Problem-Try）形式で分析。
* **マルチプラットフォーム**: AndroidおよびiOSに対応。

## 2. 技術スタック

* **言語**: Kotlin (2.3.0+)
* **UI基盤**: Compose Multiplatform (1.9.3+)
* **非同期処理**: Coroutines & Flow (Reactive UDF)
* **DI**: Koin
* **データ保存**: Room (Single Source of Truth)
* **ネットワーク**: Ktor
* **日付処理**: kotlinx-datetime
* **AI**: Gemini 2.5 Flash Lite

## 3. アーキテクチャ指針

公式のAndroidアーキテクチャガイドに準拠した **2レイヤー構成（UIレイヤー & データレイヤー）** を採用しています。

### 基本原則

* **単方向データフロー (UDF)**:
* **イベント（下向き）**: UI -> ViewModel -> Repository -> Data Source
* **データ（上向き）**: Data Source -> Repository -> ViewModel -> UI (Flowによるストリーム)


* **信頼できる唯一の情報源 (SSOT)**: すべてのデータは一度データベース（Room）に保存され、Repository経由のFlowとしてUIに配信されます。
* **ドメインレイヤーの省略**:
  複雑なビジネスロジックがないため、現在はUseCaseは存在しません。複雑なビジネスロジックが必要な場合はドメインレイヤーであるUseCaseを作成しましょう。

### UIレイヤーの設計

* **UI State**: 不変（immutable）な `data class/object` で定義。
* **ViewModel**: `StateFlow` を使用して状態を公開。
* リアクティブ（Timeline）: 複数のFlowを `combine` し、`stateIn` で変換。
* 命令的（Report）: ユーザーアクションに応じて `MutableStateFlow` を手動更新。

## 4. モジュール構成ルール

機能別およびレイヤー別で分割されたマルチモジュール構成です。

* **`composeApp`**: アプリの統合、DI設定、ナビゲーション。
* **`feature:*`**: 画面単位の機能（`timeline`, `report`）。
* *ルール*: 他のfeatureモジュールに依存してはならない。


* **`core:data`**: Repositoryの実装。
* **`core:database` / `core:network**`: 具体的なデータソース。
* **`core:designsystem`**: 共通UIコンポーネント（Material 3）。
* **`core:model`**: 依存関係を持たない純粋なデータモデル。

## 5. コーディング規約

* **UIコンポーネント**: `core:designsystem` の共通コンポーネントを優先的に使用する。
* **データアクセス**: UIから直接DAOやAPI Clientを呼ばず、必ずRepositoryを経由する。
* **スレッド管理**: Repositoryの `suspend` 関数内、またはViewModelの `viewModelScope`
  で適切なDispatcherを使用する。
* **AIプロンプト**: `ReportViewModel` にてコンテキストを集約し、`AiFeedbackRepository` に渡す。

## 6. 開発コマンド

### セットアップ

* `local.properties` に `GEMINI_API_KEY` を設定。

### ビルド・実行

* **Android**: `./gradlew :composeApp:installDebug`
* **iOS (Simulator)**: `./gradlew :composeApp:iosSimulatorArm64Run`
* **全テスト実行**: `./gradlew test`

---

**AIへの指示**: コード生成時は上記アーキテクチャとモジュール構成を遵守し、特にデータフローがUDF（UI ->
VM -> Repo -> DB -> Flow -> UI）になるようにしてください。
# TimeInventory アプリケーション仕様書

## 1. アプリケーション概要

### 1.1 目的
TimeInventoryは、「理想の1日（計画）」と「現実の1日（実績）」のギャップを可視化し、AIによる分析を通じて時間管理を改善するためのモバイルアプリケーションです。

### 1.2 ターゲットユーザー
- 時間管理を改善したい個人
- 生産性向上を目指すビジネスパーソン
- 自己分析・自己改善に関心のある学生

### 1.3 主な特徴
- **視覚的な時間比較**: 計画と実績を左右に並べて24時間タイムライン表示
- **AI駆動のフィードバック**: Gemini 2.5 Flash LiteによるKPT形式の分析
- **マルチプラットフォーム対応**: AndroidおよびiOSで動作
- **オフラインファースト**: ローカルデータベース（Room）による高速動作

---

## 2. 機能要件

### 2.1 タイムライン機能

#### 2.1.1 タイムライン表示
- **3列レイアウト構成**:
  - 左列: 時間ラベル（00:00～23:59）
  - 中央列: 実績ログ（LogEvent）
  - 右列: 計画イベント（PlannedEvent）
- **24時間連続表示**: 縦スクロール可能
- **イベントブロック仕様**:
  - 開始時刻と期間に基づいて垂直位置と高さを自動計算
  - カテゴリカラーを30%透明度で表示
  - 左側に濃いカラーバー（カテゴリ識別用）
  - 20分未満: ラベル非表示
  - 20～30分: ラベルのみ
  - 30分以上: パディング付きラベル
  - 最小高さ: 8dp

#### 2.1.2 日付選択
- **週カレンダー**: スワイプ可能な7日間表示
- **日付ナビゲーション**: 前週/次週への移動
- **当日表示**: 選択中の日付をハイライト

#### 2.1.3 イベント管理（計画）
- **作成・編集フォーム**（ボトムシート形式）:
  - 活動名（必須）
  - カテゴリ選択（ドロップダウン）
  - 開始日時（DateTimePicker）
  - 終了日時（DateTimePicker）
  - メモ（任意）
  - 繰り返しルール（任意）
- **繰り返しルール設定**:
  - 頻度: 毎日/毎週/毎月
  - 間隔: 1～99
  - 曜日指定（週次の場合）
  - 終了条件: 終了日または回数
- **操作**:
  - 新規作成
  - 編集（タップで表示）
  - 削除（確認ダイアログ付き）

#### 2.1.4 ログ記録（実績）
- **タイマー機能**:
  - 開始: カテゴリ選択で計測開始
  - 停止: 実行中のタイマーを停止
  - 自動記録: 開始・終了時刻を自動保存
- **手動記録**:
  - 活動名、カテゴリ、開始/終了時刻を手動入力
  - 過去のログを編集可能
- **バリデーション**:
  - 1分未満のログは無効
  - 同時実行中のタイマーは1つまで
  - 終了時刻 > 開始時刻のチェック

#### 2.1.5 カテゴリ管理
- **デフォルトカテゴリ（7種類）**:
  | カテゴリ | 色 | ARGB値 |
  |---------|---|--------|
  | 仕事 | Blue | 0xFF2196F3 |
  | 勉強 | Green | 0xFF4CAF50 |
  | 運動 | Orange | 0xFFFF9800 |
  | 趣味 | Purple | 0xFF9C27B0 |
  | 睡眠 | Blue Grey | 0xFF607D8B |
  | 食事 | Deep Orange | 0xFFFF5722 |
  | その他 | Grey | 0xFF9E9E9E |
- **カスタムカテゴリ**:
  - 作成: 名前、色、表示順序を設定
  - 編集: 既存カテゴリの変更
  - 削除: 関連イベントがない場合のみ削除可能（RESTRICT制約）

---

### 2.2 レポート機能

#### 2.2.1 AI分析
- **実行トリガー**: 「AI分析」ボタンをタップ
- **分析対象**: 選択した日付の全ログイベント
- **分析形式**: KPT（Keep-Problem-Try）フレームワーク
- **生成内容**:
  - **Summary**: その日の全体概要（50～100文字）
  - **Keep**: 続けるべき良い習慣（タイトル + 説明）
  - **Problem**: 改善すべき問題点（タイトル + 説明）
  - **Try**: 次に試すべきアクション（タイトル + 説明）

#### 2.2.2 フィードバック表示
- **AIアシスタントカード**:
  - アイコン: ロボット
  - タイトル: "AI アシスタント"
  - Summary を表示
- **KPTカード（3種類）**:
  | 種類 | アイコン | 色 | 内容 |
  |------|---------|---|------|
  | Keep | ✓ | Green | 続けること |
  | Problem | ⚠ | Orange | 問題点 |
  | Try | ✨ | Blue | 次に試すこと |
- **レイアウト**: 縦スクロール可能なカードリスト

#### 2.2.3 キャッシング
- **保存場所**: ローカルデータベース（ai_feedbackテーブル）
- **キャッシュキー**: 対象日（targetDate）
- **再利用**: 同じ日付の分析は再実行せずキャッシュを使用

---

### 2.3 ナビゲーション

#### 2.3.1 ボトムナビゲーションバー
- **タイムラインタブ**:
  - アイコン: ViewTimeline
  - 初期表示画面
- **レポートタブ**:
  - アイコン: Analytics
  - AI分析画面

#### 2.3.2 ナビゲーション動作
- **状態保存**: タブ切り替え時にスクロール位置を保持
- **バックスタック制御**: `popUpTo`でスタックをクリア（メモリ効率化）
- **単一インスタンス**: `launchSingleTop`で重複画面を防止

---

## 3. 非機能要件

### 3.1 パフォーマンス
- **起動時間**: 3秒以内（初回起動時のカテゴリ初期化含む）
- **画面遷移**: 300ms以内
- **AI分析応答時間**: 5秒以内（ネットワーク遅延除く）
- **スクロール性能**: 60fps以上（24時間タイムライン）

### 3.2 セキュリティ
- **APIキー管理**: `local.properties`に保存（Gitコミット対象外）
- **データ暗号化**: Room データベースの暗号化は未実装（将来検討）
- **通信**: HTTPS（Gemini API）

### 3.3 可用性
- **オフライン動作**: タイムライン機能は完全オフライン対応
- **ネットワーク要件**: AI分析時のみインターネット接続必須

### 3.4 保守性
- **アーキテクチャ**: 2レイヤー構成（UI + Data）
- **モジュール設計**: 機能別・レイヤー別の分離
- **テスタビリティ**: Repository インターフェースによるモック化対応

### 3.5 プラットフォーム対応
- **Android**: API Level 24（Android 7.0）以上
- **iOS**: iOS 15.0 以上

---

## 4. 技術スタック

### 4.1 開発言語
- **Kotlin**: 2.3.0+

### 4.2 UIフレームワーク
- **Compose Multiplatform**: 1.9.3+
- **Material 3**: Material Design 3準拠

### 4.3 アーキテクチャコンポーネント
| 用途 | ライブラリ |
|------|-----------|
| 非同期処理 | Kotlin Coroutines & Flow |
| 依存性注入 | Koin |
| ローカルDB | Room (KMP対応版) |
| ネットワーク | Ktor Client |
| 日付処理 | kotlinx-datetime |
| UUID生成 | kotlinx-uuid |

### 4.4 外部サービス
- **AI**: Google Gemini 2.5 Flash Lite API

---

## 5. アーキテクチャ

### 5.1 全体構成

```
┌─────────────────────────────────────────────────────────┐
│                    UI Layer                             │
│  ┌─────────────────┐         ┌─────────────────┐       │
│  │  TimelineScreen │         │   ReportScreen  │       │
│  └────────┬────────┘         └────────┬────────┘       │
│           │                           │                 │
│  ┌────────▼────────┐         ┌───────▼─────────┐       │
│  │ TimelineViewModel│         │ ReportViewModel │       │
│  │  (StateFlow)     │         │  (StateFlow)    │       │
│  └────────┬────────┘         └────────┬────────┘       │
└───────────┼─────────────────────────────┼───────────────┘
            │                             │
            │      Repository Interface   │
            │      (Single Source of Truth)
            │                             │
┌───────────▼─────────────────────────────▼───────────────┐
│                   Data Layer                            │
│  ┌──────────────────────────────────────────────────┐   │
│  │        Repository Implementation                 │   │
│  │  ├─ CategoryRepository                           │   │
│  │  ├─ LogEventRepository                           │   │
│  │  ├─ PlannedEventRepository                       │   │
│  │  └─ AiFeedbackRepository                         │   │
│  └───────┬───────────────────────────┬──────────────┘   │
│          │                           │                  │
│  ┌───────▼────────┐         ┌───────▼──────────┐       │
│  │  Room Database │         │ GeminiApiClient  │       │
│  │  (DAO)         │         │ (Ktor)           │       │
│  └────────────────┘         └──────────────────┘       │
└─────────────────────────────────────────────────────────┘
```

### 5.2 モジュール構成

```
TimeInventory/
├── composeApp/                     # アプリ統合層
│   ├── di/                         # DIモジュール定義
│   ├── navigation/                 # ナビゲーション定義
│   └── ui/                         # MainScreen
│
├── feature/                        # フィーチャーモジュール
│   ├── timeline/                   # タイムライン機能
│   │   ├── component/              # UI コンポーネント
│   │   │   ├── TimelineGrid.kt
│   │   │   ├── TimeBlock.kt
│   │   │   ├── TimelineHeader.kt
│   │   │   └── EventBottomSheetContent.kt
│   │   ├── TimelineScreen.kt
│   │   ├── TimelineViewModel.kt
│   │   └── TimelineUiState.kt
│   │
│   └── report/                     # レポート機能
│       ├── component/
│       │   ├── AiFeedbackContent.kt
│       │   ├── AiAssistantCard.kt
│       │   └── KptCard.kt
│       ├── ReportScreen.kt
│       ├── ReportViewModel.kt
│       └── ReportUiState.kt
│
├── core/                           # コア機能
│   ├── model/                      # ドメインモデル（依存なし）
│   │   ├── Category.kt
│   │   ├── LogEvent.kt
│   │   ├── PlannedEvent.kt
│   │   └── AiFeedback.kt
│   │
│   ├── data/                       # データ層
│   │   ├── repository/             # Repository インターフェース
│   │   ├── repository/impl/        # Repository 実装
│   │   └── mapper/                 # Entity ↔ Model 変換
│   │
│   ├── database/                   # Room DB
│   │   ├── entity/                 # Entity定義
│   │   ├── dao/                    # DAO定義
│   │   └── TimeInventoryDatabase.kt
│   │
│   ├── network/                    # ネットワーク層
│   │   ├── GeminiApiClient.kt
│   │   └── dto/                    # API レスポンス DTO
│   │
│   └── designsystem/               # 共通UIコンポーネント
│       ├── component/
│       │   ├── PrimaryButton.kt
│       │   ├── DestructiveButton.kt
│       │   ├── OutlinedTextField.kt
│       │   ├── DropdownMenu.kt
│       │   └── TimePickerDialog.kt
│       └── theme/                  # テーマ定義
│
└── gradle/                         # ビルド設定
```

### 5.3 依存関係ルール

1. **Feature モジュール間は依存禁止**
   - `feature:timeline` ← ✗ → `feature:report`

2. **Feature は Core に依存可能**
   - `feature:timeline` → `core:model`, `core:data`, `core:designsystem`

3. **Core モジュール間の依存**
   - `core:data` → `core:model`, `core:database`, `core:network`
   - `core:database` → `core:model`
   - `core:network` → `core:model`

---

## 6. データモデル仕様

### 6.1 PlannedEvent（計画イベント）

```kotlin
data class PlannedEvent(
    val id: Uuid,                           // 一意識別子
    val activity: String,                   // 活動内容
    val category: Category,                 // カテゴリ
    val startDateTime: Instant,             // 開始日時（UTC）
    val endDateTime: Instant,               // 終了日時（UTC）
    val isAllDay: Boolean = false,          // 終日フラグ
    val recurrenceRule: RecurrenceRule?,    // 繰り返しルール（RFC 5545準拠）
    val memo: String,                       // メモ
    val externalCalendarId: String?,        // 外部カレンダーID（将来の連携用）
    val source: PlannedEventSource,         // データソース
    val isActive: Boolean = true            // アクティブフラグ（論理削除）
)

enum class PlannedEventSource {
    MANUAL,              // 手動入力
    APPLE_CALENDAR,      // Apple カレンダー連携（未実装）
    GOOGLE_CALENDAR      // Google カレンダー連携（未実装）
}
```

#### 6.1.1 RecurrenceRule（繰り返しルール）

```kotlin
data class RecurrenceRule(
    val frequency: Frequency,               // 頻度
    val interval: Int = 1,                  // 間隔（例: 2週間ごと = 2）
    val daysOfWeek: List<DayOfWeek>?,       // 繰り返し曜日（週次のみ）
    val endDate: LocalDate?,                // 終了日
    val count: Int?                         // 繰り返し回数
)

enum class Frequency {
    DAILY,               // 毎日
    WEEKLY,              // 毎週
    MONTHLY              // 毎月
}
```

**バリデーション規則**:
- `interval` は 1～99
- `endDate` と `count` は排他的（どちらか一方のみ）
- `daysOfWeek` は `WEEKLY` の場合必須

---

### 6.2 LogEvent（実績ログ）

```kotlin
data class LogEvent(
    val id: Uuid,                           // 一意識別子
    val startDateTime: Instant,             // 開始日時（UTC）
    val endDateTime: Instant?,              // 終了日時（null = 実行中）
    val activity: String,                   // 活動内容
    val category: Category,                 // カテゴリ
    val memo: String                        // メモ
) {
    // 計算プロパティ
    val isActive: Boolean                   // endDateTime == null
        get() = endDateTime == null

    val duration: Duration                  // 経過時間
        get() = if (endDateTime != null) {
            endDateTime - startDateTime
        } else {
            Clock.System.now() - startDateTime
        }

    // ビジネスロジック
    fun complete(endDateTime: Instant): LogEvent {
        require(endDateTime > startDateTime) { "終了時刻は開始時刻より後である必要があります" }
        return copy(endDateTime = endDateTime)
    }

    fun isValidDuration(): Boolean {
        return duration.inWholeMinutes >= 1  // 1分以上
    }
}
```

**バリデーション規則**:
- `activity` は空文字列不可
- `startDateTime` < `endDateTime`（nullでない場合）
- 有効なログは 1分以上

---

### 6.3 Category（カテゴリ）

```kotlin
data class Category(
    val id: Uuid,                           // 一意識別子
    val name: String,                       // カテゴリ名
    val colorArgb: Int,                     // ARGB形式の色（例: 0xFF2196F3）
    val sortOrder: Int = 0                  // 表示順序
)
```

**デフォルトカテゴリ**:
```kotlin
object DefaultCategoryColors {
    val Work = Color(0xFF2196F3)            // 仕事 - Blue
    val Study = Color(0xFF4CAF50)           // 勉強 - Green
    val Exercise = Color(0xFFFF9800)        // 運動 - Orange
    val Hobby = Color(0xFF9C27B0)           // 趣味 - Purple
    val Sleep = Color(0xFF607D8B)           // 睡眠 - Blue Grey
    val Meal = Color(0xFFFF5722)            // 食事 - Deep Orange
    val Other = Color(0xFF9E9E9E)           // その他 - Grey
}
```

---

### 6.4 AiFeedback（AI分析結果）

```kotlin
data class AiFeedback(
    val id: Uuid,                           // 一意識別子
    val targetDate: LocalDate,              // 対象日（YYYY-MM-DD）
    val summary: String,                    // 全体概要
    val keep: KptElement,                   // 続けること
    val problem: KptElement,                // 問題点
    val tryAction: KptElement               // 次に試すこと
)

data class KptElement(
    val title: String,                      // 見出し（10～30文字）
    val description: String                 // 詳細説明（50～150文字）
)
```

---

## 7. データベース設計

### 7.1 テーブル定義

#### 7.1.1 category テーブル

| カラム名 | 型 | 制約 | 説明 |
|---------|---|------|------|
| id | TEXT | PRIMARY KEY | UUID |
| name | TEXT | NOT NULL | カテゴリ名 |
| colorArgb | INTEGER | NOT NULL | ARGB色コード |
| sortOrder | INTEGER | NOT NULL, DEFAULT 0 | 表示順序 |

**インデックス**: なし

---

#### 7.1.2 log_event テーブル

| カラム名 | 型 | 制約 | 説明 |
|---------|---|------|------|
| id | TEXT | PRIMARY KEY | UUID |
| startDateTime | INTEGER | NOT NULL | 開始タイムスタンプ（ミリ秒） |
| endDateTime | INTEGER | NULL | 終了タイムスタンプ（ミリ秒） |
| activity | TEXT | NOT NULL | 活動内容 |
| categoryId | TEXT | NOT NULL, FK | カテゴリID |
| memo | TEXT | NOT NULL, DEFAULT '' | メモ |

**外部キー**:
- `categoryId` → `category(id)` ON DELETE RESTRICT

**インデックス**:
- `idx_log_event_start_date` ON (startDateTime)
- `idx_log_event_end_date` ON (endDateTime)
- `idx_log_event_category` ON (categoryId)

---

#### 7.1.3 planned_event テーブル

| カラム名 | 型 | 制約 | 説明 |
|---------|---|------|------|
| id | TEXT | PRIMARY KEY | UUID |
| activity | TEXT | NOT NULL | 活動内容 |
| categoryId | TEXT | NOT NULL, FK | カテゴリID |
| startDateTime | INTEGER | NOT NULL | 開始タイムスタンプ |
| endDateTime | INTEGER | NOT NULL | 終了タイムスタンプ |
| isAllDay | INTEGER | NOT NULL, DEFAULT 0 | 終日フラグ（0/1） |
| recurrenceRuleJson | TEXT | NULL | 繰り返しルールJSON |
| memo | TEXT | NOT NULL, DEFAULT '' | メモ |
| externalCalendarId | TEXT | NULL | 外部カレンダーID |
| source | TEXT | NOT NULL, DEFAULT 'MANUAL' | データソース |
| isActive | INTEGER | NOT NULL, DEFAULT 1 | アクティブフラグ |

**外部キー**:
- `categoryId` → `category(id)` ON DELETE RESTRICT

**インデックス**:
- `idx_planned_event_start_date` ON (startDateTime)
- `idx_planned_event_category` ON (categoryId)
- `idx_planned_event_active` ON (isActive)

---

#### 7.1.4 ai_feedback テーブル

| カラム名 | 型 | 制約 | 説明 |
|---------|---|------|------|
| id | TEXT | PRIMARY KEY | UUID |
| targetDate | TEXT | NOT NULL, UNIQUE | 対象日（YYYY-MM-DD） |
| summary | TEXT | NOT NULL | 全体概要 |
| keepTitle | TEXT | NOT NULL | Keep 見出し |
| keepDescription | TEXT | NOT NULL | Keep 説明 |
| problemTitle | TEXT | NOT NULL | Problem 見出し |
| problemDescription | TEXT | NOT NULL | Problem 説明 |
| tryTitle | TEXT | NOT NULL | Try 見出し |
| tryDescription | TEXT | NOT NULL | Try 説明 |

**インデックス**:
- `idx_ai_feedback_date` ON (targetDate) UNIQUE

---

### 7.2 主要クエリ

#### 7.2.1 日付範囲でログイベント取得

```sql
SELECT log_event.*, category.*
FROM log_event
INNER JOIN category ON log_event.categoryId = category.id
WHERE startDateTime >= :startTimestamp
  AND startDateTime < :endTimestamp
ORDER BY startDateTime ASC
```

#### 7.2.2 実行中のタイマー取得

```sql
SELECT log_event.*, category.*
FROM log_event
INNER JOIN category ON log_event.categoryId = category.id
WHERE endDateTime IS NULL
LIMIT 1
```

#### 7.2.3 計画イベント取得（繰り返し展開）

```sql
SELECT planned_event.*, category.*
FROM planned_event
INNER JOIN category ON planned_event.categoryId = category.id
WHERE isActive = 1
  AND startDateTime >= :startTimestamp
  AND startDateTime < :endTimestamp
ORDER BY startDateTime ASC
```

**Note**: 繰り返しイベントは Repository 層で展開処理を実施

---

## 8. API仕様（Gemini連携）

### 8.1 エンドポイント

**ベースURL**: `https://generativelanguage.googleapis.com/v1beta`

**エンドポイント**: `POST /models/gemini-2.5-flash-lite:generateContent`

### 8.2 リクエスト仕様

#### 8.2.1 ヘッダー
```
Content-Type: application/json
x-goog-api-key: {GEMINI_API_KEY}
```

#### 8.2.2 リクエストボディ

```json
{
  "contents": [
    {
      "role": "user",
      "parts": [
        {
          "text": "{プロンプト文字列}"
        }
      ]
    }
  ],
  "generationConfig": {
    "temperature": 0.7,
    "topK": 40,
    "topP": 0.95,
    "maxOutputTokens": 1024,
    "responseMimeType": "application/json"
  }
}
```

#### 8.2.3 プロンプト構造

```
あなたは時間管理のエキスパートです。
以下のログデータを分析し、KPT形式でフィードバックを提供してください。

【対象日】
{targetDate}

【ログデータ】
{logEvents の一覧}
- 00:00-01:30: 睡眠
- 07:00-08:00: 朝食
- ...

【出力形式】
以下のJSON形式で返却してください：
{
  "summary": "全体概要（50～100文字）",
  "keep": {
    "title": "続けるべきこと（10～30文字）",
    "description": "詳細説明（50～150文字）"
  },
  "problem": {
    "title": "改善すべき問題（10～30文字）",
    "description": "詳細説明（50～150文字）"
  },
  "try": {
    "title": "次に試すこと（10～30文字）",
    "description": "詳細説明（50～150文字）"
  }
}
```

### 8.3 レスポンス仕様

#### 8.3.1 成功レスポンス（200 OK）

```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "{JSON文字列}"
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP"
    }
  ]
}
```

**JSON文字列の内容**:
```json
{
  "summary": "本日は仕事に8時間集中し、運動と趣味の時間も確保できた充実した1日でした。",
  "keep": {
    "title": "朝のルーティン確立",
    "description": "7時起床で朝食と運動を実施。この習慣が1日のリズムを作っています。"
  },
  "problem": {
    "title": "夜更かしの傾向",
    "description": "24時過ぎまで趣味に没頭し、睡眠時間が6時間を切りました。"
  },
  "try": {
    "title": "22時以降はリラックスタイム",
    "description": "趣味の時間を21時までに制限し、就寝前は読書やストレッチに切り替えましょう。"
  }
}
```

#### 8.3.2 エラーレスポンス

**400 Bad Request**:
```json
{
  "error": {
    "code": 400,
    "message": "Invalid API key",
    "status": "INVALID_ARGUMENT"
  }
}
```

**429 Too Many Requests**:
```json
{
  "error": {
    "code": 429,
    "message": "Quota exceeded",
    "status": "RESOURCE_EXHAUSTED"
  }
}
```

---

## 9. UI仕様

### 9.1 タイムライン画面

#### 9.1.1 レイアウト構造

```
┌─────────────────────────────────────────┐
│  ◀ 週カレンダー（7日分）▶               │  ← ヘッダー
│  [時間] [実績]      [計画]              │
├─────────────────────────────────────────┤
│ 00:00 │            │                    │
│       │            │                    │
│ 01:00 │  [睡眠]    │  [睡眠]            │  ← イベントブロック
│       │            │                    │
│ 02:00 │            │                    │
│  ...  │    ...     │    ...             │
│ 23:00 │            │                    │
└─────────────────────────────────────────┘
│  [Timeline] [Report]                    │  ← ボトムナビゲーション
└─────────────────────────────────────────┘
```

#### 9.1.2 イベントブロック配置計算

**垂直オフセット**:
```kotlin
val offset = (hour * 60 + minute) * hourHeight / 60
```

**ブロック高さ**:
```kotlin
val height = max(durationMinutes * hourHeight / 60, 8.dp)
```

**例**: 08:30～10:15 のイベント（105分）
- オフセット = (8 * 60 + 30) * hourHeight / 60
- 高さ = 105 * hourHeight / 60

#### 9.1.3 ボトムシート（イベント編集）

**フォーム項目**:
1. 活動名（TextField）
2. カテゴリ（Dropdown）
3. 開始日時（DateTimePicker）
4. 終了日時（DateTimePicker）
5. メモ（TextField、複数行）
6. 繰り返し設定（オプション）

**ボタン**:
- 保存（PrimaryButton）
- キャンセル（TextButton）
- 削除（DestructiveButton）※編集時のみ

---

### 9.2 レポート画面

#### 9.2.1 レイアウト構造

```
┌─────────────────────────────────────────┐
│  📅 2026-02-08                          │  ← 日付表示
│  [AI分析を実行]                         │  ← アクションボタン
├─────────────────────────────────────────┤
│  ┌───────────────────────────────────┐  │
│  │ 🤖 AI アシスタント               │  │
│  │ {Summary}                         │  │  ← AIアシスタントカード
│  └───────────────────────────────────┘  │
│                                         │
│  ┌───────────────────────────────────┐  │
│  │ ✓ {Keep Title}                   │  │
│  │ {Keep Description}                │  │  ← Keep カード（緑）
│  └───────────────────────────────────┘  │
│                                         │
│  ┌───────────────────────────────────┐  │
│  │ ⚠ {Problem Title}                │  │
│  │ {Problem Description}             │  │  ← Problem カード（橙）
│  └───────────────────────────────────┘  │
│                                         │
│  ┌───────────────────────────────────┐  │
│  │ ✨ {Try Title}                    │  │
│  │ {Try Description}                 │  │  ← Try カード（青）
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

#### 9.2.2 カードデザイン

**共通スタイル**:
- Material 3 Card
- 角丸: 12dp
- 影: elevation 2dp
- パディング: 16dp

**KPT カラー**:
- Keep: `Color(0xFF4CAF50)` (Green)
- Problem: `Color(0xFFFF9800)` (Orange)
- Try: `Color(0xFF2196F3)` (Blue)

---

## 10. データフロー

### 10.1 タイムライン画面のリアクティブフロー

```kotlin
// ViewModel
val uiState: StateFlow<TimelineUiState> = combine(
    selectedDateFlow,                           // 選択日の Flow
    categoryRepository.getCategoriesStream(),   // カテゴリ一覧の Flow
    logEventRepository.getLogEventsByDateStream(selectedDate),     // ログの Flow
    plannedEventRepository.getPlannedEventsByDateStream(selectedDate) // 計画の Flow
) { selectedDate, categories, logEvents, plannedEvents ->
    TimelineUiState(
        selectedDate = selectedDate,
        categories = categories,
        logEvents = logEvents,
        plannedEvents = plannedEvents
    )
}.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = TimelineUiState()
)
```

**フロー図**:
```
Room DB (category)
  ↓ Flow<List<CategoryEntity>>
CategoryRepository.getCategoriesStream()
  ↓ map { entities.map(::toModel) }
  ↓ Flow<List<Category>>
ViewModel.combine()
  ↓ stateIn
  ↓ StateFlow<TimelineUiState>
Screen.collectAsState()
  ↓ State<TimelineUiState>
UI再描画
```

---

### 10.2 イベント処理フロー（ユーザーアクション → DB更新）

```
UI: ボタンクリック
  ↓
ViewModel.onEvent(event)
  ↓
viewModelScope.launch {
    repository.upsertPlannedEvent(event)
}
  ↓
Repository.upsertPlannedEvent()
  ↓ withContext(Dispatchers.IO)
  ↓ toEntity(domainModel)
DAO.upsert(entity)
  ↓
Room DB 更新
  ↓ Flow 自動発火
Repository.getPlannedEventsByDateStream()
  ↓ map { entities.map(::toModel) }
ViewModel (combine が再計算)
  ↓ stateIn
UI 自動再描画
```

---

### 10.3 AI分析フロー

```
UI: [AI分析] ボタンタップ
  ↓
ReportViewModel.generateFeedback()
  ↓
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }

    // 1. プロンプト生成
    val prompt = buildPrompt(logEvents, targetDate)

    // 2. Repository 呼び出し
    val feedback = aiFeedbackRepository.generateFeedback(targetDate, prompt)

    // 3. UI更新
    _uiState.update { it.copy(aiFeedback = feedback, isLoading = false) }
}
  ↓
AiFeedbackRepository.generateFeedback()
  ↓
// キャッシュ確認
val cached = aiFeedbackDao.getByDate(targetDate)
if (cached != null) return cached.toModel()
  ↓
// API 呼び出し
val response = geminiApiClient.generateContent(prompt)
  ↓
// JSON パース
val json = Json.decodeFromString<AiFeedbackDto>(response.text)
  ↓
// ドメインモデル変換
val feedback = json.toModel(targetDate)
  ↓
// DB 保存
aiFeedbackDao.insert(feedback.toEntity())
  ↓
return feedback
  ↓
UI: AiFeedbackContent に表示
```

---

## 11. 初期化処理

### 11.1 初回起動フロー

```
App 起動
  ↓
TimelineScreen.LaunchedEffect(Unit) {
    viewModel.initialize()
}
  ↓
TimelineViewModel.initialize()
  ↓
viewModelScope.launch {
    val isFirstLaunch = preferencesRepository.isFirstLaunch()

    if (isFirstLaunch) {
        // デフォルトカテゴリ作成
        val defaultCategories = listOf(
            Category(name = "仕事", colorArgb = 0xFF2196F3.toInt(), ...),
            Category(name = "勉強", colorArgb = 0xFF4CAF50.toInt(), ...),
            // ... 7カテゴリ
        )

        categoryRepository.initializeDefaultCategories(defaultCategories)

        // 初期化完了フラグ
        preferencesRepository.markInitialized()
    }
}
```

### 11.2 デフォルトカテゴリ一覧

| ID | 名前 | 色 | sortOrder |
|----|------|---|-----------|
| UUID1 | 仕事 | 0xFF2196F3 | 0 |
| UUID2 | 勉強 | 0xFF4CAF50 | 1 |
| UUID3 | 運動 | 0xFFFF9800 | 2 |
| UUID4 | 趣味 | 0xFF9C27B0 | 3 |
| UUID5 | 睡眠 | 0xFF607D8B | 4 |
| UUID6 | 食事 | 0xFFFF5722 | 5 |
| UUID7 | その他 | 0xFF9E9E9E | 6 |

---

## 12. セットアップ手順

### 12.1 環境要件

- **JDK**: 17 以上
- **Android Studio**: Ladybug 以上
- **Xcode**: 15.0 以上（iOS ビルド時）
- **Kotlin**: 2.3.0+

### 12.2 初期設定

#### 12.2.1 APIキー設定

1. Gemini API キーを取得（https://ai.google.dev/）
2. プロジェクトルートに `local.properties` を作成
3. 以下を追加:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

#### 12.2.2 依存関係インストール

```bash
./gradlew build
```

### 12.3 実行コマンド

#### Android
```bash
# デバッグビルドインストール
./gradlew :composeApp:installDebug

# エミュレータ起動（adb経由）
adb shell am start -n com.example.timeinventory/.MainActivity
```

#### iOS
```bash
# Simulator（ARM64）実行
./gradlew :composeApp:iosSimulatorArm64Run

# 実機デバイス（要: 署名設定）
./gradlew :composeApp:iosArm64Run
```

### 12.4 テスト実行

```bash
# 全テスト実行
./gradlew test

# 特定モジュールのテスト
./gradlew :core:data:test
```

---

## 13. 制約・制限事項

### 13.1 現在の制約

1. **外部カレンダー連携**: 未実装（Apple Calendar / Google Calendar）
2. **データ同期**: クラウド同期機能なし（ローカルのみ）
3. **マルチデバイス**: デバイス間のデータ共有不可
4. **AI分析モード**: GENTLE/NORMAL/STRICT のトーン切り替え未実装
5. **通知機能**: 計画イベントのリマインダー機能なし
6. **エクスポート**: データのCSV/PDF出力機能なし

### 13.2 既知の問題

1. **繰り返しイベント**: 月次繰り返しの「月末」指定が未対応
2. **タイムゾーン**: UTC固定（ユーザーのローカルタイムゾーン未対応）
3. **AI応答パース失敗**: JSON形式が不正な場合のフォールバック処理なし

---

## 14. 将来の拡張予定

### 14.1 機能拡張

- [ ] 週次/月次レポート（集計統計）
- [ ] カスタムKPIトラッキング
- [ ] 外部カレンダー連携（Apple / Google）
- [ ] データエクスポート（CSV / PDF）
- [ ] 通知・リマインダー機能
- [ ] ダークモード対応

### 14.2 技術改善

- [ ] データベース暗号化（SQLCipher）
- [ ] クラウド同期（Firebase / Supabase）
- [ ] オフライン時のAI分析キュー
- [ ] パフォーマンスモニタリング（Crashlytics）

---

## 付録A: 用語集

| 用語 | 説明 |
|------|------|
| **KPT** | Keep-Problem-Try の略。振り返りフレームワーク |
| **UDF** | 単方向データフロー（Unidirectional Data Flow） |
| **SSOT** | 信頼できる唯一の情報源（Single Source of Truth） |
| **DAO** | Data Access Object（データアクセス層） |
| **DTO** | Data Transfer Object（データ転送オブジェクト） |
| **UUID** | 汎用一意識別子（Universally Unique Identifier） |
| **RFC 5545** | iCalendar 仕様（繰り返しルールの標準） |

---

## 付録B: 参考資料

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Gemini API Documentation](https://ai.google.dev/docs)
- [Material Design 3](https://m3.material.io/)

---

**ドキュメントバージョン**: 1.0.0
**最終更新日**: 2026-02-08
**作成者**: Claude (AI Assistant)

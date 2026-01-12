# core:network モジュール

Gemini APIとの通信を担当するネットワーク層

## アーキテクチャ

### レイヤー構成

```
┌─────────────────────────────────────┐
│     GeminiApiClient                 │  ← APIクライアント（HTTPリクエスト送信）
├─────────────────────────────────────┤
│  GeminiRequestBuilder               │  ← リクエスト構築
│  GeminiResponseParser               │  ← レスポンスパース
│  GeminiJsonSchema                   │  ← JSON Schema定義
├─────────────────────────────────────┤
│  DTO (Request/Response/Error)       │  ← データ転送オブジェクト
│  Exception (GeminiApiException)     │  ← カスタム例外
└─────────────────────────────────────┘
```

### 責務分離 (Single Responsibility Principle)

| クラス | 責務 | ファイル |
|--------|------|----------|
| `GeminiApiClient` | HTTPリクエスト送信、エラーハンドリング | `GeminiApiClient.kt` |
| `GeminiRequestBuilder` | リクエストボディ構築 | `builder/GeminiRequestBuilder.kt` |
| `GeminiResponseParser` | レスポンスパース、バリデーション | `parser/GeminiResponseParser.kt` |
| `GeminiJsonSchema` | JSON Schema定義 | `schema/GeminiJsonSchema.kt` |
| DTO | データ構造定義 | `dto/*.kt` |
| Exception | エラー型定義 | `exception/GeminiApiException.kt` |

## ディレクトリ構成

```
core/network/src/
├── commonMain/kotlin/com/example/timeinventory/core/network/
│   ├── GeminiApiClient.kt                    # メインクライアント
│   ├── builder/
│   │   └── GeminiRequestBuilder.kt           # リクエストビルダー
│   ├── parser/
│   │   └── GeminiResponseParser.kt           # レスポンスパーサー
│   ├── schema/
│   │   └── GeminiJsonSchema.kt               # JSON Schema定義
│   ├── dto/
│   │   ├── GeminiRequest.kt                  # リクエストDTO
│   │   ├── GeminiResponse.kt                 # レスポンスDTO
│   │   └── GeminiError.kt                    # エラーDTO
│   └── exception/
│       └── GeminiApiException.kt             # カスタム例外
├── androidMain/kotlin/
│   └── GeminiApiClient.android.kt            # OkHttpエンジン
└── iosMain/kotlin/
    └── GeminiApiClient.ios.kt                # Darwinエンジン
```

## 使用例

### KPTフィードバック生成

```kotlin
val client = GeminiApiClient()

try {
    val feedback = client.generateKptFeedback(
        prompt = """
        あなたは時間管理のコーチです。
        ユーザーの1日を分析し、KPT形式でフィードバックを生成してください。

        【予定】
        - 09:00-10:00: 会議

        【実績】
        - 09:00-10:30: 会議
        """.trimIndent()
    )

    println("Generated: $feedback")

} catch (e: GeminiApiException.ApiError) {
    println("API Error: ${e.errorMessage}")
} catch (e: GeminiApiException.NetworkError) {
    println("Network Error: ${e.message}")
} catch (e: GeminiApiException.ConfigurationError) {
    println("Config Error: ${e.message}")
}
```

### カスタムリクエスト

```kotlin
val request = GeminiRequestBuilder()
    .withPrompt("質問内容")
    .withKptFeedbackJsonResponse()
    .build()

val response = client.generateContent(request)
```

## エラーハンドリング

### 例外階層

```
GeminiApiException (sealed class)
├── NetworkError          # ネットワークエラー
├── ApiError              # APIエラー (400, 500など)
├── ParseError            # パースエラー
├── EmptyResponseError    # 空レスポンス
└── ConfigurationError    # 設定エラー (API keyなど)
```

### エラー処理の推奨パターン

```kotlin
try {
    val result = client.generateKptFeedback(prompt)
    // 成功時の処理
} catch (e: GeminiApiException) {
    when (e) {
        is GeminiApiException.ApiError -> {
            // APIエラー時の処理
            logger.error("API Error [${e.statusCode}]: ${e.errorMessage}")
        }
        is GeminiApiException.NetworkError -> {
            // ネットワークエラー時の処理
            logger.error("Network error", e)
        }
        is GeminiApiException.ConfigurationError -> {
            // 設定エラー時の処理
            logger.error("Configuration error: ${e.message}")
        }
        else -> {
            // その他のエラー
            logger.error("Unexpected error", e)
        }
    }
}
```

## テスト

### ユニットテスト例

```kotlin
class GeminiResponseParserTest {
    private val parser = GeminiResponseParser()

    @Test
    fun `正常なレスポンスをパースできる`() {
        val json = """
        {
          "candidates": [{
            "content": {
              "parts": [{"text": "生成されたテキスト"}]
            }
          }]
        }
        """.trimIndent()

        val response = parser.parseSuccessResponse(json)
        val text = parser.extractGeneratedText(response)

        assertEquals("生成されたテキスト", text)
    }

    @Test
    fun `空のレスポンスで例外がスローされる`() {
        val json = """{"candidates": []}"""
        val response = parser.parseSuccessResponse(json)

        assertFailsWith<GeminiApiException.EmptyResponseError> {
            parser.extractGeneratedText(response)
        }
    }
}
```

## 設定

### API Key

`local.properties`にAPIキーを設定：

```properties
GEMINI_API_KEY=your_api_key_here
```

### モデル設定

`GeminiApiClient.kt`の`MODEL_NAME`定数で変更可能：

```kotlin
companion object {
    private const val MODEL_NAME = "gemini-2.5-flash-lite"
    // または "gemini-1.5-flash", "gemini-1.5-pro" など
}
```

## 依存関係

- **Ktor Client**: HTTP通信
  - Android: OkHttp engine
  - iOS: Darwin engine
- **kotlinx.serialization**: JSON シリアライゼーション
- **BuildKonfig**: 設定管理

## パフォーマンス

- **HttpClient**: シングルトンで再利用
- **JSON Parser**: 事前設定済みインスタンスを使用
- **接続プール**: Ktorが自動管理

## セキュリティ

- ✅ API Keyは`local.properties`で管理（gitignore対象）
- ✅ BuildKonfigでコンパイル時に埋め込み
- ✅ HTTPSのみ使用
- ⚠️ ログにAPIキーを出力しない

## 今後の改善案

- [ ] リトライロジックの追加
- [ ] レート制限対応
- [ ] キャッシュ機構
- [ ] メトリクス収集
- [ ] タイムアウト設定のカスタマイズ

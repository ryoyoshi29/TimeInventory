package com.example.timeinventory.core.network.exception

/**
 * Gemini API 関連の例外基底クラス
 */
sealed class GeminiApiException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * ネットワークエラー
     */
    class NetworkError(
        message: String,
        cause: Throwable? = null
    ) : GeminiApiException("Network error: $message", cause)

    /**
     * APIエラー（400, 500など）
     */
    class ApiError(
        val statusCode: Int,
        val errorMessage: String,
        val errorStatus: String
    ) : GeminiApiException("API error [$statusCode]: $errorMessage")

    /**
     * レスポンスパースエラー
     */
    class ParseError(
        message: String,
        cause: Throwable? = null
    ) : GeminiApiException("Parse error: $message", cause)

    /**
     * 空レスポンスエラー
     */
    class EmptyResponseError(
        message: String = "API returned empty response"
    ) : GeminiApiException(message)

    /**
     * 設定エラー（API keyなど）
     */
    class ConfigurationError(
        message: String
    ) : GeminiApiException("Configuration error: $message")
}

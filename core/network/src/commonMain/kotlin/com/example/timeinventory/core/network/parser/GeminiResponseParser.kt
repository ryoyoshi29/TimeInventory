package com.example.timeinventory.core.network.parser

import com.example.timeinventory.core.network.dto.GeminiErrorResponse
import com.example.timeinventory.core.network.dto.GeminiResponse
import com.example.timeinventory.core.network.exception.GeminiApiException
import kotlinx.serialization.json.Json

/**
 * Gemini APIレスポンスをパースするクラス
 */
class GeminiResponseParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * 成功レスポンスをパース
     *
     * @throws GeminiApiException.ParseError パースに失敗した場合
     */
    fun parseSuccessResponse(responseText: String): GeminiResponse {
        return try {
            json.decodeFromString<GeminiResponse>(responseText)
        } catch (e: Exception) {
            throw GeminiApiException.ParseError(
                message = "Failed to parse success response",
                cause = e
            )
        }
    }

    /**
     * エラーレスポンスをパース
     *
     * @throws GeminiApiException.ParseError パースに失敗した場合
     */
    fun parseErrorResponse(responseText: String): GeminiErrorResponse {
        return try {
            json.decodeFromString<GeminiErrorResponse>(responseText)
        } catch (e: Exception) {
            throw GeminiApiException.ParseError(
                message = "Failed to parse error response",
                cause = e
            )
        }
    }

    /**
     * レスポンスからテキストを抽出
     *
     * @throws GeminiApiException.EmptyResponseError レスポンスが空の場合
     */
    fun extractGeneratedText(response: GeminiResponse): String {
        val text = response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text

        return text ?: throw GeminiApiException.EmptyResponseError()
    }

    /**
     * レスポンステキストがエラーを含むかチェック
     */
    fun isErrorResponse(responseText: String): Boolean {
        return responseText.contains("\"error\"", ignoreCase = false)
    }
}

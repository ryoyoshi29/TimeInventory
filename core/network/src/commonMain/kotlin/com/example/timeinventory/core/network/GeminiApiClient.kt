package com.example.timeinventory.core.network

import com.example.timeinventory.core.network.builder.GeminiRequestBuilder
import com.example.timeinventory.core.network.dto.GeminiRequest
import com.example.timeinventory.core.network.exception.GeminiApiException
import com.example.timeinventory.core.network.parser.GeminiResponseParser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Gemini API クライアント
 */
class GeminiApiClient {

    companion object {
        private const val MODEL_NAME = "gemini-2.5-flash-lite"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    private val httpClient: HttpClient = createHttpClient()
    private val responseParser = GeminiResponseParser()

    private val apiKey: String
        get() = BuildKonfig.GEMINI_API_KEY.takeIf { it.isNotEmpty() }
            ?: throw GeminiApiException.ConfigurationError(
                "GEMINI_API_KEY is missing. Please add it to local.properties"
            )

    /**
     * KPTフィードバックを生成
     *
     * @param prompt ユーザープロンプト
     * @return 生成されたJSON文字列
     * @throws GeminiApiException API呼び出しに失敗した場合
     */
    suspend fun generateKptFeedback(prompt: String): String {
        return generateContent(
            GeminiRequestBuilder()
                .withPrompt(prompt)
                .withKptFeedbackJsonResponse()
                .build()
        )
    }

    /**
     * コンテンツを生成（汎用）
     *
     * @param request Geminiリクエスト
     * @return 生成されたテキスト
     * @throws GeminiApiException API呼び出しに失敗した場合
     */
    suspend fun generateContent(request: GeminiRequest): String {
        try {
            val responseText = executeRequest(request)

            if (responseParser.isErrorResponse(responseText)) {
                handleErrorResponse(responseText)
            }

            val response = responseParser.parseSuccessResponse(responseText)
            val generatedText = responseParser.extractGeneratedText(response)

            return generatedText

        } catch (e: GeminiApiException) {
            throw e
        } catch (e: Exception) {
            throw GeminiApiException.NetworkError(
                message = e.message ?: "Unknown error",
                cause = e
            )
        }
    }

    /**
     * HTTPリクエストを実行
     */
    private suspend fun executeRequest(request: GeminiRequest): String {
        val response = httpClient.post {
            url("$BASE_URL/$MODEL_NAME:generateContent")
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return response.body<String>()
    }

    /**
     * エラーレスポンスを処理
     */
    private fun handleErrorResponse(responseText: String): Nothing {
        val errorResponse = responseParser.parseErrorResponse(responseText)
        val error = errorResponse.error

        println("❌ API Error: [${error.code}] ${error.message}")

        throw GeminiApiException.ApiError(
            statusCode = error.code,
            errorMessage = error.message,
            errorStatus = error.status
        )
    }

    /**
     * HttpClientを作成
     */
    private fun createHttpClient(): HttpClient {
        return HttpClient(platformEngine()) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            expectSuccess = false
        }
    }
}

/**
 * プラットフォーム固有のHttpエンジンを提供
 */
expect fun platformEngine(): io.ktor.client.engine.HttpClientEngineFactory<*>

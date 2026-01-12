package com.example.timeinventory.core.network.dto

import kotlinx.serialization.Serializable

/**
 * Gemini API エラーレスポンス
 */
@Serializable
data class GeminiErrorResponse(
    val error: ApiError
)

@Serializable
data class ApiError(
    val code: Int,
    val message: String,
    val status: String
)

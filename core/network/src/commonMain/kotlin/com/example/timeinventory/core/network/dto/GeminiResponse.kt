package com.example.timeinventory.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Gemini API レスポンス
 */
@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null,
    @SerialName("finishReason")
    val finishReason: String? = null
)

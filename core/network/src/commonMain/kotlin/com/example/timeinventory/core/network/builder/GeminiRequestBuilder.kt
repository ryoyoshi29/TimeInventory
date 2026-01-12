package com.example.timeinventory.core.network.builder

import com.example.timeinventory.core.network.dto.Content
import com.example.timeinventory.core.network.dto.GeminiRequest
import com.example.timeinventory.core.network.dto.GenerationConfig
import com.example.timeinventory.core.network.dto.Part
import com.example.timeinventory.core.network.schema.GeminiJsonSchema
import kotlinx.serialization.json.JsonObject

/**
 * Gemini APIリクエストを構築するビルダー
 */
class GeminiRequestBuilder {

    private var prompt: String = ""
    private var responseMimeType: String? = null
    private var responseSchema: JsonObject? = null

    /**
     * プロンプトを設定
     */
    fun withPrompt(prompt: String) = apply {
        this.prompt = prompt
    }

    fun withKptFeedbackJsonResponse() = apply {
        this.responseMimeType = "application/json"
        this.responseSchema = GeminiJsonSchema.createKptFeedbackSchema()
    }

    fun build(): GeminiRequest {
        require(prompt.isNotEmpty()) { "Prompt must not be empty" }

        val generationConfig = if (responseMimeType != null) {
            GenerationConfig(
                responseMimeType = responseMimeType!!,
                responseSchema = responseSchema
            )
        } else {
            null
        }

        return GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = generationConfig
        )
    }
}

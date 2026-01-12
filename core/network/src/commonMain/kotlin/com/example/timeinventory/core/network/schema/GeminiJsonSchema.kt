package com.example.timeinventory.core.network.schema

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Gemini API用のJSON Schema定義
 */
object GeminiJsonSchema {

    /**
     * KPTフィードバック用のJSON Schema
     *
     * 構造:
     * ```json
     * {
     *   "summary": "string",
     *   "keep": { "title": "string", "description": "string" },
     *   "problem": { "title": "string", "description": "string" },
     *   "tryAction": { "title": "string", "description": "string" }
     * }
     * ```
     */
    fun createKptFeedbackSchema(): JsonObject {
        return buildJsonObject {
            put("type", "OBJECT")

            putJsonObject("properties") {
                // Summary field
                putJsonObject("summary") {
                    put("type", "STRING")
                }

                putKptItemSchema("keep")
                putKptItemSchema("problem")
                putKptItemSchema("tryAction")
            }

            putJsonArray("required") {
                add(JsonPrimitive("summary"))
                add(JsonPrimitive("keep"))
                add(JsonPrimitive("problem"))
                add(JsonPrimitive("tryAction"))
            }
        }
    }

    private fun JsonObjectBuilder.putKptItemSchema(
        fieldName: String
    ) {
        putJsonObject(fieldName) {
            put("type", "OBJECT")

            putJsonObject("properties") {
                putJsonObject("title") {
                    put("type", "STRING")
                }
                putJsonObject("description") {
                    put("type", "STRING")
                }
            }

            putJsonArray("required") {
                add(JsonPrimitive("title"))
                add(JsonPrimitive("description"))
            }
        }
    }
}

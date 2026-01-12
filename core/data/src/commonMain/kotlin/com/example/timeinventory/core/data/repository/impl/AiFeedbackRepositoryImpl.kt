package com.example.timeinventory.core.data.repository.impl

import com.example.timeinventory.core.data.dto.AiFeedbackDto
import com.example.timeinventory.core.data.mapper.toDomainModel
import com.example.timeinventory.core.data.mapper.toEntity
import com.example.timeinventory.core.data.repository.AiFeedbackRepository
import com.example.timeinventory.core.database.dao.AiFeedbackDao
import com.example.timeinventory.core.model.AiFeedback
import com.example.timeinventory.core.network.GeminiApiClient
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AiFeedbackRepositoryImpl(
    private val aiFeedbackDao: AiFeedbackDao,
    private val geminiApiClient: GeminiApiClient,
) : AiFeedbackRepository {

    override suspend fun getFeedbackByDate(targetDate: LocalDate): AiFeedback? {
        return aiFeedbackDao.getByDate(targetDate.toString())?.toDomainModel()
    }

    override suspend fun generateFeedback(targetDate: LocalDate, prompt: String): AiFeedback {
        val response = geminiApiClient.generateKptFeedback(prompt)
        val newFeedback = parseFeedbackResponse(response).toDomainModel(targetDate)

        aiFeedbackDao.upsert(newFeedback.toEntity())
        return newFeedback
    }

    private fun parseFeedbackResponse(generatedText: String): AiFeedbackDto {
        return try {
            Json.decodeFromString<AiFeedbackDto>(generatedText)
        } catch (e: Exception) {
            // TODO: パースに失敗した場合のエラー処理
        } as AiFeedbackDto
    }
}

private fun LocalDateTime.toInstant(timeZone: TimeZone): kotlinx.datetime.Instant {
    return LocalDateTime(date, time).toInstant(timeZone)
}

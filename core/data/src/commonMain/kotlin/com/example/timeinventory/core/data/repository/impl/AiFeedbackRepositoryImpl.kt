package com.example.timeinventory.core.data.repository.impl

import com.example.timeinventory.core.data.mapper.toDomainModel
import com.example.timeinventory.core.data.mapper.toEntity
import com.example.timeinventory.core.data.repository.AiFeedbackRepository
import com.example.timeinventory.core.database.dao.AiFeedbackDao
import com.example.timeinventory.core.model.AiFeedback
import com.example.timeinventory.core.model.KptElement
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AiFeedbackRepositoryImpl(
    private val aiFeedbackDao: AiFeedbackDao,
) : AiFeedbackRepository {

    override suspend fun getFeedbackByDate(targetDate: LocalDate): AiFeedback? {
        return aiFeedbackDao.getByDate(targetDate.toString())?.toDomainModel()
    }

    override suspend fun getOrGenerateFeedback(targetDate: LocalDate): AiFeedback {
        val existing = getFeedbackByDate(targetDate)
        if (existing != null) {
            return existing
        }

        // フィードバックが存在しない場合は生成（現時点ではダミーデータ）
        // TODO: Gemini APIを使った実際のフィードバック生成を実装
        val newFeedback = AiFeedback(
            id = Uuid.random(),
            targetDate = targetDate,
            summary = "フィードバックを生成中...",
            keep = KptElement(
                title = "Keep",
                description = "継続すべきこと",
            ),
            problem = KptElement(
                title = "Problem",
                description = "改善すべきこと",
            ),
            tryAction = KptElement(
                title = "Try",
                description = "次に試すこと",
            ),
        )

        aiFeedbackDao.upsert(newFeedback.toEntity())
        return newFeedback
    }
}

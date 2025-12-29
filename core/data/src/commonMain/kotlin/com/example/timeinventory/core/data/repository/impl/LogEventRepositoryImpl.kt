package com.example.timeinventory.core.data.repository.impl

import com.example.timeinventory.core.data.exception.LogEventNotFoundException
import com.example.timeinventory.core.data.mapper.toDomainModel
import com.example.timeinventory.core.data.mapper.toEntity
import com.example.timeinventory.core.data.repository.LogEventRepository
import com.example.timeinventory.core.database.dao.LogEventDao
import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.LogEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class LogEventRepositoryImpl(
    private val logEventDao: LogEventDao,
) : LogEventRepository {

    override fun getLogEventsByDateStream(date: LocalDate): Flow<List<LogEvent>> {
        val tz = TimeZone.currentSystemDefault()
        val startTimestamp = date.atStartOfDayIn(tz).toEpochMilliseconds()
        val nextDay = date.plus(1, DateTimeUnit.DAY)
        val endTimestamp = nextDay.atStartOfDayIn(tz).toEpochMilliseconds()

        return logEventDao.getByRangeWithCategoryAsFlow(startTimestamp, endTimestamp)
            .map { results ->
                results.map { it.logEvent.toDomainModel(it.category.toDomainModel()) }
            }
    }

    override fun getLogEventsByPeriodStream(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<LogEvent>> {
        val tz = TimeZone.currentSystemDefault()
        val startTimestamp = startDate.atStartOfDayIn(tz).toEpochMilliseconds()
        val nextDay = endDate.plus(1, DateTimeUnit.DAY)
        val endTimestamp = nextDay.atStartOfDayIn(tz).toEpochMilliseconds()

        return logEventDao.getByPeriodWithCategoryAsFlow(startTimestamp, endTimestamp)
            .map { results ->
                results.map { it.logEvent.toDomainModel(it.category.toDomainModel()) }
            }
    }

    override suspend fun startTimer(category: Category): LogEvent {
        val now = Clock.System.now()
        val logEvent = LogEvent(
            id = Uuid.random(),
            startDateTime = now,
            endDateTime = null,
            activity = "",
            category = category,
            memo = "",
        )
        logEventDao.upsert(logEvent.toEntity())
        return logEvent
    }

    override suspend fun stopTimer(logEventId: Uuid): Result<Unit> {
        val entity = logEventDao.getById(logEventId.toString())
            ?: return Result.failure(LogEventNotFoundException(logEventId))

        // 既に停止済みの場合は何もせず成功を返す
        if (entity.endDateTime != null) {
            return Result.success(Unit)
        }

        val updatedEntity = entity.copy(endDateTime = Clock.System.now())
        logEventDao.upsert(updatedEntity)
        return Result.success(Unit)
    }

    override suspend fun getActiveLogEvent(): LogEvent? {
        val result = logEventDao.getActiveWithCategory() ?: return null
        return result.logEvent.toDomainModel(result.category.toDomainModel())
    }

    override suspend fun upsertLogEvent(logEvent: LogEvent) {
        logEventDao.upsert(logEvent.toEntity())
    }

    override suspend fun deleteLogEvent(id: Uuid) {
        logEventDao.deleteById(id.toString())
    }

    override suspend fun getLogEventById(id: Uuid): LogEvent? {
        val result = logEventDao.getByIdWithCategory(id.toString()) ?: return null
        return result.logEvent.toDomainModel(result.category.toDomainModel())
    }
}

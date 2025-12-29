package com.example.timeinventory.core.data.repository.impl

import com.example.timeinventory.core.data.mapper.toDomainModel
import com.example.timeinventory.core.data.mapper.toEntity
import com.example.timeinventory.core.data.repository.PlannedEventRepository
import com.example.timeinventory.core.database.dao.PlannedEventDao
import com.example.timeinventory.core.model.PlannedEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PlannedEventRepositoryImpl(
    private val plannedEventDao: PlannedEventDao,
) : PlannedEventRepository {

    override fun getPlannedEventsByDateStream(date: LocalDate): Flow<List<PlannedEvent>> {
        val tz = TimeZone.currentSystemDefault()
        val startTimestamp = date.atStartOfDayIn(tz).toEpochMilliseconds()
        val nextDay = date.plus(1, DateTimeUnit.DAY)
        val endTimestamp = nextDay.atStartOfDayIn(tz).toEpochMilliseconds()

        return plannedEventDao.getByRangeWithCategoryAsFlow(startTimestamp, endTimestamp).map { results ->
            results.map { it.plannedEvent.toDomainModel(it.category.toDomainModel()) }
        }
    }

    override fun getPlannedEventsByPeriodStream(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<PlannedEvent>> {
        val tz = TimeZone.currentSystemDefault()
        val startTimestamp = startDate.atStartOfDayIn(tz).toEpochMilliseconds()
        val nextDay = endDate.plus(1, DateTimeUnit.DAY)
        val endTimestamp = nextDay.atStartOfDayIn(tz).toEpochMilliseconds()

        return plannedEventDao.getByPeriodWithCategoryAsFlow(startTimestamp, endTimestamp).map { results ->
            results.map { it.plannedEvent.toDomainModel(it.category.toDomainModel()) }
        }
    }

    override suspend fun upsertPlannedEvent(plannedEvent: PlannedEvent) {
        plannedEventDao.upsert(plannedEvent.toEntity())
    }

    override suspend fun deletePlannedEvent(id: Uuid) {
        plannedEventDao.deleteById(id.toString())
    }

    override suspend fun getPlannedEventById(id: Uuid): PlannedEvent? {
        val result = plannedEventDao.getByIdWithCategory(id.toString()) ?: return null
        return result.plannedEvent.toDomainModel(result.category.toDomainModel())
    }
}

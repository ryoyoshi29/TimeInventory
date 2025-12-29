package com.example.timeinventory.core.data.mapper

import com.example.timeinventory.core.database.entity.PlannedEventEntity
import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.Frequency
import com.example.timeinventory.core.model.PlannedEvent
import com.example.timeinventory.core.model.PlannedEventSource
import com.example.timeinventory.core.model.RecurrenceRule
import kotlinx.datetime.DayOfWeek
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun PlannedEventEntity.toDomainModel(category: Category): PlannedEvent {
    val freq = recurrenceFrequency
    val recurrenceRule = freq?.let {
        RecurrenceRule(
            frequency = Frequency.valueOf(it),
            interval = recurrenceInterval ?: 1,
            daysOfWeek = recurrenceDaysOfWeek?.split(",")?.map { DayOfWeek(it.toInt()) },
            endDate = recurrenceEndDate,
            count = recurrenceCount,
        )
    }

    return PlannedEvent(
        id = Uuid.parse(id),
        activity = activity,
        category = category,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        isAllDay = isAllDay,
        recurrenceRule = recurrenceRule,
        memo = memo,
        externalCalendarId = externalCalendarId,
        source = PlannedEventSource.valueOf(source),
        isActive = isActive,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun PlannedEvent.toEntity(): PlannedEventEntity {
    return PlannedEventEntity(
        id = id.toString(),
        activity = activity,
        categoryId = category.id.toString(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        isAllDay = isAllDay,
        recurrenceFrequency = recurrenceRule?.frequency?.name,
        recurrenceInterval = recurrenceRule?.interval,
        recurrenceDaysOfWeek = recurrenceRule?.daysOfWeek?.joinToString(",") { (it.ordinal + 1).toString() },
        recurrenceEndDate = recurrenceRule?.endDate,
        recurrenceCount = recurrenceRule?.count,
        memo = memo,
        externalCalendarId = externalCalendarId,
        source = source.name,
        isActive = isActive,
    )
}

package com.example.timeinventory.core.data.mapper

import com.example.timeinventory.core.database.entity.LogEventEntity
import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.LogEvent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun LogEventEntity.toDomainModel(category: Category): LogEvent {
    return LogEvent(
        id = Uuid.parse(id),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        activity = activity,
        category = category,
        memo = memo,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun LogEvent.toEntity(): LogEventEntity {
    return LogEventEntity(
        id = id.toString(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        activity = activity,
        categoryId = category.id.toString(),
        memo = memo,
    )
}

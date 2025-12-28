package com.example.timeinventory.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

@Entity(
    tableName = "planned_event",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["startDateTime"]),
        Index(value = ["categoryId"]),
        Index(value = ["isActive"]),
    ],
)
data class PlannedEventEntity(
    @PrimaryKey
    val id: String,
    val activity: String,
    val categoryId: String,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val isAllDay: Boolean = false,
    val recurrenceFrequency: String? = null,
    val recurrenceInterval: Int? = null,
    val recurrenceDaysOfWeek: String? = null,
    val recurrenceEndDate: LocalDate? = null,
    val recurrenceCount: Int? = null,
    val memo: String = "",
    val externalCalendarId: String? = null,
    val source: String = "MANUAL",
    val isActive: Boolean = true,
)

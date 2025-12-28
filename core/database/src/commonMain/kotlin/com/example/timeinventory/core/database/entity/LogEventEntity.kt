package com.example.timeinventory.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(
    tableName = "log_event",
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
        Index(value = ["endDateTime"]),
        Index(value = ["categoryId"]),
    ],
)
data class LogEventEntity(
    @PrimaryKey
    val id: String,
    val startDateTime: Instant,
    val endDateTime: Instant? = null,
    val activity: String = "",
    val categoryId: String,
    val memo: String = "",
)

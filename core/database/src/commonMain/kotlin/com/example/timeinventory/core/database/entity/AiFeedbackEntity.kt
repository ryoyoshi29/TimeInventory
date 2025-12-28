package com.example.timeinventory.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "ai_feedback",
    indices = [
        Index(value = ["targetDate"], unique = true),
    ],
)
data class AiFeedbackEntity(
    @PrimaryKey
    val id: String,
    val targetDate: LocalDate,
    val summary: String,
    val keepTitle: String,
    val keepDescription: String,
    val problemTitle: String,
    val problemDescription: String,
    val tryActionTitle: String,
    val tryActionDescription: String,
)

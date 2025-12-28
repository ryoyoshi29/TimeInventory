package com.example.timeinventory.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val colorArgb: Int,
    val sortOrder: Int = 0,
)

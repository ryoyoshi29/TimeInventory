package com.example.timeinventory.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.timeinventory.core.database.converter.Converters
import com.example.timeinventory.core.database.dao.AiFeedbackDao
import com.example.timeinventory.core.database.dao.CategoryDao
import com.example.timeinventory.core.database.dao.LogEventDao
import com.example.timeinventory.core.database.dao.PlannedEventDao
import com.example.timeinventory.core.database.entity.AiFeedbackEntity
import com.example.timeinventory.core.database.entity.CategoryEntity
import com.example.timeinventory.core.database.entity.LogEventEntity
import com.example.timeinventory.core.database.entity.PlannedEventEntity

@Database(
    entities = [
        CategoryEntity::class,
        LogEventEntity::class,
        PlannedEventEntity::class,
        AiFeedbackEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(TimeInventoryDatabaseConstructor::class)
abstract class TimeInventoryDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun logEventDao(): LogEventDao
    abstract fun plannedEventDao(): PlannedEventDao
    abstract fun aiFeedbackDao(): AiFeedbackDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TimeInventoryDatabaseConstructor : RoomDatabaseConstructor<TimeInventoryDatabase> {
    override fun initialize(): TimeInventoryDatabase
}

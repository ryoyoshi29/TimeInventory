package com.example.timeinventory.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timeinventory.core.database.entity.PlannedEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedEventDao {
    @Query("""
        SELECT * FROM planned_event
        WHERE isActive = 1
          AND date(startDateTime / 1000, 'unixepoch', 'localtime') = date(:timestamp / 1000, 'unixepoch', 'localtime')
        ORDER BY startDateTime ASC
    """)
    fun getByDateAsFlow(timestamp: Long): Flow<List<PlannedEventEntity>>

    @Query("""
        SELECT * FROM planned_event
        WHERE isActive = 1
          AND startDateTime >= :startTimestamp AND startDateTime <= :endTimestamp
        ORDER BY startDateTime ASC
    """)
    fun getByPeriodAsFlow(startTimestamp: Long, endTimestamp: Long): Flow<List<PlannedEventEntity>>

    @Query("SELECT * FROM planned_event WHERE id = :id")
    suspend fun getById(id: String): PlannedEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plannedEvent: PlannedEventEntity)

    @Query("DELETE FROM planned_event WHERE id = :id")
    suspend fun deleteById(id: String)
}

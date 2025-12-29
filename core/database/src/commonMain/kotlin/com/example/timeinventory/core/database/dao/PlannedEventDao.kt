package com.example.timeinventory.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timeinventory.core.database.entity.PlannedEventEntity
import com.example.timeinventory.core.database.model.PlannedEventWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedEventDao {
    @Query(
        """
        SELECT
            planned_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM planned_event
        INNER JOIN category ON planned_event.categoryId = category.id
        WHERE planned_event.isActive = 1
          AND planned_event.startDateTime >= :startTimestamp AND planned_event.startDateTime < :endTimestamp
        ORDER BY planned_event.startDateTime ASC
    """
    )
    fun getByRangeWithCategoryAsFlow(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<List<PlannedEventWithCategory>>

    @Query(
        """
        SELECT
            planned_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM planned_event
        INNER JOIN category ON planned_event.categoryId = category.id
        WHERE planned_event.isActive = 1
          AND planned_event.startDateTime >= :startTimestamp AND planned_event.startDateTime < :endTimestamp
        ORDER BY planned_event.startDateTime ASC
    """
    )
    fun getByPeriodWithCategoryAsFlow(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<List<PlannedEventWithCategory>>

    @Query(
        """
        SELECT
            planned_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM planned_event
        INNER JOIN category ON planned_event.categoryId = category.id
        WHERE planned_event.id = :id
    """
    )
    suspend fun getByIdWithCategory(id: String): PlannedEventWithCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plannedEvent: PlannedEventEntity)

    @Query("DELETE FROM planned_event WHERE id = :id")
    suspend fun deleteById(id: String)
}

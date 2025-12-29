package com.example.timeinventory.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timeinventory.core.database.entity.LogEventEntity
import com.example.timeinventory.core.database.model.LogEventWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface LogEventDao {
    @Query(
        """
        SELECT
            log_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM log_event
        INNER JOIN category ON log_event.categoryId = category.id
        WHERE log_event.startDateTime >= :startTimestamp AND log_event.startDateTime < :endTimestamp
        ORDER BY log_event.startDateTime ASC
    """
    )
    fun getByRangeWithCategoryAsFlow(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<List<LogEventWithCategory>>

    @Query(
        """
        SELECT
            log_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM log_event
        INNER JOIN category ON log_event.categoryId = category.id
        WHERE log_event.startDateTime >= :startTimestamp AND log_event.startDateTime < :endTimestamp
        ORDER BY log_event.startDateTime ASC
    """
    )
    fun getByPeriodWithCategoryAsFlow(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<List<LogEventWithCategory>>

    @Query(
        """
        SELECT
            log_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM log_event
        INNER JOIN category ON log_event.categoryId = category.id
        WHERE log_event.id = :id
    """
    )
    suspend fun getByIdWithCategory(id: String): LogEventWithCategory?

    @Query("SELECT * FROM log_event WHERE id = :id")
    suspend fun getById(id: String): LogEventEntity?

    @Query(
        """
        SELECT
            log_event.*,
            category.id as category_id,
            category.name as category_name,
            category.colorArgb as category_colorArgb,
            category.sortOrder as category_sortOrder
        FROM log_event
        INNER JOIN category ON log_event.categoryId = category.id
        WHERE log_event.endDateTime IS NULL
        LIMIT 1
    """
    )
    suspend fun getActiveWithCategory(): LogEventWithCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(logEvent: LogEventEntity)

    @Query("DELETE FROM log_event WHERE id = :id")
    suspend fun deleteById(id: String)
}

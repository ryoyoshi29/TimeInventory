package com.example.timeinventory.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timeinventory.core.database.entity.LogEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogEventDao {
    @Query(
        """
        SELECT * FROM log_event
        WHERE date(startDateTime / 1000, 'unixepoch', 'localtime') = date(:timestamp / 1000, 'unixepoch', 'localtime')
        ORDER BY startDateTime ASC
    """
    )
    fun getByDateAsFlow(timestamp: Long): Flow<List<LogEventEntity>>

    @Query(
        """
        SELECT * FROM log_event
        WHERE startDateTime >= :startTimestamp AND startDateTime <= :endTimestamp
        ORDER BY startDateTime ASC
    """
    )
    fun getByPeriodAsFlow(startTimestamp: Long, endTimestamp: Long): Flow<List<LogEventEntity>>

    @Query("SELECT * FROM log_event WHERE id = :id")
    suspend fun getById(id: String): LogEventEntity?

    @Query("SELECT * FROM log_event WHERE endDateTime IS NULL LIMIT 1")
    suspend fun getActive(): LogEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(logEvent: LogEventEntity)

    @Query("DELETE FROM log_event WHERE id = :id")
    suspend fun deleteById(id: String)
}

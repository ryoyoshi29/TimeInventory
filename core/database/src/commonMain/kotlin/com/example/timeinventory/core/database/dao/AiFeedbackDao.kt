package com.example.timeinventory.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timeinventory.core.database.entity.AiFeedbackEntity

@Dao
interface AiFeedbackDao {
    @Query("SELECT * FROM ai_feedback WHERE targetDate = :targetDate")
    suspend fun getByDate(targetDate: String): AiFeedbackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(feedback: AiFeedbackEntity)
}

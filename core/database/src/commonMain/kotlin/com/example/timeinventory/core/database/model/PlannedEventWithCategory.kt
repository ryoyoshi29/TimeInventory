package com.example.timeinventory.core.database.model

import androidx.room.Embedded
import com.example.timeinventory.core.database.entity.CategoryEntity
import com.example.timeinventory.core.database.entity.PlannedEventEntity

/**
 * PlannedEventとCategoryを結合したデータモデル
 *
 * Room の手動JOIN結果を受け取るための中間オブジェクト。
 * N+1問題を回避するため、1クエリで両方のデータを取得する。
 */
data class PlannedEventWithCategory(
    @Embedded val plannedEvent: PlannedEventEntity,
    @Embedded(prefix = "category_") val category: CategoryEntity,
)

package com.example.timeinventory.feature.timeline

import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent
import kotlinx.datetime.LocalDate

/**
 * タイムライン画面の状態
 */
data class TimelineUiState(
    val selectedDate: LocalDate,
    val categories: List<Category> = emptyList(),
    val eventState: TimelineEventState = TimelineEventState.Loading
)

/**
 * イベントの状態
 */
sealed interface TimelineEventState {
    /** ロード中 */
    data object Loading : TimelineEventState

    /** 読み込み完了 */
    data class Success(
        val logEvents: List<LogEvent>,
        val plannedEvents: List<PlannedEvent>,
    ) : TimelineEventState

    /** エラー発生 */
    data class Error(val message: String) : TimelineEventState
}
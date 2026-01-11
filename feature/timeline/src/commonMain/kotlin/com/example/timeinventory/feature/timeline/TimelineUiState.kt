package com.example.timeinventory.feature.timeline

import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent

/**
 * タイムラインUI状態
 */
sealed interface TimelineUiState {
    /** ロード中 */
    data object Loading : TimelineUiState

    /** 成功（データ表示） */
    data class Success(
        val logEvents: List<LogEvent>,
        val plannedEvents: List<PlannedEvent>,
    ) : TimelineUiState

    /** エラー */
    data class Error(val message: String) : TimelineUiState
}

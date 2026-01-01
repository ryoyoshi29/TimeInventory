package com.example.timeinventory.feature.timeline

/**
 * タイムラインUI状態
 */
sealed interface TimelineUiState {
    /** ロード中 */
    data object Loading : TimelineUiState

    /** 成功（データ表示） */
    data class Success(
        val logEvents: List<Any>, // TODO: LogEventWithCategoryに変更
        val plannedEvents: List<Any>, // TODO: PlannedEventWithCategoryに変更
    ) : TimelineUiState

    /** エラー */
    data class Error(val message: String) : TimelineUiState
}

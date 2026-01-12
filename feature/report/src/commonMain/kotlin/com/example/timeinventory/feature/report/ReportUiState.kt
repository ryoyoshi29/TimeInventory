package com.example.timeinventory.feature.report

import com.example.timeinventory.core.model.AiFeedback

data class ReportUiState(
    val aiFeedbackState: AiFeedbackState = AiFeedbackState.Initial,
)

/**
 * AIFeedbackの状態
 */
sealed interface AiFeedbackState {
    data object Initial : AiFeedbackState

    data object Loading : AiFeedbackState

    data class Success(
        val aiFeedback: AiFeedback
    ) : AiFeedbackState

    data class Error(val message: String) : AiFeedbackState
}
package com.example.timeinventory.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeinventory.core.data.repository.AiFeedbackRepository
import com.example.timeinventory.core.data.repository.LogEventRepository
import com.example.timeinventory.core.data.repository.PlannedEventRepository
import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ReportViewModel(
    private val logEventRepository: LogEventRepository,
    private val plannedEventRepository: PlannedEventRepository,
    private val aiFeedbackRepository: AiFeedbackRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun generateFeedback(targetDate: LocalDate, prompt: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(aiFeedbackState = AiFeedbackState.Loading)

            try {
//                val existingFeedback = aiFeedbackRepository.getFeedbackByDate(targetDate)
//                if (existingFeedback != null) {
//                    _uiState.value =
//                        _uiState.value.copy(
//                            aiFeedbackState = AiFeedbackState.Success(
//                                existingFeedback
//                            )
//                        )
//                    return@launch
//                }

                val newFeedback = aiFeedbackRepository.generateFeedback(
                    targetDate,
                    prompt
                )

                _uiState.value = _uiState.value.copy(
                    aiFeedbackState = AiFeedbackState.Success(newFeedback)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    aiFeedbackState = AiFeedbackState.Error(
                        message = e.message ?: "Unknown error occurred"
                    )
                )
            }
        }
    }

    suspend fun getFormattedLogEvents(targetDate: LocalDate): String {
        val logEvents = logEventRepository.getLogEventsByDateStream(targetDate).first()
        return formatEventsForPrompt(logEvents)
    }

    suspend fun getFormattedPlannedEvents(targetDate: LocalDate): String {
        val plannedEvents = plannedEventRepository.getPlannedEventsByDateStream(targetDate).first()
        return formatEventsForPrompt(plannedEvents)
    }

    private fun <T> formatEventsForPrompt(events: List<T>): String {
        if (events.isEmpty()) {
            return "なし"
        }

        val timeZone = TimeZone.currentSystemDefault()

        return events.joinToString(separator = "\n") { event ->
            when (event) {
                is LogEvent -> {
                    val startTime = event.startDateTime.toLocalDateTime(timeZone).time
                    val endTime = event.endDateTime?.toLocalDateTime(timeZone)?.time
                        ?: Clock.System.now().toLocalDateTime(timeZone).time
                    // e.g. "- 09:00 - 10:00: [Work] Meeting"
                    "- ${formatTime(startTime)} - ${formatTime(endTime)}: [${event.category.name}] ${event.activity}"
                }

                is PlannedEvent -> {
                    val startTime = event.startDateTime.toLocalDateTime(timeZone).time
                    val endTime = event.endDateTime.toLocalDateTime(timeZone).time

                    "- ${formatTime(startTime)} - ${formatTime(endTime)}: [${event.category.name}] ${event.activity}"
                }

                else -> ""
            }
        }
    }

    private fun formatTime(time: kotlinx.datetime.LocalTime): String {
        return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
    }
}

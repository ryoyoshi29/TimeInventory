package com.example.timeinventory.feature.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent
import com.example.timeinventory.feature.timeline.component.EventBottomSheetContent
import com.example.timeinventory.feature.timeline.component.TimelineGrid
import com.example.timeinventory.feature.timeline.component.TimelineHeader
import com.example.timeinventory.feature.timeline.util.toLocalTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import timeinventory.feature.timeline.generated.resources.Res
import timeinventory.feature.timeline.generated.resources.default_category_exercise
import timeinventory.feature.timeline.generated.resources.default_category_hobby
import timeinventory.feature.timeline.generated.resources.default_category_meal
import timeinventory.feature.timeline.generated.resources.default_category_other
import timeinventory.feature.timeline.generated.resources.default_category_sleep
import timeinventory.feature.timeline.generated.resources.default_category_study
import timeinventory.feature.timeline.generated.resources.default_category_work
import kotlin.uuid.ExperimentalUuidApi

/**
 * タイムライン画面
 *
 * アプリのメイン画面。タイムログと予定を表示する
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    // ボトムシート状態
    var bottomSheetContent by remember { mutableStateOf<BottomSheetContent?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(true)

    // アプリ初期化（初回起動時のみ実行）
    LaunchedEffect(Unit) {
        viewModel.initialize(
            workLabel = getString(Res.string.default_category_work),
            studyLabel = getString(Res.string.default_category_study),
            exerciseLabel = getString(Res.string.default_category_exercise),
            hobbyLabel = getString(Res.string.default_category_hobby),
            sleepLabel = getString(Res.string.default_category_sleep),
            mealLabel = getString(Res.string.default_category_meal),
            otherLabel = getString(Res.string.default_category_other),
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text("${selectedDate.month.number}/${selectedDate.year}")
            },
            windowInsets = TopAppBarDefaults.windowInsets
        )

        when (uiState.eventState) {
            is TimelineEventState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TimelineEventState.Success -> {
                val successState = uiState.eventState as TimelineEventState.Success

                Column(modifier = Modifier.fillMaxSize()) {
                    TimelineHeader(
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.selectDate(it) }
                    )

                    TimelineGrid(
                        logEvents = successState.logEvents,
                        plannedEvents = successState.plannedEvents,
                        onLogColumnLongPress = { startTime, endTime ->
                            bottomSheetContent = BottomSheetContent.CreateLog(startTime, endTime)
                        },
                        onScheduleColumnLongPress = { startTime, endTime ->
                            bottomSheetContent =
                                BottomSheetContent.CreatePlanned(startTime, endTime)
                        },
                        onLogEventClick = { logEvent ->
                            bottomSheetContent = BottomSheetContent.EditLog(logEvent)
                        },
                        onPlannedEventClick = { plannedEvent ->
                            bottomSheetContent = BottomSheetContent.EditPlanned(plannedEvent)
                        }
                    )
                }
            }

            is TimelineEventState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "エラー: ${(uiState.eventState as TimelineEventState.Error).message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    bottomSheetContent?.let { content ->
        ModalBottomSheet(
            onDismissRequest = { bottomSheetContent = null },
            sheetState = bottomSheetState
        ) {
            when (content) {
                is BottomSheetContent.CreateLog -> {
                    EventBottomSheetContent(
                        initialStartTime = content.startTime,
                        initialEndTime = content.endTime,
                        categories = uiState.categories,
                        onSave = { title, startTime, endTime, category, memo ->
                            viewModel.createLogEvent(title, startTime, endTime, category, memo)
                            bottomSheetContent = null
                        },
                        onDismiss = { bottomSheetContent = null },
                        onDelete = null
                    )
                }

                is BottomSheetContent.CreatePlanned -> {
                    EventBottomSheetContent(
                        initialStartTime = content.startTime,
                        initialEndTime = content.endTime,
                        categories = uiState.categories,
                        onSave = { title, startTime, endTime, category, memo ->
                            viewModel.createPlannedEvent(title, startTime, endTime, category, memo)
                            bottomSheetContent = null
                        },
                        onDismiss = { bottomSheetContent = null },
                        onDelete = null
                    )
                }

                is BottomSheetContent.EditLog -> {
                    EventBottomSheetContent(
                        initialTitle = content.event.activity,
                        initialCategory = content.event.category,
                        initialStartTime = content.event.startDateTime.toLocalTime(),
                        initialEndTime = content.event.endDateTime?.toLocalTime()
                            ?: content.event.startDateTime.toLocalTime(),
                        initialMemo = content.event.memo,
                        categories = uiState.categories,
                        editEventId = content.event.id,
                        onSave = { title, startTime, endTime, category, memo ->
                            viewModel.updateLogEvent(
                                content.event.id,
                                title,
                                startTime,
                                endTime,
                                category,
                                memo
                            )
                            bottomSheetContent = null
                        },
                        onDismiss = { bottomSheetContent = null },
                        onDelete = { id ->
                            viewModel.deleteLogEvent(id)
                            bottomSheetContent = null
                        }
                    )
                }

                is BottomSheetContent.EditPlanned -> {
                    EventBottomSheetContent(
                        initialTitle = content.event.activity,
                        initialCategory = content.event.category,
                        initialStartTime = content.event.startDateTime.toLocalTime(),
                        initialEndTime = content.event.endDateTime.toLocalTime(),
                        initialMemo = content.event.memo,
                        categories = uiState.categories,
                        editEventId = content.event.id,
                        onSave = { title, startTime, endTime, category, memo ->
                            viewModel.updatePlannedEvent(
                                content.event.id,
                                title,
                                startTime,
                                endTime,
                                category,
                                memo
                            )
                            bottomSheetContent = null
                        },
                        onDismiss = { bottomSheetContent = null },
                        onDelete = { id ->
                            viewModel.deletePlannedEvent(id)
                            bottomSheetContent = null
                        }
                    )
                }
            }
        }
    }
}

/**
 * ボトムシートの状態
 */
private sealed interface BottomSheetContent {
    /**
     * LogEvent作成
     */
    data class CreateLog(
        val startTime: LocalTime,
        val endTime: LocalTime
    ) : BottomSheetContent

    /**
     * PlannedEvent作成
     */
    data class CreatePlanned(
        val startTime: LocalTime,
        val endTime: LocalTime
    ) : BottomSheetContent

    /**
     * LogEvent編集
     */
    data class EditLog(val event: LogEvent) : BottomSheetContent

    /**
     * PlannedEvent編集
     */
    data class EditPlanned(val event: PlannedEvent) : BottomSheetContent
}

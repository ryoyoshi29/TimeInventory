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
import com.example.timeinventory.feature.timeline.component.EventBottomSheetContent
import com.example.timeinventory.feature.timeline.component.TimelineGrid
import com.example.timeinventory.feature.timeline.component.TimelineHeader
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
 * イベント作成ボトムシートの種類
 */
private enum class EventBottomSheetType {
    LOG_EVENT,
    PLANNED_EVENT
}

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

    // イベント作成ボトムシート状態
    var showBottomSheet by remember { mutableStateOf(false) }
    var bottomSheetType by remember { mutableStateOf<EventBottomSheetType?>(null) }
    var bottomSheetStartTime by remember { mutableStateOf(LocalTime(0, 0)) }
    var bottomSheetEndTime by remember { mutableStateOf(LocalTime(1, 0)) }
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
                Text("${uiState.selectedDate.month.number}/${uiState.selectedDate.year}")
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
                        selectedDate = uiState.selectedDate,
                        onDateSelected = { viewModel.selectDate(it) }
                    )

                    TimelineGrid(
                        logEvents = successState.logEvents,
                        plannedEvents = successState.plannedEvents,
                        onLogColumnLongPress = { startTime, endTime ->
                            bottomSheetStartTime = startTime
                            bottomSheetEndTime = endTime
                            bottomSheetType = EventBottomSheetType.LOG_EVENT
                            showBottomSheet = true
                        },
                        onScheduleColumnLongPress = { startTime, endTime ->
                            bottomSheetStartTime = startTime
                            bottomSheetEndTime = endTime
                            bottomSheetType = EventBottomSheetType.PLANNED_EVENT
                            showBottomSheet = true
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

    // イベント作成ボトムシート
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            EventBottomSheetContent(
                initialStartTime = bottomSheetStartTime,
                initialEndTime = bottomSheetEndTime,
                categories = uiState.categories,
                onSave = { title, startTime, endTime, category, memo ->
                    when (bottomSheetType) {
                        EventBottomSheetType.LOG_EVENT -> {
                            viewModel.createLogEvent(title, startTime, endTime, category, memo)
                        }
                        EventBottomSheetType.PLANNED_EVENT -> {
                            viewModel.createPlannedEvent(title, startTime, endTime, category, memo)
                        }
                        null -> {}
                    }
                    showBottomSheet = false
                },
                onDismiss = {
                    showBottomSheet = false
                }
            )
        }
    }
}

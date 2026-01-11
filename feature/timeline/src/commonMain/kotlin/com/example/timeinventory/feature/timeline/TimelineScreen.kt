package com.example.timeinventory.feature.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.timeinventory.feature.timeline.component.TimelineGrid
import com.example.timeinventory.feature.timeline.component.TimelineHeader
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

/**
 * タイムライン画面
 *
 * アプリのメイン画面。タイムログと予定を表示する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

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

        when (uiState) {
            is TimelineUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TimelineUiState.Success -> {
                val successState = uiState as TimelineUiState.Success

                Column(modifier = Modifier.fillMaxSize()) {
                    TimelineHeader(
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.selectDate(it) }
                    )

                    TimelineGrid(
                        logEvents = successState.logEvents,
                        plannedEvents = successState.plannedEvents
                    )
                }
            }

            is TimelineUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "エラー: ${(uiState as TimelineUiState.Error).message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

package com.example.timeinventory.feature.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

/**
 * タイムライン画面
 *
 * アプリのメイン画面。タイムログと予定を表示する
 */
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // アプリ初期化（初回起動時のみ実行）
    LaunchedEffect(Unit) {
        viewModel.initialize(
            workLabel = "仕事", // TODO: stringResourceでローカライズ
            studyLabel = "勉強",
            exerciseLabel = "運動",
            hobbyLabel = "趣味",
            sleepLabel = "睡眠",
            mealLabel = "食事",
            otherLabel = "その他",
        )
    }

    Scaffold { paddingValues ->
        when (uiState) {
            is TimelineUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TimelineUiState.Success -> {
                // TODO: 実際のタイムラインUIを実装
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "タイムライン画面（MVP実装予定）",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            is TimelineUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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

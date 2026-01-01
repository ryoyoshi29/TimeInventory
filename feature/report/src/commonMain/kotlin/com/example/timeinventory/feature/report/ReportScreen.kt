package com.example.timeinventory.feature.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * レポート画面
 *
 * グラフや統計情報を表示する画面（MVP: プレースホルダー）
 */
@Composable
fun ReportScreen() {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "レポート画面（Phase 4で実装予定）",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

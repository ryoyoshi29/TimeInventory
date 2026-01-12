package com.example.timeinventory.feature.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent
import kotlinx.datetime.LocalTime

// 定数定義
/** 1時間あたりの高さ */
private val HOUR_HEIGHT: Dp = 60.dp

/** 時間列の幅 */
internal val TIME_COLUMN_WIDTH: Dp = 56.dp

/**
 * タイムライングリッド（24時間表示）
 *
 * 3列構成：時間ラベル列 | ログ列 | スケジュール列
 *
 * @param logEvents ログイベントリスト
 * @param plannedEvents 予定イベントリスト
 * @param onLogColumnLongPress ログ列長押し時のコールバック（開始時刻、終了時刻）
 * @param modifier Modifier
 */
@Composable
fun TimelineGrid(
    logEvents: List<LogEvent> = arrayListOf(),
    plannedEvents: List<PlannedEvent> = arrayListOf(),
    onLogColumnLongPress: (LocalTime, LocalTime) -> Unit,
    onScheduleColumnLongPress: (LocalTime, LocalTime) -> Unit = { _, _ -> },
    onLogEventClick: (LogEvent) -> Unit = {},
    onPlannedEventClick: (PlannedEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TimeColumn()

        LogColumn(
            logEvents = logEvents,
            onLongPress = onLogColumnLongPress,
            onLogEventClick = onLogEventClick,
            modifier = Modifier.weight(1f).padding(top = 8.dp)
        )

        VerticalDivider(
            modifier = Modifier.height(HOUR_HEIGHT * 24),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        ScheduleColumn(
            plannedEvents = plannedEvents,
            onLongPress = onScheduleColumnLongPress,
            onPlannedEventClick = onPlannedEventClick,
            modifier = Modifier.weight(1f).padding(top = 8.dp)
        )
    }
}

/**
 * 時間列（00:00, 01:00...の24時間ラベル）
 */
@Composable
private fun TimeColumn(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(TIME_COLUMN_WIDTH)
            .height(HOUR_HEIGHT * 24)
    ) {
        (0..23).forEach { hour ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HOUR_HEIGHT),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = formatHourLabel(hour),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
            }
        }
    }
}

/**
 * ログ列（LogEventブロックを表示）
 */
@Composable
private fun LogColumn(
    logEvents: List<LogEvent>,
    onLongPress: (LocalTime, LocalTime) -> Unit,
    onLogEventClick: (LogEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .height(HOUR_HEIGHT * 24)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            (0..23).forEach { hour ->
                HourSlot(
                    hour = hour,
                    onLongPress = onLongPress
                )
            }
        }

        // LogEventブロック
        logEvents.forEach { logEvent ->
            TimeBlock(
                startDateTime = logEvent.startDateTime,
                endDateTime = logEvent.endDateTime,
                label = logEvent.activity,
                colorArgb = logEvent.category.colorArgb,
                hourHeight = HOUR_HEIGHT,
                alpha = 0.3f,
                onClick = { onLogEventClick(logEvent) },
                modifier = Modifier
            )
        }
    }
}

/**
 * 1時間分のスロット（長押しで視覚フィードバック表示）
 */
@Composable
private fun HourSlot(
    hour: Int,
    onLongPress: (LocalTime, LocalTime) -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HOUR_HEIGHT)
            .background(
                if (isPressed) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                } else {
                    androidx.compose.ui.graphics.Color.Transparent
                }
            )
            .pointerInput(hour) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onLongPress = {
                        val startTime = LocalTime(hour, 0)
                        val endTime = if (hour < 23) {
                            LocalTime(hour + 1, 0)
                        } else {
                            LocalTime(23, 59)
                        }
                        onLongPress(startTime, endTime)
                    }
                )
            }
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

/**
 * スケジュール列（PlannedEventブロックを表示）
 */
@Composable
private fun ScheduleColumn(
    plannedEvents: List<PlannedEvent>,
    onLongPress: (LocalTime, LocalTime) -> Unit,
    onPlannedEventClick: (PlannedEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .height(HOUR_HEIGHT * 24)
    ) {
        // 時間区切り線（1時間ごと）+ 長押し検出
        Column(modifier = Modifier.fillMaxSize()) {
            (0..23).forEach { hour ->
                HourSlot(
                    hour = hour,
                    onLongPress = onLongPress
                )
            }
        }

        // PlannedEventブロックを絶対位置で配置
        plannedEvents.forEach { plannedEvent ->
            TimeBlock(
                startDateTime = plannedEvent.startDateTime,
                endDateTime = plannedEvent.endDateTime,
                label = plannedEvent.activity,
                colorArgb = plannedEvent.category.colorArgb,
                hourHeight = HOUR_HEIGHT,
                alpha = 0.3f,
                onClick = { onPlannedEventClick(plannedEvent) },
                modifier = Modifier
            )
        }
    }
}

/**
 * 時間を24時間形式のラベルに変換
 *
 * @param hour 0-23の時間
 * @return "00:00", "01:00", "13:00"等
 */
private fun formatHourLabel(hour: Int): String {
    return "${hour.toString().padStart(2, '0')}:00"
}


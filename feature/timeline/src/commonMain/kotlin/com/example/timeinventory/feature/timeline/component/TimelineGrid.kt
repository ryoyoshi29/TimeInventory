package com.example.timeinventory.feature.timeline.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi

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
 * @param modifier Modifier
 */
@Composable
fun TimelineGrid(
    logEvents: List<LogEvent> = arrayListOf<LogEvent>(),
    plannedEvents: List<PlannedEvent> = arrayListOf<PlannedEvent>(),
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
            modifier = Modifier.weight(1f).padding(top = 8.dp)
        )

        VerticalDivider(
            modifier = Modifier.height(HOUR_HEIGHT * 24),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        ScheduleColumn(
            plannedEvents = plannedEvents,
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
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .height(HOUR_HEIGHT * 24)
    ) {
        // 時間区切り線（1時間ごと）
        Column(modifier = Modifier.fillMaxSize()) {
            (0..23).forEach { hour ->
                Box(modifier = Modifier.height(HOUR_HEIGHT)) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }

        val logEvents = createSampleLogEvents()

        logEvents.forEach { logEvent ->
            TimeBlock(
                startDateTime = logEvent.startDateTime,
                endDateTime = logEvent.endDateTime,
                label = logEvent.activity,
                colorArgb = logEvent.category.colorArgb,
                hourHeight = HOUR_HEIGHT,
                alpha = 0.3f,
                onClick = {},
                modifier = Modifier
            )
        }
    }
}

/**
 * スケジュール列（PlannedEventブロックを表示）
 */
@Composable
private fun ScheduleColumn(
    plannedEvents: List<PlannedEvent>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .height(HOUR_HEIGHT * 24)
    ) {
        // 時間区切り線（1時間ごと）
        Column(modifier = Modifier.fillMaxSize()) {
            (0..23).forEach { hour ->
                Box(modifier = Modifier.height(HOUR_HEIGHT)) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
            }
        }

        val plannedEvents = createSamplePlannedEvents()

        // PlannedEventブロックを絶対位置で配置
        plannedEvents.forEach { plannedEvent ->
            TimeBlock(
                startDateTime = plannedEvent.startDateTime,
                endDateTime = plannedEvent.endDateTime,
                label = plannedEvent.activity,
                colorArgb = plannedEvent.category.colorArgb,
                hourHeight = HOUR_HEIGHT,
                alpha = 0.3f,
                onClick = {},
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

@OptIn(ExperimentalUuidApi::class)
fun createSamplePlannedEvents(): List<PlannedEvent> {
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = kotlinx.datetime.LocalDate(2026, 1, 4)

    return listOf(
        PlannedEvent(
            activity = "朝会",
            category = SampleCategories.work,
            startDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(9, 0)
            ).toInstant(timeZone),
            endDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(9, 20)
            ).toInstant(timeZone)
        ),

        PlannedEvent(
            activity = "ランチミーティング",
            category = SampleCategories.breakTime,
            startDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(12, 0)
            ).toInstant(timeZone),
            endDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(13, 0)
            ).toInstant(timeZone)
        ),

        PlannedEvent(
            activity = "資料作成",
            category = SampleCategories.work,
            startDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(14, 0)
            ).toInstant(timeZone),
            endDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(16, 0)
            ).toInstant(timeZone)
        ),

        PlannedEvent(
            activity = "コードレビュー",
            category = SampleCategories.work,
            startDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(16, 30)
            ).toInstant(timeZone),
            endDateTime = kotlinx.datetime.LocalDateTime(
                localDate,
                kotlinx.datetime.LocalTime(17, 30)
            ).toInstant(timeZone)
        )
    )
}

@OptIn(ExperimentalUuidApi::class)
fun createSampleLogEvents(): List<LogEvent> {
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = kotlinx.datetime.LocalDate(2026, 1, 4)
    val localTime = kotlinx.datetime.LocalTime(12, 0)
    val localDateTime = kotlinx.datetime.LocalDateTime(localDate, localTime)
    val baseTime = localDateTime.toInstant(timeZone)

    return listOf(
        LogEvent(
            startDateTime = baseTime + 15.minutes,
            endDateTime = baseTime + 75.minutes,
            activity = "昼休憩",
            category = SampleCategories.breakTime
        ),

        LogEvent(
            startDateTime = baseTime + 75.minutes,
            endDateTime = baseTime + 85.minutes,
            activity = "カフェへ移動",
            category = SampleCategories.moving
        ),

        LogEvent(
            startDateTime = Clock.System.now() - 45.minutes,
            endDateTime = null,
            activity = "企画MTG",
            category = SampleCategories.work,
            memo = "新機能のプロトタイプについて"
        )
    )
}

@OptIn(ExperimentalUuidApi::class)
object SampleCategories {
    val work = Category(
        name = "仕事",
        colorArgb = 0xFF2196F3.toInt(),
        sortOrder = 1
    )
    val breakTime = Category(
        name = "休憩",
        colorArgb = 0xFF4CAF50.toInt(),
        sortOrder = 2
    )
    val moving = Category(
        name = "移動・買い物",
        colorArgb = 0xFF9C27B0.toInt(),
        sortOrder = 3
    )
}


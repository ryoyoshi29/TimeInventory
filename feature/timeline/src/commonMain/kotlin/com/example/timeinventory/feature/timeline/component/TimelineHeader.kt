package com.example.timeinventory.feature.timeline.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource
import timeinventory.feature.timeline.generated.resources.Res
import timeinventory.feature.timeline.generated.resources.day_of_week_friday
import timeinventory.feature.timeline.generated.resources.day_of_week_monday
import timeinventory.feature.timeline.generated.resources.day_of_week_saturday
import timeinventory.feature.timeline.generated.resources.day_of_week_sunday
import timeinventory.feature.timeline.generated.resources.day_of_week_thursday
import timeinventory.feature.timeline.generated.resources.day_of_week_tuesday
import timeinventory.feature.timeline.generated.resources.day_of_week_wednesday
import timeinventory.feature.timeline.generated.resources.timeline_column_log
import timeinventory.feature.timeline.generated.resources.timeline_column_schedule
import kotlin.time.Clock

/**
 * 日付選択ヘッダー（週表示カレンダー形式 + タイムラインヘッダー）
 *
 * @param selectedDate 選択中の日付
 * @param onDateSelected 日付選択時のコールバック
 * @param modifier Modifier
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineHeader(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        WeekCalendar(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )

        TimelineColumnHeader()

        Spacer(Modifier.height(16.dp))

        HorizontalDivider()
    }
}

/**
 * 週カレンダー（スワイプ可能）
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WeekCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // 基準日として今日の日付を固定（再コンポーズされても変わらない）
    val baseDate = remember { today }

    // 初期ページを中央（大きな数）に設定して、前後に無限にスクロール可能にする
    val initialPage = Int.MAX_VALUE / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val weekOffset = page - initialPage
        val weekDates = getWeekDates(baseDate.plus(weekOffset * 7, DateTimeUnit.DAY))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDates.forEach { date ->
                DateItem(
                    date = date,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}

/**
 * タイムライン2列ヘッダー（「ログ」「スケジュール」）
 */
@Composable
private fun TimelineColumnHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(TIME_COLUMN_WIDTH))
        Text(
            text = stringResource(Res.string.timeline_column_log),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(Res.string.timeline_column_schedule),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 日付アイテム（曜日 + 日付）
 */
@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val dayOfWeekText = getDayOfWeekText(date.dayOfWeek)
    val dateColors = getDateItemColors(isSelected, isToday)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 曜日ラベル
        Text(
            text = dayOfWeekText,
            style = MaterialTheme.typography.bodySmall,
            color = dateColors.dayOfWeekColor
        )

        // 日付（円形背景あり）
        DateCircle(
            day = date.day,
            isSelected = isSelected,
            backgroundColor = dateColors.backgroundColor,
            textColor = dateColors.textColor,
            onClick = onClick
        )
    }
}

/**
 * 日付サークル（アニメーション付き）
 */
@Composable
private fun DateCircle(
    day: Int,
    isSelected: Boolean,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    val animatedBackgroundColor by animateColorAsState(backgroundColor)
    val animatedTextColor by animateColorAsState(textColor)
    val scale by animateFloatAsState(if (isSelected) 1f else 0.95f)

    Box(
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(animatedBackgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = animatedTextColor
        )
    }
}

/**
 * 日付アイテムの色セット
 */
private data class DateItemColors(
    val backgroundColor: Color,
    val textColor: Color,
    val dayOfWeekColor: Color,
)

/**
 * 日付アイテムの色を計算
 */
@Composable
private fun getDateItemColors(isSelected: Boolean, isToday: Boolean): DateItemColors {
    return DateItemColors(
        backgroundColor = when {
            isSelected && isToday -> MaterialTheme.colorScheme.primary
            isSelected -> MaterialTheme.colorScheme.surfaceVariant
            else -> Color.Transparent
        },
        textColor = when {
            isSelected && isToday -> MaterialTheme.colorScheme.onPrimary
            isSelected -> MaterialTheme.colorScheme.onSurface
            isToday -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        },
        dayOfWeekColor = if (isToday) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
}

/**
 * 曜日テキストを取得
 */
@Composable
private fun getDayOfWeekText(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.SUNDAY -> stringResource(Res.string.day_of_week_sunday)
        DayOfWeek.MONDAY -> stringResource(Res.string.day_of_week_monday)
        DayOfWeek.TUESDAY -> stringResource(Res.string.day_of_week_tuesday)
        DayOfWeek.WEDNESDAY -> stringResource(Res.string.day_of_week_wednesday)
        DayOfWeek.THURSDAY -> stringResource(Res.string.day_of_week_thursday)
        DayOfWeek.FRIDAY -> stringResource(Res.string.day_of_week_friday)
        DayOfWeek.SATURDAY -> stringResource(Res.string.day_of_week_saturday)
    }
}

/**
 * 選択日を含む週の日付リストを取得（日曜始まり）
 */
private fun getWeekDates(selectedDate: LocalDate): List<LocalDate> {
    val sunday = getWeekStartDate(selectedDate)
    return (0..6).map { sunday.plus(it, DateTimeUnit.DAY) }
}

/**
 * 指定日を含む週の開始日（日曜日）を取得
 */
private fun getWeekStartDate(date: LocalDate): LocalDate {
    val daysToSunday = when (date.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
    }
    return date.minus(daysToSunday, DateTimeUnit.DAY)
}

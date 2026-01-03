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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import kotlin.time.Clock

/**
 * 日付選択ヘッダー（週表示カレンダー形式）
 *
 * @param selectedDate 選択中の日付
 * @param onDateSelected 日付選択時のコールバック
 * @param modifier Modifier
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateSelectionHeader(
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

    // スワイプ時：ページが変更されたら、同じ曜日の日付を選択
    LaunchedEffect(pagerState.currentPage) {
        val weekOffset = pagerState.currentPage - initialPage
        val currentWeekDates = getWeekDates(baseDate.plus(weekOffset * 7, DateTimeUnit.DAY))
        currentWeekDates.find { it.dayOfWeek == selectedDate.dayOfWeek }?.let {
            onDateSelected(it)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val weekOffset = page - initialPage
        // 基準日からのオフセットで週の開始日を計算（selectedDateに依存しない）
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
 * 日付アイテム（曜日 + 日付）
 */
@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val dayOfWeekText = getDayOfWeekText(date.dayOfWeek) // TODO: ローカライズ

    // 日付部分の背景色
    val dateBackgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && isToday -> MaterialTheme.colorScheme.primary
            isSelected -> MaterialTheme.colorScheme.surfaceVariant
            else -> Color.Transparent
        }
    )

    // 日付部分の文字色
    val dateTextColor by animateColorAsState(
        targetValue = when {
            isSelected && isToday -> MaterialTheme.colorScheme.onPrimary
            isSelected -> MaterialTheme.colorScheme.onSurface
            isToday -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        }
    )

    // 曜日部分の文字色（背景色なし）
    val dayOfWeekColor by animateColorAsState(
        targetValue = when {
            isToday -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    )

    // 日付部分のスケールアニメーション
    val dateScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.95f
    )

    Column(
        modifier = Modifier
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 曜日（背景色なし）
        Text(
            text = dayOfWeekText,
            style = MaterialTheme.typography.bodySmall,
            color = dayOfWeekColor
        )

        // 日付（円形背景あり）
        Box(
            modifier = Modifier
                .size(36.dp)
                .scale(dateScale)
                .clip(CircleShape)
                .background(dateBackgroundColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.day.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = dateTextColor
            )
        }
    }
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

package com.example.timeinventory.feature.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * タイムブロック（LogEventまたはPlannedEventを表示）
 *
 * タイムライン上でイベントを視覚的に表示する共通コンポーネント。
 * 開始時刻に基づいて垂直位置を、期間に基づいて高さを計算する。
 *
 * @param startDateTime 開始日時
 * @param endDateTime 終了日時（nullの場合は現在時刻を使用）
 * @param label 表示ラベル
 * @param colorArgb カテゴリの色（ARGB形式）
 * @param hourHeight 1時間あたりの高さ（dp）
 * @param alpha 背景色の透明度（0.0-1.0）
 * @param onClick タップ時のコールバック
 * @param modifier Modifier
 */
@Composable
fun TimeBlock(
    startDateTime: Instant,
    endDateTime: Instant?,
    label: String,
    colorArgb: Int,
    hourHeight: Dp,
    alpha: Float = 1f,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // InstantをLocalDateTimeに変換（システムタイムゾーン）
    val timeZone = TimeZone.currentSystemDefault()
    val startTime = startDateTime.toLocalDateTime(timeZone)
    val endTime = endDateTime?.toLocalDateTime(timeZone)
        ?: Clock.System.now().toLocalDateTime(timeZone)

    // 位置と高さを計算
    val topOffset = calculateTimeOffset(
        hour = startTime.hour,
        minute = startTime.minute,
        hourHeight = hourHeight
    )
    val blockHeight = calculateDurationHeight(
        startHour = startTime.hour,
        startMinute = startTime.minute,
        endHour = endTime.hour,
        endMinute = endTime.minute,
        hourHeight = hourHeight
    )

    val backgroundColor = Color(colorArgb).copy(alpha = alpha)
    val categoryColor = Color(colorArgb)

    val durationMinutes =
        calculateDurationMinutes(startTime.hour, startTime.minute, endTime.hour, endTime.minute)

    val blockStyle = BlockTextStyle.fromDuration(durationMinutes, categoryColor)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = topOffset)
            .height(blockHeight)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        // ラベル（20分以上の場合のみ表示、左上配置）
        if (blockStyle.showLabel) {
            Text(
                text = label,
                style = blockStyle.textStyle,
                color = blockStyle.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = blockStyle.topPadding)
            )
        }

        // 右側の濃いカラーバー
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(4.dp)
                .background(categoryColor)
        )
    }
}

/**
 * TimeBlockのテキスト表示スタイル設定
 *
 * @property textStyle テキストスタイル
 * @property textColor テキストの色
 * @property topPadding テキストの上部padding
 * @property showLabel ラベルを表示するかどうか
 */
private data class BlockTextStyle(
    val textStyle: TextStyle,
    val textColor: Color,
    val topPadding: Dp,
    val showLabel: Boolean,
) {
    companion object {
        /**
         * 期間に応じた適切なテキスト表示スタイルを生成
         *
         * @param durationMinutes イベントの期間（分）
         * @param categoryColor カテゴリの色
         * @return BlockTextStyle テキスト表示設定
         */
        @Composable
        fun fromDuration(durationMinutes: Int, categoryColor: Color): BlockTextStyle {
            val textColor = categoryColor.darken()

            return when {
                // 20分未満: ラベル非表示
                durationMinutes < 20 -> BlockTextStyle(
                    textStyle = MaterialTheme.typography.bodySmall,
                    textColor = textColor,
                    topPadding = 0.dp,
                    showLabel = false
                )
                // 20-30分: 小フォント、padding無し
                durationMinutes < 30 -> BlockTextStyle(
                    textStyle = MaterialTheme.typography.labelSmall,
                    textColor = textColor,
                    topPadding = 0.dp,
                    showLabel = true
                )
                // 30分以上: 通常フォント、padding有り
                else -> BlockTextStyle(
                    textStyle = MaterialTheme.typography.bodySmall,
                    textColor = textColor,
                    topPadding = 2.dp,
                    showLabel = true
                )
            }
        }
    }
}

/**
 * 色を暗くする拡張関数
 *
 * @param factor 暗くする係数（0.0-1.0、小さいほど暗い）デフォルトは0.7
 * @return 暗くした色
 */
private fun Color.darken(factor: Float = 0.5f): Color {
    return copy(
        red = red * factor,
        green = green * factor,
        blue = blue * factor
    )
}

/**
 * 指定時刻の真夜中（0:00）からのオフセットを計算
 *
 * @param hour 時（0-23）
 * @param minute 分（0-59）
 * @param hourHeight 1時間あたりの高さ（dp）
 * @return 真夜中からの距離（dp）
 */
private fun calculateTimeOffset(
    hour: Int,
    minute: Int,
    hourHeight: Dp,
): Dp {
    val totalMinutes = hour * 60 + minute
    // 1時間 = hourHeight なので、1分 = hourHeight / 60
    return (totalMinutes * hourHeight.value / 60).dp
}

/**
 * 開始時刻から終了時刻までの期間を分単位で計算
 *
 * @param startHour 開始時（0-23）
 * @param startMinute 開始分（0-59）
 * @param endHour 終了時（0-23）
 * @param endMinute 終了分（0-59）
 * @return 期間（分）
 */
private fun calculateDurationMinutes(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
): Int {
    val startMinutes = startHour * 60 + startMinute
    val endMinutes = endHour * 60 + endMinute
    return endMinutes - startMinutes
}

/**
 * 開始時刻から終了時刻までの期間に応じたブロックの高さを計算
 *
 * @param startHour 開始時（0-23）
 * @param startMinute 開始分（0-59）
 * @param endHour 終了時（0-23）
 * @param endMinute 終了分（0-59）
 * @param hourHeight 1時間あたりの高さ（dp）
 * @return ブロックの高さ（dp）
 */
private fun calculateDurationHeight(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    hourHeight: Dp,
): Dp {
    val durationMinutes = calculateDurationMinutes(startHour, startMinute, endHour, endMinute)

    // 最小高さ8dp（最小1分でも視認可能にする）
    return maxOf(8.dp, (durationMinutes * hourHeight.value / 60).dp)
}

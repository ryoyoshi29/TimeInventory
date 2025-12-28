package com.example.timeinventory.core.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 計画されたスケジュールブロックを表すドメインモデル
 *
 * LogEventと比較することで予実管理を実現する。
 * 外部カレンダー（Apple Calendar, Google Calendar）からのイベント取得にも対応。
 *
 * @property id PlannedEventの一意識別子
 * @property activity 活動内容（何をするか）
 * @property category 分類カテゴリ
 * @property startDateTime 開始日時（絶対時刻）
 * @property endDateTime 終了日時（絶対時刻）
 * @property isAllDay 終日イベントかどうか
 * @property recurrenceRule 繰り返しルール（nullの場合は一回限り）
 * @property memo メモ（オプション）
 * @property externalCalendarId 外部カレンダーのイベントID（連携時に使用）
 * @property source データソース（手動作成 or 外部カレンダー連携）
 * @property isActive 有効/無効（アーカイブ管理用）
 */
@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
data class PlannedEvent(
    val id: Uuid = Uuid.random(),
    val activity: String,
    val category: Category,
    val startDateTime: Instant,
    val endDateTime: Instant,
    val isAllDay: Boolean = false,
    val recurrenceRule: RecurrenceRule? = null,
    val memo: String = "",
    val externalCalendarId: String? = null,
    val source: PlannedEventSource = PlannedEventSource.MANUAL,
    val isActive: Boolean = true,
) {
    init {
        require(activity.isNotBlank()) { "Activity must not be blank" }
        require(endDateTime > startDateTime) { "End time must be after start time" }
    }
}

/**
 * 繰り返しルール
 *
 * RFC 5545（iCalendar）のRRULE形式に対応できる設計。
 *
 * @property frequency 繰り返し頻度（毎日/毎週/毎月）
 * @property interval 間隔（例: 2週間ごと = interval: 2）
 * @property daysOfWeek 特定曜日の繰り返し（週次の場合に使用）
 * @property endDate 繰り返し終了日（nullの場合は無期限）
 * @property count 繰り返し回数（nullの場合は無制限）
 */
data class RecurrenceRule(
    val frequency: Frequency,
    val interval: Int = 1,
    val daysOfWeek: List<DayOfWeek>? = null,
    val endDate: LocalDate? = null,
    val count: Int? = null,
) {
    init {
        require(interval > 0) { "Interval must be positive" }
        count?.let { require(it > 0) { "Count must be positive" } }
    }
}

/**
 * 繰り返し頻度
 */
enum class Frequency {
    /** 毎日 */
    DAILY,

    /** 毎週 */
    WEEKLY,

    /** 毎月 */
    MONTHLY,
}

/**
 * PlannedEventのデータソース
 */
enum class PlannedEventSource {
    /** 手動作成 */
    MANUAL,

    /** Apple Calendar連携 */
    APPLE_CALENDAR,

    /** Google Calendar連携 */
    GOOGLE_CALENDAR,
}

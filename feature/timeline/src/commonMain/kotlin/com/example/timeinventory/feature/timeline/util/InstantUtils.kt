package com.example.timeinventory.feature.timeline.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * kotlin.time.Instant を kotlinx.datetime.LocalTime に変換
 *
 * @param timeZone タイムゾーン（デフォルト: システムのタイムゾーン）
 * @return LocalTime
 */
fun Instant.toLocalTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    return toLocalDateTime(timeZone).time
}

/**
 * LocalDate と LocalTime から kotlin.time.Instant を生成
 *
 * @param date 日付
 * @param time 時刻
 * @param timeZone タイムゾーン（デフォルト: システムのタイムゾーン）
 * @return kotlin.time.Instant
 */
fun createInstant(
    date: LocalDate,
    time: LocalTime,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Instant {
    val kotlinxInstant = LocalDateTime(date, time).toInstant(timeZone)
    return Instant.fromEpochMilliseconds(kotlinxInstant.toEpochMilliseconds())
}

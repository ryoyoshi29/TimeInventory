package com.example.timeinventory.core.model

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 実際に記録された時間ログを表すドメインモデル
 *
 * ユーザーの実際の行動記録（現実の1日）を表現する。
 * リアルタイムタイマーまたは手動入力で作成される。
 *
 * @property id LogEventの一意識別子
 * @property startDateTime 開始日時
 * @property endDateTime 終了日時 (タイマー実行中の場合はnull)
 * @property activity 活動内容（何をしたか）
 * @property category 分類カテゴリ
 * @property memo メモ (オプション)
 */
@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
data class LogEvent(
    val id: Uuid = Uuid.random(),
    val startDateTime: Instant,
    val endDateTime: Instant? = null,
    val activity: String = "",
    val category: Category,
    val memo: String = "",
) {
    init {
        // 終了時刻が設定されている場合、開始時刻より後である必要がある
        endDateTime?.let { end ->
            require(end > startDateTime) { "End time must be after start time" }
        }
    }

    /**
     * このLogEventがアクティブ（タイマー実行中）かどうか
     */
    val isActive: Boolean
        get() = endDateTime == null

    /**
     * 経過時間
     *
     * 終了時刻が設定されている場合は確定した経過時間、
     * タイマー実行中の場合は現在時刻までの経過時間を返す。
     */
    val duration: Duration
        get() =
            if (endDateTime != null) {
                endDateTime - startDateTime
            } else {
                Clock.System.now() - startDateTime
            }

    /**
     * LogEventを完了させる
     *
     * @param endDateTime 終了時刻 (デフォルトは現在時刻)
     * @return 終了時刻が設定された新しいLogEventインスタンス
     * @throws IllegalArgumentException 終了時刻が開始時刻より前の場合
     */
    fun complete(endDateTime: Instant = Clock.System.now()): LogEvent {
        require(endDateTime > startDateTime) { "End time must be after start time" }
        return copy(endDateTime = endDateTime)
    }

    /**
     * 有効な期間かどうかを検証する
     *
     * 最小期間（1分）以上であることを確認する。
     *
     * @return 1分以上の場合true
     */
    fun isValidDuration(): Boolean {
        return duration >= 1.minutes
    }
}

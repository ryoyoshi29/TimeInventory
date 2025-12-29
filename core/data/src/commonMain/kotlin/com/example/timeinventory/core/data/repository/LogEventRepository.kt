package com.example.timeinventory.core.data.repository

import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.LogEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * LogEventのデータアクセスを提供するRepository
 *
 * Single Source of Truth (SSOT) として機能し、
 * ローカルデータベースからLogEventを取得・保存する。
 */
@OptIn(ExperimentalUuidApi::class)
interface LogEventRepository {

    /**
     * 特定の日付のLogEventをFlowとして取得
     *
     * データベースの変更を監視し、変更があれば自動的に新しいリストを発行する。
     *
     * @param date 対象日
     * @return 指定日のLogEventリストを発行するFlow
     */
    fun getLogEventsByDateStream(date: LocalDate): Flow<List<LogEvent>>

    /**
     * 特定の期間のLogEventをFlowとして取得
     *
     * データベースの変更を監視し、変更があれば自動的に新しいリストを発行する。
     *
     * @param startDate 開始日（この日を含む）
     * @param endDate 終了日（この日を含む）
     * @return 指定期間のLogEventリストを発行するFlow
     */
    fun getLogEventsByPeriodStream(startDate: LocalDate, endDate: LocalDate): Flow<List<LogEvent>>

    /**
     * IDで特定のLogEventを取得
     *
     * @param id LogEventのID
     * @return LogEvent、存在しない場合はnull
     */
    suspend fun getLogEventById(id: Uuid): LogEvent?

    /**
     * 新しいタイマーを開始し、LogEventを作成
     *
     * endDateTimeがnullのLogEventを作成してデータベースに保存する。
     *
     * @param category カテゴリ
     * @return 作成されたLogEvent
     */
    suspend fun startTimer(category: Category): LogEvent

    /**
     * 実行中のタイマーを停止
     *
     * 指定されたLogEventのendDateTimeを設定してデータベースを更新する。
     *
     * @param logEventId 停止するLogEventのID
     * @return 成功時はSuccess、失敗時はFailureを返す
     *         - LogEventNotFoundException: LogEventが見つからない場合
     */
    suspend fun stopTimer(logEventId: Uuid): Result<Unit>

    /**
     * LogEventを保存または更新
     *
     * IDが既存のものと一致する場合は更新、そうでない場合は新規作成。
     *
     * @param logEvent 保存するLogEvent
     */
    suspend fun upsertLogEvent(logEvent: LogEvent)

    /**
     * LogEventを削除
     *
     * @param id 削除するLogEventのID
     */
    suspend fun deleteLogEvent(id: Uuid)

    /**
     * アクティブ（実行中）なLogEventを取得
     *
     * endDateTimeがnullのLogEventを返す。
     *
     * @return アクティブなLogEvent、存在しない場合はnull
     */
    suspend fun getActiveLogEvent(): LogEvent?
}

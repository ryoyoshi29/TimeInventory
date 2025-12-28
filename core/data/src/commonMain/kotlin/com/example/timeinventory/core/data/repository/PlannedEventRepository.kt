package com.example.timeinventory.core.data.repository

import com.example.timeinventory.core.model.PlannedEvent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * PlannedEventのデータアクセスを提供するRepository
 *
 * Single Source of Truth (SSOT) として機能し、
 * ローカルデータベースからPlannedEventを取得・保存する。
 */
@OptIn(ExperimentalUuidApi::class)
interface PlannedEventRepository {

    /**
     * 特定の日付のPlannedEventをFlowとして取得
     *
     * データベースの変更を監視し、変更があれば自動的に新しいリストを発行する。
     * 繰り返しルールが設定されている場合は、指定日に該当するイベントのみを返す。
     *
     * @param date 対象日
     * @return 指定日のPlannedEventリストを発行するFlow
     */
    fun getPlannedEventsByDateStream(date: LocalDate): Flow<List<PlannedEvent>>

    /**
     * 特定の期間のPlannedEventをFlowとして取得
     *
     * データベースの変更を監視し、変更があれば自動的に新しいリストを発行する。
     * 繰り返しルールが設定されている場合は、期間内に該当するイベントを展開して返す。
     *
     * @param startDate 開始日（この日を含む）
     * @param endDate 終了日（この日を含む）
     * @return 指定期間のPlannedEventリストを発行するFlow
     */
    fun getPlannedEventsByPeriodStream(startDate: LocalDate, endDate: LocalDate): Flow<List<PlannedEvent>>

    /**
     * IDで特定のPlannedEventを取得
     *
     * @param id PlannedEventのID
     * @return PlannedEvent、存在しない場合はnull
     */
    suspend fun getPlannedEventById(id: Uuid): PlannedEvent?

    /**
     * PlannedEventを保存または更新
     *
     * IDが既存のものと一致する場合は更新、そうでない場合は新規作成。
     *
     * @param plannedEvent 保存するPlannedEvent
     */
    suspend fun upsertPlannedEvent(plannedEvent: PlannedEvent)

    /**
     * PlannedEventを削除
     *
     * @param id 削除するPlannedEventのID
     */
    suspend fun deletePlannedEvent(id: Uuid)
}

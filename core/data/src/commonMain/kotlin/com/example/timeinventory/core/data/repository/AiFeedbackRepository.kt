package com.example.timeinventory.core.data.repository

import com.example.timeinventory.core.model.AiFeedback
import kotlinx.datetime.LocalDate

/**
 * AiFeedbackのデータアクセスを提供するRepository
 *
 * Single Source of Truth (SSOT) として機能し、
 * ローカルデータベースとGemini APIからAiFeedbackを取得・保存する。
 */
interface AiFeedbackRepository {

    /**
     * 特定の日のAiFeedbackを取得
     *
     * ローカルデータベースから取得する。
     * 1日に1つのフィードバックのみ保存される（targetDateがユニーク制約）。
     *
     * @param targetDate 対象日
     * @return AiFeedback、存在しない場合はnull
     */
    suspend fun getFeedbackByDate(targetDate: LocalDate): AiFeedback?

    /**
     * フィードバックを生成または取得
     *
     * ローカルデータベースにキャッシュがあればそれを返す。
     * なければGemini APIを呼び出してフィードバックを生成し、データベースに保存してから返す。
     * 既に同じ日のフィードバックが存在する場合は上書きする。
     *
     * @param targetDate 対象日
     * @return 生成または取得されたAiFeedback
     */
    suspend fun getOrGenerateFeedback(targetDate: LocalDate): AiFeedback
}

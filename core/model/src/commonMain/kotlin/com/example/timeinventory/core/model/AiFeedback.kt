package com.example.timeinventory.core.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalDate

/**
 * AIフィードバックを表すドメインモデル
 *
 * Gemini APIから生成されたKPTフレームワークによる振り返り結果を保持する。
 * レポート画面で表示され、ユーザーの行動改善をサポートする。
 *
 * @property id AiFeedbackの一意識別子
 * @property targetDate 対象日（どの日のLogEventを分析したか）
 * @property summary フィードバック全体の要約
 * @property keep 良かったこと・続けること
 * @property problem 問題点・改善点
 * @property tryAction 次に試すこと
 */
@OptIn(ExperimentalUuidApi::class)
data class AiFeedback(
    val id: Uuid = Uuid.random(),
    val targetDate: LocalDate,
    val summary: String,
    val keep: KptElement,
    val problem: KptElement,
    val tryAction: KptElement,
) {
    init {
        require(summary.isNotBlank()) { "Summary must not be blank" }
    }
}

/**
 * KPTフレームワークの各要素
 *
 * Keep/Problem/Tryそれぞれの見出しと詳細説明を保持する。
 *
 * @property title AIが生成するキャッチーな見出し
 * @property description 具体的な事実や分析、提案の内容
 */
data class KptElement(
    val title: String,
    val description: String,
) {
    init {
        require(title.isNotBlank()) { "Title must not be blank" }
        require(description.isNotBlank()) { "Description must not be blank" }
    }
}

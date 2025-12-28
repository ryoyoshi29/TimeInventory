package com.example.timeinventory.core.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * カテゴリを表すドメインモデル
 *
 * LogEventやPlannedEventの分類に使用される。
 *
 * @property id カテゴリの一意識別子
 * @property name カテゴリ名
 * @property colorArgb カテゴリの色 (ARGB形式: 0xFFRRGGBB)
 * @property sortOrder 表示順序 (小さいほど優先)
 */
@OptIn(ExperimentalUuidApi::class)
data class Category(
    val id: Uuid = Uuid.random(),
    val name: String,
    val colorArgb: Long,
    val sortOrder: Int = 0,
) {
    init {
        require(name.isNotBlank()) { "Category name must not be blank" }
    }
}

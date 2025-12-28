package com.example.timeinventory.core.data.repository

import com.example.timeinventory.core.model.Category
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

/**
 * Categoryのデータアクセスを提供するRepository
 *
 * Single Source of Truth (SSOT) として機能し、
 * ローカルデータベースからCategoryを取得・保存する。
 */
@OptIn(ExperimentalUuidApi::class)
interface CategoryRepository {

    /**
     * すべてのCategoryをFlowとして取得
     *
     * データベースの変更を監視し、変更があれば自動的に新しいリストを発行する。
     * sortOrder順にソートされて返される。
     *
     * @return Categoryのリストを発行するFlow
     */
    fun getCategoriesStream(): Flow<List<Category>>

    /**
     * IDで特定のCategoryを取得
     *
     * @param id CategoryのID
     * @return Category、存在しない場合はnull
     */
    suspend fun getCategoryById(id: Uuid): Category?

    /**
     * Categoryを保存または更新
     *
     * IDが既存のものと一致する場合は更新、そうでない場合は新規作成。
     *
     * @param category 保存するCategory
     */
    suspend fun upsertCategory(category: Category)

    /**
     * Categoryを削除
     *
     * @param id 削除するCategoryのID
     */
    suspend fun deleteCategory(id: Uuid)

    /**
     * デフォルトカテゴリを初期化
     *
     * 初回起動時にデフォルトのカテゴリ（仕事、休憩、睡眠など）を作成する。
     * 既にカテゴリが存在する場合は何もしない。
     */
    suspend fun initializeDefaultCategories()
}

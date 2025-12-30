package com.example.timeinventory.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.timeinventory.core.database.TimeInventoryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Platform-specific Database builder
 *
 * expect/actual パターンで各プラットフォームのBuilder取得を実装する。
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<TimeInventoryDatabase>

/**
 * 共通のDatabase構築関数
 *
 * プラットフォーム固有のBuilderを受け取り、共通設定を適用してDatabaseインスタンスを生成する。
 */
fun getRoomDatabase(
    builder: RoomDatabase.Builder<TimeInventoryDatabase>
): TimeInventoryDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

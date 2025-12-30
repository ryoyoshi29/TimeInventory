package com.example.timeinventory.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timeinventory.core.database.TimeInventoryDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS用のDatabase builder実装
 *
 * @return RoomDatabase.Builder（ビルド設定は getRoomDatabase() で適用）
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<TimeInventoryDatabase> {
    val dbFilePath = documentDirectory() + "/time_inventory.db"
    return Room.databaseBuilder<TimeInventoryDatabase>(
        name = dbFilePath
    )
}

/**
 * iOSのドキュメントディレクトリパスを取得
 *
 * @return ドキュメントディレクトリの絶対パス
 */
@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
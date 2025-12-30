package com.example.timeinventory.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.timeinventory.core.database.TimeInventoryDatabase

/**
 * Android用のDatabase builder実装
 *
 * @param context Android Context（データベースファイルパス取得に必要）
 * @return RoomDatabase.Builder（ビルド設定は getRoomDatabase() で適用）
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TimeInventoryDatabase> {
    val dbFile = context.getDatabasePath("time_inventory.db")
    return Room.databaseBuilder<TimeInventoryDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}

/**
 * expect宣言の実装（Androidでは使用不可）
 *
 * AndroidではContextが必須のため、この関数は呼び出せません。
 * 代わりに getDatabaseBuilder(context: Context) を使用してください。
 *
 * @throws IllegalStateException 常に例外をスロー
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<TimeInventoryDatabase> {
    throw IllegalStateException("Context required for Android. Use getDatabaseBuilder(context) instead.")
}

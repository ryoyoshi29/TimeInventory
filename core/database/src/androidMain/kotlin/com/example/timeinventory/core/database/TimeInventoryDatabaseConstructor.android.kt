package com.example.timeinventory.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TimeInventoryDatabase> {
    val dbFile = context.getDatabasePath("time_inventory.db")
    return Room.databaseBuilder<TimeInventoryDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}

package com.example.timeinventory.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

fun getDatabaseBuilder(): RoomDatabase.Builder<TimeInventoryDatabase> {
    val dbFilePath = NSHomeDirectory() + "/time_inventory.db"
    return Room.databaseBuilder<TimeInventoryDatabase>(
        name = dbFilePath,
    )
}

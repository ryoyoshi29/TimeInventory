package com.example.timeinventory.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

/**
 * Android用のSettings builder実装
 */
fun getSettings(context: Context): Settings {
    val sharedPreferences = context.getSharedPreferences("time_inventory_prefs", Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sharedPreferences)
}

actual fun getSettings(): Settings {
    throw IllegalStateException("Context required for Android. Use getSettings(context) instead.")
}

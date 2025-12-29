package com.example.timeinventory.core.data.repository.impl

import com.example.timeinventory.core.data.repository.PreferencesRepository
import com.russhwolf.settings.Settings

/**
 * PreferencesRepositoryの実装
 *
 * multiplatform-settings を使用してプラットフォーム固有のストレージに保存する。
 * - Android: SharedPreferences
 * - iOS: NSUserDefaults
 */
class PreferencesRepositoryImpl(
    private val settings: Settings,
) : PreferencesRepository {

    private companion object {
        const val KEY_FIRST_LAUNCH = "is_first_launch"
    }

    override suspend fun isFirstLaunch(): Boolean {
        return settings.getBoolean(KEY_FIRST_LAUNCH, defaultValue = true)
    }

    override suspend fun markInitialized() {
        settings.putBoolean(KEY_FIRST_LAUNCH, false)
    }
}

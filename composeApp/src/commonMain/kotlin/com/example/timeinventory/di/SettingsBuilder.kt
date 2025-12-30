package com.example.timeinventory.di

import com.russhwolf.settings.Settings

/**
 * Platform-specific Settings builder
 *
 * expect/actual パターンで各プラットフォームのSettings構築を実装する。
 */
expect fun getSettings(): Settings

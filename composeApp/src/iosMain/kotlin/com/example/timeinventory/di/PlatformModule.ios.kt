package com.example.timeinventory.di

import org.koin.dsl.module

/**
 * iOS用のPlatform Module実装
 */
actual fun platformModule() = module {
    single { getRoomDatabase(getDatabaseBuilder()) }
    single { getSettings() }
}

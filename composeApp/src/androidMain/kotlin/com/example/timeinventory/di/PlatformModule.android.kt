package com.example.timeinventory.di

import org.koin.dsl.module

/**
 * Android用のPlatform Module実装
 */
actual fun platformModule() = module {
    single { getRoomDatabase(getDatabaseBuilder(context = get())) }
    single { getSettings(context = get()) }
}

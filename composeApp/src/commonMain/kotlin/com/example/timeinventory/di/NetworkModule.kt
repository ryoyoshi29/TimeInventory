package com.example.timeinventory.di

import com.example.timeinventory.core.network.GeminiApiClient
import org.koin.dsl.module

val networkModule = module {
    // API Key は google-services.json / GoogleService-Info.plist から自動的に読み込まれる
    single<GeminiApiClient> { GeminiApiClient() }
}

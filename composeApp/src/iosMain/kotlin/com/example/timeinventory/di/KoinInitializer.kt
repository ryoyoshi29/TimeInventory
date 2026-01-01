package com.example.timeinventory.di

import org.koin.core.context.startKoin

/**
 * iOS用のKoin初期化関数
 *
 * SwiftのApp構造体から呼び出される
 */
fun initKoin() {
    startKoin {
        modules(
            platformModule(),
            dataModule,
            viewModelModule,
        )
    }
}

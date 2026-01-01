package com.example.timeinventory

import android.app.Application
import com.example.timeinventory.di.dataModule
import com.example.timeinventory.di.platformModule
import com.example.timeinventory.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Android Application class
 *
 * Koinの初期化を行う
 */
class TimeInventoryApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@TimeInventoryApplication)
            modules(
                platformModule(),
                dataModule,
                viewModelModule,
            )
        }
    }
}

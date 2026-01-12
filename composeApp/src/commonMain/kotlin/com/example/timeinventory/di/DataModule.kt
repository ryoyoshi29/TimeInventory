package com.example.timeinventory.di

import com.example.timeinventory.core.data.repository.AiFeedbackRepository
import com.example.timeinventory.core.data.repository.CategoryRepository
import com.example.timeinventory.core.data.repository.LogEventRepository
import com.example.timeinventory.core.data.repository.PlannedEventRepository
import com.example.timeinventory.core.data.repository.PreferencesRepository
import com.example.timeinventory.core.data.repository.impl.AiFeedbackRepositoryImpl
import com.example.timeinventory.core.data.repository.impl.CategoryRepositoryImpl
import com.example.timeinventory.core.data.repository.impl.LogEventRepositoryImpl
import com.example.timeinventory.core.data.repository.impl.PlannedEventRepositoryImpl
import com.example.timeinventory.core.data.repository.impl.PreferencesRepositoryImpl
import com.example.timeinventory.core.database.TimeInventoryDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { get<TimeInventoryDatabase>().categoryDao() }
    single { get<TimeInventoryDatabase>().logEventDao() }
    single { get<TimeInventoryDatabase>().plannedEventDao() }
    single { get<TimeInventoryDatabase>().aiFeedbackDao() }

    singleOf(::CategoryRepositoryImpl) bind CategoryRepository::class
    singleOf(::LogEventRepositoryImpl) bind LogEventRepository::class
    singleOf(::PlannedEventRepositoryImpl) bind PlannedEventRepository::class
    singleOf(::PreferencesRepositoryImpl) bind PreferencesRepository::class
    singleOf(::AiFeedbackRepositoryImpl) bind AiFeedbackRepository::class
}

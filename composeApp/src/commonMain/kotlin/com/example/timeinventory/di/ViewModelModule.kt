package com.example.timeinventory.di

import com.example.timeinventory.feature.report.ReportViewModel
import com.example.timeinventory.feature.timeline.TimelineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * ViewModel layer dependencies
 */
val viewModelModule = module {
    viewModel { TimelineViewModel(get(), get(), get(), get()) }
    viewModel { ReportViewModel(get(), get(), get()) }
}

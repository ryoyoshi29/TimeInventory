package com.example.timeinventory.di

import org.koin.core.module.Module

/**
 * Platform-specific dependencies (Database, Settings)
 */
expect fun platformModule(): Module

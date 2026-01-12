package com.example.timeinventory.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

/**
 * iOS実装: Darwinエンジンを提供
 */
actual fun platformEngine(): HttpClientEngineFactory<*> = Darwin

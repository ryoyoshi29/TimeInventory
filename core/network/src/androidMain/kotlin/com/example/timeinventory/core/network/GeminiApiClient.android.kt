package com.example.timeinventory.core.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android実装: OkHttpエンジンを提供
 */
actual fun platformEngine(): HttpClientEngineFactory<*> = OkHttp

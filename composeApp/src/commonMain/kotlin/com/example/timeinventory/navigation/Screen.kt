package com.example.timeinventory.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation destinations (type-safe routes)
 *
 * Navigation Compose 2.8+のType-safe navigation用の画面定義
 */
sealed interface Screen {
    /**
     * タイムライン画面（メイン画面）
     */
    @Serializable
    data object Timeline : Screen

    /**
     * レポート画面（グラフ・統計）
     */
    @Serializable
    data object Report : Screen
}

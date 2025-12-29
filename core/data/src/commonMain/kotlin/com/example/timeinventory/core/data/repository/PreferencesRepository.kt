package com.example.timeinventory.core.data.repository

/**
 * アプリケーション設定を管理するRepository
 *
 * ユーザー設定や初回起動フラグなどの永続化を担当する。
 */
interface PreferencesRepository {

    /**
     * 初回起動かどうかを取得
     *
     * デフォルトカテゴリの初期化判定などに使用される。
     *
     * @return 初回起動の場合true、それ以外false
     */
    suspend fun isFirstLaunch(): Boolean

    /**
     * 初期化完了をマーク
     *
     * デフォルトカテゴリ初期化後に呼び出され、以降 isFirstLaunch() は false を返す。
     */
    suspend fun markInitialized()
}

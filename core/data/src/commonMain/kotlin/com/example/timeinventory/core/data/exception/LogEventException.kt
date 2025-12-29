package com.example.timeinventory.core.data.exception

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * LogEvent関連の例外基底クラス
 *
 * UI層でダイアログ表示に使用するための情報を持つ。
 *
 * @property title ダイアログタイトル
 * @property description ダイアログ説明文
 */
@OptIn(ExperimentalUuidApi::class)
sealed class LogEventException(
    val title: String,
    val description: String,
) : Exception(description)

/**
 * LogEventが見つからない場合の例外
 *
 * @property id 見つからなかったLogEventのID
 */
@OptIn(ExperimentalUuidApi::class)
class LogEventNotFoundException(val id: Uuid) : LogEventException(
    title = "タイマーが見つかりません",
    description = "このタイマーは既に削除されているか、存在しません。",
)

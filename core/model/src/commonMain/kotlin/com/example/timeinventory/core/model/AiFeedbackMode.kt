package com.example.timeinventory.core.model

/**
 * AIフィードバックの厳しさモード
 *
 * ユーザーがUI層で選択し、Gemini APIへのプロンプト構築時に使用される。
 * フィードバックのトーンや内容の厳しさを調整する。
 */
enum class AiFeedbackMode {
    /** やさしいフィードバック（励まし重視） */
    GENTLE,

    /** ノーマルなフィードバック（バランス型） */
    NORMAL,

    /** 厳しいフィードバック（改善点重視） */
    STRICT,
}

# TimeInventory

**AIのインサイトを活用して、「理想の1日」と「現実の1日」のギャップを埋める**

---

## プロジェクトについて

### 主な機能

**1. タイムラインの可視化とリアルタイム計測**

* **比較表示**: 予定イベント（理想）と実績ログ（現実）を左右に並べて比較。
* **タイマー計測**: FAB（フローティングアクションボタン）から即座に実績記録を開始。スナックバーでバックグラウンド計測中も状態を確認可能。
* **カテゴリ管理**: 色分けされたカテゴリにより、何に時間を使っているか一目で把握。

**2. 外部カレンダー連携**

* **双方向同期**: GoogleカレンダーやAppleカレンダーから予定（PlannedEvent）を自動インポート。

**3. 統計レポートと分析**

* **カテゴリ別時間割合**: 円グラフで1日の時間配分を可視化。
* **カテゴリ別達成率**: 予定に対してどれだけ実績が伴ったかを棒グラフで表示。

**4. AIパワーによるフィードバック**

* **KPT分析**: Vertex AIがその日のパフォーマンスを分析し、構造化されたフィードバックを生成。
* **Keep**: 継続すべき良い習慣
* **Problem**: 改善が必要な課題
* **Try**: 明日に向けた具体的なアクション

**5. ユーザー基盤とサービス提供**

* **オンボーディング**: アプリの価値を伝える導入フロー。
* **ログイン機能**: 複数デバイス間でのデータ同期。
* **サブスクリプション**: 高度な分析機能やプレミアム機能の提供。

---

## 技術スタック

### コアテクノロジー

* **Kotlin Multiplatform (KMP)**
* **Compose Multiplatform**
* **Material 3 デザインシステム**

### ライブラリ & フレームワーク

| カテゴリ              | テクノロジー                                                               | 目的                            |
|-------------------|----------------------------------------------------------------------|-------------------------------|
| **AI / Backend**  | **Vertex AI for Firebase**                                           | 生成AI（Gemini）の安全な呼び出しとクライアント統合 |
| **Auth / Sync**   | **Firebase Authentication**                                          | ログイン機能とユーザー管理                 |
| **Network**       | [Ktor Client](https://ktor.io/)                                      | API通信（カレンダー連携、その他外部サービス）      |
| **Database**      | [Room](https://developer.android.com/jetpack/androidx/releases/room) | ローカル永続化（SSOT）                 |
| **Visualization** | Compose Charts (or Custom Canvas)                                    | 円グラフ・棒グラフの描画                  |
| **Async**         | Kotlin Coroutines & Flow                                             | UDF（単方向データフロー）に基づくリアクティブ制御    |
| **DI**            | [Koin](https://insert-koin.io/)                                      | マルチプラットフォームでの依存性注入            |
| **Date/Time**     | [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime)       | タイムゾーンを考慮したマルチプラットフォーム日時操作    |

---

## AI統合

**Gemini 2.5 Flash Lite (via Vertex AI for Firebase SDK)**

これまでのREST API経由の直接呼び出しから、Firebaseを通じたセキュアな実装へ移行しました。

* **Vertex AI for Firebase SDK**:
* クライアントサイドから直接Geminiモデルを呼び出し。
* APIキーをアプリに埋め込まず、Firebase App Checkによる安全な保護を推奨。


* **実装方式**:
* `commonMain` でSDKを抽象化。
* Geminiの「Response Schema」機能を利用した構造化JSON出力。
* Sealedクラスを用いたエラーハンドリングにより、ネットワークエラーやクォータ制限をUIで適切に処理。

---

## アーキテクチャ

TimeInventoryは、[Android公式アーキテクチャガイド](https://developer.android.com/topic/architecture)
に基づいた、**クリーンアーキテクチャ**原則と**リアクティブな単方向データフロー（UDF）**モデルを採用しています。

**詳細ドキュメント:** [Architecture.md](https://www.google.com/search?q=docs/Architecture.md) (英語)

---

## モジュール化

ビルド時間の短縮、関心の分離、および将来的な機能追加（サブスクリプション、統計など）への柔軟な対応のため、
**マルチモジュールアーキテクチャ**を維持しています。

**詳細ドキュメント:** [Modularization.md](https://www.google.com/search?q=docs/Modularization.md) (
英語)

---

### 今回のアップデートによる追加工数見積もり (参考)

* **計測・タイマー関連**: 18h
* **外部連携・分析表示**: 12h
* **AI基盤移行 (Vertex AI)**: 4h
* **ユーザー基盤 (Auth/Onboarding/Billing)**: 15h

---
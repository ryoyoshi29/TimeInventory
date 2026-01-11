package com.example.timeinventory.core.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * アウトラインテキストフィールド
 *
 * アプリ内で統一されたスタイルのOutlinedTextField
 *
 * @param value 入力値
 * @param onValueChange 値変更時のコールバック
 * @param modifier Modifier
 * @param placeholder プレースホルダーテキスト
 * @param leadingIcon 先頭アイコン
 * @param singleLine 単一行モードかどうか
 * @param enabled 有効/無効状態
 */
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(placeholder, color = Color.Gray)
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.LightGray
        ),
        singleLine = singleLine,
        enabled = enabled
    )
}

package com.example.timeinventory.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import timeinventory.core.designsystem.generated.resources.Res
import timeinventory.core.designsystem.generated.resources.action_cancel
import timeinventory.core.designsystem.generated.resources.action_confirm

/**
 * 時刻選択ダイアログ
 *
 * Material3のTimePickerを使用した時刻選択ダイアログ
 *
 * @param initialTime 初期時刻
 * @param onDismiss ダイアログを閉じる時のコールバック
 * @param onConfirm 時刻確定時のコールバック（選択された時刻を渡す）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime(
                        hour = timePickerState.hour,
                        minute = timePickerState.minute
                    )
                    onConfirm(selectedTime)
                }
            ) {
                Text(stringResource(Res.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

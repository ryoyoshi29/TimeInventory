package com.example.timeinventory.feature.timeline.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timeinventory.core.designsystem.component.DestructiveButton
import com.example.timeinventory.core.designsystem.component.DropdownMenu
import com.example.timeinventory.core.designsystem.component.OutlinedTextField
import com.example.timeinventory.core.designsystem.component.PrimaryButton
import com.example.timeinventory.core.designsystem.component.TimePickerDialog
import com.example.timeinventory.core.model.Category
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import timeinventory.feature.timeline.generated.resources.Res
import timeinventory.feature.timeline.generated.resources.cd_time_select
import timeinventory.feature.timeline.generated.resources.event_sheet_action_create
import timeinventory.feature.timeline.generated.resources.event_sheet_action_delete
import timeinventory.feature.timeline.generated.resources.event_sheet_action_save
import timeinventory.feature.timeline.generated.resources.event_sheet_label_activity
import timeinventory.feature.timeline.generated.resources.event_sheet_label_category
import timeinventory.feature.timeline.generated.resources.event_sheet_label_end_time
import timeinventory.feature.timeline.generated.resources.event_sheet_label_memo
import timeinventory.feature.timeline.generated.resources.event_sheet_label_start_time
import timeinventory.feature.timeline.generated.resources.event_sheet_placeholder_activity
import timeinventory.feature.timeline.generated.resources.event_sheet_placeholder_memo
import timeinventory.feature.timeline.generated.resources.event_sheet_title_create
import timeinventory.feature.timeline.generated.resources.event_sheet_title_edit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun EventBottomSheetContent(
    initialTitle: String = "",
    initialCategory: Category? = null,
    initialStartTime: LocalTime,
    initialEndTime: LocalTime,
    initialMemo: String = "",
    categories: List<Category>,
    editEventId: Uuid? = null,
    onSave: (title: String, startTime: LocalTime, endTime: LocalTime, category: Category, memo: String) -> Unit,
    onDismiss: () -> Unit,
    onDelete: ((Uuid) -> Unit)? = null,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var startTime by remember { mutableStateOf(initialStartTime) }
    var endTime by remember { mutableStateOf(initialEndTime) }
    var memo by remember { mutableStateOf(initialMemo) }
    var selectedCategory by remember { mutableStateOf(initialCategory ?: categories.firstOrNull()) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val isSaveEnabled = title.isNotBlank() && selectedCategory != null
    val isEditMode = editEventId != null
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Text(
            text = stringResource(
                if (isEditMode) Res.string.event_sheet_title_edit
                else Res.string.event_sheet_title_create
            ),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        SectionTitle(stringResource(Res.string.event_sheet_label_activity))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.event_sheet_placeholder_activity),
            leadingIcon = Icons.Default.Create,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(stringResource(Res.string.event_sheet_label_category))
        DropdownMenu(
            items = categories,
            selectedItem = selectedCategory,
            onItemSelected = { selectedCategory = it },
            itemLabel = { it.name },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Default.Folder
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(stringResource(Res.string.event_sheet_label_start_time))
                TimeCard(time = startTime, onClick = { showStartTimePicker = true })
            }
            Column(modifier = Modifier.weight(1f)) {
                SectionTitle(stringResource(Res.string.event_sheet_label_end_time))
                TimeCard(time = endTime, onClick = { showEndTimePicker = true })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle(stringResource(Res.string.event_sheet_label_memo))
        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = stringResource(Res.string.event_sheet_placeholder_memo),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditMode) {
                DestructiveButton(
                    text = stringResource(Res.string.event_sheet_action_delete),
                    onClick = {
                        editEventId?.let { onDelete?.invoke(it) }
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            PrimaryButton(
                text = stringResource(
                    if (isEditMode) Res.string.event_sheet_action_save
                    else Res.string.event_sheet_action_create
                ),
                onClick = {
                    selectedCategory?.let { category ->
                        onSave(title, startTime, endTime, category, memo)
                    }
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                enabled = isSaveEnabled
            )
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            initialTime = startTime,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { newTime ->
                startTime = newTime
                if (startTime > endTime) {
                    endTime = startTime
                }
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            initialTime = endTime,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { newTime ->
                if (newTime >= startTime) {
                    endTime = newTime
                }
                showEndTimePicker = false
            }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun TimeCard(time: LocalTime, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${time.hour.toString().padStart(2, '0')}:${
                time.minute.toString().padStart(2, '0')
            }",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = stringResource(Res.string.cd_time_select),
            tint = Color.Gray
        )
    }
}

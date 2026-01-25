package com.example.timeinventory.feature.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.timeinventory.feature.report.component.AiFeedbackContent
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import timeinventory.feature.report.generated.resources.Res
import timeinventory.feature.report.generated.resources.ai_analyze_description
import timeinventory.feature.report.generated.resources.ai_analyze_title
import timeinventory.feature.report.generated.resources.ai_feedback_prompt
import timeinventory.feature.report.generated.resources.error_occurred
import timeinventory.feature.report.generated.resources.generate_feedback_button
import timeinventory.feature.report.generated.resources.generating_feedback_loading
import timeinventory.feature.report.generated.resources.report_title
import timeinventory.feature.report.generated.resources.retry_button
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val targetDate = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.report_title)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (val feedbackState = uiState.aiFeedbackState) {
                is AiFeedbackState.Initial -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(Res.string.ai_analyze_title),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.ai_analyze_description),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    val formattedPlannedEvents =
                                        viewModel.getFormattedPlannedEvents(targetDate)
                                    val formattedLogEvents =
                                        viewModel.getFormattedLogEvents(targetDate)
                                    val prompt = getString(
                                        Res.string.ai_feedback_prompt,
                                        targetDate,
                                        formattedPlannedEvents,
                                        formattedLogEvents
                                    )
                                    viewModel.generateFeedback(targetDate, prompt)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.generate_feedback_button))
                        }
                    }
                }

                is AiFeedbackState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(Res.string.generating_feedback_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is AiFeedbackState.Success -> {
                    AiFeedbackContent(
                        feedback = feedbackState.aiFeedback,
                        targetDate = targetDate,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is AiFeedbackState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.error_occurred),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = feedbackState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val formattedPlannedEvents =
                                        viewModel.getFormattedPlannedEvents(targetDate)
                                    val formattedLogEvents =
                                        viewModel.getFormattedLogEvents(targetDate)
                                    val prompt = getString(
                                        Res.string.ai_feedback_prompt,
                                        formattedPlannedEvents,
                                        formattedLogEvents
                                    )
                                    viewModel.generateFeedback(targetDate, prompt)
                                }
                            }
                        ) {
                            Text(stringResource(Res.string.retry_button))
                        }
                    }
                }
            }
        }
    }
}
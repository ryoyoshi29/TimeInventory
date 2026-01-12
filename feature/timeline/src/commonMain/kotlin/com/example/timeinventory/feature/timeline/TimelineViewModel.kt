package com.example.timeinventory.feature.timeline

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeinventory.core.data.repository.CategoryRepository
import com.example.timeinventory.core.data.repository.LogEventRepository
import com.example.timeinventory.core.data.repository.PlannedEventRepository
import com.example.timeinventory.core.data.repository.PreferencesRepository
import com.example.timeinventory.core.designsystem.theme.DefaultCategoryColors
import com.example.timeinventory.core.model.Category
import com.example.timeinventory.core.model.LogEvent
import com.example.timeinventory.core.model.PlannedEvent
import com.example.timeinventory.feature.timeline.util.createInstant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * タイムラインViewModel
 *
 * タイムライン画面の状態管理と、アプリ初期化処理を担当
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TimelineViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val categoryRepository: CategoryRepository,
    private val logEventRepository: LogEventRepository,
    private val plannedEventRepository: PlannedEventRepository,
) : ViewModel() {
    private val _selectedDate =
        MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    val selectedDate = _selectedDate.asStateFlow()

    private val eventStateFlow: Flow<TimelineEventState> = _selectedDate.flatMapLatest { date ->
        combine<List<LogEvent>, List<PlannedEvent>, TimelineEventState>(
            logEventRepository.getLogEventsByDateStream(date),
            plannedEventRepository.getPlannedEventsByDateStream(date)
        ) { logEvents, plannedEvents ->
            TimelineEventState.Success(logEvents, plannedEvents)
        }
            .onStart {
                emit(TimelineEventState.Loading)
            }
            .catch { e ->
                emit(TimelineEventState.Error(e.message ?: "Unknown Error"))
            }
    }

    val uiState: StateFlow<TimelineUiState> = combine(
        _selectedDate,
        categoryRepository.getCategoriesStream(),
        eventStateFlow
    ) { date, categories, eventState ->
        TimelineUiState(
            categories = categories,
            eventState = eventState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimelineUiState()
    )

    /**
     * 初期化処理
     *
     * @param workLabel 「仕事」のローカライズ文字列
     * @param studyLabel 「勉強」のローカライズ文字列
     * @param exerciseLabel 「運動」のローカライズ文字列
     * @param hobbyLabel 「趣味」のローカライズ文字列
     * @param sleepLabel 「睡眠」のローカライズ文字列
     * @param mealLabel 「食事」のローカライズ文字列
     * @param otherLabel 「その他」のローカライズ文字列
     */
    fun initialize(
        workLabel: String,
        studyLabel: String,
        exerciseLabel: String,
        hobbyLabel: String,
        sleepLabel: String,
        mealLabel: String,
        otherLabel: String,
    ) {
        viewModelScope.launch {
            try {
                // 初期化処理
                val isFirstLaunch = preferencesRepository.isFirstLaunch()

                if (isFirstLaunch) {
                    val defaultCategories = createDefaultCategories(
                        workLabel = workLabel,
                        studyLabel = studyLabel,
                        exerciseLabel = exerciseLabel,
                        hobbyLabel = hobbyLabel,
                        sleepLabel = sleepLabel,
                        mealLabel = mealLabel,
                        otherLabel = otherLabel,
                    )

                    categoryRepository.initializeDefaultCategories(defaultCategories)

                    preferencesRepository.markInitialized()
                }
            } catch (e: Exception) {
                // TODO: エラー処理
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun createDefaultCategories(
        workLabel: String,
        studyLabel: String,
        exerciseLabel: String,
        hobbyLabel: String,
        sleepLabel: String,
        mealLabel: String,
        otherLabel: String,
    ): List<Category> = listOf(
        Category(
            id = Uuid.random(),
            name = workLabel,
            colorArgb = DefaultCategoryColors.Work.toArgb(),
            sortOrder = 0
        ),
        Category(
            id = Uuid.random(),
            name = studyLabel,
            colorArgb = DefaultCategoryColors.Study.toArgb(),
            sortOrder = 1
        ),
        Category(
            id = Uuid.random(),
            name = exerciseLabel,
            colorArgb = DefaultCategoryColors.Exercise.toArgb(),
            sortOrder = 2
        ),
        Category(
            id = Uuid.random(),
            name = hobbyLabel,
            colorArgb = DefaultCategoryColors.Hobby.toArgb(),
            sortOrder = 3
        ),
        Category(
            id = Uuid.random(),
            name = sleepLabel,
            colorArgb = DefaultCategoryColors.Sleep.toArgb(),
            sortOrder = 4
        ),
        Category(
            id = Uuid.random(),
            name = mealLabel,
            colorArgb = DefaultCategoryColors.Meal.toArgb(),
            sortOrder = 5
        ),
        Category(
            id = Uuid.random(),
            name = otherLabel,
            colorArgb = DefaultCategoryColors.Other.toArgb(),
            sortOrder = 6
        ),
    )

    /**
     * 日付選択
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    /**
     * LogEventを作成
     */
    @OptIn(ExperimentalUuidApi::class)
    fun createLogEvent(
        title: String,
        startTime: LocalTime,
        endTime: LocalTime,
        category: Category,
        memo: String,
    ) {
        viewModelScope.launch {
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val selectedDate = _selectedDate.value

                val logEvent = LogEvent(
                    activity = title,
                    category = category,
                    startDateTime = createInstant(selectedDate, startTime, timeZone),
                    endDateTime = createInstant(selectedDate, endTime, timeZone),
                    memo = memo
                )

                logEventRepository.upsertLogEvent(logEvent)
            } catch (e: Exception) {
                // TODO: エラーハンドリング
            }
        }
    }

    /**
     * PlannedEventを作成
     */
    @OptIn(ExperimentalUuidApi::class)
    fun createPlannedEvent(
        title: String,
        startTime: LocalTime,
        endTime: LocalTime,
        category: Category,
        memo: String,
    ) {
        viewModelScope.launch {
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val selectedDate = _selectedDate.value

                val plannedEvent = PlannedEvent(
                    activity = title,
                    category = category,
                    startDateTime = createInstant(selectedDate, startTime, timeZone),
                    endDateTime = createInstant(selectedDate, endTime, timeZone),
                    memo = memo
                )

                plannedEventRepository.upsertPlannedEvent(plannedEvent)
            } catch (e: Exception) {
                // TODO: エラーハンドリング
            }
        }
    }

    /**
     * LogEventを更新
     */
    @OptIn(ExperimentalUuidApi::class)
    fun updateLogEvent(
        id: Uuid,
        title: String,
        startTime: LocalTime,
        endTime: LocalTime,
        category: Category,
        memo: String,
    ) {
        viewModelScope.launch {
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val selectedDate = _selectedDate.value

                val logEvent = LogEvent(
                    id = id,
                    activity = title,
                    category = category,
                    startDateTime = createInstant(selectedDate, startTime, timeZone),
                    endDateTime = createInstant(selectedDate, endTime, timeZone),
                    memo = memo
                )

                logEventRepository.upsertLogEvent(logEvent)
            } catch (e: Exception) {
                // TODO: エラーハンドリング
            }
        }
    }

    /**
     * LogEventを削除
     */
    @OptIn(ExperimentalUuidApi::class)
    fun deleteLogEvent(id: Uuid) {
        viewModelScope.launch {
            try {
                logEventRepository.deleteLogEvent(id)
            } catch (e: Exception) {
                // TODO: エラーハンドリング
            }
        }
    }

    /**
     * PlannedEventを更新
     */
    @OptIn(ExperimentalUuidApi::class)
    fun updatePlannedEvent(
        id: Uuid,
        title: String,
        startTime: LocalTime,
        endTime: LocalTime,
        category: Category,
        memo: String,
    ) {
        viewModelScope.launch {
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val selectedDate = _selectedDate.value

                val plannedEvent = PlannedEvent(
                    id = id,
                    activity = title,
                    category = category,
                    startDateTime = createInstant(selectedDate, startTime, timeZone),
                    endDateTime = createInstant(selectedDate, endTime, timeZone),
                    memo = memo
                )

                plannedEventRepository.upsertPlannedEvent(plannedEvent)
            } catch (e: Exception) {
                // TODO: エラーハンドリング
            }
        }
    }

    /**
     * PlannedEventを削除
     */
    @OptIn(ExperimentalUuidApi::class)
    fun deletePlannedEvent(id: Uuid) {
        viewModelScope.launch {
            try {
                plannedEventRepository.deletePlannedEvent(id)
            } catch (e: Exception) {
                // TODO: エラーハンドリング
            }
        }
    }
}

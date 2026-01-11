package com.example.timeinventory.feature.timeline

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeinventory.core.data.repository.CategoryRepository
import com.example.timeinventory.core.data.repository.PreferencesRepository
import com.example.timeinventory.core.designsystem.theme.DefaultCategoryColors
import com.example.timeinventory.core.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
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
class TimelineViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    private val _selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(
        Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

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
                _uiState.value = TimelineUiState.Loading

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

                // TODO: タイムラインデータを取得
                _uiState.value = TimelineUiState.Success(
                    logEvents = emptyList(), // TODO: 実装
                    plannedEvents = emptyList() // TODO: 実装
                )
            } catch (e: Exception) {
                _uiState.value = TimelineUiState.Error(e.message ?: "Unknown error")
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
        // TODO: 選択日付のデータを再読み込み
    }
}

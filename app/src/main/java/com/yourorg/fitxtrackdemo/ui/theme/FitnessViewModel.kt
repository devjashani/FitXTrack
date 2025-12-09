package com.yourorg.fitxtrackdemo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

// Data Models
data class DailyProgress(
    val date: LocalDate,
    val steps: Int = 0,
    val distance: Double = 0.0,
    val caloriesBurned: Double = 0.0,
    val caloriesIntake: Double = 0.0,
    val caloriesGoal: Double = 600.0,
    val workout: Workout? = null
)

data class Workout(
    val type: String,
    val completed: Boolean = false
)

// ViewModel
@RequiresApi(Build.VERSION_CODES.O)
class FitnessViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _dailyProgress = MutableStateFlow<Map<LocalDate, DailyProgress>>(emptyMap())
    val dailyProgress: StateFlow<Map<LocalDate, DailyProgress>> = _dailyProgress

    init {
        // Sample data for today
        val today = LocalDate.now()
        val sampleProgress = DailyProgress(
            date = today,
            steps = 3246,
            distance = 2.51,
            caloriesBurned = 123.4,
            caloriesIntake = 120.0,
            workout = Workout("PUSH DAY - CHEST", true)
        )

        _dailyProgress.value = mapOf(today to sampleProgress)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun getProgressForDate(date: LocalDate): DailyProgress? {
        return _dailyProgress.value[date]
    }

    fun updateProgress(progress: DailyProgress) {
        viewModelScope.launch {
            val updated = _dailyProgress.value.toMutableMap()
            updated[progress.date] = progress
            _dailyProgress.value = updated
        }
    }

    fun updateSteps(date: LocalDate, steps: Int) {
        viewModelScope.launch {
            val current = getProgressForDate(date) ?: DailyProgress(date = date)
            val updated = current.copy(steps = steps)
            updateProgress(updated)
        }
    }

    fun updateCalories(date: LocalDate, calories: Double) {
        viewModelScope.launch {
            val current = getProgressForDate(date) ?: DailyProgress(date = date)
            val updated = current.copy(caloriesIntake = calories)
            updateProgress(updated)
        }
    }
}
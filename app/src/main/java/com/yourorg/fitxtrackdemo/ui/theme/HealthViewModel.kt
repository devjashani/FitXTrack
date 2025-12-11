// ui/viewmodels/SimpleHealthViewModel.kt
package com.yourorg.fitxtrackdemo.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourorg.fitxtrackdemo.manager.SimpleHealthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyProgress(
    val date: LocalDate = LocalDate.now(),
    val steps: Int = 0,
    val calories: Int = 0,
    val distance: Double = 0.0,
    val stepsGoal: Int = 10000,
    val caloriesGoal: Int = 600,
    val stepsProgress: Float = 0f,
    val caloriesProgress: Float = 0f
)

class SimpleHealthViewModel(context: Context) : ViewModel() {

    private val healthManager = SimpleHealthManager(context)

    private val _todayProgress = MutableStateFlow(DailyProgress())
    val todayProgress: StateFlow<DailyProgress> = _todayProgress.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        // Start observing health data
        startHealthUpdates()
    }

    private fun startHealthUpdates() {
        viewModelScope.launch {
            // Periodically update progress (every 2 seconds)
            while (true) {
                kotlinx.coroutines.delay(2000)
                updateProgress()
            }
        }
    }

    private fun updateProgress() {
        val currentSteps = healthManager.currentSteps
        val currentCalories = healthManager.currentCalories
        val currentDistance = healthManager.currentDistance

        val progress = DailyProgress(
            date = LocalDate.now(),
            steps = currentSteps,
            calories = currentCalories,
            distance = currentDistance,
            stepsProgress = (currentSteps.toFloat() / 10000).coerceAtMost(1f),
            caloriesProgress = (currentCalories.toFloat() / 600).coerceAtMost(1f)
        )

        _todayProgress.value = progress

        // Also save to history for today
        saveTodayToHistory()
    }

    private fun saveTodayToHistory() {
        val today = LocalDate.now().toString()
        healthManager.saveStepsForDate(
            date = today,
            steps = healthManager.currentSteps,
            calories = healthManager.currentCalories,
            distance = healthManager.currentDistance
        )
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDateProgress(date)
    }

    private fun loadDateProgress(date: LocalDate) {
        viewModelScope.launch {
            val (steps, calories, distance) = healthManager.getStepsForDate(date.toString())

            val progress = DailyProgress(
                date = date,
                steps = steps,
                calories = calories,
                distance = distance,
                stepsProgress = (steps.toFloat() / 10000).coerceAtMost(1f),
                caloriesProgress = (calories.toFloat() / 600).coerceAtMost(1f)
            )

            _todayProgress.value = progress
        }
    }

    fun getWeeklyData(): Map<String, Triple<Int, Int, Double>> {
        return healthManager.getWeeklyData()
    }

    fun resetSteps() {
        // You can add reset functionality if needed
    }
}
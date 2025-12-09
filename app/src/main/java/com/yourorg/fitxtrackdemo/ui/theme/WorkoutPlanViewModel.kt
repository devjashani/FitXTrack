package com.yourorg.fitxtrackdemo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourorg.fitxtrackdemo.data.model.WorkoutPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class WorkoutPlanViewModel : ViewModel() {

    private val _weeklyPlans = MutableStateFlow<Map<DayOfWeek, WorkoutPlan?>>(
        DayOfWeek.values().associateWith { null }
    )
    val weeklyPlans = _weeklyPlans.asStateFlow()

    private val _todayWorkout = MutableStateFlow<WorkoutPlan?>(null)
    val todayWorkout = _todayWorkout.asStateFlow()

    init {
        updateTodayWorkout()
    }

    fun setWorkoutForDay(day: DayOfWeek, workoutName: String) {
        viewModelScope.launch {
            val workoutType = when {
                workoutName.contains("Push", ignoreCase = true) -> "push"
                workoutName.contains("Pull", ignoreCase = true) -> "pull"
                workoutName.contains("Leg", ignoreCase = true) -> "legs"
                workoutName.contains("Arm", ignoreCase = true) -> "arms"
                workoutName.contains("Full Body", ignoreCase = true) -> "fullbody"
                else -> "custom"
            }

            val plan = WorkoutPlan(
                id = "${day.name}_${System.currentTimeMillis()}",
                day = day.name,  // Changed to String
                workoutName = workoutName,
                workoutType = workoutType,
                exercises = getDefaultExercises(workoutType),
                duration = 60,
                difficulty = "Intermediate",
                isCustom = workoutName == "Custom Workout"
            )

            _weeklyPlans.value = _weeklyPlans.value.toMutableMap().apply {
                this[day] = plan
            }

            updateTodayWorkout()
        }
    }

    fun removeWorkoutForDay(day: DayOfWeek) {
        viewModelScope.launch {
            _weeklyPlans.value = _weeklyPlans.value.toMutableMap().apply {
                this[day] = null
            }
            updateTodayWorkout()
        }
    }

    fun getWorkoutForDay(day: DayOfWeek): WorkoutPlan? {
        return _weeklyPlans.value[day]
    }

    fun hasPlanForDay(day: DayOfWeek): Boolean {
        return _weeklyPlans.value[day] != null
    }

    private fun updateTodayWorkout() {
        val today = java.time.DayOfWeek.from(java.time.LocalDate.now())
        _todayWorkout.value = _weeklyPlans.value[today]
    }

    private fun getDefaultExercises(type: String): List<String> {
        return when (type) {
            "push" -> listOf("Bench Press", "Shoulder Press", "Tricep Pushdown", "Chest Fly")
            "pull" -> listOf("Pull-ups", "Bent Over Rows", "Bicep Curls", "Face Pulls")
            "legs" -> listOf("Squats", "Leg Press", "Lunges", "Leg Curls")
            "arms" -> listOf("Bicep Curls", "Tricep Extensions", "Hammer Curls", "Dips")
            "fullbody" -> listOf("Deadlifts", "Squats", "Bench Press", "Rows")
            else -> listOf("Custom Exercise 1", "Custom Exercise 2", "Custom Exercise 3")
        }
    }

    // Add this function to your WorkoutPlanViewModel class
    fun getWorkoutNavigationRoute(workoutName: String): String {
        return when {
            workoutName.contains("Push", ignoreCase = true) -> "pushday"
            workoutName.contains("Pull", ignoreCase = true) -> "pullDay"
            workoutName.contains("Leg", ignoreCase = true) -> "legDay"
            workoutName.contains("Full Body", ignoreCase = true) -> "fullBody"
            workoutName.contains("Arm", ignoreCase = true) -> "armsDay"
            workoutName.contains(
                "Shoulder",
                ignoreCase = true
            ) -> "workoutMain" // or create shoulders screen
            workoutName.contains(
                "Cardio",
                ignoreCase = true
            ) -> "workoutMain" // or create cardio screen
            workoutName.contains("Abs", ignoreCase = true) -> "absCore"
            else -> "pushday" // Default
        }
    }
}
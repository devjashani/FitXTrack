package com.yourorg.fitxtrackdemo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourorg.fitxtrackdemo.data.model.WorkoutPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class WorkoutPlanViewModel : ViewModel() {

    private val _weeklyPlans = MutableStateFlow<Map<DayOfWeek, WorkoutPlan?>>(
        DayOfWeek.values().associateWith { null }
    )
    val weeklyPlans = _weeklyPlans.asStateFlow()

    private val _todayWorkout = MutableStateFlow<WorkoutPlan?>(null)
    val todayWorkout = _todayWorkout.asStateFlow()

    // ADD THIS: Flow for list format (for HomeScreen)
    private val _weeklyPlansList = MutableStateFlow<List<WorkoutPlan>>(emptyList())
    val weeklyPlansList = _weeklyPlansList.asStateFlow()

    init {
        updateTodayWorkout()
        updateWeeklyPlansList()
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
                day = day.name,  // String format like "MONDAY"
                dayOfWeek = day.value,  // ADD THIS: Int value 1-7
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
            updateWeeklyPlansList() // ADD THIS
        }
    }

    fun removeWorkoutForDay(day: DayOfWeek) {
        viewModelScope.launch {
            _weeklyPlans.value = _weeklyPlans.value.toMutableMap().apply {
                this[day] = null
            }
            updateTodayWorkout()
            updateWeeklyPlansList() // ADD THIS
        }
    }

    fun getWorkoutForDay(day: DayOfWeek): WorkoutPlan? {
        return _weeklyPlans.value[day]
    }

    fun hasPlanForDay(day: DayOfWeek): Boolean {
        return _weeklyPlans.value[day] != null
    }

    private fun updateTodayWorkout() {
        val today = DayOfWeek.from(LocalDate.now())
        _todayWorkout.value = _weeklyPlans.value[today]
    }

    // ADD THIS FUNCTION: Convert map to list for HomeScreen
    private fun updateWeeklyPlansList() {
        _weeklyPlansList.value = _weeklyPlans.value.values.filterNotNull()
    }

    // ADD THIS FUNCTION: Get workout for specific day (int format)
    fun getWorkoutForDayInt(dayInt: Int): WorkoutPlan? {
        return _weeklyPlansList.value.find { it.dayOfWeek == dayInt }
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

    fun getWorkoutNavigationRoute(workoutName: String): String {
        return when {
            workoutName.contains("Push", ignoreCase = true) -> "pushday"
            workoutName.contains("Pull", ignoreCase = true) -> "pullDay"
            workoutName.contains("Leg", ignoreCase = true) -> "legDay"
            workoutName.contains("Full Body", ignoreCase = true) -> "fullBody"
            workoutName.contains("Arm", ignoreCase = true) -> "armsDay"
            workoutName.contains("Shoulder", ignoreCase = true) -> "workoutMain"
            workoutName.contains("Cardio", ignoreCase = true) -> "workoutMain"
            workoutName.contains("Abs", ignoreCase = true) -> "absCore"
            else -> "pushday"
        }
    }

    // ADD THIS: Helper function for HomeScreen
    fun getWorkoutForSelectedDate(selectedDate: LocalDate): WorkoutPlan? {
        val dayInt = selectedDate.dayOfWeek.value
        return getWorkoutForDayInt(dayInt)
    }
}
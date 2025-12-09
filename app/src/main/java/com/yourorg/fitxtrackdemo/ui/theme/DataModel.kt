// DataModel.kt
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean = true,
    val progress: DailyProgress? = null
)
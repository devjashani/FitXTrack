// ProgressRepository.kt
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.O)
class ProgressRepository {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _dailyProgress = MutableStateFlow<Map<LocalDate, DailyProgress>>(emptyMap())
    val dailyProgress: StateFlow<Map<LocalDate, DailyProgress>> = _dailyProgress

    // Sample data
    init {
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
        val updated = _dailyProgress.value.toMutableMap()
        updated[progress.date] = progress
        _dailyProgress.value = updated
    }
}
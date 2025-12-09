package com.yourorg.fitxtrackdemo.manager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yourorg.fitxtrackdemo.data.WorkoutSession
import com.yourorg.fitxtrackdemo.data.WorkoutStats
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object WorkoutHistoryManager {
    var workoutSessions by mutableStateOf<List<WorkoutSession>>(emptyList())
        private set

    fun addWorkoutSession(session: WorkoutSession) {
        workoutSessions = workoutSessions + session
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWorkoutStats(): WorkoutStats {
        val totalWorkouts = workoutSessions.size
        val totalDuration = workoutSessions.sumOf { it.duration }
        val totalCalories = workoutSessions.sumOf { it.caloriesBurned }

        // Calculate weekly average (last 4 weeks) - FIXED
        val fourWeeksAgo = LocalDate.now().minusWeeks(4)
        val recentWorkouts = workoutSessions.filter {
            it.date.toLocalDate().isAfter(fourWeeksAgo) || it.date.toLocalDate().isEqual(fourWeeksAgo)
        }
        val weeklyAverage = if (recentWorkouts.isNotEmpty()) {
            recentWorkouts.size / 4
        } else 0

        // Find favorite workout
        val favoriteWorkout = workoutSessions
            .groupBy { it.workoutName }
            .maxByOrNull { it.value.size }
            ?.key ?: "No workouts yet"

        // Calculate current streak
        val currentStreak = calculateCurrentStreak()

        return WorkoutStats(
            totalWorkouts = totalWorkouts,
            totalDuration = totalDuration,
            totalCalories = totalCalories,
            weeklyAverage = weeklyAverage,
            favoriteWorkout = favoriteWorkout,
            currentStreak = currentStreak
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateCurrentStreak(): Int {
        if (workoutSessions.isEmpty()) return 0

        val sortedSessions = workoutSessions.sortedByDescending { it.date }
        var streak = 0
        var currentDate = LocalDate.now()

        for (session in sortedSessions) {
            val sessionDate = session.date.toLocalDate()
            val daysBetween = ChronoUnit.DAYS.between(sessionDate, currentDate)

            when {
                daysBetween == 0L -> {
                    // Workout on current day
                    streak++
                }
                daysBetween == 1L -> {
                    // Workout on previous day
                    streak++
                    currentDate = sessionDate
                }
                daysBetween > 1L -> {
                    // Gap in streak, break
                    break
                }
            }
        }

        return streak
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWorkoutsByMonth(): Map<String, List<WorkoutSession>> {
        return workoutSessions
            .sortedByDescending { it.date }
            .groupBy { session ->
                "${session.date.month.toString().lowercase().replaceFirstChar { it.uppercase() }} ${session.date.year}"
            }
    }

    // Sample data for testing
    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeSampleData() {
        workoutSessions = listOf(
            WorkoutSession(
                workoutName = "Push Day",
                date = LocalDateTime.now().minusDays(1),
                duration = 45,
                caloriesBurned = 320,
                exercises = listOf(
                    com.yourorg.fitxtrackdemo.data.Exercise("Bench Press"),
                    com.yourorg.fitxtrackdemo.data.Exercise("Shoulder Press")
                ),
                completed = true
            ),
            WorkoutSession(
                workoutName = "Pull Day",
                date = LocalDateTime.now().minusDays(3),
                duration = 50,
                caloriesBurned = 280,
                exercises = listOf(
                    com.yourorg.fitxtrackdemo.data.Exercise("Pull Ups"),
                    com.yourorg.fitxtrackdemo.data.Exercise("Bicep Curls")
                ),
                completed = true
            ),
            WorkoutSession(
                workoutName = "Leg Day",
                date = LocalDateTime.now().minusDays(5),
                duration = 60,
                caloriesBurned = 450,
                exercises = listOf(
                    com.yourorg.fitxtrackdemo.data.Exercise("Squats"),
                    com.yourorg.fitxtrackdemo.data.Exercise("Lunges")
                ),
                completed = true
            )
        )
    }
}
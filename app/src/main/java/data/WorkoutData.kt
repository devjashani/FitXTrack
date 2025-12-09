package com.yourorg.fitxtrackdemo.data

import java.time.LocalDate
import java.time.LocalDateTime

data class WorkoutSession(
    val id: String = "",
    val workoutName: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val duration: Int = 0, // in minutes
    val caloriesBurned: Int = 0,
    val exercises: List<Exercise> = emptyList(),
    val completed: Boolean = false
)

data class Exercise(
    val name: String = "",
    val sets: List<ExerciseSet> = emptyList(),
    val completed: Boolean = false
)

data class ExerciseSet(
    val setNumber: Int = 1,
    val weight: Double = 0.0,
    val reps: Int = 0,
    val completed: Boolean = false
)

data class WorkoutStats(
    val totalWorkouts: Int = 0,
    val totalDuration: Int = 0, // in minutes
    val totalCalories: Int = 0,
    val weeklyAverage: Int = 0,
    val favoriteWorkout: String = "",
    val currentStreak: Int = 0
)
data class CustomWorkoutPlan(
    val id: String = "",
    val name: String,
    val description: String = "",
    val exercises: List<String>,
    val duration: String = "Custom",
    val createdAt: Long = System.currentTimeMillis()
)

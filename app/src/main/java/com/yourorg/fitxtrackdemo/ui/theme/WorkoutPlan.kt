package com.yourorg.fitxtrackdemo.data.model

import java.time.DayOfWeek

data class WorkoutPlan(
    val id: String = "",
    val day: String = "", // String like "MONDAY"
    val dayOfWeek: Int = 0, // ADD THIS: Int 1-7 (Monday=1, Sunday=7)
    val workoutName: String = "",
    val workoutType: String = "",
    val exercises: List<String> = emptyList(),
    val duration: Int = 0,
    val difficulty: String = "",
    val isCustom: Boolean = false
)

// Workout types
val workoutTypes = listOf(
    "Push Day - Chest & Triceps",
    "Pull Day - Back & Biceps",
    "Leg Day - Quads & Hamstrings",
    "Full Body Workout",
    "Arm Day - Biceps & Triceps",
    "Shoulders & Abs",
    "Cardio & Core",
    "Custom Workout",
    "REST"
)

// Day mapping
val dayNames = mapOf(
    "MONDAY" to "Monday",
    "TUESDAY" to "Tuesday",
    "WEDNESDAY" to "Wednesday",
    "THURSDAY" to "Thursday",
    "FRIDAY" to "Friday",
    "SATURDAY" to "Saturday",
    "SUNDAY" to "Sunday"
)
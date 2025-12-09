package com.yourorg.fitxtrackdemo.data.model

import java.time.DayOfWeek

data class WorkoutPlan(
    val id: String = "",
    val day: String,
    val workoutName: String,
    val workoutType: String, // "push", "pull", "legs", "custom"
    val exercises: List<String> = emptyList(),
    val duration: Int = 60, // minutes
    val difficulty: String = "Intermediate",
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
package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun LegDayScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Leg Day - Legs & Core",
        defaultExercises = listOf(
            // Legs Exercises
            "Squats - Barbell or Dumbbell",
            "Leg Press",
            "Lunges",
            "Romanian Deadlift (RDL)",
            "Leg Extension",
            "Leg Curl",
            "Calf Raise",
            "Hip Thrust",
            "Abductor",

            // Core Exercises
            "Crunches",
            "Planks",
            "Leg Raises",
            "Russian Twists",
            "Bicycle Crunches",
            "Mountain Climbers",
            "Hanging Leg Raises"
        )
    )
}
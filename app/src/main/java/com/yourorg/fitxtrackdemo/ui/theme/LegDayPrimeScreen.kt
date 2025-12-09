package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun LegDayPrimeScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Leg Day - Prime (Legs & Shoulder)",
        defaultExercises = listOf(
            // Legs Exercises
            "Squats - Barbell or Dumbbell",
            "Leg Press",
            "Lunges",
            "Romanian Deadlift (RDL)",
            "Leg Extension",
            "Leg Curl",
            "Calf Raise",

            // Shoulder Exercises
            "Overhead Press",
            "Dumbbell Shoulder Press",
            "Lateral Raises",
            "Front Raises",
            "Upright Rows"
        )
    )
}
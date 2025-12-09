package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun FullBodyScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Full Body Workout",
        defaultExercises = listOf(
            "Burpees",
            "Kettlebell Swings",
            "Thrusters (Squat + Press)",
            "Deadlifts",
            "Clean & Press",
            "Push-ups",
            "Pull-ups",
            "Dumbbell Snatch",
            "Farmer's Walk"
        )
    )
}
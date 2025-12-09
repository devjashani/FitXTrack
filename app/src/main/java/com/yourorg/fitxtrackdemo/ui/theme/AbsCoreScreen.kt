package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun AbsCoreScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Abs & Core - Core Strengthening",
        defaultExercises = listOf(
            // Core Exercises
            "Crunches",
            "Planks",
            "Leg Raises",
            "Russian Twists",
            "Bicycle Crunches",
            "Mountain Climbers",
            "Hanging Leg Raises",
            "Ab Rollout",
            "Side Planks",
            "Flutter Kicks",
            "Lying Leg Raises",
            "Captain's Chair",
            "Toe Touches",
            "Windshield Wipers"
        )
    )
}
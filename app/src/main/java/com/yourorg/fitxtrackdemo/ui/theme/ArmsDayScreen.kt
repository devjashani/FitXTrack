package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ArmsDayScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Arms Day - Biceps, Triceps & Forearm",
        defaultExercises = listOf(
            // Biceps Exercises
            "Barbell Curl",
            "Dumbbell Bicep Curl",
            "Hammer Curl",
            "Concentration Curl",
            "Preacher Curl",
            "Cable Curl",

            // Triceps Exercises
            "Tricep Push Down",
            "Skull Crusher",
            "Overhead Dumbbell Extension",
            "Close Grip Bench Press",
            "Dips",
            "Rope Push Down",

            // Forearm Exercises
            "Wrist Curls",
            "Reverse Wrist Curls",
            "Farmer's Walk",
            "Plate Pinches"
        )
    )
}
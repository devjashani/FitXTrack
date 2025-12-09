package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController



@Composable
fun PushDayEliteScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Push Day Elite - Chest, Shoulder & Triceps",
        defaultExercises = listOf(
            // Chest Exercises
            "Flat Bench Press",
            "Inclined Dumbbell Press",
            "Decline Bench Press",
            "Cable Crossover",
            "Pec Deck Fly",

            // Shoulder Exercises
            "Overhead Press",
            "Dumbbell Lateral Raise",
            "Front Raise",
            "Rear Delt Fly",
            "Arnold Press",

            // Triceps Exercises
            "Tricep Push Down",
            "Skull Crusher",
            "Overhead Dumbbell Extension",
            "Close Grip Bench Press",
            "Dips"
        )
    )
}
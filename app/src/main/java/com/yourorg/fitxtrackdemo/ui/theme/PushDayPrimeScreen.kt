package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushDayScreen(
    navController: NavController
) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Push Day - Chest & Triceps",
        defaultExercises = listOf(
            "Flat Bench Press",
            "Inclined Dumbbell Press",
            "Decline Rope Push Down",
            "Inclined Peck Fly",
            "Cable Crossover",
            "Decline Bench Press",
            "Tricep Push Down",
            "Skull Crusher",
            "Overhead Dumbbell",
            "Rope Push Down",
            "Pec Deck Fly",

        )
    )
}
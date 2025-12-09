package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun PullDayScreen(navController: NavController) {
    WorkoutDayScreen(
        navController = navController,
        workoutName = "Pull Day - Back & Biceps",
        defaultExercises = listOf(
            // Back Exercises
            "Pull-Ups",
            "Lat Pulldown",
            "Wide Grip Seated Cable Row",
            "Close Grip Seated Cable Row",
            "One Arm Dumbbell Row",
            "Deadlift",
            "T-Bar Row",
            "Bent-Over Barbell Row",
            "Face Pulls",

            // Biceps Exercises
            "Barbell Curl",
            "Dumbbell Bicep Curl",
            "Hammer Curl",
            "Concentration Curl",
            "Preacher Curl"
        )
    )
}
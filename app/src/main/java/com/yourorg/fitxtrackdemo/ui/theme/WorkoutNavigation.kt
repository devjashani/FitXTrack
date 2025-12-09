package com.yourorg.fitxtrackdemo.utils

object WorkoutNavigation {

    fun getRouteForWorkout(workoutName: String): String {
        return when {
            workoutName.contains("Push", ignoreCase = true) -> "pushday"
            workoutName.contains("Pull", ignoreCase = true) -> "pullDay"
            workoutName.contains("Leg", ignoreCase = true) -> "legDay"
            workoutName.contains("Full Body", ignoreCase = true) -> "fullBody"
            workoutName.contains("Arm", ignoreCase = true) -> "armsDay"
            workoutName.contains("Abs", ignoreCase = true) -> "absCore"
            workoutName.contains("Shoulder", ignoreCase = true) -> "workoutMain" // or specific route
            workoutName.contains("Cardio", ignoreCase = true) -> "workoutMain"
            workoutName.contains("Core", ignoreCase = true) -> "absCore"
            else -> "workoutMain" // Default to workout main
        }
    }

    fun getWorkoutDisplayName(workoutName: String): String {
        return when {
            workoutName.contains("Push", ignoreCase = true) -> "Push Day Workout"
            workoutName.contains("Pull", ignoreCase = true) -> "Pull Day Workout"
            workoutName.contains("Leg", ignoreCase = true) -> "Leg Day Workout"
            workoutName.contains("Full Body", ignoreCase = true) -> "Full Body Workout"
            workoutName.contains("Arm", ignoreCase = true) -> "Arm Day Workout"
            workoutName.contains("Abs", ignoreCase = true) -> "Abs & Core Workout"
            workoutName.contains("Shoulder", ignoreCase = true) -> "Shoulder Workout"
            workoutName.contains("Cardio", ignoreCase = true) -> "Cardio Workout"
            else -> workoutName
        }
    }
}
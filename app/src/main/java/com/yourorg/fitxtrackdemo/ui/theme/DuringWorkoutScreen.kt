package com.yourorg.fitxtrackdemo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yourorg.fitxtrackdemo.manager.WorkoutHistoryManager
import com.yourorg.fitxtrackdemo.data.WorkoutSession
import com.yourorg.fitxtrackdemo.data.Exercise
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuringWorkoutScreen(
    navController: NavController,
    exercisesString: String? = ""
) {
    val exercises = exercisesString?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    var completedExercises by remember { mutableStateOf(setOf<Int>()) }
    var isWorkoutCompleted by remember { mutableStateOf(false) }
    var workoutStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var currentTime by remember { mutableStateOf(0) } // in seconds

    // Add video state
    var showVideoScreen by remember { mutableStateOf(false) }
    var currentVideoExercise by remember { mutableStateOf("") }

    // Timer that runs when workout is active
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (!isWorkoutCompleted) {
                currentTime = ((System.currentTimeMillis() - workoutStartTime) / 1000).toInt()
            }
        }
    }

    // Save workout when completed
    LaunchedEffect(isWorkoutCompleted) {
        if (isWorkoutCompleted) {
            // Calculate workout duration in minutes
            val durationMinutes = currentTime / 60
            // Simple calorie calculation (you can make this more sophisticated)
            val caloriesBurned = (durationMinutes * 5).coerceAtLeast(50)

            // Save workout to history
            val workoutSession = WorkoutSession(
                workoutName = "Custom Workout",
                date = LocalDateTime.now(),
                duration = durationMinutes,
                caloriesBurned = caloriesBurned,
                exercises = exercises.mapIndexed { index, exercise ->
                    Exercise(
                        name = exercise,
                        completed = completedExercises.contains(index)
                    )
                },
                completed = true
            )
            WorkoutHistoryManager.addWorkoutSession(workoutSession)
        }
    }

    // Navigate to video screen
    if (showVideoScreen) {
        LaunchedEffect(showVideoScreen) {
            navController.navigate("exerciseVideo/${currentVideoExercise}")
            showVideoScreen = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "During Workout",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Timer display
                    Text(
                        text = formatTime(currentTime),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            )
        },
        bottomBar = {
            if (isWorkoutCompleted) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Workout Completed! 🎉",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        // Workout summary
                        val durationMinutes = currentTime / 60
                        val caloriesBurned = (durationMinutes * 5).coerceAtLeast(50)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SummaryItem("Duration", "${durationMinutes} min")
                            SummaryItem("Exercises", "${completedExercises.size}/${exercises.size}")
                            SummaryItem("Calories", "$caloriesBurned kcal")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                // Save workout and navigate back to home
                                navController.popBackStack("home", inclusive = false)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "Done")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finish Workout")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Progress indicator
            val progress = if (exercises.isNotEmpty()) {
                completedExercises.size.toFloat() / exercises.size
            } else {
                0f
            }

            Text(
                text = "Workout Progress",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${completedExercises.size}/${exercises.size} exercises completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Exercises",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No exercises selected",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(exercises) { index, exercise ->
                        // DEBUG: Print each exercise name to Logcat
                        println("DEBUG: Exercise: '$exercise'")

                        ExerciseItemWithVideo(
                            exerciseName = exercise,
                            isCompleted = completedExercises.contains(index),
                            onToggle = {
                                completedExercises = if (completedExercises.contains(index)) {
                                    completedExercises - index
                                } else {
                                    val newCompleted = completedExercises + index
                                    // Check if all exercises are completed
                                    if (newCompleted.size == exercises.size) {
                                        isWorkoutCompleted = true
                                    }
                                    newCompleted
                                }
                            },
                            onVideoClick = {
                                currentVideoExercise = exercise
                                showVideoScreen = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItemWithVideo(
    exerciseName: String,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onVideoClick: () -> Unit
) {
    // Check if video exists for this exercise
    val hasVideo = remember(exerciseName) {
        val exerciseVideos = mapOf(
            "Flat Bench Press" to "flat_bench_press",
            "Inclined Dumbbell Press" to "inclined_dumbbell_press",
            "Decline Rope Push Down" to "decline_rope_push_down",
            "Decline Bench Press" to "decline_bench_press",
            "Inclined Peck Fly" to "inclined_peck_fly",
            "Cable Crossover" to "cable_crossover",

            //triceps
            "Tricep Push Down" to "tricep_push_down",
            "Skull Crusher" to "skull_crusher",
            "Overhead Dumbbell" to "overhead_dumbbell",
            "Rope Push Down" to "rope_push_down",

            //back
            "Pull-Ups" to "pull_ups",
            "Lat Pulldown" to "lat_pulldown",
            "Wide Grip Seated Cable Row" to "wide_grip_seated_cable_row",
            "Close Grip Seated Cable Row" to "close_grip_seated_cable_row",
            "One Arm Dumbbell Row" to "one_arm_dumbbell_row",
            "Deadlift" to "deadlifts",
            "T-Bar Row" to "t_bar_row",
            "Bent-Over Barbell Row" to "bent_over_barbell_row",
            "Face Pulls" to "face_pulls",
            "Barbell Curl" to "barbell_curl",
            "Dumbbell Bicep Curl" to "dumbbell_bicep_curl",
            "Hammer Curl" to "hammer_curl",
            "Concentration Curl" to "concentration_curl",
            "Preacher Curl" to "preacher_curl",
            "Pull-ups" to "pull_ups",

            // Push Day Elite
            "Pec Deck Fly" to "pec_deck_fly",
            "Overhead Press" to "overhead_press",
            "Dumbbell Lateral Raise" to "dumbbell_lateral_raise",
            "Front Raise" to "front_raise",
            "Rear Delt Fly" to "rear_delt_fly",
            "Arnold Press" to "arnold_press",
            "Overhead Dumbbell Extension" to "overhead_dumbbell_extension",
            "Close Grip Bench Press" to "close_grip_bench_press",
            "Dips" to "dips",

            // Leg Day Prime
            "Squats - Barbell or Dumbbell" to "squats",
            "Leg Press" to "leg_press",
            "Lunges" to "lunges",
            "Romanian Deadlift (RDL)" to "romanian_deadlift",
            "Leg Extension" to "leg_extension",
            "Leg Curl" to "leg_curl",
            "Calf Raise" to "calf_raise",

            // Shoulder (some duplicates - consolidated)
            "Dumbbell Shoulder Press" to "dumbbell_shoulder_press",
            "Lateral Raises" to "lateral_raises",
            "Front Raises" to "front_raises",
            "Upright Rows" to "upright_rows",

            // Leg Day Elite
            "Hip Thrust" to "hip_thrust",
            "Abductor" to "abductor",

            // Core
            "Crunches" to "crunches",
            "Planks" to "planks",
            "Leg Raises" to "leg_raises",
            "Russian Twists" to "russian_twists",
            "Bicycle Crunches" to "bicycle_crunches",
            "Mountain Climbers" to "mountain_climbers",
            "Hanging Leg Raises" to "hanging_leg_raises",

            // Arms & Miscellaneous
            "Cable Curl" to "cable_curl",
            "Wrist Curls" to "wrist_curls",
            "Reverse Wrist Curls" to "reverse_wrist_curls",
            "Farmer's Walk" to "farmers_walk",
            "Plate Pinches" to "plate_pinches",
            "Ab Rollout" to "ab_rollout",
            "Side Planks" to "side_planks",
            "Flutter Kicks" to "flutter_kicks",
            "Lying Leg Raises" to "lying_leg_raises",
            "Captain's Chair" to "captains_chair",
            "Toe Touches" to "toe_touches",
            "Windshield Wipers" to "windshield_wipers",

            // Full Body
            "Burpees" to "burpees",
            "Kettlebell Swings" to "kettlebell_swings",
            "Thrusters (Squat + Press)" to "thrusters",
            "Clean & Press" to "clean_press",
            "Push-ups" to "push_ups",
            "Dumbbell Snatch" to "dumbbell_snatch",


            // Add more as you have videos
        )
        exerciseVideos.containsKey(exerciseName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise Name
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Video Button (if video exists)
            if (hasVideo) {
                IconButton(
                    onClick = onVideoClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Watch video guide",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Checkbox - Clickable only on the checkbox itself
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onToggle() }
                    .background(
                        color = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = if (isCompleted) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import com.yourorg.fitxtrackdemo.data.ExerciseSet
import kotlinx.coroutines.delay
import java.time.LocalDateTime

// Data class to track exercise details - make it immutable for better state management
@Immutable
data class ExerciseTracking(
    val name: String,
    val isCompleted: Boolean = false,
    val sets: List<ExerciseSet> = emptyList(),
    val timerRunning: Boolean = false,
    val elapsedTime: Long = 0,
    val startTime: Long? = null
)

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

    // Track individual exercise data using a map for better performance
    var exerciseTrackingMap by remember {
        mutableStateOf(
            exercises.associate { it to ExerciseTracking(name = it) }
        )
    }

    // Add video state
    var showVideoScreen by remember { mutableStateOf(false) }
    var currentVideoExercise by remember { mutableStateOf("") }

    // Timer that runs when workout is active
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (!isWorkoutCompleted) {
                currentTime = ((System.currentTimeMillis() - workoutStartTime) / 1000).toInt()

                // Update individual exercise timers - create a new map to avoid concurrency issues
                val currentTimeMillis = System.currentTimeMillis()
                val updatedMap = exerciseTrackingMap.toMutableMap()

                exerciseTrackingMap.forEach { (name, tracking) ->
                    if (tracking.timerRunning && tracking.startTime != null) {
                        updatedMap[name] = tracking.copy(
                            elapsedTime = currentTimeMillis - tracking.startTime!!
                        )
                    }
                }

                if (updatedMap != exerciseTrackingMap) {
                    exerciseTrackingMap = updatedMap
                }
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

            // Convert tracking data to Exercise objects with sets
            val completedExercisesData = exercises.map { exerciseName ->
                val tracking = exerciseTrackingMap[exerciseName] ?: ExerciseTracking(name = exerciseName)
                val index = exercises.indexOf(exerciseName)

                Exercise(
                    name = exerciseName,
                    sets = tracking.sets,
                    completed = completedExercises.contains(index)
                )
            }

            // Save workout to history
            val workoutSession = WorkoutSession(
                workoutName = "Custom Workout",
                date = LocalDateTime.now(),
                duration = durationMinutes,
                caloriesBurned = caloriesBurned,
                exercises = completedExercisesData,
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
                        val tracking = exerciseTrackingMap[exercise] ?: ExerciseTracking(name = exercise)

                        ExerciseItemWithTracking(
                            exerciseName = exercise,
                            exerciseTracking = tracking,
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
                            },
                            onStartTimer = {
                                val currentTracking = exerciseTrackingMap[exercise] ?: ExerciseTracking(name = exercise)
                                exerciseTrackingMap = exerciseTrackingMap.toMutableMap().apply {
                                    this[exercise] = currentTracking.copy(
                                        timerRunning = true,
                                        startTime = System.currentTimeMillis() - currentTracking.elapsedTime
                                    )
                                }
                            },
                            onStopTimer = {
                                val currentTracking = exerciseTrackingMap[exercise] ?: ExerciseTracking(name = exercise)
                                if (currentTracking.timerRunning) {
                                    exerciseTrackingMap = exerciseTrackingMap.toMutableMap().apply {
                                        this[exercise] = currentTracking.copy(
                                            timerRunning = false,
                                            elapsedTime = System.currentTimeMillis() - (currentTracking.startTime ?: 0)
                                        )
                                    }
                                }
                            },
                            onAddSet = {
                                val currentTracking = exerciseTrackingMap[exercise] ?: ExerciseTracking(name = exercise)
                                val newSets = currentTracking.sets.toMutableList()
                                val newSet = ExerciseSet(
                                    setNumber = newSets.size + 1,
                                    weight = 0.0,
                                    reps = 0,
                                    completed = true
                                )
                                newSets.add(newSet)
                                exerciseTrackingMap = exerciseTrackingMap.toMutableMap().apply {
                                    this[exercise] = currentTracking.copy(
                                        sets = newSets
                                    )
                                }
                            },
                            onRemoveSet = {
                                val currentTracking = exerciseTrackingMap[exercise] ?: ExerciseTracking(name = exercise)
                                if (currentTracking.sets.isNotEmpty()) {
                                    val newSets = currentTracking.sets.toMutableList()
                                    newSets.removeLast()
                                    // Update set numbers
                                    newSets.forEachIndexed { setIndex, set ->
                                        newSets[setIndex] = set.copy(setNumber = setIndex + 1)
                                    }
                                    exerciseTrackingMap = exerciseTrackingMap.toMutableMap().apply {
                                        this[exercise] = currentTracking.copy(
                                            sets = newSets
                                        )
                                    }
                                }
                            },
                            onUpdateSetReps = { setIndex, reps ->
                                val currentTracking = exerciseTrackingMap[exercise] ?: ExerciseTracking(name = exercise)
                                if (setIndex < currentTracking.sets.size) {
                                    val newSets = currentTracking.sets.toMutableList()
                                    newSets[setIndex] = newSets[setIndex].copy(reps = reps)
                                    exerciseTrackingMap = exerciseTrackingMap.toMutableMap().apply {
                                        this[exercise] = currentTracking.copy(
                                            sets = newSets
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItemWithTracking(
    exerciseName: String,
    exerciseTracking: ExerciseTracking,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onVideoClick: () -> Unit,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    onUpdateSetReps: (Int, Int) -> Unit
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // First row: Exercise name and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
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

                // Timer Controls
                IconButton(
                    onClick = {
                        if (exerciseTracking.timerRunning) {
                            onStopTimer()
                        } else {
                            onStartTimer()
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (exerciseTracking.timerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (exerciseTracking.timerRunning) "Stop timer" else "Start timer",
                        tint = if (exerciseTracking.timerRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Checkbox
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

            Spacer(modifier = Modifier.height(12.dp))

            // Second row: Timer display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Exercise Timer:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = formatMillisToTime(exerciseTracking.elapsedTime),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (exerciseTracking.timerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Third row: Sets information
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sets: ${exerciseTracking.sets.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row {
                        IconButton(
                            onClick = onRemoveSet,
                            modifier = Modifier.size(32.dp),
                            enabled = exerciseTracking.sets.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Remove set",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onAddSet,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add set",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Display individual sets
                if (exerciseTracking.sets.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        exerciseTracking.sets.forEachIndexed { index, set ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Set ${set.setNumber}:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Reps control for each set
                                    IconButton(
                                        onClick = {
                                            if (set.reps > 0) {
                                                onUpdateSetReps(index, set.reps - 1)
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Remove,
                                            contentDescription = "Decrease reps",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    Text(
                                        text = "${set.reps} reps",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.width(50.dp)
                                    )

                                    IconButton(
                                        onClick = { onUpdateSetReps(index, set.reps + 1) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Increase reps",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
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

private fun formatMillisToTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
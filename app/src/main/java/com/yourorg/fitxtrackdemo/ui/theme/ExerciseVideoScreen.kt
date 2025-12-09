package com.yourorg.fitxtrackdemo.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseVideoScreen(
    navController: NavController,
    exerciseName: String
) {
    val context = LocalContext.current

    // Map exercise names to video file names
    val exerciseVideos = mapOf(
//        chest & triceps
        "Flat Bench Press" to "flat_bench_press",
        "Inclined Dumbbell Press" to "inclined_dumbbell_press",
        "Decline Rope Push Down" to "decline_rope_push_down",
        "Decline Bench Press" to "decline_bench_press",
        "Inclined Peck Fly" to "inclined_peck_fly",
        "Pec Deck Fly" to "pec_deck_fly",
        "Tricep Push Down" to "tricep_push_down",
        "Skull Crusher" to "skull_crusher",
        "Overhead Dumbbell" to "overhead_dumbbell",
        "Rope Push Down" to "rope_push_down",
        "Cable Crossover" to "cable_crossover",
        "Close Grip Bench Press" to "close_grip_bench_press",

        // Back Day
        "Pull-Ups" to "pull_ups",
        "Lat Pulldown" to "lat_pulldown",
        "Wide Grip Seated Cable Row" to "wide_grip_seated_cable_row",
        "Close Grip Seated Cable Row" to "close_grip_seated_cable_row",
        "One Arm Dumbbell Row" to "one_arm_dumbbell_row",
        "Deadlift" to "deadlifts",
        "T-Bar Row" to "t_bar_row",
        "Bent-Over Barbell Row" to "bent_over_barbell_row",
        "Face Pulls" to "face_pulls",
        "Pull-ups" to "pull_ups",

        // Biceps
        "Barbell Curl" to "barbell_curl",
        "Dumbbell Bicep Curl" to "dumbbell_bicep_curl",
        "Hammer Curl" to "hammer_curl",
        "Concentration Curl" to "concentration_curl",
        "Preacher Curl" to "preacher_curl",

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
        // Add more exercises here as you add videos
    )

    val videoFileName = exerciseVideos[exerciseName]
    val videoResourceId = remember(videoFileName) {
        if (videoFileName != null) {
            val resourceName = "raw/$videoFileName"
            val resourceId = context.resources.getIdentifier(resourceName, null, context.packageName)
            if (resourceId != 0) resourceId else null
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "$exerciseName - Video Guide",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (videoResourceId != null) {
                // Video Player
                ExerciseVideoPlayer(
                    videoResourceId = videoResourceId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                // Exercise Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Proper Form Tips:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getExerciseTips(exerciseName),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                // No video available
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.PlayArrow, // Changed from VideocamOff to PlayArrow
                            contentDescription = "No video",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Video Not Available",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Video guide for $exerciseName is coming soon!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseVideoPlayer(
    videoResourceId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(
                Uri.parse("android.resource://${context.packageName}/$videoResourceId")
            )
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                setShowNextButton(false)
                setShowPreviousButton(false)
            }
        },
        modifier = modifier
    )
}

// Helper function for exercise tips
fun getExerciseTips(exerciseName: String): String {
    return when (exerciseName) {
        "Flat Bench Press" -> "• Keep your back flat on the bench\n• Grip slightly wider than shoulder width\n• Lower the bar to your chest\n• Push up explosively"
        "Inclined Dumbbell Press" -> "• Set bench to 30-45 degree angle\n• Keep dumbbells at chest level\n• Press up in an arc motion\n• Don't lock elbows at the top"
        "Tricep Push Down" -> "• Keep elbows close to your body\n• Use rope attachment for full extension\n• Squeeze triceps at the bottom\n• Control the negative movement"
        "Skull Crusher" -> "• Lie flat on bench with barbell\n• Lower weight towards forehead\n• Keep elbows stationary\n• Extend arms fully at the top"

        // Back Exercises
        "Pull Ups" -> "• Grip bar slightly wider than shoulders\n• Pull yourself up until chin clears bar\n• Lower yourself with control\n• Engage your core throughout"
        "Lat Pulldown" -> "• Lean back slightly, chest up\n• Pull bar to upper chest\n• Squeeze shoulder blades together\n• Control the weight up"
        "Wide Grip Seated Cable Row" -> "• Keep chest up, back straight\n• Pull handles to lower chest\n• Squeeze back muscles\n• Extend arms fully on release"
        "Close Grip Seated Cable Row" -> "• Use close grip attachment\n• Pull to lower abdomen\n• Keep elbows close to body\n• Squeeze at the peak"
        "One Arm Dumbbell Row" -> "• Place knee and hand on bench\n• Pull dumbbell to hip\n• Keep back parallel to floor\n• Squeeze shoulder blade"
        "Deadlift" -> "• Keep back straight, chest up\n• Drive through your heels\n• Stand up fully at the top\n• Lower with control"
        "T-Bar Row" -> "• Stand with feet shoulder-width\n• Pull handle to chest\n• Keep back straight\n• Squeeze back muscles"
        "Bent-Over Barbell Row" -> "• Bend knees slightly, hinge at hips\n• Pull bar to lower chest\n• Keep back straight\n• Squeeze shoulder blades"
        "Face Pulls" -> "• Use rope attachment at chest height\n• Pull towards your face\n• External rotation at the end\n• Squeeze rear delts"

        // Biceps Exercises
        "Barbell Curl" -> "• Keep elbows close to body\n• Curl bar to shoulder level\n• Don't swing your body\n• Squeeze biceps at top"
        "Dumbbell Bicep Curl" -> "• Alternate or curl both arms\n• Keep palms facing up\n• Full range of motion\n• Control the descent"
        "Hammer Curl" -> "• Keep palms facing each other\n• Curl dumbbells to shoulders\n• Works brachialis muscle\n• Don't use momentum"
        "Concentration Curl" -> "• Sit on bench, elbow on inner thigh\n• Isolate the bicep completely\n• Squeeze at the top\n• Slow controlled movement"
        "Preacher Curl" -> "• Use preacher bench for support\n• Isolates bicep effectively\n• Don't hyperextend at bottom\n• Squeeze at peak contraction"

        "Pec Deck Fly" -> "• Sit upright on machine\n• Bring arms together in front\n• Squeeze chest muscles\n• Control the stretch"
        "Overhead Press" -> "• Keep core tight\n• Press bar overhead\n• Don't arch back excessively\n• Lower with control"
        "Dumbbell Lateral Raise" -> "• Slight bend in elbows\n• Raise to shoulder level\n• Control the descent\n• Don't use momentum"
        "Front Raise" -> "• Stand with feet shoulder-width\n• Raise dumbbells to eye level\n• Keep arms straight\n• Alternate arms"
        "Rear Delt Fly" -> "• Bend forward at hips\n• Raise arms out to sides\n• Squeeze rear delts\n• Keep back straight"
        "Arnold Press" -> "• Start with palms facing you\n• Rotate palms as you press\n• Full range of motion\n• Great shoulder builder"
        "Overhead Dumbbell Extension" -> "• Hold one dumbbell with both hands\n• Lower behind head\n• Extend arms fully\n• Keep elbows in"
        "Close Grip Bench Press" -> "• Hands shoulder-width apart\n• Focus on triceps\n• Lower to lower chest\n• Powerful press up"
        "Dips" -> "• Lean forward for chest focus\n• Keep upright for triceps\n• Don't go too deep\n• Control the movement"

        // Leg Day Prime
        "Squats - Barbell or Dumbbell" -> "• Feet shoulder-width apart\n• Keep chest up, back straight\n• Go parallel or below\n• Drive through heels"
        "Leg Press" -> "• Place feet shoulder-width\n• Don't lock knees at top\n• Lower until 90 degrees\n• Push through entire foot"
        "Lunges" -> "• Step forward with control\n• Lower until both knees 90°\n• Keep front knee behind toes\n• Push back to start"
        "Romanian Deadlift (RDL)" -> "• Slight bend in knees\n• Hinge at hips, keep back straight\n• Lower until stretch in hamstrings\n• Squeeze glutes to stand"
        "Leg Extension" -> "• Adjust machine for knee alignment\n• Extend legs fully\n• Squeeze quads at top\n• Control the negative"
        "Leg Curl" -> "• Lie face down on machine\n• Curl heels toward glutes\n• Squeeze hamstrings\n• Full range of motion"
        "Calf Raise" -> "• Use machine or free weights\n• Rise up on toes\n• Squeeze calves at top\n• Stretch at bottom"

        // Shoulder
        "Dumbbell Shoulder Press" -> "• Sit upright on bench\n• Press dumbbells overhead\n• Don't lock elbows\n• Control the descent"
        "Lateral Raise" -> "• Light weight for proper form\n• Raise arms to shoulder level\n• Slight bend in elbows\n• Focus on side delts"
        "Front Raises" -> "• Can use barbell or dumbbells\n• Raise to eye level\n• Keep core engaged\n• Alternate or together"
        "Upright Rows" -> "• Close grip on barbell\n• Pull to chin level\n• Keep elbows high\n• Avoid shoulder impingement"

        // Leg Day Elite
        "Hip Thrust" -> "• Upper back on bench\n• Drive hips upward\n• Squeeze glutes at top\n• Lower with control"
        "Abductor" -> "• Sit on abductor machine\n• Push knees outward\n• Squeeze outer thighs\n• Control the return"

        // Core
        "Crunches" -> "• Hands behind head, don't pull\n• Lift shoulders off floor\n• Exhale as you crunch\n• Engage abs throughout"
        "Planks" -> "• Straight line head to heels\n• Engage core and glutes\n• Don't sag or hike hips\n• Breathe normally"
        "Leg Raise" -> "• Lie on back, legs straight\n• Lower legs toward floor\n• Don't arch back\n• Use controlled movement"
        "Russian Twists" -> "• Lean back slightly\n• Rotate torso side to side\n• Keep feet off ground\n• Engage obliques"
        "Bicycle Crunches" -> "• Alternate elbow to knee\n• Keep movements controlled\n• Don't pull on neck\n• Full range of motion"
        "Mountain Climbers" -> "• Start in plank position\n• Alternate knees to chest\n• Keep hips low\n• Fast but controlled"
        "Hanging Leg Raises" -> "• Hang from bar\n• Raise legs to parallel\n• Don't swing body\n• Lower with control"

        // Arms & Miscellaneous
        "Cable Curl" -> "• Use rope or straight bar\n• Keep elbows stationary\n• Full contraction\n• Slow negative"
        "Wrist Curls" -> "• Forearms on bench\n• Curl weight up\n• Isolate forearm flexors\n• Small range of motion"
        "Reverse Wrist Curls" -> "• Palms facing down\n• Curl weight upward\n• Works forearm extensors\n• Control the movement"
        "Farmer's Walk" -> "• Hold heavy weights\n• Walk with upright posture\n• Grip until failure\n• Great for grip strength"
        "Plate Pinches" -> "• Pinch weight plates together\n• Hold for time\n• Improves pinch grip\n• Challenge yourself"
        "Ab Rollout" -> "• Start on knees\n• Roll forward until stretched\n• Engage core to return\n• Don't sag in lower back"
        "Side Planks" -> "• Stack feet, prop on elbow\n• Lift hips off ground\n• Hold side plank position\n• Engage obliques"
        "Flutter Kicks" -> "• Lie on back, legs straight\n• Alternate small kicks\n• Keep lower back pressed down\n• Engage lower abs"
        "Lying Leg Raises" -> "• Lie flat on back\n• Raise legs vertically\n• Lower with control\n• Don't let feet touch floor"
        "Captain's Chair" -> "• Support on forearm pads\n• Raise knees to chest\n• Don't swing body\n• Squeeze abs at top"
        "Toe Touches" -> "• Lie on back, legs up\n• Reach hands toward toes\n• Crunch upward\n• Controlled movement"
        "Windshield Wiper" -> "• Lie on back, legs vertical\n• Rotate legs side to side\n• Keep shoulders down\n• Advanced core exercise"

        // Full Body
        "Burpees" -> "• Squat down, hands on floor\n• Kick feet back to plank\n• Do a push-up\n• Jump up with arms overhead"
        "Kettlebell Swing" -> "• Hinge at hips, not squat\n• Swing bell to chest height\n• Powerful hip thrust\n• Let arms follow momentum"
        "Thrusters (Squat + Press)" -> "• Squat with weight at shoulders\n• Explosively stand and press\n• Full body coordination\n• Great metabolic exercise"
        "Clean & Press" -> "• Pull barbell from floor\n• Catch at shoulders\n• Press overhead\n• Complete Olympic movement"
        "Push-ups" -> "• Hands shoulder-width apart\n• Keep body straight\n• Lower until chest nearly touches\n• Push back up"
        "Dumbbell Snatch" -> "• Single arm movement\n• Pull dumbbell from floor\n• Overhead in one motion\n• Explosive power movement"

        else -> "• Maintain proper form throughout\n• Breathe out during exertion\n• Control the movement\n• Don't use momentum"
    }
}
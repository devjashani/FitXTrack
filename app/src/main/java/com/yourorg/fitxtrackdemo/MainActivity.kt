// MainActivity.kt - UPDATED VERSION
package com.yourorg.fitxtrackdemo

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.yourorg.fitxtrackdemo.manager.SimpleHealthManager  // ADD THIS IMPORT
import com.yourorg.fitxtrackdemo.manager.UserData
import com.yourorg.fitxtrackdemo.manager.UserManager
import com.yourorg.fitxtrackdemo.ui.screens.*
import com.yourorg.fitxtrackdemo.ui.theme.AuthScreen
import com.yourorg.fitxtrackdemo.ui.theme.FitXTrackDemoTheme
import com.yourorg.fitxtrackdemo.ui.theme.WorkoutViewModel
import kotlinx.coroutines.delay
//import kotlin.math.PI
//import kotlin.math.cos
//import kotlin.math.sin
//import kotlin.random.Random

// IMPORT ONLY MATERIAL 3
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.tooling.preview.Preview

// NEW COLOR THEME - Clean gym aesthetic
val gymDarkBlue = Color(0xFF0A0E17)
val gymMediumBlue = Color(0xFF1A2332)
val gymAccentBlue = Color(0xFF4A90E2)
val gymLightBlue = Color(0xFF3498DB)
val gymTextWhite = Color(0xFFF8F9FA)
val gymSubtitleGray = Color(0xFF95A5A6)
val gymSteelDark = Color(0xFF2C3E50)
val gymSteelMedium = Color(0xFF34495E)

class MainActivity : ComponentActivity() {
    private val fitnessViewModel: FitnessViewModel by   viewModels()
    private lateinit var healthManager: SimpleHealthManager  // ADD THIS LINE

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Health Manager - ADD THIS
        healthManager = SimpleHealthManager(this)

        setContent {
            FitXTrackDemoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(activity = this, fitnessViewModel = fitnessViewModel)
                }
            }
        }
    }

    // ADD THIS FUNCTION TO CLEAN UP
    override fun onDestroy() {
        super.onDestroy()
        healthManager.unregisterListener()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(activity: ComponentActivity, fitnessViewModel: FitnessViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start") {

        composable("start") {
            CleanGymStartScreen(onStartClicked = { navController.navigate("welcome") })
        }

        composable("welcome") {
            SimpleGymWelcomeScreen(onContinue = { navController.navigate("home") })
        }

        // Keep all your existing routes exactly the same...
        composable("calendar") {
            CalendarScreen(
                viewModel = fitnessViewModel,
                onBack = { navController.popBackStack() },
                onDataInputClick = { selectedDate ->
                    navController.navigate("dataInput/$selectedDate")
                }
            )
        }

        composable("fitnessProgress") {
            FitnessProgressScreen(
                viewModel = fitnessViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "dataInput/{selectedDate}",
            arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDateStr = backStackEntry.arguments?.getString("selectedDate") ?: ""
            DataInputScreen(
                viewModel = fitnessViewModel,
                selectedDate = selectedDateStr,
                onSave = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "duringWorkout/{exercises}",
            arguments = listOf(navArgument("exercises") { type = NavType.StringType })
        ) { backStackEntry ->
            val exercisesString = backStackEntry.arguments?.getString("exercises")
            DuringWorkoutScreen(
                navController = navController,
                exercisesString = exercisesString
            )
        }

        composable("settings") {
            SettingScreen(navController = navController)
        }

        composable("meditation") {
            MeditationScreen(navController = navController)
        }

        composable("editProfile") {
            ProfileEditScreen(navController = navController)
        }

        composable("workoutHistory") {
            WorkoutHistoryScreen(navController = navController)
        }

        composable("pushDay") {
            PushDayScreen(navController = navController)
        }

        composable("pullDay") {
            PullDayScreen(navController = navController)
        }

        composable("legDay") {
            LegDayScreen(navController = navController)
        }

        composable("fullBody") {
            FullBodyScreen(navController = navController)
        }

        composable("pushDayElite") {
            PushDayEliteScreen(navController = navController)
        }

        composable("legDayPrime") {
            LegDayPrimeScreen(navController = navController)
        }

        composable("armsDay") {
            ArmsDayScreen(navController = navController)
        }

        composable("absCore") {
            AbsCoreScreen(navController = navController)
        }

        composable("weeklyPlanner") {
            WeeklyPlannerScreen(navController = navController)
        }

        composable("workoutMain") {
            val workoutViewModel: WorkoutViewModel = viewModel()
            WorkoutMainScreen(navController = navController, workoutViewModel = workoutViewModel)
        }

        composable("createCustomPlan") {
            val workoutViewModel: WorkoutViewModel = viewModel()
            CreateCustomPlanScreen(navController = navController, workoutViewModel = workoutViewModel)
        }

        composable("workoutCategory/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("$categoryName Workouts - Coming Soon!")
            }
        }

        composable("exerciseVideo/{exerciseName}") { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: ""
            ExerciseVideoScreen(
                navController = navController,
                exerciseName = exerciseName
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                profileImageUrl = null,
                onProfileClick = {
                    if (UserManager.isUserLoggedIn()) {
                        navController.navigate("settings")
                    } else {
                        navController.navigate("auth")
                    }
                },
                onCalendarClick = {
                    navController.navigate("calendar")
                },
                onProgressClick = {
                    navController.navigate("fitnessProgress")
                }
            )
        }

        composable("auth") {
            AuthScreen(
                onSignedIn = {
                    val userName = if (FirebaseAuth.getInstance().currentUser?.displayName != null) {
                        FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
                    } else {
                        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) ?: "User"
                    }

                    UserManager.login(
                        UserData(
                            name = userName,
                            email = FirebaseAuth.getInstance().currentUser?.email ?: "",
                            isLoggedIn = true
                        )
                    )

                    Toast.makeText(activity, "Successfully Signed In!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                activity = activity
            )
        }
    }
}

// NEW CLEAN GYM START SCREEN
@Composable
fun CleanGymStartScreen(onStartClicked: () -> Unit) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(1200)
            onStartClicked()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gymDarkBlue, gymMediumBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle animated background dots
        CleanBackgroundDots()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Animated weight plates
            CleanWeightPlates(isAnimating)

            Spacer(modifier = Modifier.height(40.dp))

            // App name with subtle pulse
            CleanAppName()

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle with typing effect
            CleanTypingSubtitle()

            Spacer(modifier = Modifier.height(60.dp))

            // Start button with weightlifting animation
            CleanGymStartButton(
                isAnimating = isAnimating,
                onClick = { isAnimating = true }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Stats counter
            CleanStatsCounter()
        }

        // Bottom motivational text
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "No Pain, No Gain",
                fontSize = 14.sp,
                color = gymAccentBlue,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun CleanBackgroundDots() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Create a simple grid of subtle dots
        val dotCountX = 15
        val dotCountY = 30

        val dotSpacingX = size.width / dotCountX
        val dotSpacingY = size.height / dotCountY

        for (x in 0..dotCountX) {
            for (y in 0..dotCountY) {
                val xPos = x * dotSpacingX
                val yPos = y * dotSpacingY

                // Very subtle dots
                val alpha = 0.03f

                drawCircle(
                    color = gymAccentBlue.copy(alpha = alpha),
                    center = Offset(xPos, yPos),
                    radius = 1.5.dp.toPx()
                )
            }
        }
    }
}

@Composable
fun CleanWeightPlates(isAnimating: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        )
    )

    val liftAnim by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .size(180.dp)
            .offset(y = (-15 * liftAnim).dp),
        contentAlignment = Alignment.Center
    ) {
        // Weight plates
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Draw 3 weight plates in a stack
            val plateSizes = listOf(70f, 60f, 50f)

            plateSizes.forEachIndexed { index, size ->
                drawCircle(
                    color = if (index == 0) gymSteelDark else if (index == 1) gymSteelMedium else gymAccentBlue,
                    center = center,
                    radius = size,
                    style = Stroke(width = 8f - (index * 2f))
                )
            }

            // Barbell through center
            drawRect(
                color = gymSubtitleGray,
                topLeft = Offset(center.x - 100f, center.y - 4f),
                size = Size(200f, 8f)
            )
        }

        // Rotating center icon
        Box(
            modifier = Modifier
                .size(70.dp)
                .rotate(rotation)
                .clip(CircleShape)
                .background(gymMediumBlue)
                .border(
                    width = 2.dp,
                    color = gymAccentBlue,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = "Fitness",
                tint = gymAccentBlue,
                modifier = Modifier.size(32.dp)
            )
        }

        // Animated rings when lifting
        if (isAnimating) {
            CleanAnimatedRings()
        }
    }
}

@Composable
fun CleanAnimatedRings() {
    val infiniteTransition = rememberInfiniteTransition()

    val ring1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        )
    )

    val ring2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 200),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Ring 1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = gymAccentBlue.copy(alpha = 1f - ring1),
                    shape = CircleShape
                )
                .scale(1f + ring1 * 0.5f)
        )

        // Ring 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = gymLightBlue.copy(alpha = 1f - ring2),
                    shape = CircleShape
                )
                .scale(1f + ring2 * 0.7f)
        )
    }
}

@Composable
fun CleanAppName() {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing)
        )
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "FITX",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = gymTextWhite,
            letterSpacing = 2.sp,
            modifier = Modifier.scale(pulse)
        )

        Text(
            text = "TRACK",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = gymAccentBlue,
            letterSpacing = 2.sp,
            modifier = Modifier.scale(pulse)
        )
    }
}

@Composable
fun CleanTypingSubtitle() {
    var displayedText by remember { mutableStateOf("") }
    val fullText = "LIFT • TRACK • PROGRESS"

    LaunchedEffect(Unit) {
        fullText.forEachIndexed { index, char ->
            displayedText += char
            delay(50)
        }
    }

    Text(
        text = displayedText,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = gymSubtitleGray,
        letterSpacing = 1.sp
    )
}

@Composable
fun CleanGymStartButton(isAnimating: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val buttonPulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing)
        )
    )

    val liftAnim by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .width(200.dp)
            .height(55.dp)
            .scale(buttonPulse)
            .offset(y = (-5 * liftAnim).dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        gymAccentBlue,
                        gymLightBlue,
                        gymAccentBlue
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "START WORKOUT",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            // Animated arrow when clicked
            AnimatedVisibility(
                visible = isAnimating,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally()
            ) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Start",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Shine effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            ),
                            blendMode = BlendMode.Overlay
                        )
                    }
                }
        )
    }
}

@Composable
fun CleanStatsCounter() {
    var repsCount by remember { mutableStateOf(0) }
    val targetReps = 1000000

    LaunchedEffect(Unit) {
        // Animate counting up
        while (repsCount < targetReps) {
            repsCount += 237
            if (repsCount > targetReps) repsCount = targetReps
            delay(20)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Reps counter
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${repsCount.formatted()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = gymAccentBlue
            )
            Text(
                text = "REPS TRACKED",
                fontSize = 10.sp,
                color = gymSubtitleGray,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .height(20.dp)
                .width(1.dp)
                .background(gymSubtitleGray.copy(alpha = 0.3f))
        )

        // Users counter
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "50K+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = gymAccentBlue
            )
            Text(
                text = "ACTIVE USERS",
                fontSize = 10.sp,
                color = gymSubtitleGray,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// NEW SIMPLE WELCOME SCREEN
@Composable
fun SimpleGymWelcomeScreen(onContinue: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
        delay(1500) // Short welcome screen
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gymDarkBlue, gymMediumBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + scaleIn(spring(dampingRatio = 0.7f))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Simple icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(gymAccentBlue.copy(alpha = 0.2f))
                        .border(
                            2.dp,
                            gymAccentBlue.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = "Welcome",
                        tint = gymAccentBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "Welcome",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = gymTextWhite
                )

                Text(
                    text = "Ready to transform?",
                    fontSize = 16.sp,
                    color = gymSubtitleGray,
                    fontWeight = FontWeight.Medium
                )

                // Simple loading dots
                SimpleLoadingDots()
            }
        }
    }
}

@Composable
fun SimpleLoadingDots() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition()
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, delayMillis = (index * 200L).toInt()),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(gymAccentBlue.copy(alpha = alpha))
            )
        }
    }
}

// Helper function to format numbers
fun Int.formatted(): String {
    return if (this >= 1000000) {
        "${this / 1000000}M+"
    } else if (this >= 1000) {
        "${this / 1000}K+"
    } else {
        this.toString()
    }
}

// Keep your existing IconTest and IconTestPreview functions at the bottom
@Composable
fun IconTest() {
    Icon(
        Icons.Default.FitnessCenter,
        contentDescription = "Test",
        modifier = Modifier.size(48.dp)
    )
}

@Preview
@Composable
fun IconTestPreview() {
    IconTest()
}
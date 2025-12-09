package com.yourorg.fitxtrackdemo

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.yourorg.fitxtrackdemo.manager.UserData
import com.yourorg.fitxtrackdemo.manager.UserManager
import com.yourorg.fitxtrackdemo.ui.screens.*
import com.yourorg.fitxtrackdemo.ui.theme.AuthScreen
import com.yourorg.fitxtrackdemo.ui.theme.FitXTrackDemoTheme
import com.yourorg.fitxtrackdemo.ui.theme.WorkoutViewModel
import kotlinx.coroutines.delay

// IMPORT ONLY MATERIAL 3
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.tooling.preview.Preview

// Color definitions
val deepNavy = Color(0xFF021024)
val darkBlue = Color(0xFF052659)
val tealBlue = Color(0xFF548383)
val lightBlue = Color(0xFF7DA0CA)
val skyBlue = Color(0xFFC1E8FF)
val offWhite = Color(0xFFF8FAFC)

class MainActivity : ComponentActivity() {
    private val fitnessViewModel: FitnessViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitXTrackDemoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(activity = this, fitnessViewModel = fitnessViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(activity: ComponentActivity, fitnessViewModel: FitnessViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start") {

        composable("start") {
            StartScreen(onStartClicked = { navController.navigate("welcome") })
        }

        composable("welcome") {
            WelcomeScreen(onContinue = { navController.navigate("home") })
        }

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
           SettingScreen(navController = navController)
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

@Composable
fun StartScreen(onStartClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FitXTrack",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = onStartClicked,
                modifier = Modifier.width(160.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = darkBlue
                )
            ) {
                Text(text = "Start", fontSize = 18.sp)
            }
        }
    }
}

// OPTION 1: FADE-IN ANIMATION (SIMPLE & ELEGANT)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Small delay before animation starts
        isVisible = true
        delay(1500)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(darkBlue, tealBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Logo/Icon
                AnimatedContent(
                    targetState = isVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) with
                                fadeOut(animationSpec = tween(500))
                    }
                ) { visible ->
                    if (visible) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = "Fitness Icon",
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Text with animation
                AnimatedContent(
                    targetState = isVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500, delayMillis = 300)) with
                                fadeOut(animationSpec = tween(500))
                    }
                ) { visible ->
                    if (visible) {
                        Text(
                            text = "Welcome",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle with animation
                AnimatedContent(
                    targetState = isVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500, delayMillis = 600)) with
                                fadeOut(animationSpec = tween(500))
                    }
                ) { visible ->
                    if (visible) {
                        Text(
                            text = "To Your Fitness Journey",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = skyBlue,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Loading indicator
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 1200))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                LoadingAnimation()
            }
        }
    }
}

@Composable
fun LoadingAnimation() {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimating = true
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val delay = index * 200L
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isAnimating) 1f else 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.3f at delay.toInt()
                        1f at (delay + 300).toInt()
                        0.3f at (delay + 600).toInt()
                    },
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(skyBlue.copy(alpha = animatedAlpha))
            )
        }
    }
}

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
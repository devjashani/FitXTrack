package com.yourorg.fitxtrackdemo.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yourorg.fitxtrackdemo.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextOverflow
import com.yourorg.fitxtrackdemo.manager.UserManager
import com.yourorg.fitxtrackdemo.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.yourorg.fitxtrackdemo.manager.HealthManagerHolder
import com.yourorg.fitxtrackdemo.manager.SimpleHealthManager
import com.yourorg.fitxtrackdemo.ui.viewmodels.WorkoutPlanViewModel
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex

// Using theme colors from Theme.kt
val deepNavy = FitnessDarkBlue      // #0A1931
val lightBlue = FitnessLightBlue    // #B3CFE5
val mediumBlue = FitnessMediumBlue  // #4A7FA7
val deepBlue = FitnessDeepBlue      // #1A3D63
val offWhite = FitnessOffWhite      // #F6FAFD

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    profileImageUrl: String? = null,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {
        // NAVIGATE TO SETTINGS
//        navController.navigate("settings")
    },
    onCalendarClick: () -> Unit,
    onProgressClick: () -> Unit
) {
    // ============ ADD DEBUG CODE HERE ============
    val context = LocalContext.current
    val healthManager = remember {
        HealthManagerHolder.getInstance(context)
    }

    // State to trigger recomposition when steps change
    val steps by remember { mutableStateOf(healthManager.currentSteps) }
    val calories by remember { mutableStateOf(healthManager.currentCalories) }
    val distance by remember { mutableStateOf(healthManager.currentDistance) }

    // STATE FOR SELECTED DATE
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // STATE FOR DISPLAYING SELECTED DATE DATA - FIXED VERSION
    var selectedDateData by remember(selectedDate) {
        mutableStateOf<Triple<Int, Int, Double>?>(null)
    }

    // Initialize selected date data
    LaunchedEffect(selectedDate) {
        selectedDateData = if (selectedDate == LocalDate.now()) {
            Triple(steps, calories, distance)
        } else {
            healthManager.getStepsForDate(selectedDate)
        }
    }

    // Update selected data when steps change (for today)
    LaunchedEffect(steps, calories, distance) {
        if (selectedDate == LocalDate.now()) {
            selectedDateData = Triple(steps, calories, distance)
        }
    }

    // Get display values safely
    val displaySteps = selectedDateData?.first ?: steps
    val displayCalories = selectedDateData?.second ?: calories
    val displayDistance = selectedDateData?.third ?: distance

    // Auto-update UI every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            // This will trigger recomposition with updated values
            with(healthManager) {
                // Accessing these values updates the mutable states
                healthManager.currentSteps
                healthManager.currentCalories
                healthManager.currentDistance

                // Always save today's data when it updates
                if (selectedDate == LocalDate.now()) {
                    healthManager.saveStepsForDate(
                        LocalDate.now().toString(),
                        healthManager.currentSteps,
                        healthManager.currentCalories,
                        healthManager.currentDistance
                    )
                }
            }
        }
    }

    // Update selected date data when date changes
    LaunchedEffect(selectedDate) {
        if (selectedDate == LocalDate.now()) {
            selectedDateData = Triple(steps, calories, distance)
        } else {
            // Get historical data for the selected date
            selectedDateData = healthManager.getStepsForDate(selectedDate)
        }
    }
    // Debug: Check sensor availability
    LaunchedEffect(Unit) {
        Log.d("STEP_TRACKING", "Step sensor available: ${healthManager.isStepSensorAvailable()}")
        Log.d("STEP_TRACKING", "Current steps: ${healthManager.currentSteps}")
        Log.d("STEP_TRACKING", "Current calories: ${healthManager.currentCalories}")
        Log.d("STEP_TRACKING", "Current distance: ${healthManager.currentDistance}")

        // Also save initial today's data
        healthManager.saveStepsForDate(
            LocalDate.now().toString(),
            healthManager.currentSteps,
            healthManager.currentCalories,
            healthManager.currentDistance
        )
    }
    // ============ END DEBUG CODE ============

    val auth = FirebaseAuth.getInstance()
    // Use LaunchedEffect to listen for auth state changes
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    // ADD THIS: Extract profile image URL
    val profileImageUrl = remember(currentUser) {
        currentUser?.photoUrl?.toString()?.let { url ->
            // Google photos need size parameter for better quality
            if (url.contains("googleusercontent.com")) {
                "$url?sz=400" // Add size parameter for higher resolution
            } else {
                url
            }
        }
    }

    LaunchedEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)

        // Don't forget to remove listener in onDispose if using LaunchedEffect
    }

    val isLoggedIn = currentUser != null
    val userName = currentUser?.displayName ?:
    currentUser?.email?.split("@")?.first() ?:
    "User"
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    val todayWorkout by workoutPlanViewModel.todayWorkout.collectAsState()
    val weeklyPlans by workoutPlanViewModel.weeklyPlansList.collectAsState() // ← CHANGED

    Box(modifier = modifier.fillMaxSize().background(offWhite)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with gradient background using new colors
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(deepBlue, mediumBlue)
                        )
                    )
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    TopRow(
                        navController = navController,  // ← ADD THIS LINE
                        isLoggedIn = isLoggedIn,
                        userName = userName,
                        profileImageUrl = profileImageUrl,
                        streakText = "🔥 5 Weeks",
                        leagueText = "🏆 Elite",
                        onProfileClick = onProfileClick,
                        onLoginClick = {
                            try {
                                navController.navigate("auth") {
                                    // Clear back stack if needed
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    // Prevent multiple instances
                                    launchSingleTop = true
                                    // Restore state when going back
                                    restoreState = true
                                }
                                Log.d("HomeScreen", "Navigating to auth screen")
                            } catch (e: Exception) {
                                Log.e("HomeScreen", "Navigation failed: ${e.message}", e)
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // UPDATED CALENDAR - Now connected to selectedDate
                    CalendarWeekUI(
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            selectedDate = date
                            Log.d("HomeScreen", "Calendar date clicked: $date")
                        }
                    )
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // TODAY'S WORKOUT Section
                Text(
                    text = if (selectedDate == LocalDate.now()) "TODAY'S WORKOUT" else "WORKOUT FOR ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = deepBlue.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

// ================ FIXED LOGIC - Show Today's Planned Workout ================
// Get today's workout from weekly plans
                val todayWorkoutPlan = remember(weeklyPlans, selectedDate) {
                    if (selectedDate == LocalDate.now()) {
                        // Find workout for today
                        weeklyPlans.find { plan ->
                            plan.dayOfWeek == LocalDate.now().dayOfWeek.value
                        }
                    } else {
                        // Find workout for selected historical date
                        weeklyPlans.find { plan ->
                            plan.dayOfWeek == selectedDate.dayOfWeek.value
                        }
                    }
                }


// Show appropriate card
                if (todayWorkoutPlan != null) {
                    PlannedWorkoutCard(
                        workout = todayWorkoutPlan,
                        modifier = Modifier.fillMaxWidth(),
                        onStartClick = {
                            // Navigate based on workout type using helper function
                            when {
                                todayWorkoutPlan.workoutName.contains("Push", ignoreCase = true) ->
                                    navController.navigate("pushday")
                                todayWorkoutPlan.workoutName.contains("Pull", ignoreCase = true) ->
                                    navController.navigate("pullDay")
                                todayWorkoutPlan.workoutName.contains("Leg", ignoreCase = true) ->
                                    navController.navigate("legDay")
                                todayWorkoutPlan.workoutName.contains("Full Body", ignoreCase = true) ->
                                    navController.navigate("fullBody")
                                todayWorkoutPlan.workoutName.contains("Arm", ignoreCase = true) ->
                                    navController.navigate("armsDay")
                                todayWorkoutPlan.workoutName.contains("Cardio", ignoreCase = true) ->
                                    navController.navigate("cardioWorkout")
                                todayWorkoutPlan.workoutName.contains("Yoga", ignoreCase = true) ->
                                    navController.navigate("yogaWorkout")
                                todayWorkoutPlan.workoutName.contains("HIIT", ignoreCase = true) ->
                                    navController.navigate("hiitWorkout")
                                else -> navController.navigate("customWorkout")
                            }
                        }
                    )
                } else if (selectedDate == LocalDate.now()) {
                    AddWorkoutCard(
                        modifier = Modifier.fillMaxWidth(),
                        onAddClick = {
                            navController.navigate("weeklyPlanner")
                        }
                    )
                } else {
                    // Show historical workout card or message
                    HistoricalWorkoutCard(
                        date = selectedDate,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // ================ END NEW ================

                // ================= Personal Training System =================
                @Composable
                fun PersonalTrainingCard(
                    modifier: Modifier = Modifier,
                    onClick: () -> Unit = {}
                ) {
                    Card(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(160.dp)  // Adjust height as needed
                            .clickable(onClick = onClick),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header with gradient
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                            colors = listOf(deepBlue, mediumBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "PERSONAL TRAINING",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "1-on-1 Coaching with Dev",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = lightBlue
                                            )
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = "Training",
                                            tint = deepBlue,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            // Content
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Custom Plans + Nutrition + Accountability",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = deepBlue
                                        ),
                                        maxLines = 2
                                    )
                                }
                                Button(
                                    onClick = onClick,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = deepBlue,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text("Learn More", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Today's Progress Section
                Text(
                    text = if (selectedDate == LocalDate.now()) {
                        "Today's Progress"
                    } else {
                        "Progress for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = deepBlue.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StepsCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp), // ← Add fixed height
                        stepsCount = displaySteps,
                        distanceKm = String.format("%.2f", displayDistance),
                        kcal = "$displayCalories",
                        healthManager = healthManager  // ✅ Pass the singleton
                    )
                    CaloriesCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp), // ← Same fixed height
                        calories = displayCalories,
                        healthManager = healthManager,  // ✅ Pass the singleton
                        goalText = if (selectedDate == LocalDate.now()) "Hit 600 calories" else "Daily Goal"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FITNESS TRACKING CARDS
                Text(
                    text = "Fitness Tracking",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = deepBlue.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Calendar Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onCalendarClick),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(lightBlue, mediumBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = "Calendar",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fitness Calendar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = deepBlue
                                )
                            )
                            Text(
                                text = "Track daily progress",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = mediumBlue.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }

                    // Progress Tracking Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onProgressClick),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(offWhite, lightBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = "Progress",
                                    tint = mediumBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Progress Analytics",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = deepBlue
                                )
                            )
                            Text(
                                text = "View statistics",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = mediumBlue.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }

                    // ================ NEW: Weekly Planner Card ================
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate("weeklyPlanner") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(lightBlue, mediumBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Weekly Plan",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Weekly Planner",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = deepBlue
                                )
                            )
                            Text(
                                text = "Plan your workouts",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = mediumBlue.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                    // ================ END NEW ================
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Trending Workout Section
                Text(
                    text = "Trending Workouts",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = deepBlue.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                TrendingWorkoutsRow(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(20.dp))

                // Workout History Section
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = deepBlue.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("workoutHistory") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(lightBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = "Analytics",
                                tint = mediumBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Workout History & Analytics",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = deepBlue
                                )
                            )
                            Text(
                                text = "View your progress and statistics",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = mediumBlue.copy(alpha = 0.6f)
                                )
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "View",
                            tint = mediumBlue
                        )
                    }
                }

                // ================ ADD PERSONAL TRAINING CARD HERE ================
                Spacer(modifier = Modifier.height(20.dp))

                // Personal Training Section
                Text(
                    text = "Personal Training",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = deepBlue.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                PersonalTrainingCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("personalTraining")
                    }
                )
// ================ END PERSONAL TRAINING CARD ================

                // Add extra space at the bottom for the navigation pill
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Bottom Navigation Pill - Positioned at bottom of screen
        BottomPillNav(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            selectedIndex = 0, // Home is index 0
            navController = navController
        )
    }
}
// ================ NEW COMPONENTS ADDED BELOW ================

@Composable
fun PlannedWorkoutCard(
    workout: com.yourorg.fitxtrackdemo.data.model.WorkoutPlan,
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(lightBlue, mediumBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = "workout thumb",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.workoutName.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = deepBlue
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = lightBlue.copy(alpha = 0.1f),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = "✨ Planned",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = mediumBlue
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${workout.duration} min • ${workout.difficulty}",
                        fontSize = 11.sp,
                        color = deepBlue.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mediumBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(140.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "START WORKOUT",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddWorkoutCard(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Workout",
                    tint = mediumBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PLAN YOUR WORKOUT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = deepBlue
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Add a workout plan for today",
                    fontSize = 12.sp,
                    color = deepBlue.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = deepBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "ADD PLAN",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ================ EXISTING CODE BELOW (NO CHANGES) ================

@Composable
private fun TopRow(
    navController: NavController,  // ← ADD THIS PARAMETER
    isLoggedIn: Boolean,
    userName: String,
    profileImageUrl: String?,
    streakText: String,
    leagueText: String,
    onProfileClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileImage(profileImageUrl = profileImageUrl, onClick = onProfileClick)
            Spacer(modifier = Modifier.width(12.dp))

            Column  {

                if (isLoggedIn && userName.isNotBlank()) {
                    Text(
                        text = "Hi, $userName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            color = lightBlue
                        )

                    )
                } else {
                    Text(
                        text = "Login / Signup",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Start your fitness journey",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            color = lightBlue
                        )

                    )
                }
            }
        }

        if (isLoggedIn) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.wrapContentWidth()  //
            ) {

                Chip(text = leagueText, color = lightBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Chip(text = streakText, color = mediumBlue)
            }
        } else {
            Button(
                onClick = {
                    Log.d("TopRow", "Login button clicked, isLoggedIn: $isLoggedIn")
                    try {
                        onLoginClick()
                    } catch (e: Exception) {
                        Log.e("TopRow", "Login button error: ${e.message}", e)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(36.dp)
                    .wrapContentWidth(), // add this

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = deepBlue
                )
            ) {
                Text("Login")
            }
        }
    }
}

@Composable
private fun ProfileImage(
    profileImageUrl: String?,
    sizeDp: Int = 48,
    onClick: () -> Unit
) {
    val modifier = Modifier
        .size(sizeDp.dp)
        .clip(CircleShape)
        .clickable(onClick = onClick)

    if (!profileImageUrl.isNullOrBlank()) {
        // User is logged in with Google profile image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profileImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Profile image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        // Not logged in - show Anonymous Avatar with Gradient
        Box(
            modifier = modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(lightBlue, mediumBlue)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,  // Changed from AccountCircle to Person
                contentDescription = "Login",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun Chip(text: String, color: Color = lightBlue) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f),
        modifier = Modifier
            .wrapContentWidth()
            .heightIn(min = 32.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                fontSize = 12.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarWeekUI(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val weekDates = remember(selectedDate) { getWeekDates(selectedDate) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weekDates) { date ->
                DayItem(
                    date = date,
                    isToday = date == today,
                    isSelected = date == selectedDate,
                    onDateClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayItem(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    onDateClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> Color.White
        isToday -> lightBlue.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> mediumBlue
        isToday -> Color.White
        else -> lightBlue
    }

    Column(
        modifier = Modifier
            .width(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onDateClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getWeekDates(centerDate: LocalDate): List<LocalDate> {
    // Get dates for a week centered around the selected date
    val startOfWeek = centerDate.minusDays((centerDate.dayOfWeek.value % 7).toLong())
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

@Composable
fun HistoricalWorkoutCard(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(lightBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Historical Data",
                    tint = mediumBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HISTORICAL DATA",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = deepBlue
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd")),
                    fontSize = 12.sp,
                    color = deepBlue.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Keep your existing PushWorkoutCard for reference or backup
@Composable
fun PushWorkoutCard(
    modifier: Modifier = Modifier,
    thumbnailRes: Int = R.drawable.ic_workout_placeholder,
    title: String = "Push Day - chest & triceps",
    planText: String = "✨ Plan",
    onStartClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(lightBlue, mediumBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = "workout thumb",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = deepBlue
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = lightBlue.copy(alpha = 0.1f),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = planText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = mediumBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mediumBlue,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(140.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = "START WORKOUT",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StepsCard(
    modifier: Modifier = Modifier,
    stepsCount: Int = 3246,
    distanceKm: String = "2.51 km",
    kcal: String = "123.4 kcal",
    healthManager: SimpleHealthManager,  // Add this parameter
    weekBars: List<Int> = listOf(30, 55, 70, 60, 80, 50, 65)
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DirectionsWalk,
                            contentDescription = "Steps",
                            tint = mediumBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STEPS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = mediumBlue
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        // Use passed steps count, not healthManager.currentSteps
                        text = "$stepsCount steps",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = deepBlue
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        // Use passed distance and calories
                        text = "$distanceKm km | $kcal kcal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = lightBlue
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "more",
                    modifier = Modifier.size(18.dp),
                    tint = lightBlue
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                weekBars.forEachIndexed { index, value ->
                    val height = (8 + (value / 100f * 36)).dp
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(height)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(lightBlue, mediumBlue)
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listOf("M", "T", "W", "T", "F", "S", "S")[index],
                            fontSize = 9.sp,
                            color = mediumBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaloriesCard(
    modifier: Modifier = Modifier,
    calories: Int = 120, // Default value, will be overridden
    healthManager: SimpleHealthManager,  // Add this parameter
    goalText: String = "Hit 600 calories"
) {
    val progress = calories / 600f

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = "Calories",
                    tint = lightBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CALORIES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = lightBlue
                    )
                )
            }

            // Simple version without circular progress
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(mediumBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        // Use passed calories, not healthManager.currentCalories
                        text = "$calories",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = deepBlue
                        )
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = mediumBlue
                        )
                    )
                }
            }

            Text(
                text = goalText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = mediumBlue
            )
        }
    }
}

@Composable
fun TrendingWorkoutsRow(
    modifier: Modifier = Modifier,
    items: List<Int> = listOf(R.drawable.ic_thumb1_placeholder, R.drawable.ic_thumb2_placeholder),
    onItemClick: (index: Int) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { res ->
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(100.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onItemClick(items.indexOf(res)) }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(res)
                            .crossfade(true)
                            .build(),
                        contentDescription = "training thumb",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        deepBlue.copy(alpha = 0.4f)
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun BottomPillNav(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelected: (index: Int) -> Unit = {},
    navController: NavController
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .height(68.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val navItems = listOf(
                NavItem(Icons.Default.Home, "Home"),
                NavItem(R.drawable.ic_fitness_centre, "Workout"),
                NavItem(R.drawable.ic_self_improve, "Meditation"),
                NavItem(Icons.Default.Settings, "Settings")
            )

            navItems.forEachIndexed { index, navItem ->
                val isSelected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onItemSelected(index)
                            // Also handle navigation directly
                            when (index) {
                                0 -> {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                1 -> {
                                    navController.navigate("workoutMain") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                2 -> {
                                    navController.navigate("meditation") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                3 -> {
                                    navController.navigate("settings") {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) mediumBlue else Color.Transparent
                            )
                    ) {
                        when (navItem.icon) {
                            is ImageVector -> {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label,
                                    tint = if (isSelected) Color.White else lightBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            is Int -> {
                                Icon(
                                    painter = painterResource(id = navItem.icon),
                                    contentDescription = navItem.label,
                                    tint = if (isSelected) Color.White else lightBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = navItem.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = if (isSelected) mediumBlue else lightBlue,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

data class NavItem(
    val icon: Any,
    val label: String
)
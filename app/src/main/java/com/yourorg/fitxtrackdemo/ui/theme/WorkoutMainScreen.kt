package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yourorg.fitxtrackdemo.ui.theme.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkoutMainScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel  // ADD THIS PARAMETER
) {
    val customPlans by workoutViewModel.customPlans.collectAsState()

    // Debug logging to track custom plans updates
    LaunchedEffect(customPlans) {
        println("DEBUG: Custom plans updated: ${customPlans.size}")
        customPlans.forEachIndexed { index, plan ->
            println("DEBUG: Plan $index: ${plan.name} with ${plan.exercises.size} exercises")
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { 3 })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Workout Plans",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Navigate to create custom workout
                        navController.navigate("createCustomPlan")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Workout")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("workout") // Navigate to exercise selection
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = "Start Workout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quick Start")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Workout Progress Summary
            WorkoutSummaryCard()

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row for different workout sections
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("My Plans", "Browse", "History").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // Horizontal Pager for tab content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> MyWorkoutPlans(navController, customPlans)
                    1 -> BrowseWorkouts(navController)
                    2 -> WorkoutHistory(navController)
                }
            }
        }
    }
}

@Composable
fun WorkoutSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem("This Week", "4", "Workouts")
            SummaryItem("Active", "12", "Days")
            SummaryItem("Calories", "2.4k", "Burned")
        }
    }
}

@Composable
fun SummaryItem(title: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MyWorkoutPlans(navController: NavController, customPlans: List<com.yourorg.fitxtrackdemo.data.CustomWorkoutPlan>) {
    val defaultWorkoutPlans = listOf(
        WorkoutPlan("Push Day - Prime", "Chest & Triceps", "45 min", Icons.Default.FitnessCenter, Color(0xFFFF6B6B)),
        WorkoutPlan("Push Day Elite", "Chest, Shoulder & Triceps", "50 min", Icons.Default.FitnessCenter, Color(0xFFFF8E6B)),
        WorkoutPlan("Pull Day", "Back & Biceps", "50 min", Icons.Default.ArrowUpward, Color(0xFF4ECDC4)),
        WorkoutPlan("Leg Day - Prime", "Legs & Shoulder", "55 min", Icons.Default.DirectionsRun, Color(0xFF45B7D1)),
        WorkoutPlan("Leg Day - Elite", "Legs & Core", "60 min", Icons.Default.DirectionsRun, Color(0xFF6BA8FF)),
        WorkoutPlan("Arms Day", "Biceps, Triceps & Forearm", "40 min", Icons.Default.FitnessCenter, Color(0xFFD16BFF)),
        WorkoutPlan("Abs & Core", "Core Strengthening", "30 min", Icons.Default.SelfImprovement, Color(0xFF96CEB4)),
        WorkoutPlan("Full Body", "All Muscle Groups", "55 min", Icons.Default.Sports, Color(0xFF4CAF50))
    )

    // Debug logging to see what's being rendered
    LaunchedEffect(customPlans) {
        println("DEBUG: MyWorkoutPlans - Rendering ${customPlans.size} custom plans")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Default workout plans
        items(defaultWorkoutPlans) { plan ->
            WorkoutPlanCard(plan = plan, onClick = {
                when (plan.title) {
                    "Push Day - Prime" -> navController.navigate("pushDay")
                    "Push Day Elite" -> navController.navigate("pushDayElite")
                    "Pull Day" -> navController.navigate("pullDay")
                    "Leg Day - Prime" -> navController.navigate("legDayPrime")
                    "Leg Day - Elite" -> navController.navigate("legDay")
                    "Arms Day" -> navController.navigate("armsDay")
                    "Abs & Core" -> navController.navigate("absCore")
                    "Full Body" -> navController.navigate("fullBody")
                }
            })
        }

        // Custom workout plans section
        if (customPlans.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Custom Plans",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(customPlans) { customPlan ->
                println("DEBUG: Rendering custom plan card: ${customPlan.name}")
                CustomWorkoutPlanCard(
                    plan = customPlan,
                    onClick = {
                        // Navigate to workout screen with custom exercises
                        navController.navigate("workoutDay/${customPlan.name}?exercises=${customPlan.exercises.joinToString(",")}")
                    }
                )
            }
        } else {
            // Show empty state when no custom plans
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = "No custom plans",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Custom Plans Yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Create your first custom workout plan to see it here",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Create Custom Plan Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Navigate to create custom plan screen
                        navController.navigate("createCustomPlan")
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create New", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Create Custom Plan",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CustomWorkoutPlanCard(
    plan: com.yourorg.fitxtrackdemo.data.CustomWorkoutPlan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .size(50.dp)
                    .background(Color(0xFF9C27B0).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = plan.name,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = plan.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "${plan.exercises.size} exercises",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.duration,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun BrowseWorkouts(navController: NavController) {
    val categories = listOf(
        WorkoutCategory("Strength", "Build muscle mass", Icons.Default.FitnessCenter, 12),
        WorkoutCategory("Cardio", "Improve endurance", Icons.Default.DirectionsRun, 8),
        WorkoutCategory("Flexibility", "Increase mobility", Icons.Default.SelfImprovement, 6),
        WorkoutCategory("HIIT", "High intensity training", Icons.Default.Speed, 10),
        WorkoutCategory("Yoga", "Mind-body connection", Icons.Default.Spa, 7),
        WorkoutCategory("Recovery", "Active recovery", Icons.Default.Healing, 5)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            WorkoutCategoryCard(category = category, onClick = {
                navController.navigate("workoutCategory/${category.name}")
            })
        }
    }
}

@Composable
fun WorkoutHistory(navController: NavController) {
    val workoutHistory = listOf(
        WorkoutHistoryItem("Push Day - Prime", "Mon, Dec 12", "45 min", "320 kcal"),
        WorkoutHistoryItem("Pull Day", "Wed, Dec 14", "50 min", "280 kcal"),
        WorkoutHistoryItem("Leg Day - Elite", "Fri, Dec 16", "60 min", "450 kcal"),
        WorkoutHistoryItem("Cardio", "Sun, Dec 18", "30 min", "220 kcal")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (workoutHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = "No workouts",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Workout History",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Complete your first workout to see history here",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(workoutHistory) { history ->
                WorkoutHistoryCard(history = history, onClick = {
                    // View workout details
                })
            }
        }
    }
}

// Data Classes
data class WorkoutPlan(
    val title: String,
    val description: String,
    val duration: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

data class WorkoutCategory(
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val workoutCount: Int
)

data class WorkoutHistoryItem(
    val workoutName: String,
    val date: String,
    val duration: String,
    val calories: String
)

// Card Components
@Composable
fun WorkoutPlanCard(plan: WorkoutPlan, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .size(50.dp)
                    .background(plan.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = plan.icon,
                    contentDescription = plan.title,
                    tint = plan.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = plan.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.duration,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Start",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun WorkoutCategoryCard(category: WorkoutCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "${category.workoutCount} workouts",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutHistoryCard(history: WorkoutHistoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.workoutName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = history.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = history.duration,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = history.calories,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
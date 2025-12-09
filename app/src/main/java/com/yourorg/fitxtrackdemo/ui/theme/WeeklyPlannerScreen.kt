package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourorg.fitxtrackdemo.data.model.WorkoutPlan
import com.yourorg.fitxtrackdemo.data.model.dayNames
import com.yourorg.fitxtrackdemo.data.model.workoutTypes
import com.yourorg.fitxtrackdemo.ui.theme.*
import com.yourorg.fitxtrackdemo.ui.viewmodels.WorkoutPlanViewModel
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlannerScreen(
    navController: NavController,
    viewModel: WorkoutPlanViewModel = viewModel()
) {
    val weeklyPlans by viewModel.weeklyPlans.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Weekly Workout Planner",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FitnessDarkBlue
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FitnessOffWhite)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Instruction Header
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FitnessLightBlue.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = FitnessDarkBlue
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Plan Your Week",
                                    fontWeight = FontWeight.Bold,
                                    color = FitnessDarkBlue,
                                    fontSize = 18.sp
                                )
                            }
                            Text(
                                "Set your workout for each day. Today's workout will appear on the home screen.",
                                color = FitnessDarkBlue.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Days List
                items(DayOfWeek.values().size) { index ->
                    val day = DayOfWeek.values()[index]
                    val plan = weeklyPlans[day]
                    val isToday = day == LocalDate.now().dayOfWeek

                    DayPlanCard(
                        day = day,
                        plan = plan,
                        isToday = isToday,
                        onSelectWorkout = { selectedWorkout ->
                            viewModel.setWorkoutForDay(day, selectedWorkout)
                        },
                        onRemove = {
                            viewModel.removeWorkoutForDay(day)
                        },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                // Save/Complete Button
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FitnessMediumBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Save Weekly Plan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlanCard(
    day: DayOfWeek,
    plan: WorkoutPlan?,
    isToday: Boolean,
    onSelectWorkout: (String) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWorkoutSelector by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) FitnessLightBlue.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Day Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            dayNames[day.name] ?: day.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = FitnessDarkBlue
                        )
                        if (isToday) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = FitnessMediumBlue
                            ) {
                                Text("TODAY", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }

                    if (plan != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            plan.workoutName,
                            fontSize = 14.sp,
                            color = FitnessMediumBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${plan.duration} min • ${plan.difficulty}",
                            fontSize = 12.sp,
                            color = FitnessDarkBlue.copy(alpha = 0.6f)
                        )
                    }
                }

                // Action Buttons
                if (plan != null) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = { showWorkoutSelector = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FitnessDarkBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Add", fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Workout Selection Dialog
    if (showWorkoutSelector) {
        AlertDialog(
            onDismissRequest = { showWorkoutSelector = false },
            title = {
                Text(
                    "Select Workout for ${dayNames[day.name] ?: day.name}",
                    fontWeight = FontWeight.Bold,
                    color = FitnessDarkBlue
                )
            },
            text = {
                LazyColumn {
                    items(workoutTypes.size) { index ->
                        val workout = workoutTypes[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSelectWorkout(workout)
                                    showWorkoutSelector = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = FitnessOffWhite
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            FitnessMediumBlue.copy(alpha = 0.2f),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.FitnessCenter,
                                        contentDescription = "Workout",
                                        tint = FitnessMediumBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    workout,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FitnessDarkBlue
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showWorkoutSelector = false }
                ) {
                    Text("Cancel", color = FitnessDarkBlue)
                }
            },
            containerColor = Color.White
        )
    }
}
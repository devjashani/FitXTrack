package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourorg.fitxtrackdemo.data.CustomWorkoutPlan
import com.yourorg.fitxtrackdemo.ui.theme.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomPlanScreen( navController: NavController,
                            workoutViewModel: WorkoutViewModel) {
    var planName by remember { mutableStateOf(TextFieldValue()) }
    var planDescription by remember { mutableStateOf(TextFieldValue()) }
    var selectedExercises by remember { mutableStateOf(setOf<String>()) }
    var showExerciseSelector by remember { mutableStateOf(false) }

    // Sample exercises for selection
    val allExercises = listOf(
        // Chest
        "Bench Press", "Incline Dumbbell Press", "Chest Fly", "Push Ups", "Decline Bench Press",
        // Back
        "Pull Ups", "Lat Pulldown", "Bent Over Row", "Deadlift", "T-Bar Row", "Seated Cable Row",
        // Shoulders
        "Overhead Press", "Lateral Raises", "Front Raises", "Shrugs", "Arnold Press", "Face Pulls",
        // Legs
        "Squats", "Lunges", "Leg Press", "Calf Raises", "Leg Extensions", "Leg Curls", "Hip Thrusts",
        // Arms
        "Bicep Curls", "Tricep Extensions", "Hammer Curls", "Dips", "Skull Crushers", "Preacher Curls",
        // Core
        "Planks", "Crunches", "Leg Raises", "Russian Twists", "Mountain Climbers", "Hanging Leg Raises",
        // Cardio
        "Running", "Cycling", "Jump Rope", "Burpees", "Kettlebell Swings"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Custom Plan",
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
                    IconButton(
                        onClick = {
                            if (planName.text.isNotBlank() && selectedExercises.isNotEmpty()) {
                                // Save the custom plan to ViewModel
                                val customPlan = CustomWorkoutPlan(
                                    name = planName.text,
                                    description = planDescription.text.ifBlank { "Custom workout plan" },
                                    exercises = selectedExercises.toList()
                                )
                                workoutViewModel.addCustomPlan(customPlan)
                                navController.popBackStack()
                            }
                        },
                        enabled = planName.text.isNotBlank() && selectedExercises.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save Plan")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Create Your Custom Workout Plan",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Plan Name *",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    OutlinedTextField(
                        value = planName,
                        onValueChange = { planName = it },
                        label = { Text("Enter plan name") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., My Custom Routine") },
                        singleLine = true,
                        isError = planName.text.isBlank() && planName.text.isNotEmpty()
                    )
                    if (planName.text.isBlank() && planName.text.isNotEmpty()) {
                        Text(
                            text = "Plan name is required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Plan Description (Optional)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    OutlinedTextField(
                        value = planDescription,
                        onValueChange = { planDescription = it },
                        label = { Text("Enter description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        placeholder = { Text("Describe your workout plan...") }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showExerciseSelector = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = "Select Exercises",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Select Exercises *",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "${selectedExercises.size} exercises selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            if (selectedExercises.isEmpty()) {
                                Text(
                                    text = "At least one exercise is required",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Text(
                            text = "Choose",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            if (selectedExercises.isNotEmpty()) {
                item {
                    Text(
                        text = "Selected Exercises (${selectedExercises.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                items(selectedExercises.toList()) { exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exercise,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Remove",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.clickable {
                                    selectedExercises = selectedExercises - exercise
                                }
                            )
                        }
                    }
                }
            }

            item {
                // Save button at the bottom for better UX
                Button(
                    onClick = {
                        if (planName.text.isNotBlank() && selectedExercises.isNotEmpty()) {
                            val customPlan = CustomWorkoutPlan(
                                name = planName.text,
                                description = planDescription.text.ifBlank { "Custom workout plan" },
                                exercises = selectedExercises.toList()
                            )
                            workoutViewModel.addCustomPlan(customPlan)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = planName.text.isNotBlank() && selectedExercises.isNotEmpty()
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Custom Plan")
                }
            }
        }
    }

    // Exercise Selector Dialog
    if (showExerciseSelector) {
        AlertDialog(
            onDismissRequest = { showExerciseSelector = false },
            title = {
                Text(
                    text = "Select Exercises",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = "Choose exercises for your custom plan:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Search/Filter functionality
                    var searchQuery by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search exercises") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    val filteredExercises = if (searchQuery.isBlank()) {
                        allExercises
                    } else {
                        allExercises.filter { it.contains(searchQuery, ignoreCase = true) }
                    }

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredExercises) { exercise ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedExercises = if (selectedExercises.contains(exercise)) {
                                            selectedExercises - exercise
                                        } else {
                                            selectedExercises + exercise
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (selectedExercises.contains(exercise))
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedExercises.contains(exercise)) {
                                        Text(
                                            text = "✓",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = exercise,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Selected exercises count
                    Text(
                        text = "Selected: ${selectedExercises.size} exercises",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExerciseSelector = false }
                ) {
                    Text("Done")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExerciseSelector = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourorg.fitxtrackdemo.ui.viewmodels.ProfileViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.alpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(navController: NavController) {
    // Get your existing ViewModel
    val viewModel: ProfileViewModel = viewModel()

    // Collect states from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Local snackbar state
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar for save status
    LaunchedEffect(saveStatus) {
        when (saveStatus) {
            ProfileViewModel.SaveStatus.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Profile saved successfully!",
                    duration = SnackbarDuration.Short
                )
                // Wait a bit then navigate back
                delay(1500)
                navController.popBackStack()
            }
            is ProfileViewModel.SaveStatus.Error -> {
                val errorMessage = (saveStatus as ProfileViewModel.SaveStatus.Error).message
                snackbarHostState.showSnackbar(
                    message = "Error: $errorMessage",
                    duration = SnackbarDuration.Long
                )
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Edit Profile",
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
                    when (saveStatus) {
                        ProfileViewModel.SaveStatus.Saving -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        else -> {
                            IconButton(
                                onClick = {
                                    viewModel.saveUserProfile()
                                }
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.saveUserProfile()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp),
//                enabled = saveStatus != ProfileViewModel.SaveStatus.Saving && !isLoading
            ) {
                when (saveStatus) {
                    ProfileViewModel.SaveStatus.Saving -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    else -> {
                        Icon(Icons.Default.Save, contentDescription = "Save Profile")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Show loading indicator on initial load
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Profile Header
                    ProfileHeaderSection()

                    Spacer(modifier = Modifier.height(24.dp))

                    // Personal Information Section
                    PersonalInfoSection(
                        userName = uiState.name,
                        onNameChange = { viewModel.updateField("name", it) },
                        age = uiState.age,
                        onAgeChange = { viewModel.updateField("age", it) },
                        selectedGender = uiState.gender,
                        onGenderSelected = { viewModel.updateField("gender", it) },
                        height = uiState.height,
                        onHeightChange = { viewModel.updateField("height", it) },
                        weight = uiState.weight,
                        onWeightChange = { viewModel.updateField("weight", it) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Fitness Information Section
                    FitnessInfoSection(
                        fitnessGoal = uiState.fitnessGoal,
                        onFitnessGoalSelected = { viewModel.updateField("fitnessGoal", it) },
                        experienceLevel = uiState.experienceLevel,
                        onExperienceLevelSelected = { viewModel.updateField("experienceLevel", it) }
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// REST OF YOUR COMPOSABLE FUNCTIONS REMAIN EXACTLY THE SAME
// ProfileHeaderSection, PersonalInfoSection, GenderDropdown, etc.
// [PASTE ALL YOUR EXISTING COMPOSABLE FUNCTIONS HERE WITHOUT CHANGES]

@Composable
fun ProfileHeaderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Update Your Profile",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Keep your information up to date for better workout recommendations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PersonalInfoSection(
    userName: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle("Personal Information", Icons.Default.Person)

        Spacer(modifier = Modifier.height(16.dp))

        // Name Field
        OutlinedTextField(
            value = userName,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Age and Gender Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Age Field
            OutlinedTextField(
                value = age,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                        onAgeChange(newValue)
                    }
                },
                label = { Text("Age") },
                leadingIcon = {
                    Icon(Icons.Default.Cake, contentDescription = "Age")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Gender Dropdown
            GenderDropdown(
                selectedGender = selectedGender,
                onGenderSelected = onGenderSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Height and Weight Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Height Field
            OutlinedTextField(
                value = height,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() || it == '.' }) {
                        onHeightChange(newValue)
                    }
                },
                label = { Text("Height (cm)") },
                leadingIcon = {
                    Icon(Icons.Default.Straighten, contentDescription = "Height")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("175") }
            )

            // Weight Field
            OutlinedTextField(
                value = weight,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() || it == '.' }) {
                        onWeightChange(newValue)
                    }
                },
                label = { Text("Weight (kg)") },
                leadingIcon = {
                    Icon(Icons.Default.MonitorWeight, contentDescription = "Weight")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("70") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val genders = listOf("Male", "Female", "Other", "Prefer not to say")

    Box(modifier = modifier) {
        // Use ExposedDropdownMenuBox instead
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedGender,
                onValueChange = {},
                label = { Text("Gender") },
                leadingIcon = {
                    Icon(Icons.Default.Face, contentDescription = "Gender")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genders.forEach { gender ->
                    DropdownMenuItem(
                        text = { Text(gender) },
                        onClick = {
                            onGenderSelected(gender)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FitnessInfoSection(
    fitnessGoal: String,
    onFitnessGoalSelected: (String) -> Unit,
    experienceLevel: String,
    onExperienceLevelSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitle("Fitness Information", Icons.Default.FitnessCenter)

        Spacer(modifier = Modifier.height(16.dp))

        // Fitness Goal Dropdown
        FitnessGoalDropdown(
            selectedGoal = fitnessGoal,
            onGoalSelected = onFitnessGoalSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Experience Level Dropdown
        ExperienceLevelDropdown(
            selectedLevel = experienceLevel,
            onLevelSelected = onExperienceLevelSelected,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessGoalDropdown(
    selectedGoal: String,
    onGoalSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val fitnessGoals = listOf(
        "Weight Loss",
        "Muscle Gain",
        "Strength Training",
        "Endurance",
        "General Fitness",
        "Body Toning"
    )

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedGoal,
                onValueChange = {},
                label = { Text("Fitness Goal") },
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = "Fitness Goal")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                fitnessGoals.forEach { goal ->
                    DropdownMenuItem(
                        text = { Text(goal) },
                        onClick = {
                            onGoalSelected(goal)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceLevelDropdown(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val experienceLevels = listOf(
        "Beginner",
        "Intermediate",
        "Advanced",
        "Professional"
    )

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLevel,
                onValueChange = {},
                label = { Text("Experience Level") },
                leadingIcon = {
                    Icon(Icons.Default.TrendingUp, contentDescription = "Experience Level")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                experienceLevels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            onLevelSelected(level)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
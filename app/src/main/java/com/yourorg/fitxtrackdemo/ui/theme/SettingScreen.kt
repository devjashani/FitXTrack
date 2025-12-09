package com.yourorg.fitxtrackdemo.ui.screens

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
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    // States for toggle switches
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var weeklyReportsEnabled by remember { mutableStateOf(true) }
    var soundEffectsEnabled by remember { mutableStateOf(true) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Profile Section
            item {
                SettingSection(title = "Profile") {
                    SettingItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Update your personal information",
                        onClick = { navController.navigate("editProfile") }
                    )
                    SettingItem(
                        icon = Icons.Default.FitnessCenter,
                        title = "Fitness Goals",
                        subtitle = "Set your workout targets",
                        onClick = { /* Navigate to fitness goals */ }
                    )
                    SettingItem(
                        icon = Icons.Default.MonitorWeight,
                        title = "Body Metrics",
                        subtitle = "Track your progress",
                        onClick = { /* Navigate to body metrics */ }
                    )
                }
            }

            // Preferences Section
            item {
                SettingSection(title = "Preferences") {
                    SettingToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Receive workout reminders",
                        isChecked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    SettingToggleItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Switch between themes",
                        isChecked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                    SettingToggleItem(
                        icon = Icons.Default.Assessment,
                        title = "Weekly Reports",
                        subtitle = "Get weekly progress updates",
                        isChecked = weeklyReportsEnabled,
                        onCheckedChange = { weeklyReportsEnabled = it }
                    )
                    SettingToggleItem(
                        icon = Icons.Default.VolumeUp,
                        title = "Sound Effects",
                        subtitle = "Enable workout sounds",
                        isChecked = soundEffectsEnabled,
                        onCheckedChange = { soundEffectsEnabled = it }
                    )
                    SettingToggleItem(
                        icon = Icons.Default.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Vibration during workouts",
                        isChecked = hapticFeedbackEnabled,
                        onCheckedChange = { hapticFeedbackEnabled = it }
                    )
                }
            }

            // Workout Settings Section
            item {
                SettingSection(title = "Workout Settings") {
                    SettingItem(
                        icon = Icons.Default.Timer,
                        title = "Rest Timer",
                        subtitle = "Set rest intervals between sets",
                        onClick = {  navController.navigate("editProfile") }
                    )
                    SettingItem(
                        icon = Icons.Default.Speed,
                        title = "Units",
                        subtitle = "kg/lb, km/miles",
                        onClick = { /* Navigate to units settings */ }
                    )
                    SettingItem(
                        icon = Icons.Default.Warning,
                        title = "Safety Features",
                        subtitle = "Spotter alerts and form correction",
                        onClick = { /* Navigate to safety settings */ }
                    )
                }
            }

            // Support Section
            item {
                SettingSection(title = "Support") {
                    SettingItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        subtitle = "Get help with the app",
                        onClick = { /* Navigate to help */ }
                    )
                    SettingItem(
                        icon = Icons.Default.Description,
                        title = "Terms & Privacy",
                        subtitle = "Legal information",
                        onClick = { /* Navigate to terms */ }
                    )
                    SettingItem(
                        icon = Icons.Default.Star,
                        title = "Rate App",
                        subtitle = "Share your experience",
                        onClick = { /* Open play store */ }
                    )
                    SettingItem(
                        icon = Icons.Default.Share,
                        title = "Share App",
                        subtitle = "Tell your friends",
                        onClick = { /* Share app */ }
                    )
                }
            }

            // Account Section
            item {
                SettingSection(title = "Account") {
                    SettingItem(
                        icon = Icons.Default.ExitToApp,
                        title = "Sign Out",
                        subtitle = "Log out from your account",
                        onClick = {
                            // Handle sign out
                            navController.navigate("auth") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        titleColor = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // App Version
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FitX Track v1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SettingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
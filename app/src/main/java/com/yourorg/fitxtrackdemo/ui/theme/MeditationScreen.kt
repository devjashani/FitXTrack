package com.yourorg.fitxtrackdemo.ui.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.yourorg.fitxtrackdemo.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(navController: NavController) {
    var currentTime by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf(300) } // 5 minutes default
    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.meditation_music).apply {
            isLooping = true
        }
    }

    // Timer effect
    LaunchedEffect(isPlaying, currentTime) {
        if (isPlaying && currentTime < selectedDuration) {
            delay(1000L)
            currentTime++
        } else if (currentTime >= selectedDuration) {
            isPlaying = false
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }

    // Cleanup media player
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    val meditationPurple = Color(0xFF667eea)
    val meditationDarkPurple = Color(0xFF764ba2)
    val lightPurple = Color(0xFFA78BFA)
    val whiteWithOpacity = Color.White.copy(alpha = 0.8f)

    Box(modifier = Modifier.fillMaxSize().background(offWhite)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(meditationPurple, meditationDarkPurple)
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "Meditation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }

            // Main Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Important: This makes space for bottom pill
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(meditationPurple, meditationDarkPurple)
                        )
                    )
            ) {
                // Background decorative elements
                BackgroundElements()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Meditation visualization
                    MeditationVisualization(isPlaying = isPlaying)

                    Spacer(modifier = Modifier.height(40.dp))

                    // Timer display
                    Text(
                        text = formatTime(currentTime),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 64.sp,
                            color = Color.White
                        )
                    )

                    Text(
                        text = formatTime(selectedDuration),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Main control button
                    MainControlButton(
                        isPlaying = isPlaying,
                        onPlayPause = {
                            isPlaying = !isPlaying
                            if (isPlaying) {
                                mediaPlayer.isLooping = true
                                mediaPlayer.start()
                            } else {
                                mediaPlayer.pause()
                            }
                        },
                        onReset = {
                            isPlaying = false
                            currentTime = 0
                            mediaPlayer.pause()
                            mediaPlayer.seekTo(0)
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Duration selector
                    DurationSelector(
                        selectedDuration = selectedDuration,
                        onDurationSelected = { duration ->
                            selectedDuration = duration
                            if (currentTime > duration) {
                                currentTime = 0
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Meditation sessions with bottom padding
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 80.dp) // Add bottom padding for pill
                    ) {
                        MeditationSessions()
                    }
                }
            }
        }

        // Meditation-themed Bottom Navigation Pill - UPDATED
        MeditationBottomPill(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            selectedIndex = 2, // Meditation is index 2
            navController = navController,
            backgroundColor = Color.White.copy(alpha = 0.15f), // Semi-transparent white
            selectedColor = Color.White, // White for selected item
            unselectedColor = Color.White.copy(alpha = 0.7f), // Semi-transparent white for unselected
            pillColor = meditationPurple // Purple background for selected pill
        )
    }
}

// Custom Meditation-themed Bottom Pill - NEW COMPONENT
@Composable
fun MeditationBottomPill(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    navController: NavController,
    backgroundColor: Color = Color.White.copy(alpha = 0.15f),
    selectedColor: Color = Color.White,
    unselectedColor: Color = Color.White.copy(alpha = 0.7f),
    pillColor: Color = Color(0xFF667eea)
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        shadowElevation = 4.dp,
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
                MeditationNavItem(Icons.Default.Home, "Home"),
                MeditationNavItem(R.drawable.ic_fitness_centre, "Workout"),
                MeditationNavItem(R.drawable.ic_self_improve, "Meditation"),
                MeditationNavItem(Icons.Default.Settings, "Settings")
            )

            navItems.forEachIndexed { index, navItem ->
                val isSelected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
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
                                    // Already on meditation screen
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
                                if (isSelected) pillColor else Color.Transparent
                            )
                    ) {
                        when (navItem.icon) {
                            is ImageVector -> {
                                Icon(
                                    imageVector = navItem.icon,
                                    contentDescription = navItem.label,
                                    tint = if (isSelected) selectedColor else unselectedColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            is Int -> {
                                Icon(
                                    painter = painterResource(id = navItem.icon),
                                    contentDescription = navItem.label,
                                    tint = if (isSelected) selectedColor else unselectedColor,
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
                        color = if (isSelected) selectedColor else unselectedColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

data class MeditationNavItem(
    val icon: Any,
    val label: String
)

@Composable
fun BackgroundElements() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Floating circles
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-30).dp, y = 100.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 300.dp, y = 400.dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun MeditationVisualization(isPlaying: Boolean) {
    val animatedSize by animateDpAsState(
        targetValue = if (isPlaying) 180.dp else 160.dp,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 2000,
            easing = androidx.compose.animation.core.LinearEasing
        )
    )

    Box(
        modifier = Modifier
            .size(animatedSize)
            .background(
                color = Color.White.copy(alpha = 0.15f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_lotus), // You'll need to add this drawable
            contentDescription = "Lotus flower",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Fit
        )

        // Pulsing circles when playing
        if (isPlaying) {
            repeat(3) { index ->
                val pulseSize by animateDpAsState(
                    targetValue = if (isPlaying) 240.dp + (index * 30).dp else 180.dp,
                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                        animation = androidx.compose.animation.core.tween(3000),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Restart
                    )
                )

                Box(
                    modifier = Modifier
                        .size(pulseSize)
                        .background(
                            color = Color.White.copy(alpha = 0.05f - (index * 0.015f)),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun MainControlButton(isPlaying: Boolean, onPlayPause: () -> Unit, onReset: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset button
        IconButton(
            onClick = onReset,
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(
                Icons.Default.Replay,
                contentDescription = "Reset",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Main play/pause button
        Button(
            onClick = onPlayPause,
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF667eea)
            )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(32.dp)
            )
        }

        // Stop button (placeholder for future features)
        IconButton(
            onClick = { /* Future: Stop and save session */ },
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Favorite",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DurationSelector(selectedDuration: Int, onDurationSelected: (Int) -> Unit) {
    val durations = listOf(
        DurationOption("5 min", 300),
        DurationOption("10 min", 600),
        DurationOption("15 min", 900),
        DurationOption("20 min", 1200),
        DurationOption("30 min", 1800)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Duration",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            durations.forEach { duration ->
                DurationChip(
                    duration = duration,
                    isSelected = selectedDuration == duration.seconds,
                    onSelected = { onDurationSelected(duration.seconds) }
                )
            }
        }
    }
}

@Composable
fun DurationChip(duration: DurationOption, isSelected: Boolean, onSelected: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelected() },
        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
        contentColor = if (isSelected) Color(0xFF667eea) else Color.White
    ) {
        Text(
            text = duration.label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun MeditationSessions() {
    val sessions = listOf(
        MeditationSession("Breathing Basics", "5 min", "Beginner"),
        MeditationSession("Stress Relief", "10 min", "Intermediate"),
        MeditationSession("Deep Relaxation", "15 min", "Advanced"),
        MeditationSession("Sleep Meditation", "20 min", "All Levels")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Guided Sessions",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp) // Add padding at bottom
        ) {
            items(sessions) { session ->
                MeditationSessionCard(session = session)
            }

            // Add extra space at bottom
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MeditationSessionCard(session: MeditationSession) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to guided session */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.SelfImprovement,
                    contentDescription = "Meditation",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "${session.duration} • ${session.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Helper functions and data classes
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

data class DurationOption(val label: String, val seconds: Int)
data class MeditationSession(val title: String, val duration: String, val level: String)
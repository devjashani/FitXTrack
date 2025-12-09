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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Meditation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
                .padding(paddingValues)
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

                // Meditation sessions
                MeditationSessions()
            }
        }
    }
}

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions) { session ->
                MeditationSessionCard(session = session)
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
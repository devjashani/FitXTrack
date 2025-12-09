/*
package com.yourorg.fitxtrackdemo.ui.screens

import android.os.Build
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import java.time.format.TextStyle
import java.util.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.yourorg.fitxtrackdemo.manager.UserManager


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    profileImageUrl: String? = null,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onCalendarClick: () -> Unit,
    onProgressClick: () -> Unit
) {
    val isLoggedIn by remember { mutableStateOf(UserManager.isUserLoggedIn()) }
    val userName by remember { mutableStateOf(UserManager.getUserName()) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TopRow(
                    isLoggedIn = isLoggedIn,
                    userName = userName,
                    profileImageUrl = profileImageUrl,
                    streakText = "🔥 5 Weeks",
                    leagueText = "💀 Elite",
                    onProfileClick = onProfileClick,
                    onLoginClick = {
                        navController.navigate("auth")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                CalendarWeekUI()
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // TODAY'S WORKOUT Section
                Text(
                    text = "TODAY'S WORKOUT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                PushWorkoutCard(
                    thumbnailRes = R.drawable.ic_workout_placeholder,
                    modifier = Modifier.fillMaxWidth(),
                    onStartClick = {
                        // Navigate to WorkoutScreen - this is passed correctly now
                        navController.navigate("workout")
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Today's Progress Section
                Text(
                    text = "Today's progress",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StepsCard(modifier = Modifier.weight(1f))
                    CaloriesCard(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FITNESS TRACKING CARDS - ADDED THIS SECTION
                Text(
                    text = "Fitness Tracking",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
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
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fitness Calendar",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Track daily progress",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Progress Tracking Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onProgressClick),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = "Progress",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Progress Analytics",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "View statistics",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Trending Workout Section
                Text(
                    text = "Trending workout",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                TrendingWorkoutsRow(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(20.dp))

                // Workout History Section
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("workoutHistory") },
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Workout History & Analytics",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "View your progress and statistics",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Icon(Icons.Default.TrendingUp, contentDescription = "Analytics", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                // Add extra space at the bottom for the navigation pill
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Bottom Navigation Pill - Positioned at bottom of screen
        BottomPillNav(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            navController = navController // Add this line
        )
    }
}

@Composable
private fun TopRow(
    isLoggedIn: Boolean,
    userName: String,
    profileImageUrl: String?,
    streakText: String,
    leagueText: String,
    onProfileClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileImage(profileImageUrl = profileImageUrl, onClick = onProfileClick)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                if (isLoggedIn && userName.isNotBlank()) {
                    // User is logged in - show their name
                    Text(
                        text = "Hi, $userName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else {
                    // User is not logged in - show login prompt
                    Text(
                        text = "Login / Signup",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Start your fitness journey",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Only show chips if user is logged in
        if (isLoggedIn) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Chip(text = leagueText)
                Spacer(modifier = Modifier.width(6.dp))
                Chip(text = streakText)
            }
        } else {
            // Show login button instead of chips
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Login")
            }
        }
    }
}

@Composable
private fun ProfileImage(profileImageUrl: String?, sizeDp: Int = 48, onClick: () -> Unit) {
    val modifier = Modifier
        .size(sizeDp.dp)
        .clip(CircleShape)
        .clickable(onClick = onClick)

    if (!profileImageUrl.isNullOrBlank()) {
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
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "D",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .wrapContentWidth()
            .heightIn(min = 32.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                fontSize = 12.sp
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarWeekUI() {
    val today = LocalDate.now()
    val weekDates = remember { getWeekDates(today) }

    // State to track the selected date
    var selectedDate by remember { mutableStateOf(today) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(weekDates) { date ->
            DayItem(
                date = date,
                isToday = date == today,
                isSelected = date == selectedDate,
                onDateClick = { selectedDate = date }
            )
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
    // Determine background and text colors based on selection state
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    Column(
        modifier = Modifier
            .width(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onDateClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            fontSize = 12.sp,
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
private fun getWeekDates(today: LocalDate): List<LocalDate> {
    val startOfWeek = today.minusDays((today.dayOfWeek.value % 7).toLong())
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

@Composable
fun PushWorkoutCard(
    modifier: Modifier = Modifier,
    thumbnailRes: Int = R.drawable.ic_workout_placeholder,
    title: String = "Push Day - chest & triceps",
    planText: String = "✨ Plan",
    onStartClick: () -> Unit = {} // This callback is passed from HomeScreen
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(thumbnailRes)
                        .crossfade(true)
                        .build(),
                    contentDescription = "workout thumb",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 1.dp
                ) {
                    Text(
                        text = planText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Start Workout Button - This calls the onStartClick passed from HomeScreen
            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(155.dp)
                    .height(36.dp)
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
fun StepsCard(modifier: Modifier = Modifier, stepsCount: Int = 3246, distanceKm: String = "2.51 km", kcal: String = "123.4 kcal", weekBars: List<Int> = listOf(30, 55, 70, 60, 80, 50, 65)) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STEPS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$stepsCount steps",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$distanceKm | $kcal",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "more",
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                weekBars.forEachIndexed { index, value ->
                    val height = (8 + (value / 100f * 40)).dp
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(height)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listOf("M", "T", "W", "T", "F", "S", "S")[index],
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaloriesCard(modifier: Modifier = Modifier, calories: Int = 120, goalText: String = "Hit 600 calories") {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🔥 CALORIES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
            Text(
                text = "$calories kcal",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("◔◑◕", fontSize = 14.sp)
            }
            Text(
                text = goalText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TrendingWorkoutsRow(modifier: Modifier = Modifier, items: List<Int> = listOf(R.drawable.ic_thumb1_placeholder, R.drawable.ic_thumb2_placeholder), onItemClick: (index: Int) -> Unit = {}) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { res ->
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
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
            }
        }
    }
}

@Composable
fun BottomPillNav(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelected: (index: Int) -> Unit = {},
    navController: NavController // Add navController parameter
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 12.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .height(76.dp)
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
                NavItem(Icons.Default.Settings, "Setting")
            )

            navItems.forEachIndexed { index, navItem ->
                val isSelected = index == selectedIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemSelected(index)
                            when (index) {
                                0 -> { */
/* Home - already here *//*
 }
                                1 -> navController.navigate("workoutMain") // Workout main screen
                                2 -> navController.navigate("meditation") // Meditation
                                3 -> navController.navigate("settings") // Settings
                            }
                        }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Animated icon container
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                        modifier = Modifier.size(36.dp),
                        tonalElevation = if (isSelected) 6.dp else 0.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (navItem.icon) {
                                is ImageVector -> {
                                    Icon(
                                        imageVector = navItem.icon,
                                        contentDescription = navItem.label,
                                        tint = if (isSelected) Color.White
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                is Int -> {
                                    Icon(
                                        painter = painterResource(id = navItem.icon),
                                        contentDescription = navItem.label,
                                        tint = if (isSelected) Color.White
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Premium text styling
                    Text(
                        text = navItem.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            fontSize = 11.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Data class for navigation items (add this outside the function)
data class NavItem(
    val icon: Any,
    val label: String
)*/

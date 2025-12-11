// ui/components/ProgressCards.kt
package com.yourorg.fitxtrackdemo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StepsProgressCard(
    steps: Int,
    goal: Int = 10000,
    distance: Double = 0.0,
    modifier: Modifier = Modifier
) {
    HealthProgressCard(
        title = "STEPS",
        value = steps,
        goal = goal,
        unit = "steps",
        progress = steps.toFloat() / goal,
        additionalInfo = "${String.format("%.2f", distance)} km",
        modifier = modifier
    )
}

@Composable
fun CaloriesProgressCard(
    calories: Int,
    goal: Int = 600,
    modifier: Modifier = Modifier
) {
    HealthProgressCard(
        title = "CALORIES",
        value = calories,
        goal = goal,
        unit = "kcal",
        progress = calories.toFloat() / goal,
        additionalInfo = "Goal: $goal kcal",
        modifier = modifier
    )
}

@Composable
fun HealthProgressCard(
    title: String,
    value: Int,
    goal: Int,
    unit: String,
    progress: Float,
    additionalInfo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = additionalInfo,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
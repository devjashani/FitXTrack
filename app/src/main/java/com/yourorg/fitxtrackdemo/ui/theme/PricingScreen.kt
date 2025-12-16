package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.yourorg.fitxtrackdemo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Training Packages",
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
                .background(offWhite),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Choose Your Plan",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = deepBlue
                        )
                    )
                    Text(
                        text = "Transform your fitness journey",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = mediumBlue
                        )
                    )
                }
            }

            // Basic Package
            item {
                PricingCard(
                    title = "BASIC",
                    subtitle = "For Beginners",
                    price = "₹999",
                    period = "per month",
                    color = mediumBlue,
                    features = listOf(
                        "✓ 2 Training Sessions/Month",
                        "✓ Basic Nutrition Plan",
                        "✓ WhatsApp Support",
                        "✓ Progress Tracking",
                        "✗ Custom Workout Plans",
                        "✗ Video Form Analysis"
                    ),
                    isPopular = false,
                    onSelect = {
                        // Handle package selection
                    }
                )
            }

            // Popular Package
            item {
                PricingCard(
                    title = "PRO",
                    subtitle = "Most Popular",
                    price = "₹2,499",
                    period = "per month",
                    color = deepBlue,
                    features = listOf(
                        "✓ 8 Training Sessions/Month",
                        "✓ Customized Nutrition Plan",
                        "✓ 24/7 WhatsApp Support",
                        "✓ Detailed Progress Analytics",
                        "✓ Personalized Workout Plans",
                        "✓ Weekly Form Check"
                    ),
                    isPopular = true,
                    onSelect = {
                        // Handle package selection
                    }
                )
            }

            // Premium Package
            item {
                PricingCard(
                    title = "PREMIUM",
                    subtitle = "Complete Transformation",
                    price = "₹4,999",
                    period = "per month",
                    color = Color(0xFFFF6B35), // Orange
                    features = listOf(
                        "✓ 16 Training Sessions/Month",
                        "✓ Advanced Nutrition Coaching",
                        "✓ Daily Check-ins & Support",
                        "✓ Comprehensive Analytics",
                        "✓ Customized Recovery Plans",
                        "✓ Video Analysis & Corrections",
                        "✓ Meal Planning & Recipes",
                        "✓ Priority Support"
                    ),
                    isPopular = false,
                    onSelect = {
                        // Handle package selection
                    }
                )
            }

            // Single Session
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "PAY-PER-SESSION",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = deepBlue
                                    )
                                )
                                Text(
                                    text = "Try before you commit",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = mediumBlue
                                    )
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "₹499",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = deepBlue
                                    )
                                )
                                Text(
                                    text = "per session",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = mediumBlue
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Book single session
                                navController.navigate("booking")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = mediumBlue.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = lightBlue.copy(alpha = 0.1f),
                                contentColor = mediumBlue
                            ),
                            border = null // Remove the border property
                        ) {
                            Text("Book Single Session")
                        }
                    }
                }
            }

            // FAQ Section
            item {
                FAQSection()
            }
        }
    }
}

@Composable
fun PricingCard(
    title: String,
    subtitle: String,
    price: String,
    period: String,
    color: Color,
    features: List<String>,
    isPopular: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = if (isPopular) CardDefaults.cardElevation(defaultElevation = 8.dp)
        else CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = if (isPopular) BorderStroke(
            width = 2.dp,
            color = color
        ) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Popular badge
            if (isPopular) {
                Card(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = color)
                ) {
                    Text(
                        text = "MOST POPULAR",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = mediumBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = deepBlue
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = period,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = mediumBlue
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (feature.startsWith("✓")) Icons.Default.CheckCircle
                            else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (feature.startsWith("✓")) mediumBlue else Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature.substring(2),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = deepBlue.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Select button
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = color,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isPopular) "CHOOSE THIS PLAN" else "SELECT PLAN",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FAQSection() {
    val faqs = listOf(
        "Q: Can I change my plan later?" to "A: Yes, you can upgrade or downgrade your plan anytime.",
        "Q: Do you offer refunds?" to "A: We offer a 7-day money-back guarantee for all plans.",
        "Q: How do the sessions work?" to "A: Sessions can be online via video call or in-person at the gym.",
        "Q: Is nutrition included?" to "A: Yes, all plans include personalized nutrition guidance."
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = deepBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                faqs.forEach { (question, answer) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = offWhite
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = deepBlue
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = answer,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = deepBlue.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.yourorg.fitxtrackdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yourorg.fitxtrackdemo.data.model.BookingType
import com.yourorg.fitxtrackdemo.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    // State for selected date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // State for selected time slot
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    // State for booking type
    var selectedBookingType by remember { mutableStateOf(BookingType.ONLINE) }

    // Generate time slots (9 AM to 8 PM, hourly)
    val timeSlots = remember {
        (9..20).map { hour ->
            String.format("%02d:00", hour)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Book a Session",
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // Handle booking submission
                    coroutineScope.launch {
                        // Implement booking logic here
                    }
                },
                containerColor = deepBlue,
                modifier = Modifier.padding(16.dp),
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Book")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book Now")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(offWhite),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar Section
            item {
                BookingCalendarSection(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            // Booking Type Selection
            item {
                BookingTypeSection(
                    selectedType = selectedBookingType,
                    onTypeSelected = { selectedBookingType = it }
                )
            }

            // Time Slots
            item {
                TimeSlotsSection(
                    timeSlots = timeSlots,
                    selectedSlot = selectedTimeSlot,
                    onSlotSelected = { selectedTimeSlot = it }
                )
            }

            // Booking Details
            item {
                BookingDetailsSection(
                    selectedDate = selectedDate,
                    selectedTime = selectedTimeSlot,
                    bookingType = selectedBookingType
                )
            }
        }
    }
}

@Composable
fun BookingCalendarSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Select Date",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = deepBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simple date selector (you can implement a proper calendar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Today
                DateChip(
                    date = LocalDate.now(),
                    isSelected = selectedDate == LocalDate.now(),
                    onClick = { onDateSelected(LocalDate.now()) }
                )

                // Tomorrow
                DateChip(
                    date = LocalDate.now().plusDays(1),
                    isSelected = selectedDate == LocalDate.now().plusDays(1),
                    onClick = { onDateSelected(LocalDate.now().plusDays(1)) }
                )

                // Day after tomorrow
                DateChip(
                    date = LocalDate.now().plusDays(2),
                    isSelected = selectedDate == LocalDate.now().plusDays(2),
                    onClick = { onDateSelected(LocalDate.now().plusDays(2)) }
                )
            }
        }
    }
}

@Composable
fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEE\nd MMM")

    // Remove weight and use equal width distribution
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) mediumBlue else lightBlue.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.format(formatter),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else deepBlue
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun BookingTypeSection(
    selectedType: BookingType,
    onTypeSelected: (BookingType) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Session Type",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = deepBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BookingTypeCard(
                    type = BookingType.ONLINE,
                    title = "Online",
                    subtitle = "Video Call",
                    icon = Icons.Default.VideoCall,
                    isSelected = selectedType == BookingType.ONLINE,
                    onClick = { onTypeSelected(BookingType.ONLINE) }
                )

                BookingTypeCard(
                    type = BookingType.IN_PERSON,
                    title = "In-Person",
                    subtitle = "At Gym",
                    icon = Icons.Default.FitnessCenter,
                    isSelected = selectedType == BookingType.IN_PERSON,
                    onClick = { onTypeSelected(BookingType.IN_PERSON) }
                )

                BookingTypeCard(
                    type = BookingType.PHONE_CONSULTATION,
                    title = "Phone",
                    subtitle = "Consultation",
                    icon = Icons.Default.Call,
                    isSelected = selectedType == BookingType.PHONE_CONSULTATION,
                    onClick = { onTypeSelected(BookingType.PHONE_CONSULTATION) }
                )
            }
        }
    }
}

@Composable
fun TimeSlotsSection(
    timeSlots: List<String>,
    selectedSlot: String?,
    onSlotSelected: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Available Time Slots",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = deepBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time slots grid
            val columns = 3
            val rows = timeSlots.chunked(columns)

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rows.forEach { rowSlots ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { time ->
                            TimeSlotChip(
                                time = time,
                                isSelected = selectedSlot == time,
                                onClick = { onSlotSelected(time) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingTypeCard(
    type: BookingType,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) mediumBlue else lightBlue.copy(alpha = 0.1f),
        tonalElevation = if (isSelected) 2.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else mediumBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else deepBlue
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else mediumBlue
                )
            )
        }
    }
}

@Composable
fun BookingDetailsSection(
    selectedDate: LocalDate,
    selectedTime: String?,
    bookingType: BookingType
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Booking Details",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = deepBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Booking summary
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(label = "Date", value = selectedDate.toString())

                DetailRow(
                    label = "Time",
                    value = selectedTime ?: "Not selected"
                )

                DetailRow(
                    label = "Session Type",
                    value = when (bookingType) {
                        BookingType.ONLINE -> "Online (Video Call)"
                        BookingType.IN_PERSON -> "In-Person at Gym"
                        BookingType.PHONE_CONSULTATION -> "Phone Consultation"
                    }
                )

                DetailRow(
                    label = "Duration",
                    value = "60 minutes"
                )

                DetailRow(
                    label = "Price",
                    value = when (bookingType) {
                        BookingType.ONLINE -> "₹999"
                        BookingType.IN_PERSON -> "₹1,499"
                        BookingType.PHONE_CONSULTATION -> "₹499"
                    },
                    isHighlighted = true
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = mediumBlue
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlighted) deepBlue else deepBlue.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()  // Changed from weight to fillMaxWidth
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) deepBlue else lightBlue.copy(alpha = 0.1f),
        tonalElevation = if (isSelected) 2.dp else 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else deepBlue
                )
            )
        }
    }
}
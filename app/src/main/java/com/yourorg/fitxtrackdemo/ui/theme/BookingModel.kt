package com.yourorg.fitxtrackdemo.data.model

import com.google.firebase.Timestamp
import java.util.*

data class BookingSlot(
    val id: String = "",
    val date: Date = Date(),
    val startTime: String = "", // Format: "09:00", "14:30"
    val endTime: String = "",
    val duration: Int = 60, // minutes
    val type: BookingType = BookingType.ONLINE,
    val isBooked: Boolean = false,
    val bookedBy: String? = null, // User ID
    val bookedAt: Date? = null
)

enum class BookingType {
    ONLINE, IN_PERSON, PHONE_CONSULTATION
}

data class Booking(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val slotId: String = "",
    val date: Date = Date(),
    val startTime: String = "",
    val endTime: String = "",
    val type: BookingType = BookingType.ONLINE,
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class BookingStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}
// data/models/HealthData.kt
package com.yourorg.fitxtrackdemo.data.models

import java.time.LocalDate

// REMOVE Room annotations - Use simple data classes
data class HealthData(
    val date: String,  // Format: "2024-01-15"
    val steps: Int = 0,
    val calories: Int = 0,
    val distance: Double = 0.0,
    val stepsGoal: Int = 10000,
    val caloriesGoal: Int = 600,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromLocalDate(date: LocalDate): HealthData {
            return HealthData(date = date.toString())
        }

        fun getCurrentDateString(): String {
            return LocalDate.now().toString()
        }
    }
}

data class DailyProgress(
    val date: LocalDate = LocalDate.now(),
    val steps: Int = 0,
    val calories: Int = 0,
    val distance: Double = 0.0,
    val stepsProgress: Float = 0f,
    val caloriesProgress: Float = 0f
)

// Simple in-memory storage (or use SharedPreferences)
object HealthDataStorage {
    private val healthDataMap = mutableMapOf<String, HealthData>()

    fun saveData(data: HealthData) {
        healthDataMap[data.date] = data
    }

    fun getData(date: String): HealthData? {
        return healthDataMap[date]
    }

    fun getAllData(): List<HealthData> {
        return healthDataMap.values.toList()
    }

    fun getWeeklyData(): List<HealthData> {
        val today = LocalDate.now()
        val weeklyData = mutableListOf<HealthData>()

        // Get last 7 days
        for (i in 0..6) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.toString()
            weeklyData.add(healthDataMap[dateStr] ?: HealthData(date = dateStr))
        }

        return weeklyData
    }
}
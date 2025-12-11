// data/repository/HealthRepository.kt
package com.yourorg.fitxtrackdemo.data.repository

import com.yourorg.fitxtrackdemo.data.models.HealthData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HealthRepository {
    suspend fun saveHealthData(healthData: HealthData)
    suspend fun getHealthData(date: String): HealthData?
    fun getHealthDataFlow(date: String): Flow<HealthData?>
    suspend fun getWeeklyData(startDate: String, endDate: String): List<HealthData>
    suspend fun getTodayData(): HealthData
}
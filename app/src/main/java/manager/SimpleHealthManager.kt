// manager/SimpleHealthManager.kt
package com.yourorg.fitxtrackdemo.manager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.ZoneId

class SimpleHealthManager(context: Context) : SensorEventListener {

    // Store context as class property
    private val appContext: Context = context.applicationContext
    private val sensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = null
    private var totalSteps = 0
    private var lastTotalSteps = 0

    // Observable state
    private val _currentSteps = mutableStateOf(0)
    var currentSteps: Int by _currentSteps
        private set

    private val _currentCalories = mutableStateOf(0)
    var currentCalories: Int by _currentCalories
        private set

    private val _currentDistance = mutableStateOf(0.0)
    var currentDistance: Double by _currentDistance
        private set

    init {
        setupStepCounter()
        loadSavedSteps()
    }

    private fun setupStepCounter() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                totalSteps = it.values[0].toInt()

                if (lastTotalSteps == 0) {
                    lastTotalSteps = totalSteps
                }

                val todaySteps = totalSteps - lastTotalSteps
                currentSteps = todaySteps

                // Calculate calories (approx 0.04 calories per step)
                currentCalories = (todaySteps * 0.04).toInt()

                // Calculate distance in km (average step length ~0.762 meters)
                currentDistance = (todaySteps * 0.762) / 1000

                // Save to SharedPreferences
                saveTodayData(todaySteps, currentCalories, currentDistance)
            }
        }
    }

    private fun saveTodayData(steps: Int, calories: Int, distance: Double) {
        val sharedPref = appContext.getSharedPreferences("health_tracking", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("today_steps", steps)
            putInt("today_calories", calories)
            putFloat("today_distance", distance.toFloat())
            putLong("last_update", System.currentTimeMillis())
            apply()
        }
    }

    private fun loadSavedSteps() {
        val sharedPref = appContext.getSharedPreferences("health_tracking", Context.MODE_PRIVATE)

        // Check if data is from today
        val lastUpdate = sharedPref.getLong("last_update", 0)
        val today = System.currentTimeMillis()
        val isSameDay = isSameDay(lastUpdate, today)

        if (isSameDay) {
            currentSteps = sharedPref.getInt("today_steps", 0)
            currentCalories = sharedPref.getInt("today_calories", 0)
            currentDistance = sharedPref.getFloat("today_distance", 0f).toDouble()
        } else {
            // Reset for new day
            resetForNewDay()
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        if (time1 == 0L) return false

        val date1 = Instant.ofEpochMilli(time1).atZone(ZoneId.systemDefault()).toLocalDate()
        val date2 = Instant.ofEpochMilli(time2).atZone(ZoneId.systemDefault()).toLocalDate()
        return date1 == date2
    }

    private fun resetForNewDay() {
        // Update lastTotalSteps so new steps start counting from current sensor total
        lastTotalSteps = totalSteps

        // Reset current values
        currentSteps = 0
        currentCalories = 0
        currentDistance = 0.0

        // Save the reset values
        saveTodayData(0, 0, 0.0)
    }

    // ============== CALENDAR CONNECTION FUNCTIONS ==============
    // These must be INSIDE the class to access appContext

    fun saveStepsForDate(date: String, steps: Int, calories: Int, distance: Double) {
        val sharedPref = appContext.getSharedPreferences("health_history", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("steps_$date", steps)
            putInt("calories_$date", calories)
            putFloat("distance_$date", distance.toFloat())
            apply()
        }
    }

    fun getStepsForDate(date: String): Triple<Int, Int, Double> {
        val sharedPref = appContext.getSharedPreferences("health_history", Context.MODE_PRIVATE)
        return Triple(
            sharedPref.getInt("steps_$date", 0),
            sharedPref.getInt("calories_$date", 0),
            sharedPref.getFloat("distance_$date", 0f).toDouble()
        )
    }

    fun getWeeklyData(): Map<String, Triple<Int, Int, Double>> {
        val weeklyData = mutableMapOf<String, Triple<Int, Int, Double>>()
        val today = java.time.LocalDate.now()

        // Get last 7 days
        for (i in 0..6) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.toString()
            weeklyData[dateStr] = getStepsForDate(dateStr)
        }

        return weeklyData
    }
    // ============== END CALENDAR FUNCTIONS ==============

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }
}
// manager/HealthManager.kt
package com.yourorg.fitxtrackdemo.manager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yourorg.fitxtrackdemo.data.models.HealthData
import java.time.LocalDate
import kotlin.math.roundToInt

class HealthManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = null
    private var lastSteps = 0
    private var currentSteps = 0

    private val _steps = MutableLiveData(0)
    val steps: LiveData<Int> = _steps

    private val _calories = MutableLiveData(0)
    val calories: LiveData<Int> = _calories

    private val _distance = MutableLiveData(0.0)
    val distance: LiveData<Double> = _distance

    init {
        initializeSensor()
    }

    private fun initializeSensor() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()

                if (lastSteps == 0) {
                    lastSteps = totalSteps
                }

                currentSteps = totalSteps - lastSteps
                _steps.postValue(currentSteps)

                // Calculate calories (approx 0.04 calories per step)
                val calculatedCalories = (currentSteps * 0.04).roundToInt()
                _calories.postValue(calculatedCalories)

                // Calculate distance in km (average step length ~0.762 meters)
                val calculatedDistance = (currentSteps * 0.762) / 1000
                _distance.postValue(calculatedDistance)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    fun getTodayHealthData(): HealthData {
        return HealthData(
            date = LocalDate.now().toString(),
            steps = _steps.value ?: 0,
            calories = _calories.value ?: 0,
            distance = _distance.value ?: 0.0
        )
    }

    fun resetSteps() {
        lastSteps = 0
        currentSteps = 0
        _steps.postValue(0)
        _calories.postValue(0)
        _distance.postValue(0.0)
    }

    fun destroy() {
        sensorManager.unregisterListener(this)
    }
}
package com.yourorg.fitxtrackdemo.ui.theme

import androidx.lifecycle.ViewModel
import com.yourorg.fitxtrackdemo.data.CustomWorkoutPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutViewModel : ViewModel() {
    private val _customPlans = MutableStateFlow<List<CustomWorkoutPlan>>(emptyList())
    val customPlans: StateFlow<List<CustomWorkoutPlan>> = _customPlans.asStateFlow()

    fun addCustomPlan(plan: CustomWorkoutPlan) {
        val currentPlans = _customPlans.value.toMutableList()
        val newPlan = plan.copy(id = System.currentTimeMillis().toString())
        currentPlans.add(0, newPlan) // Add to beginning
        _customPlans.value = currentPlans
    }

    fun deleteCustomPlan(planId: String) {
        val currentPlans = _customPlans.value.toMutableList()
        currentPlans.removeAll { it.id == planId }
        _customPlans.value = currentPlans
    }
}
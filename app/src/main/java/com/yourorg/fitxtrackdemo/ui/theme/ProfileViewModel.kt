package com.yourorg.fitxtrackdemo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourorg.fitxtrackdemo.data.model.UserProfile
import com.yourorg.fitxtrackdemo.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = UserProfileRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getUserProfile()
                result.onSuccess { userProfile ->
                    userProfile?.let {
                        _uiState.value = ProfileUiState(
                            name = it.name,
                            age = it.age?.toString() ?: "",
                            gender = it.gender,
                            height = it.height?.toString() ?: "",
                            weight = it.weight?.toString() ?: "",
                            fitnessGoal = it.fitnessGoal,
                            experienceLevel = it.experienceLevel
                        )
                    }
                }.onFailure {
                    // You can handle error here if needed
                    println("Error loading profile: ${it.message}")
                }
            } catch (e: Exception) {
                println("Exception loading profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            _saveStatus.value = SaveStatus.Saving
            try {
                val userProfile = createUserProfileFromState()
                val result = repository.saveUserProfile(userProfile)

                result.onSuccess {
                    _saveStatus.value = SaveStatus.Success
                    // Reset save status after 2 seconds
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        _saveStatus.value = SaveStatus.Idle
                    }
                }.onFailure { exception ->
                    _saveStatus.value = SaveStatus.Error(exception.message ?: "Save failed")
                }
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun updateField(field: String, value: String) {
        val currentState = _uiState.value
        _uiState.value = when (field) {
            "name" -> currentState.copy(name = value)
            "age" -> currentState.copy(age = value)
            "gender" -> currentState.copy(gender = value)
            "height" -> currentState.copy(height = value)
            "weight" -> currentState.copy(weight = value)
            "fitnessGoal" -> currentState.copy(fitnessGoal = value)
            "experienceLevel" -> currentState.copy(experienceLevel = value)
            else -> currentState
        }
    }

    private fun createUserProfileFromState(): UserProfile {
        val state = _uiState.value
        return UserProfile(
            name = state.name,
            age = state.age.toIntOrNull(),
            gender = state.gender,
            height = state.height.toDoubleOrNull(),
            weight = state.weight.toDoubleOrNull(),
            fitnessGoal = state.fitnessGoal,
            experienceLevel = state.experienceLevel
        )
    }

    fun clearSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }

    // MOVE THE SAVESTATUS SEALED CLASS INSIDE THE PROFILEVIEWMODEL CLASS
    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Saving : SaveStatus()
        object Success : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }
}

// KEEP THIS OUTSIDE - it's fine
data class ProfileUiState(
    val name: String = "",
    val age: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val fitnessGoal: String = "",
    val experienceLevel: String = ""
)
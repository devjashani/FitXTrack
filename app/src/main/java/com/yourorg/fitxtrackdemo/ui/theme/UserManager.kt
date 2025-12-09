package com.yourorg.fitxtrackdemo.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserManager {
    var currentUser by mutableStateOf(UserData())
        private set

    fun login(user: UserData) {
        currentUser = user.copy(isLoggedIn = true)
    }

    fun logout() {
        currentUser = UserData()
    }

    fun updateUserProfile(updatedUser: UserData) {
        currentUser = updatedUser
    }

    fun isUserLoggedIn(): Boolean {
        return currentUser.isLoggedIn
    }

    fun getUserName(): String {
        return if (currentUser.isLoggedIn && currentUser.name.isNotBlank()) {
            currentUser.name
        } else {
            ""
        }
    }
}

data class UserData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val fitnessGoal: String = "",
    val experienceLevel: String = "",
    val isLoggedIn: Boolean = false
)
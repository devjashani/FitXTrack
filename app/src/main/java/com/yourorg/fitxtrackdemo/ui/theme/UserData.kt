package com.yourorg.fitxtrackdemo.data

data class User(
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
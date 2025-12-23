package com.yourorg.fitxtrackdemo.data

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",  // ← ADD THIS LINE
    val age: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val fitnessGoal: String = "",
    val experienceLevel: String = "",
    val isLoggedIn: Boolean = false
)
{
    // Optional: Add helper functions
    fun hasPhoneLogin(): Boolean = phone.isNotEmpty()

    fun getUserIdentifier(): String {
        return when {
            name.isNotEmpty() -> name
            email.isNotEmpty() -> email.split("@")[0]
            phone.isNotEmpty() -> "User ${phone.takeLast(4)}"
            else -> "User"
        }
    }
}

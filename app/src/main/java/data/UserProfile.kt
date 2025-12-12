package com.yourorg.fitxtrackdemo.data.model

import com.google.firebase.firestore.PropertyName
import java.util.*

data class UserProfile(
    @get:PropertyName("userId") @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("age") @set:PropertyName("age")
    var age: Int? = null,

    @get:PropertyName("gender") @set:PropertyName("gender")
    var gender: String = "",

    @get:PropertyName("height") @set:PropertyName("height")
    var height: Double? = null, // in cm

    @get:PropertyName("weight") @set:PropertyName("weight")
    var weight: Double? = null, // in kg

    @get:PropertyName("fitnessGoal") @set:PropertyName("fitnessGoal")
    var fitnessGoal: String = "",

    @get:PropertyName("experienceLevel") @set:PropertyName("experienceLevel")
    var experienceLevel: String = "",

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Date = Date()
)
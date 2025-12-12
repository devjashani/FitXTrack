package com.yourorg.fitxtrackdemo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yourorg.fitxtrackdemo.data.model.UserProfile
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class UserProfileRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            // Ensure userId matches current user
            val profileWithUserId = userProfile.copy(userId = currentUser.uid)

            firestore.collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .set(profileWithUserId)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): Result<UserProfile?> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val document = firestore.collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .get()
                .await()

            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                Result.success(userProfile)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(updates: Map<String, Any>): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val updatesWithTimestamp = updates.toMutableMap().apply {
                this["updatedAt"] = Date()
            }

            firestore.collection(USERS_COLLECTION)
                .document(currentUser.uid)
                .update(updatesWithTimestamp)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
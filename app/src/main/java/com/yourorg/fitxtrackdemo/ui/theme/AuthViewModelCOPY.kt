/*
package com.yourorg.fitxtrackdemo.ui.theme

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.yourorg.fitxtrackdemo.manager.UserManager
import com.yourorg.fitxtrackdemo.manager.UserData

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var isSignedIn by mutableStateOf(auth.currentUser != null)
    var statusMessage by mutableStateOf("")

    // ADD THIS ↓↓↓
    var currentUserName by mutableStateOf(auth.currentUser?.displayName)
        private set
    // ------------------------

    fun getGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("371869560280-i8ffme9vd7o0rr8h0qjcg34gi6l43sd6.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>, onSignedIn: () -> Unit) {
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    isSignedIn = true

                    // UPDATE NAME HERE ↓↓↓
                    currentUserName = auth.currentUser?.displayName

                    // ✅ ADDED: Save user to UserManager
                    val userName = auth.currentUser?.displayName ?:
                    auth.currentUser?.email?.split("@")?.get(0) ?: "User"

                    UserManager.login(
                        UserData(
                            name = userName,
                            email = auth.currentUser?.email ?: "",
                            isLoggedIn = true
                        )
                    )

                    statusMessage = "Signed in as ${currentUserName ?: "User"}"
                    onSignedIn()
                } else {
                    statusMessage = "Sign-in failed: ${it.exception?.message}"
                }
            }
        } catch (e: Exception) {
            statusMessage = "Error: ${e.message}"
        }
    }

    fun startPhoneSignIn() {
        statusMessage = "Phone Sign-In not yet implemented."
    }

    fun signOut(googleClient: GoogleSignInClient) {

        auth.signOut()

        // ✅ ADDED: Logout from UserManager
        UserManager.logout()

        googleClient.signOut().addOnCompleteListener {
            isSignedIn = false

            // CLEAR NAME HERE ↓↓↓
            currentUserName = null

            statusMessage = "Signed out"
        }
    }
}*/

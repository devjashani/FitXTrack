package com.yourorg.fitxtrackdemo.ui.theme

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var isSignedIn by mutableStateOf(auth.currentUser != null)
    var statusMessage by mutableStateOf("")
    var currentUserName by mutableStateOf(auth.currentUser?.displayName)
        private set

    // Phone Auth States
    var showPhoneAuthUI by mutableStateOf(false)
    var phoneNumber by mutableStateOf("")
    var verificationId by mutableStateOf<String?>(null)
    var otpCode by mutableStateOf("")
    var showOTPField by mutableStateOf(false)
    var isVerifying by mutableStateOf(false)
    var countdownTimer by mutableStateOf(60)

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    // Google Sign-In methods (keep existing)...
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
                    currentUserName = auth.currentUser?.displayName

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

    // ============= PHONE AUTHENTICATION METHODS =============

    fun startPhoneSignIn() {
        showPhoneAuthUI = true
        statusMessage = "Enter your phone number with country code (e.g., +919876543210)"
    }

    fun sendVerificationCode(activity: Activity) {
        if (phoneNumber.isEmpty() || !phoneNumber.startsWith("+")) {
            statusMessage = "Please enter a valid phone number with country code"
            return
        }

        isVerifying = true
        statusMessage = "Sending verification code..."

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval or instant verification
                    isVerifying = false
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isVerifying = false
                    statusMessage = "Verification failed: ${e.message}"
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    isVerifying = false
                    storedVerificationId = verificationId
                    resendToken = token
                    showOTPField = true
                    countdownTimer = 60
                    startCountdownTimer()
                    statusMessage = "OTP sent to $phoneNumber"
                }
            })

        if (resendToken != null) {
            options.setForceResendingToken(resendToken!!).build()
            PhoneAuthProvider.verifyPhoneNumber(options.build())
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options.build())
        }
    }

    fun verifyOTP() {
        if (otpCode.isEmpty() || otpCode.length != 6) {
            statusMessage = "Please enter 6-digit OTP"
            return
        }

        storedVerificationId?.let { verificationId ->
            isVerifying = true
            statusMessage = "Verifying OTP..."

            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            signInWithPhoneAuthCredential(credential)
        } ?: run {
            statusMessage = "Error: No verification ID found. Please request OTP again."
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                isVerifying = false
                if (task.isSuccessful) {
                    isSignedIn = true
                    val user = auth.currentUser

                    // Generate a username from phone number
                    val phone = user?.phoneNumber ?: phoneNumber
                    val userName = "User ${phone.takeLast(4)}"

                    // Save to UserManager
                    UserManager.login(
                        UserData(
                            name = userName,
                            email = "",
                            phone = phone,
                            isLoggedIn = true
                        )
                    )

                    showPhoneAuthUI = false
                    showOTPField = false
                    otpCode = ""
                    phoneNumber = ""
                    statusMessage = "Successfully signed in with phone!"
                } else {
                    statusMessage = "Verification failed: ${task.exception?.message}"
                }
            }
    }

    private fun startCountdownTimer() {
        // Simple countdown implementation
        // In production, use a proper coroutine timer
        Thread {
            repeat(60) {
                Thread.sleep(1000)
                countdownTimer = 60 - (it + 1)
            }
        }.start()
    }

    fun resendOTP(activity: Activity) {
        if (phoneNumber.isNotEmpty()) {
            countdownTimer = 60
            sendVerificationCode(activity)
        }
    }

    fun cancelPhoneAuth() {
        showPhoneAuthUI = false
        showOTPField = false
        phoneNumber = ""
        otpCode = ""
        statusMessage = ""
    }

    fun signOut(googleClient: GoogleSignInClient) {
        auth.signOut()
        UserManager.logout()

        googleClient.signOut().addOnCompleteListener {
            isSignedIn = false
            currentUserName = null
            statusMessage = "Signed out"
        }
    }
}
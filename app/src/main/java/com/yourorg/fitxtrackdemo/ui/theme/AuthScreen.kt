package com.yourorg.fitxtrackdemo.ui.theme

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.yourorg.fitxtrackdemo.manager.UserData
import com.yourorg.fitxtrackdemo.manager.UserManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onSignedIn: () -> Unit,
    activity: Activity,
    viewModel: AuthViewModel = viewModel()
) {
    // Google Sign-in launcher (keep existing)...
    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                viewModel.handleGoogleSignInResult(task) {
                    val userName = if (FirebaseAuth.getInstance().currentUser?.displayName != null) {
                        FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
                    } else {
                        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) ?: "User"
                    }

                    UserManager.login(
                        UserData(
                            name = userName,
                            email = FirebaseAuth.getInstance().currentUser?.email ?: "",
                            isLoggedIn = true
                        )
                    )

                    onSignedIn()
                }
            } catch (e: ApiException) {
                viewModel.statusMessage = "Google Sign-in failed: ${e.message}"
            }
        }

    // Show Phone Auth UI when needed
    if (viewModel.showPhoneAuthUI) {
        PhoneAuthUI(viewModel, activity, onSignedIn)
    } else {
        // Regular Auth UI
        if (viewModel.isSignedIn) {
            SignedInUI(viewModel, activity)
        } else {
            SignInUI(viewModel, activity, googleSignInLauncher)
        }
    }
}

@Composable
fun PhoneAuthUI(
    viewModel: AuthViewModel,
    activity: Activity,
    onSignedIn: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.cancelPhoneAuth() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Phone Verification",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Phone number input (always visible)
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.phoneNumber = it },
            label = { Text("Phone Number with country code") },
            placeholder = { Text("+919876543210") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isVerifying && !viewModel.showOTPField
        )

        Spacer(modifier = Modifier.height(16.dp))

        // OTP input (only shown after sending verification)
        if (viewModel.showOTPField) {
            OutlinedTextField(
                value = viewModel.otpCode,
                onValueChange = { viewModel.otpCode = it },
                label = { Text("6-digit OTP") },
                placeholder = { Text("123456") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Resend OTP timer
            if (viewModel.countdownTimer > 0) {
                Text(
                    "Resend OTP in ${viewModel.countdownTimer}s",
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                TextButton(onClick = { viewModel.resendOTP(activity) }) {
                    Text("Resend OTP")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verify OTP Button
            Button(
                onClick = { viewModel.verifyOTP() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isVerifying && viewModel.otpCode.length == 6
            ) {
                if (viewModel.isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Verify OTP")
                }
            }
        } else {
            // Send OTP Button
            Button(
                onClick = { viewModel.sendVerificationCode(activity) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isVerifying && viewModel.phoneNumber.isNotEmpty()
            ) {
                if (viewModel.isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Send Verification Code")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status message
        Text(
            text = viewModel.statusMessage,
            color = if (viewModel.statusMessage.contains("Success") ||
                viewModel.statusMessage.contains("sent")) {
                MaterialTheme.colorScheme.primary
            } else if (viewModel.statusMessage.contains("failed") ||
                viewModel.statusMessage.contains("Error")) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        // Auto-navigate after successful sign-in
        LaunchedEffect(viewModel.isSignedIn) {
            if (viewModel.isSignedIn) {
                onSignedIn()
            }
        }
    }
}

@Composable
fun SignedInUI(viewModel: AuthViewModel, activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome!", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(10.dp))
        Text(
            text = FirebaseAuth.getInstance().currentUser?.displayName ?:
            FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "User",
            fontSize = 18.sp
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val googleClient = viewModel.getGoogleSignInClient(activity)
                viewModel.signOut(googleClient)
                Toast.makeText(activity, "Successfully Signed Out!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign out")
        }

        Spacer(Modifier.height(16.dp))
        Text(viewModel.statusMessage)
    }
}

@Composable
fun SignInUI(
    viewModel: AuthViewModel,
    activity: Activity,
    googleSignInLauncher: ActivityResultLauncher<Intent>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign in", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        // Google Button
        Button(
            onClick = {
                val signInClient = viewModel.getGoogleSignInClient(activity)
                googleSignInLauncher.launch(signInClient.signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }

        Spacer(Modifier.height(12.dp))

        // Phone Sign-In
        Button(
            onClick = { viewModel.startPhoneSignIn() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Phone")
        }

        Spacer(Modifier.height(16.dp))
        Text(viewModel.statusMessage)
    }
}
package com.yourorg.fitxtrackdemo.ui.theme

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.yourorg.fitxtrackdemo.manager.UserManager
import com.yourorg.fitxtrackdemo.manager.UserData

@Composable
fun AuthScreen(
    onSignedIn: () -> Unit,
    activity: Activity,
    viewModel: AuthViewModel = viewModel()
) {
    // GOOGLE SIGN-IN RESULT HANDLER
    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                viewModel.handleGoogleSignInResult(task) {
                    // Extract username from Firebase user or use email
                    val userName = if (FirebaseAuth.getInstance().currentUser?.displayName != null) {
                        FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
                    } else {
                        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0) ?: "User"
                    }

                    // Save user to UserManager
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

    // UI CONTENT
    if (viewModel.isSignedIn) {

        // =============== SIGNED-IN UI ===================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome!", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))
            Text(FirebaseAuth.getInstance().currentUser?.displayName ?: "", fontSize = 18.sp)

            Spacer(Modifier.height(24.dp))

            // SIGN OUT BUTTON
            Button(
                onClick = {
                    val googleClient = viewModel.getGoogleSignInClient(activity)
                    viewModel.signOut(googleClient)
                    // Also logout from UserManager
                    UserManager.logout()
                    Toast.makeText(activity, "Successfully Signed Out!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign out")
            }

            Spacer(Modifier.height(16.dp))
            Text(viewModel.statusMessage)
        }

    } else {

        // ================ SIGN-IN UI ====================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sign in", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // GOOGLE BUTTON
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

            // PHONE SIGN-IN
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
}
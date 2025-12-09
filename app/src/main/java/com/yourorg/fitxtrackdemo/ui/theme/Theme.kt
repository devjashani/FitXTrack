package com.yourorg.fitxtrackdemo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ==== YOUR COLOR PALETTE ====
// From the image: 0A1931, B3CFE5, 4A7FA7, 1A3D63, F6FAFD
val FitnessDarkBlue = Color(0xFF0A1931)    // #0A1931 - Primary dark blue
val FitnessLightBlue = Color(0xFFB3CFE5)   // #B3CFE5 - Light accent blue
val FitnessMediumBlue = Color(0xFF4A7FA7)  // #4A7FA7 - Medium blue
val FitnessDeepBlue = Color(0xFF1A3D63)    // #1A3D63 - Deep blue
val FitnessOffWhite = Color(0xFFF6FAFD)    // #F6FAFD - Off-white background

// ==== LEGACY COLORS (For compatibility) ====
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ==== COLOR SCHEMES ====
private val DarkColorScheme = darkColorScheme(
    primary = FitnessLightBlue,
    secondary = FitnessMediumBlue,
    tertiary = FitnessDeepBlue,
    background = FitnessDarkBlue,
    surface = FitnessDarkBlue,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = FitnessOffWhite,
    onSurface = FitnessOffWhite,
    primaryContainer = FitnessDeepBlue,
    secondaryContainer = FitnessMediumBlue,
)

private val LightColorScheme = lightColorScheme(
    primary = FitnessDarkBlue,
    secondary = FitnessMediumBlue,
    tertiary = FitnessDeepBlue,
    background = FitnessOffWhite,
    surface = FitnessOffWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = FitnessDarkBlue,
    onSurface = FitnessDarkBlue,
    primaryContainer = FitnessLightBlue,
    secondaryContainer = FitnessMediumBlue.copy(alpha = 0.1f),
)

@Composable
fun FitXTrackDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to use your custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
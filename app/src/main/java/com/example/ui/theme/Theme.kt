package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = TealSecondary,
    onSecondary = Color.White,
    background = SeniorBackground,
    surface = SeniorSurface,
    onBackground = SeniorTextPrimary,
    onSurface = SeniorTextPrimary,
    error = SeniorRedAlert,
    onError = Color.White,
    errorContainer = SeniorRedContainer,
    onErrorContainer = OnSeniorRedContainer
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = TealSecondary,
    onPrimary = Color.Black,
    primaryContainer = OnTealContainer,
    onPrimaryContainer = TealContainer,
    secondary = TealSecondary,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFF87171)
  )

@Composable
fun SeniorMedicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent high contrast for senior readability
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}


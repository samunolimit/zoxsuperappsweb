package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = ZoxOrangeAccent,
  onPrimary = Color.Black,
  primaryContainer = ZoxPurplePrimary,
  onPrimaryContainer = Color.White,
  secondary = ZoxPurpleLight,
  onSecondary = Color.White,
  secondaryContainer = ZoxPurpleContainer,
  onSecondaryContainer = ZoxPurpleLight,
  tertiary = ZoxOrangeLight,
  onTertiary = Color.Black,
  background = ZoxDarkBackground,
  onBackground = TextPrimaryDark,
  surface = ZoxDarkSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = ZoxDarkSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  outline = Color(0xFF434358),
  error = ZoxError,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = ZoxPurplePrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEDE7F6),
  onPrimaryContainer = ZoxPurpleDark,
  secondary = ZoxOrangeAccent,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFFFFF3E0),
  onSecondaryContainer = ZoxOrangeDark,
  tertiary = Color(0xFF00897B),
  onTertiary = Color.White,
  background = ZoxLightBackground,
  onBackground = TextPrimaryLight,
  surface = ZoxLightSurface,
  onSurface = TextPrimaryLight,
  surfaceVariant = ZoxLightCard,
  onSurfaceVariant = TextSecondaryLight,
  outline = Color(0xFFD0D0E0),
  error = ZoxError,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to ZOX Luxury Dark Theme for premium super app feel
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.background.toArgb()
        window.navigationBarColor = colorScheme.surface.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

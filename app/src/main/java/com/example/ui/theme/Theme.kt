package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AngolaGold,
    onPrimary = Color(0xFF1E1E1E),
    primaryContainer = AngolaRedDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF60A5FA),
    onSecondary = Color.Black,
    tertiary = VerifiedGreen,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDarkCard,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = EmergencyRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = PrimaryBlue,
    secondary = AngolaRed,
    onSecondary = Color.White,
    tertiary = AngolaGoldDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = EmergencyRed
)

@Composable
fun TudoEmUmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

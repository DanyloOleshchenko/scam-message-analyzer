package com.example.scammessageanalyzer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Mist300,
    onPrimary = Night900,
    primaryContainer = Ink700,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFE1B58D),
    onSecondary = Night900,
    secondaryContainer = Color(0xFF5A3D26),
    onSecondaryContainer = Color(0xFFFFEBDD),
    tertiary = Color(0xFF9BC8C0),
    onTertiary = Night900,
    tertiaryContainer = Color(0xFF23413E),
    onTertiaryContainer = Color(0xFFE2F4EF),
    background = Night900,
    onBackground = Color(0xFFF3ECE3),
    surface = Night800,
    onSurface = Color(0xFFF4EFE8),
    surfaceVariant = Night700,
    onSurfaceVariant = Color(0xFFCEC1B1),
    outline = Color(0xFF8D8173),
    outlineVariant = Color(0xFF4A5757),
    error = Color(0xFFFFB4A5),
    onError = Color(0xFF5E170D),
    errorContainer = Color(0xFF8D2D20),
    onErrorContainer = Color(0xFFFFDAD2)
)

private val LightColorScheme = lightColorScheme(
    primary = Ink900,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9ECE7),
    onPrimaryContainer = Color(0xFF0C2321),
    secondary = Copper600,
    onSecondary = Color.White,
    secondaryContainer = Copper100,
    onSecondaryContainer = Color(0xFF3A2414),
    tertiary = Ink500,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD5E7E2),
    onTertiaryContainer = Color(0xFF163431),
    background = Sand50,
    onBackground = Color(0xFF1F2625),
    surface = Sand100,
    onSurface = Color(0xFF1D2423),
    surfaceVariant = Sand200,
    onSurfaceVariant = Stone600,
    outline = Color(0xFFB6A896),
    outlineVariant = Sand300,
    error = HighRiskAccent,
    onError = Color.White,
    errorContainer = HighRiskContainer,
    onErrorContainer = Color(0xFF5F190F)
)

@Composable
fun ScamMessageAnalyzerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && darkTheme -> DarkColorScheme
        dynamicColor && !darkTheme -> LightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

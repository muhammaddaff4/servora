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

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlueLight,
    onPrimary = Color.White,
    primaryContainer = SlateDark,
    onPrimaryContainer = CyanAccent,
    secondary = CyanAccent,
    onSecondary = DeepNavy,
    secondaryContainer = SlateBorderDark,
    onSecondaryContainer = Color.White,
    tertiary = EmeraldTrust,
    onTertiary = Color.White,
    background = DeepNavy,
    onBackground = TextOnDark,
    surface = CharcoalDark,
    onSurface = TextOnDark,
    surfaceVariant = SlateDark,
    onSurfaceVariant = TextSubtle,
    outline = SlateBorderDark,
    error = EmergencyRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = ElectricBlueDark,
    secondary = CharcoalDark,
    onSecondary = Color.White,
    secondaryContainer = SurfaceMuted,
    onSecondaryContainer = TextPrimary,
    tertiary = EmeraldTrust,
    onTertiary = Color.White,
    background = SurfaceCanvas,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = SlateBorder,
    error = EmergencyRed,
    onError = Color.White
)

@Composable
fun ServoraTheme(
    darkTheme: Boolean = false, // Default to pristine light/dark balance as requested
    dynamicColor: Boolean = false, // Keep branded palette consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

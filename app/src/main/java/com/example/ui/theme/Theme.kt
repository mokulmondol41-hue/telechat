package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepBluePrimary,
    onPrimary = White,
    primaryContainer = LightBlueBg,
    onPrimaryContainer = DeepBlueDark,
    secondary = DeepBlueDark,
    onSecondary = White,
    secondaryContainer = LightBlueCard,
    onSecondaryContainer = TextNavyDark,
    tertiary = DeepBlueLight,
    onTertiary = White,
    background = AppCanvasBg,
    onBackground = TextNavyDark,
    surface = SurfaceCard,
    onSurface = TextNavyDark,
    surfaceVariant = LightBlueBg,
    onSurfaceVariant = TextNavyMuted,
    outline = SurfaceCardBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = ErrorRed,
    onError = White,
    errorContainer = ErrorRedLight,
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = DeepBlueLight,
    onPrimary = White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = DeepBluePrimary,
    onSecondary = White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFF1F5F9),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF64748B),
    error = ErrorRed,
    onError = White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

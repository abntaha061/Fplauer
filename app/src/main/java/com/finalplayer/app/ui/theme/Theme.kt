package com.finalplayer.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MpvGreenPrimary,
    secondary = MpvGreenSecondary,
    tertiary = MpvGreenDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkBackground,
    onBackground = OnDarkTextPrimary,
    onSurface = OnDarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = MpvGreenSecondary,
    secondary = MpvGreenPrimary,
    tertiary = MpvGreenDark,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant
)

@Composable
fun FinalPlayerTheme(
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

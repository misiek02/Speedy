package com.speedy.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.speedy.app.core.settings.AppAccentColor
import com.speedy.app.core.settings.AppSettings

@Composable
fun SpeedyTheme(
    accentColor: AppAccentColor = AppSettings.accentColor.value,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = accentColor.primary,
        secondary = accentColor.secondary,
        tertiary = accentColor.glow,
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkCardMinimal,
        outline = DarkCardBorder,
        outlineVariant = Color.White.copy(alpha = 0.05f),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onTertiary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

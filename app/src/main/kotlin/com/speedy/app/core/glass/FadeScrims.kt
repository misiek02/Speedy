package com.speedy.app.core.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.pow

/**
 * 16-Stop Non-Blur Bottom Fade Scrim.
 *
 * Implements cubic easing (f(t) = t^2.2) to prevent visible color banding
 * on mobile OLED panels while avoiding expensive full-viewport blurs.
 */
@Composable
fun BottomFadeScrim(
    modifier: Modifier = Modifier,
    height: Dp = 110.dp,
    baseColor: Color = MaterialTheme.colorScheme.background
) {
    val gradientBrush = remember(baseColor) {
        val stops = 16
        val colors = Array(stops) { i ->
            val t = i.toFloat() / (stops - 1)
            // Cubic easing exponent 2.2 for smooth perceptual gradient
            val alpha = t.toDouble().pow(2.2).toFloat()
            baseColor.copy(alpha = alpha)
        }
        Brush.verticalGradient(colors = colors.toList())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(gradientBrush)
    )
}

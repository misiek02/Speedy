package com.speedy.app.core.glass

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Optical parameters and tokens specified by the Liquid Glass Design System.
 */
object GlassTokens {
    const val RESOLUTION_SCALE = 0.33f
    const val GLASS_RESOLUTION_SCALE = 0.33f
    const val VIBRANCY = 1.0f
    const val BLUR_RADIUS_DP = 8.0f
    const val SURFACE_OPACITY = 0.40f

    const val LENS_HEIGHT = 0.5f
    const val LENS_AMOUNT = 0.5f
    const val LENS_MAX_DP = 48.0f

    val EDGE_WIDTH: Dp = 0.5.dp
    val EDGE_COLOR: Color = Color.White.copy(alpha = 0.10f)
    val EDGE_COLOR_HIGHLIGHT: Color = Color.White.copy(alpha = 0.25f)

    // Directional light reflection angle in degrees
    const val HIGHLIGHT_ANGLE_DEG = 45.0f
    const val HIGHLIGHT_FALLOFF = 1.8f

    fun isGlassSupported(sdkInt: Int = Build.VERSION.SDK_INT): Boolean =
        sdkInt >= Build.VERSION_CODES.S // Android 12+ (API 31)

    fun isRuntimeShaderSupported(sdkInt: Int = Build.VERSION.SDK_INT): Boolean =
        sdkInt >= Build.VERSION_CODES.TIRAMISU // Android 13+ (API 33)
}

/**
 * Resolves high-contrast foreground color (pure black or pure white)
 * based on surface luminance to prevent illegibility over dynamic backgrounds.
 */
@Composable
fun glassContentColor(surfaceColor: Color = MaterialTheme.colorScheme.surface): Color =
    if (surfaceColor.luminance() > 0.5f) {
        Color.Black
    } else {
        Color.White
    }

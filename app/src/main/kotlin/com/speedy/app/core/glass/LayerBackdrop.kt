package com.speedy.app.core.glass

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Access to the root recorded backdrop layer across nested glass surfaces.
 */
val LocalAppBackdrop: ProvidableCompositionLocal<LayerBackdrop?> =
    compositionLocalOf { null }

/**
 * Indicates whether dynamic Liquid Glass optical rendering is active.
 */
val LocalLiquidGlassEnabled: ProvidableCompositionLocal<Boolean> =
    compositionLocalOf { true }

/**
 * Encapsulates the shared application graphics layer for Liquid Glass backdrop sampling.
 */
class LayerBackdrop(
    val graphicsLayer: GraphicsLayer,
    internal val onDraw: ContentDrawScope.() -> Unit
) {
    var coordinates: LayoutCoordinates? = null
        internal set

    fun updateCoordinates(layoutCoordinates: LayoutCoordinates) {
        this.coordinates = layoutCoordinates
    }
}

/**
 * Remembers a shared [LayerBackdrop] attached to the root window container.
 */
@Composable
fun rememberLayerBackdrop(
    onDraw: ContentDrawScope.() -> Unit
): LayerBackdrop {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer, onDraw) {
        LayerBackdrop(graphicsLayer = graphicsLayer, onDraw = onDraw)
    }
}

/**
 * Modifier placed on the root container in the activity to capture the application scene.
 *
 * CRITICAL REQUIREMENT (Liquid Glass Skill):
 * The opaque windowBackground MUST be drawn first before drawContent() so the buffer contains
 * zero alpha-0 (transparent) pixels, avoiding visual fringe and blur inversion artifacts.
 */
fun Modifier.captureAppBackdrop(
    backdrop: LayerBackdrop,
    windowBackground: Color,
    enabled: Boolean = true
): Modifier {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return this
    }

    return this
        .onGloballyPositioned { coords ->
            backdrop.updateCoordinates(coords)
        }
        .drawWithContent {
            // Record full window scene into the graphicsLayer
            backdrop.graphicsLayer.record {
                // 1. Paint opaque background first
                drawRect(color = windowBackground)
                // 2. Draw application layout and canvas content
                this@drawWithContent.drawContent()
            }

            // Draw the captured layer to the screen
            drawLayer(backdrop.graphicsLayer)
        }
}

package com.speedy.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.ui.theme.CyanAccent
import com.speedy.app.ui.theme.MintAccent
import com.speedy.app.ui.theme.RedAlert
import kotlin.math.cos
import kotlin.math.sin

/**
 * BitChord-style Liquid Glass Speedometer Gauge.
 * Renders circular sweep, physical specular rim highlight, glowing needle halo, and live readout.
 */
@Composable
fun SpeedGauge(
    speedData: SpeedData,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 280.dp,
    isMaterial3: Boolean = false
) {
    val speedValue = speedData.unit.convert(speedData.currentSpeedMps).coerceAtLeast(0f)
    val maxGaugeSpeed = 180f
    val progress = (speedValue / maxGaugeSpeed).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 250f),
        label = "gaugeSpeedProgress"
    )

    val surfaceModifier = if (isMaterial3) {
        modifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    } else {
        modifier
            .size(sizeDp)
            .clip(CircleShape)
            .liquidGlass(CircleShape, isMaterial3 = false)
    }

    Box(
        modifier = surfaceModifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)
            val strokeWidth = 14.dp.toPx()
            val radius = (width - strokeWidth - 36.dp.toPx()) / 2f

            // Start at 140° (bottom-left) and sweep 260° (to bottom-right)
            val startAngle = 140f
            val totalSweep = 260f

            val arcTopLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2f, radius * 2f)

            // 1. Center darkening preserving glass translucency
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.20f),
                        Color.Black.copy(alpha = 0.50f)
                    ),
                    center = center,
                    radius = radius * 0.95f
                ),
                radius = radius * 0.95f,
                center = center
            )

            // 2. Inactive background track ring
            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Active illuminated speed arc
            if (animatedProgress > 0.005f) {
                val currentSweep = totalSweep * animatedProgress
                val dynamicColor = when {
                    speedValue > 130f -> RedAlert
                    speedValue > 80f -> Color(0xFFFFB300)
                    else -> CyanAccent
                }

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            CyanAccent,
                            MintAccent,
                            dynamicColor
                        )
                    ),
                    startAngle = startAngle,
                    sweepAngle = currentSweep,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Leading glowing head
                val endAngleRad = Math.toRadians((startAngle + currentSweep).toDouble())
                val headX = center.x + radius * cos(endAngleRad).toFloat()
                val headY = center.y + radius * sin(endAngleRad).toFloat()

                // Glow halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            dynamicColor.copy(alpha = 0.85f),
                            dynamicColor.copy(alpha = 0.0f)
                        ),
                        center = Offset(headX, headY),
                        radius = 24.dp.toPx()
                    ),
                    radius = 24.dp.toPx(),
                    center = Offset(headX, headY),
                    blendMode = BlendMode.Plus
                )

                // White sharp center point
                drawCircle(
                    color = Color.White,
                    radius = 4.5.dp.toPx(),
                    center = Offset(headX, headY)
                )
            }

            // 4. Tick marks around perimeter
            val ticks = 18
            for (i in 0..ticks) {
                val fraction = i.toFloat() / ticks
                val angleDeg = startAngle + fraction * totalSweep
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val isMajor = i % 3 == 0
                val tickLength = if (isMajor) 11.dp.toPx() else 6.dp.toPx()
                val tickWidth = if (isMajor) 2.2.dp.toPx() else 1.2.dp.toPx()
                val tickAlpha = if (fraction <= animatedProgress) 0.95f else 0.22f

                val outerX = center.x + (radius - 12.dp.toPx()) * cos(angleRad).toFloat()
                val outerY = center.y + (radius - 12.dp.toPx()) * sin(angleRad).toFloat()
                val innerX = center.x + (radius - 12.dp.toPx() - tickLength) * cos(angleRad).toFloat()
                val innerY = center.y + (radius - 12.dp.toPx() - tickLength) * sin(angleRad).toFloat()

                drawLine(
                    color = Color.White.copy(alpha = tickAlpha),
                    start = Offset(innerX, innerY),
                    end = Offset(outerX, outerY),
                    strokeWidth = tickWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        // Central Speed Readout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val formatted = speedData.displaySpeed

            Text(
                text = formatted,
                color = Color.White,
                fontSize = if (formatted.length >= 4) 52.sp else 62.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-1.5).sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = speedData.displayUnit.uppercase(),
                color = CyanAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-indicator: GPS status or Simulation
            val statusText = when {
                speedData.isSimulating -> AppStrings.gaugeStatusSimulating(language)
                speedData.isGpsFixed -> AppStrings.gaugeStatusGpsLock(language, speedData.satellitesCount)
                speedData.isTracking -> AppStrings.gaugeStatusSearching(language)
                else -> AppStrings.gaugeStatusDisabled(language)
            }
            val statusColor = when {
                speedData.isSimulating -> CyanAccent
                speedData.isGpsFixed -> MintAccent
                speedData.isTracking -> Color(0xFFFFB300)
                else -> Color.White.copy(alpha = 0.5f)
            }

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

package com.speedy.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.speedy.app.core.location.SpeedData
import com.speedy.app.ui.theme.CyanAccent

/**
 * Floating speed bubble popup.
 *
 * Matches the solid tile/card aesthetic of the app:
 * - 100% opaque solid background (non-transparent)
 * - No fake reflections or rainbow chromatic rings
 * - Subtle 0.5dp hairline border matching the cards
 * - Dynamic neon speed arc
 * - High-contrast pure white bold typography
 * - Bottom GPS status LED
 */
@Composable
fun SpeedBubble(
    speedData: SpeedData,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 84.dp,
    isMaterial3: Boolean = false
) {
    val speedValue = speedData.unit.convert(speedData.currentSpeedMps).coerceAtLeast(0f)
    val maxGaugeSpeed = 160f
    val progress = (speedValue / maxGaugeSpeed).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "bubbleGaugeProgress"
    )

    // Subtle 0.5dp hairline border matching the app cards
    val borderBrush = if (isMaterial3) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF434C5E),
                Color(0xFF2E3440)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.18f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Solid Card Surface Canvas
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val radius = canvasWidth / 2f

            // 1. Solid opaque substrate (matching app cards - 100% opaque, not transparent!)
            if (isMaterial3) {
                drawCircle(
                    color = Color(0xFF1E2430),
                    radius = radius,
                    center = center
                )
            } else {
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF182236),
                            Color(0xFF111726),
                            Color(0xFF0D121F)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(canvasWidth, canvasHeight)
                    ),
                    radius = radius,
                    center = center
                )

                // Soft subtle top-left ambient light gradient (same as cards, no harsh reflection arc)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.25f, canvasHeight * 0.25f),
                        radius = radius * 0.9f
                    ),
                    radius = radius,
                    center = center
                )
            }

            // 2. Sunken Gauge Track (subtle inactive groove)
            val strokeWidth = 3.5.dp.toPx()
            val padding = strokeWidth / 2 + 4.dp.toPx()
            val arcSize = Size(canvasWidth - padding * 2, canvasHeight - padding * 2)
            val arcTopLeft = Offset(padding, padding)

            drawArc(
                color = Color.White.copy(alpha = 0.07f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Dynamic Glowing Speed Progress Arc with Neon Gradient
            if (animatedProgress > 0.01f) {
                val speedColor = when {
                    speedValue > 120f -> Color(0xFFFF3366)
                    speedValue > 70f -> Color(0xFFFFB300)
                    else -> Color(0xFF00E5FF)
                }

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            speedColor,
                            Color(0xFF76FF03)
                        )
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 4. GPS telemetry LED indicator
            val dotRadius = 2.5.dp.toPx()
            val dotColor = when {
                speedData.isSimulating -> Color(0xFF00E5FF)
                speedData.isGpsFixed -> Color(0xFF00E676)
                else -> Color(0xFFFF9100)
            }
            // Soft glow halo around LED
            drawCircle(
                color = dotColor.copy(alpha = 0.40f),
                radius = dotRadius * 2.2f,
                center = Offset(canvasWidth / 2, canvasHeight - 9.dp.toPx()),
                blendMode = BlendMode.Plus
            )
            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(canvasWidth / 2, canvasHeight - 9.dp.toPx())
            )
        }

        // Inner Speed Typography (Crisp, High-Contrast Pure White)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val formattedSpeed = speedData.displaySpeedInt

            Text(
                text = formattedSpeed,
                color = Color.White,
                fontSize = if (formattedSpeed.length >= 3) 22.sp else 26.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(1.dp))

            Text(
                text = speedData.displayUnit.uppercase(),
                color = CyanAccent,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }

        // 0.5dp hairline border matching app cards
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(GlassTokens.EDGE_WIDTH, borderBrush, CircleShape)
        )
    }
}

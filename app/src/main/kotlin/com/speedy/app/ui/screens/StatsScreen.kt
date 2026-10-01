package com.speedy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.glassContentColor
import com.speedy.app.core.glass.glassPressEffect
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.location.SpeedTracker
import com.speedy.app.core.settings.AppAccentColor
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.ui.components.SpeedyIcons
import com.speedy.app.ui.theme.DarkCardBorder
import com.speedy.app.ui.theme.DarkCardMinimal

@Composable
fun StatsScreen(
    speedData: SpeedData,
    language: AppLanguage,
    accentColor: AppAccentColor,
    isMaterial3: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.primary.copy(alpha = 0.16f))
                    .border(GlassTokens.EDGE_WIDTH, accentColor.primary.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = SpeedyIcons.Stats,
                    contentDescription = null,
                    tint = accentColor.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = AppStrings.statsHeader(language),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = AppStrings.statsSubtitle(language),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp
                )
            }
        }

        // Hero Card: Distance & Avg Speed
        val heroCardShape = RoundedCornerShape(22.dp)
        val heroModifier = if (isMaterial3) {
            Modifier
                .fillMaxWidth()
                .clip(heroCardShape)
                .background(DarkCardMinimal)
                .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, heroCardShape)
                .padding(20.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .clip(heroCardShape)
                .liquidGlass(heroCardShape, isMaterial3 = false)
                .padding(20.dp)
        }

        Box(modifier = heroModifier) {
            Column {
                Text(
                    text = AppStrings.distance(language).uppercase(),
                    color = accentColor.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = speedData.displayDistance,
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.08f))
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = AppStrings.avgSpeed(language),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${speedData.unit.format(speedData.avgSpeedMps)} ${speedData.unit.label}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = AppStrings.maxSpeed(language),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${speedData.unit.format(speedData.maxSpeedMps)} ${speedData.unit.label}",
                            color = accentColor.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Detailed Telemetry 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsMetricCard(
                title = AppStrings.satellites(language),
                value = if (speedData.isSimulating) "SIM" else "${speedData.satellitesCount}",
                unit = if (speedData.isSimulating) "TEST" else "SATS",
                accentColor = accentColor.primary,
                isMaterial3 = isMaterial3,
                modifier = Modifier.weight(1f)
            )

            val accuracyText = if (speedData.isSimulating) {
                "±0.5"
            } else if (speedData.accuracyMeters > 0f) {
                "±${String.format(java.util.Locale.US, "%.1f", speedData.accuracyMeters)}"
            } else {
                "--"
            }

            StatsMetricCard(
                title = AppStrings.gpsAccuracy(language),
                value = accuracyText,
                unit = "M",
                accentColor = accentColor.primary,
                isMaterial3 = isMaterial3,
                modifier = Modifier.weight(1f)
            )
        }

        // Reset Stats Button
        val resetShape = RoundedCornerShape(18.dp)
        val resetModifier = if (isMaterial3) {
            Modifier
                .fillMaxWidth()
                .clip(resetShape)
                .background(DarkCardMinimal)
                .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, resetShape)
                .glassPressEffect(targetScale = 0.975f)
                .clickable { SpeedTracker.resetStats() }
                .padding(vertical = 16.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .clip(resetShape)
                .liquidGlass(resetShape, isMaterial3 = false)
                .glassPressEffect(targetScale = 0.975f)
                .clickable { SpeedTracker.resetStats() }
                .padding(vertical = 16.dp)
        }

        Box(
            modifier = resetModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = AppStrings.resetStats(language),
                color = glassContentColor().copy(alpha = 0.70f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Bottom space so content is never covered by the floating dock
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun StatsMetricCard(
    title: String,
    value: String,
    unit: String,
    accentColor: Color,
    isMaterial3: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(18.dp)
    val cardModifier = if (isMaterial3) {
        modifier
            .clip(cardShape)
            .background(DarkCardMinimal)
            .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, cardShape)
            .padding(16.dp)
    } else {
        modifier
            .clip(cardShape)
            .liquidGlass(cardShape, isMaterial3 = false)
            .padding(16.dp)
    }

    Box(modifier = cardModifier) {
        Column {
            Text(
                text = title.uppercase(),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

package com.speedy.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.glassPressEffect
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.settings.AppAccentColor
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.ui.components.FloatingBubbleToggleButton
import com.speedy.app.ui.components.SpeedGauge
import com.speedy.app.ui.theme.DarkCardBorder
import com.speedy.app.ui.theme.DarkCardMinimal

@Composable
fun DashboardScreen(
    speedData: SpeedData,
    isOverlayActive: Boolean,
    onToggleOverlay: () -> Unit,
    language: AppLanguage,
    accentColor: AppAccentColor,
    isMaterial3: Boolean,
    hasLocationPermission: Boolean,
    hasOverlayPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.appTitle(language),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            // Status chip (GNSS satellite count / simulation)
            val chipShape = RoundedCornerShape(percent = 50)
            val chipModifier = if (isMaterial3) {
                Modifier
                    .clip(chipShape)
                    .background(DarkCardMinimal)
                    .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, chipShape)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            } else {
                Modifier
                    .clip(chipShape)
                    .liquidGlass(chipShape, isMaterial3 = false)
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            }

            Box(modifier = chipModifier) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dotColor = when {
                        speedData.isSimulating -> accentColor.secondary
                        speedData.isGpsFixed -> accentColor.primary
                        else -> Color.White.copy(alpha = 0.4f)
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    val statusText = when {
                        speedData.isSimulating -> AppStrings.gaugeStatusSimulating(language)
                        speedData.isGpsFixed -> AppStrings.gaugeStatusGpsLock(language, speedData.satellitesCount)
                        speedData.accuracyMeters > 0f -> AppStrings.gaugeStatusSearching(language)
                        else -> AppStrings.gaugeStatusDisabled(language)
                    }

                    Text(
                        text = statusText,
                        color = dotColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Permission Banners (Location and Overlay)
        AnimatedVisibility(
            visible = !hasLocationPermission,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val bannerShape = RoundedCornerShape(18.dp)
            val bannerModifier = if (isMaterial3) {
                Modifier
                    .fillMaxWidth()
                    .clip(bannerShape)
                    .background(DarkCardMinimal)
                    .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, bannerShape)
                    .glassPressEffect(targetScale = 0.975f)
                    .clickable { onRequestLocationPermission() }
                    .padding(16.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .clip(bannerShape)
                    .liquidGlass(bannerShape, isMaterial3 = false)
                    .glassPressEffect(targetScale = 0.975f)
                    .clickable { onRequestLocationPermission() }
                    .padding(16.dp)
            }

            Box(modifier = bannerModifier) {
                Column {
                    Text(
                        text = AppStrings.locationPermissionTitle(language),
                        color = accentColor.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = AppStrings.locationPermissionDesc(language),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !hasOverlayPermission,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val bannerShape = RoundedCornerShape(18.dp)
            val bannerModifier = if (isMaterial3) {
                Modifier
                    .fillMaxWidth()
                    .clip(bannerShape)
                    .background(DarkCardMinimal)
                    .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, bannerShape)
                    .glassPressEffect(targetScale = 0.975f)
                    .clickable { onRequestOverlayPermission() }
                    .padding(16.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .clip(bannerShape)
                    .liquidGlass(bannerShape, isMaterial3 = false)
                    .glassPressEffect(targetScale = 0.975f)
                    .clickable { onRequestOverlayPermission() }
                    .padding(16.dp)
            }

            Box(modifier = bannerModifier) {
                Column {
                    Text(
                        text = AppStrings.overlayPermissionTitle(language),
                        color = accentColor.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = AppStrings.overlayPermissionDesc(language),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Main Speedometer Gauge
        SpeedGauge(
            speedData = speedData,
            language = language,
            sizeDp = 280.dp,
            isMaterial3 = isMaterial3,
            accentColor = accentColor
        )

        // Floating Bubble Toggle Card
        FloatingBubbleToggleButton(
            isOverlayActive = isOverlayActive,
            language = language,
            onToggle = onToggleOverlay,
            isMaterial3 = isMaterial3,
            accentColor = accentColor
        )

        // Bottom space so content is never covered by the floating dock
        Spacer(modifier = Modifier.height(100.dp))
    }
}

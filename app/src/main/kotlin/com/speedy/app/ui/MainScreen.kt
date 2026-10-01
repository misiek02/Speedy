package com.speedy.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speedy.app.core.glass.BottomFadeScrim
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.settings.AppAccentColor
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppTab
import com.speedy.app.core.settings.AppThemeStyle
import com.speedy.app.ui.components.FloatingBottomDock
import com.speedy.app.ui.screens.CustomizationScreen
import com.speedy.app.ui.screens.DashboardScreen
import com.speedy.app.ui.screens.SettingsScreen
import com.speedy.app.ui.screens.StatsScreen
import com.speedy.app.ui.theme.DarkBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun MainScreen(
    speedData: SpeedData,
    isOverlayActive: Boolean,
    language: AppLanguage,
    themeStyle: AppThemeStyle,
    currentTab: AppTab,
    accentColor: AppAccentColor,
    onTabSelected: (AppTab) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onThemeStyleSelected: (AppThemeStyle) -> Unit,
    onAccentColorSelected: (AppAccentColor) -> Unit,
    hasLocationPermission: Boolean,
    hasOverlayPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onToggleOverlay: () -> Unit,
    innerPadding: PaddingValues,
    hazeState: HazeState
) {
    val isMaterial3 = themeStyle == AppThemeStyle.MATERIAL3

    Box(modifier = Modifier.fillMaxSize()) {
        // Atmospheric Multi-Orb Substrate (Layer 0) - Active in Liquid Glass / Aura mode
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            val w = size.width
            val h = size.height

            if (isMaterial3) {
                // Pure deep OLED dark obsidian surface for Material 3 Minimal
                drawRect(color = DarkBackground)
            } else {
                // 1. Rich dark OLED obsidian substrate (eliminates washed-out grey)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF020407),
                            Color(0xFF050810),
                            Color(0xFF020407)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                )

                // 2. Top-center Dynamic Accent Orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.primary.copy(alpha = 0.28f),
                            accentColor.secondary.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.50f, h * 0.26f),
                        radius = w * 0.75f
                    ),
                    radius = w * 0.75f,
                    center = Offset(w * 0.50f, h * 0.26f)
                )

                // 3. Center-right Dynamic Secondary Glow Orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.secondary.copy(alpha = 0.20f),
                            accentColor.glow.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.88f, h * 0.52f),
                        radius = w * 0.60f
                    ),
                    radius = w * 0.60f,
                    center = Offset(w * 0.88f, h * 0.52f)
                )

                // 4. Mid-left Subtle Accent Glow Halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.primary.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.08f, h * 0.44f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(w * 0.08f, h * 0.44f)
                )

                // 5. Lower-right Ambient Glow Orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.glow.copy(alpha = 0.16f),
                            accentColor.primary.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.75f, h * 0.82f),
                        radius = w * 0.65f
                    ),
                    radius = w * 0.65f,
                    center = Offset(w * 0.75f, h * 0.82f)
                )
            }
        }

        // Tab Content (Layer 1)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "tab_content_animation"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.SPEEDOMETER -> DashboardScreen(
                        speedData = speedData,
                        isOverlayActive = isOverlayActive,
                        onToggleOverlay = onToggleOverlay,
                        language = language,
                        accentColor = accentColor,
                        isMaterial3 = isMaterial3,
                        hasLocationPermission = hasLocationPermission,
                        hasOverlayPermission = hasOverlayPermission,
                        onRequestLocationPermission = onRequestLocationPermission,
                        onRequestOverlayPermission = onRequestOverlayPermission
                    )

                    AppTab.STATS -> StatsScreen(
                        speedData = speedData,
                        language = language,
                        accentColor = accentColor,
                        isMaterial3 = isMaterial3
                    )

                    AppTab.CUSTOMIZATION -> CustomizationScreen(
                        themeStyle = themeStyle,
                        onThemeStyleSelected = onThemeStyleSelected,
                        accentColor = accentColor,
                        onAccentColorSelected = onAccentColorSelected,
                        speedData = speedData,
                        language = language,
                        isMaterial3 = isMaterial3
                    )

                    AppTab.SETTINGS -> SettingsScreen(
                        speedData = speedData,
                        language = language,
                        onLanguageSelected = onLanguageSelected,
                        accentColor = accentColor,
                        isMaterial3 = isMaterial3,
                        hasLocationPermission = hasLocationPermission,
                        hasOverlayPermission = hasOverlayPermission,
                        onRequestLocationPermission = onRequestLocationPermission,
                        onRequestOverlayPermission = onRequestOverlayPermission
                    )
                }
            }
        }

        // 16-Stop OLED anti-banding bottom scrim (Layer 2)
        BottomFadeScrim(
            modifier = Modifier.align(Alignment.BottomCenter),
            height = 110.dp,
            baseColor = DarkBackground
        )

        // Floating Bottom Pill Dock (Layer 3)
        FloatingBottomDock(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            language = language,
            accentColor = accentColor,
            isMaterial3 = isMaterial3,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        )
    }
}

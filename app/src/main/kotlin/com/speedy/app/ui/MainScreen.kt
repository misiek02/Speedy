package com.speedy.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.BottomFadeScrim
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.glassContentColor
import com.speedy.app.core.glass.glassPressEffect
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.location.SpeedTracker
import com.speedy.app.core.location.SpeedUnit
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.core.settings.AppThemeStyle
import com.speedy.app.ui.components.FloatingBubbleToggleButton
import com.speedy.app.ui.components.LanguageSelector
import com.speedy.app.ui.components.MetricsGrid
import com.speedy.app.ui.components.SpeedGauge
import com.speedy.app.ui.components.ThemeStyleSelector
import com.speedy.app.ui.components.UnitSelector
import com.speedy.app.ui.theme.AmberAccent
import com.speedy.app.ui.theme.CyanAccent
import com.speedy.app.ui.theme.DarkBackground
import com.speedy.app.ui.theme.MintAccent
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun MainScreen(
    speedData: SpeedData,
    isOverlayActive: Boolean,
    language: AppLanguage,
    themeStyle: AppThemeStyle,
    onLanguageSelected: (AppLanguage) -> Unit,
    onThemeStyleSelected: (AppThemeStyle) -> Unit,
    hasLocationPermission: Boolean,
    hasOverlayPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onToggleOverlay: () -> Unit,
    innerPadding: PaddingValues,
    hazeState: HazeState
) {
    val scrollState = rememberScrollState()
    val isMaterial3 = themeStyle == AppThemeStyle.MATERIAL3

    Box(modifier = Modifier.fillMaxSize()) {
        // Atmospheric Multi-Orb Substrate (Layer 0) - Active in Liquid Glass mode
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            val w = size.width
            val h = size.height

            if (isMaterial3) {
                // Clean uniform dark surface for Material 3
                drawRect(color = Color(0xFF0F141E))
            } else {
                // 1. Rich dark diagonal gradient substrate
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF070B14),
                            Color(0xFF0F172A),
                            Color(0xFF080C16)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                )

                // 2. Top-center Electric Cobalt Orb (illuminates the speed gauge)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2962FF).copy(alpha = 0.32f),
                            Color(0xFF0039CB).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.50f, h * 0.28f),
                        radius = w * 0.75f
                    ),
                    radius = w * 0.75f,
                    center = Offset(w * 0.50f, h * 0.28f)
                )

                // 3. Center-right Vibrant Cyan / Aqua Orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.25f),
                            Color(0xFF00B0FF).copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.88f, h * 0.52f),
                        radius = w * 0.60f
                    ),
                    radius = w * 0.60f,
                    center = Offset(w * 0.88f, h * 0.52f)
                )

                // 4. Mid-left Deep Neon Violet Orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7C4DFF).copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.08f, h * 0.42f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(w * 0.08f, h * 0.42f)
                )

                // 5. Lower-right Neon Mint / Emerald Orb (illuminates bottom metrics)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MintAccent.copy(alpha = 0.20f),
                            Color(0xFF00C853).copy(alpha = 0.05f),
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

        // Scrollable content (Layer 1)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Header Bar
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

                // Simulation mode toggle chip
                val simShape = RoundedCornerShape(percent = 50)
                val simModifier = if (isMaterial3) {
                    Modifier
                        .clip(simShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, simShape)
                        .glassPressEffect(targetScale = 0.95f)
                        .clickable { SpeedTracker.toggleSimulation() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                } else {
                    Modifier
                        .clip(simShape)
                        .liquidGlass(simShape, isMaterial3 = false)
                        .glassPressEffect(targetScale = 0.95f)
                        .clickable { SpeedTracker.toggleSimulation() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                }

                Box(modifier = simModifier) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (speedData.isSimulating) AmberAccent else Color.White.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (speedData.isSimulating) AppStrings.simulationActive(language) else AppStrings.testMode(language),
                            color = if (speedData.isSimulating) AmberAccent else Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Theme Style and Language Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Theme Style Selector: [ AURA | MINIMAL ]
                ThemeStyleSelector(
                    selectedStyle = themeStyle,
                    onStyleSelected = onThemeStyleSelected,
                    language = language,
                    isMaterial3 = isMaterial3,
                    modifier = Modifier.weight(1.3f)
                )

                // Language Selector: [ PL | EN ]
                LanguageSelector(
                    selectedLanguage = language,
                    onLanguageSelected = onLanguageSelected,
                    isMaterial3 = isMaterial3,
                    modifier = Modifier.weight(0.7f)
                )
            }

            // Permission Warning Banners
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, bannerShape)
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
                            color = AmberAccent,
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, bannerShape)
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
                            color = CyanAccent,
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

            // Main BitChord Liquid Glass / Material 3 Gauge
            SpeedGauge(
                speedData = speedData,
                language = language,
                sizeDp = 280.dp,
                isMaterial3 = isMaterial3
            )

            // Speed Units Switcher (KM/H, MPH, M/S with mathematically equal button sizes)
            UnitSelector(
                selectedUnit = speedData.unit,
                onUnitSelected = { unit: SpeedUnit -> SpeedTracker.setUnit(unit) },
                isMaterial3 = isMaterial3
            )

            // Floating Bubble Toggle Button
            FloatingBubbleToggleButton(
                isOverlayActive = isOverlayActive,
                language = language,
                onToggle = onToggleOverlay,
                isMaterial3 = isMaterial3
            )

            // Trip Statistics
            MetricsGrid(
                speedData = speedData,
                language = language,
                isMaterial3 = isMaterial3
            )

            // Reset trip button
            val resetShape = RoundedCornerShape(18.dp)
            val resetModifier = if (isMaterial3) {
                Modifier
                    .fillMaxWidth()
                    .clip(resetShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
                    .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, resetShape)
                    .glassPressEffect(targetScale = 0.975f)
                    .clickable { SpeedTracker.resetStats() }
                    .padding(vertical = 14.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .clip(resetShape)
                    .liquidGlass(resetShape, isMaterial3 = false)
                    .glassPressEffect(targetScale = 0.975f)
                    .clickable { SpeedTracker.resetStats() }
                    .padding(vertical = 14.dp)
            }

            Box(
                modifier = resetModifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = AppStrings.resetStats(language),
                    color = glassContentColor().copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // 16-Stop OLED anti-banding bottom scrim
        BottomFadeScrim(
            modifier = Modifier.align(Alignment.BottomCenter),
            height = 80.dp,
            baseColor = DarkBackground
        )
    }
}

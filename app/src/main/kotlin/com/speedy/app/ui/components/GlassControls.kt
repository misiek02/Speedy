package com.speedy.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.glassContentColor
import com.speedy.app.core.glass.glassPressEffect
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.location.SpeedUnit
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.core.settings.AppThemeStyle
import com.speedy.app.ui.theme.CyanAccent
import com.speedy.app.ui.theme.MintAccent
import com.speedy.app.ui.theme.RedAlert

/**
 * Metric card showing trip statistics with Liquid Glass or Material 3 styling.
 * Uses 18dp rounded corners and adaptive high-contrast content colors.
 */
@Composable
fun GlassMetricCard(
    label: String,
    value: String,
    unit: String = "",
    modifier: Modifier = Modifier,
    isMaterial3: Boolean = false
) {
    val cardShape = RoundedCornerShape(18.dp)

    val surfaceModifier = if (isMaterial3) {
        modifier
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, cardShape)
    } else {
        modifier
            .clip(cardShape)
            .liquidGlass(cardShape, isMaterial3 = false)
    }

    Box(
        modifier = surfaceModifier.padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = label.uppercase(),
                color = glassContentColor().copy(alpha = 0.65f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = glassContentColor(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Grid of trip metrics (Max speed, Avg speed, Distance, Accuracy).
 */
@Composable
fun MetricsGrid(
    speedData: SpeedData,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    isMaterial3: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassMetricCard(
                label = AppStrings.maxSpeed(language),
                value = speedData.displayMaxSpeed,
                unit = speedData.displayUnit,
                modifier = Modifier.weight(1f),
                isMaterial3 = isMaterial3
            )
            GlassMetricCard(
                label = AppStrings.distance(language),
                value = speedData.displayDistance,
                modifier = Modifier.weight(1f),
                isMaterial3 = isMaterial3
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassMetricCard(
                label = AppStrings.gpsAccuracy(language),
                value = if (speedData.accuracyMeters > 0) String.format(java.util.Locale.US, "±%.1f", speedData.accuracyMeters) else "--",
                unit = "m",
                modifier = Modifier.weight(1f),
                isMaterial3 = isMaterial3
            )
            GlassMetricCard(
                label = AppStrings.satellites(language),
                value = if (speedData.isSimulating) "14" else "${speedData.satellitesCount}",
                unit = "fix",
                modifier = Modifier.weight(1f),
                isMaterial3 = isMaterial3
            )
        }
    }
}

/**
 * Unit selection pill switcher (KM/H, MPH, M/S).
 * All 3 options have the exact same width via Modifier.weight(1f).
 */
@Composable
fun UnitSelector(
    selectedUnit: SpeedUnit,
    onUnitSelected: (SpeedUnit) -> Unit,
    modifier: Modifier = Modifier,
    isMaterial3: Boolean = false
) {
    val pillShape = RoundedCornerShape(percent = 50)

    val containerModifier = if (isMaterial3) {
        modifier
            .fillMaxWidth()
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, pillShape)
            .padding(4.dp)
    } else {
        modifier
            .fillMaxWidth()
            .clip(pillShape)
            .liquidGlass(pillShape, isMaterial3 = false)
            .padding(4.dp)
    }

    Box(modifier = containerModifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeedUnit.entries.forEach { unit ->
                val isSelected = unit == selectedUnit
                val indicatorColor by animateColorAsState(
                    targetValue = if (isSelected) CyanAccent.copy(alpha = if (isMaterial3) 0.35f else 0.18f) else Color.Transparent,
                    label = "unitIndicatorColor"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                    label = "unitTextColor"
                )
                val itemShape = RoundedCornerShape(percent = 50)

                Box(
                    modifier = Modifier
                        .weight(1f) // EQUAL WIDTH FOR ALL 3!
                        .clip(itemShape)
                        .background(indicatorColor)
                        .glassPressEffect(targetScale = 0.94f)
                        .clickable { onUnitSelected(unit) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = unit.label.uppercase(),
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Primary Floating Bubble Control Button with tactile press feedback and accent indicator.
 */
@Composable
fun FloatingBubbleToggleButton(
    isOverlayActive: Boolean,
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isMaterial3: Boolean = false
) {
    val shape = RoundedCornerShape(22.dp)
    val indicatorColor by animateColorAsState(
        targetValue = if (isOverlayActive) MintAccent else Color.White.copy(alpha = 0.3f),
        label = "overlayToggleIndicator"
    )

    val surfaceModifier = if (isMaterial3) {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, shape)
            .glassPressEffect(targetScale = 0.975f)
            .clickable { onToggle() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    } else {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .liquidGlass(shape, isMaterial3 = false)
            .glassPressEffect(targetScale = 0.975f)
            .clickable { onToggle() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    }

    Box(modifier = surfaceModifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular status dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(indicatorColor)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = if (isOverlayActive) AppStrings.bubbleActiveTitle(language) else AppStrings.bubbleInactiveTitle(language),
                        color = glassContentColor(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOverlayActive) AppStrings.bubbleActiveDesc(language) else AppStrings.bubbleInactiveDesc(language),
                        color = glassContentColor().copy(alpha = 0.65f),
                        fontSize = 12.sp
                    )
                }
            }

            // Badge action
            val badgeShape = RoundedCornerShape(percent = 50)
            Box(
                modifier = Modifier
                    .clip(badgeShape)
                    .background(if (isOverlayActive) RedAlert.copy(alpha = 0.18f) else CyanAccent.copy(alpha = 0.18f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isOverlayActive) AppStrings.disableAction(language) else AppStrings.enableAction(language),
                    color = if (isOverlayActive) RedAlert else CyanAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Theme Style Selector pill (LIQUID GLASS vs MATERIAL 3).
 */
@Composable
fun ThemeStyleSelector(
    selectedStyle: AppThemeStyle,
    onStyleSelected: (AppThemeStyle) -> Unit,
    modifier: Modifier = Modifier,
    language: AppLanguage = AppLanguage.PL,
    isMaterial3: Boolean = false
) {
    val pillShape = RoundedCornerShape(percent = 50)

    val containerModifier = if (isMaterial3) {
        modifier
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, pillShape)
            .padding(3.dp)
    } else {
        modifier
            .clip(pillShape)
            .liquidGlass(pillShape, isMaterial3 = false)
            .padding(3.dp)
    }

    Box(modifier = containerModifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppThemeStyle.entries.forEach { style ->
                val isSelected = style == selectedStyle
                val indicatorColor by animateColorAsState(
                    targetValue = if (isSelected) CyanAccent.copy(alpha = if (isMaterial3) 0.35f else 0.22f) else Color.Transparent,
                    label = "themeStyleIndicator"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.60f),
                    label = "themeStyleText"
                )
                val itemShape = RoundedCornerShape(percent = 50)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(itemShape)
                        .background(indicatorColor)
                        .glassPressEffect(targetScale = 0.94f)
                        .clickable { onStyleSelected(style) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style.label(language).uppercase(),
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

/**
 * Language Selector pill (PL vs EN).
 */
@Composable
fun LanguageSelector(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
    isMaterial3: Boolean = false
) {
    val pillShape = RoundedCornerShape(percent = 50)

    val containerModifier = if (isMaterial3) {
        modifier
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
            .border(GlassTokens.EDGE_WIDTH, MaterialTheme.colorScheme.outlineVariant, pillShape)
            .padding(3.dp)
    } else {
        modifier
            .clip(pillShape)
            .liquidGlass(pillShape, isMaterial3 = false)
            .padding(3.dp)
    }

    Box(modifier = containerModifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLanguage.entries.forEach { lang ->
                val isSelected = lang == selectedLanguage
                val indicatorColor by animateColorAsState(
                    targetValue = if (isSelected) CyanAccent.copy(alpha = if (isMaterial3) 0.35f else 0.22f) else Color.Transparent,
                    label = "languageIndicator"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.60f),
                    label = "languageText"
                )
                val itemShape = RoundedCornerShape(percent = 50)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(itemShape)
                        .background(indicatorColor)
                        .glassPressEffect(targetScale = 0.94f)
                        .clickable { onLanguageSelected(lang) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lang.label,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

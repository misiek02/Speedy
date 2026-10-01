package com.speedy.app.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
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
import com.speedy.app.core.settings.AppThemeStyle
import com.speedy.app.ui.components.SpeedGauge
import com.speedy.app.ui.components.SpeedyIcons
import com.speedy.app.ui.theme.DarkCardBorder
import com.speedy.app.ui.theme.DarkCardMinimal

@Composable
fun CustomizationScreen(
    themeStyle: AppThemeStyle,
    onThemeStyleSelected: (AppThemeStyle) -> Unit,
    accentColor: AppAccentColor,
    onAccentColorSelected: (AppAccentColor) -> Unit,
    speedData: SpeedData,
    language: AppLanguage,
    isMaterial3: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header (minimal, no verbose subtitles)
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
                    imageVector = SpeedyIcons.Palette,
                    contentDescription = null,
                    tint = accentColor.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = AppStrings.customizationHeader(language),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        // Section 1: Visual Style Selector (clean, no descriptions)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.themeStyleTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Aura Card
                MinimalStyleCard(
                    title = "AURA",
                    isSelected = themeStyle == AppThemeStyle.LIQUID_GLASS,
                    accentColor = accentColor.primary,
                    isMaterial3 = isMaterial3,
                    onClick = { onThemeStyleSelected(AppThemeStyle.LIQUID_GLASS) },
                    modifier = Modifier.weight(1f)
                )

                // Minimal Card
                MinimalStyleCard(
                    title = "MINIMAL",
                    isSelected = themeStyle == AppThemeStyle.MATERIAL3,
                    accentColor = accentColor.primary,
                    isMaterial3 = isMaterial3,
                    onClick = { onThemeStyleSelected(AppThemeStyle.MATERIAL3) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 2: Accent Color Picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.accentColorTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            val colorPickerShape = RoundedCornerShape(20.dp)
            val pickerModifier = if (isMaterial3) {
                Modifier
                    .fillMaxWidth()
                    .clip(colorPickerShape)
                    .background(DarkCardMinimal)
                    .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, colorPickerShape)
                    .padding(16.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .clip(colorPickerShape)
                    .liquidGlass(colorPickerShape, isMaterial3 = false)
                    .padding(16.dp)
            }

            Box(modifier = pickerModifier) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppAccentColor.entries.forEach { color ->
                        val isSelected = color == accentColor

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .glassPressEffect(targetScale = 0.90f)
                                .clickable { onAccentColorSelected(color) }
                        ) {
                            Box(
                                modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(color.primary)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(3.dp, Color.White, CircleShape)
                                    } else {
                                        Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                    }
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = color.label(language),
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Live Preview Card (Clean)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.previewTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            val previewShape = RoundedCornerShape(22.dp)
            val previewModifier = if (isMaterial3) {
                Modifier
                    .fillMaxWidth()
                    .clip(previewShape)
                    .background(DarkCardMinimal)
                    .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, previewShape)
                    .padding(vertical = 16.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .clip(previewShape)
                    .liquidGlass(previewShape, isMaterial3 = false)
                    .padding(vertical = 16.dp)
            }

            Box(
                modifier = previewModifier,
                contentAlignment = Alignment.Center
            ) {
                SpeedGauge(
                    speedData = speedData,
                    language = language,
                    sizeDp = 220.dp,
                    isMaterial3 = isMaterial3,
                    accentColor = accentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun MinimalStyleCard(
    title: String,
    isSelected: Boolean,
    accentColor: Color,
    isMaterial3: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(18.dp)
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.White.copy(alpha = 0.12f),
        label = "style_card_border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.14f) else Color.Transparent,
        label = "style_card_bg"
    )

    val cardModifier = if (isMaterial3) {
        modifier
            .clip(cardShape)
            .background(DarkCardMinimal)
            .border(if (isSelected) 1.5.dp else GlassTokens.EDGE_WIDTH, borderColor, cardShape)
            .glassPressEffect(targetScale = 0.96f)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 18.dp)
    } else {
        modifier
            .clip(cardShape)
            .background(bgColor)
            .liquidGlass(cardShape, isMaterial3 = false)
            .border(if (isSelected) 1.5.dp else GlassTokens.EDGE_WIDTH, borderColor, cardShape)
            .glassPressEffect(targetScale = 0.96f)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 18.dp)
    }

    Box(
        modifier = cardModifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = if (isSelected) accentColor else Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
        }
    }
}

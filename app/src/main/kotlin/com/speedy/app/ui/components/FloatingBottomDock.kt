package com.speedy.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.glassPressEffect
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.settings.AppAccentColor
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.core.settings.AppTab
import com.speedy.app.ui.theme.DarkCardBorder
import com.speedy.app.ui.theme.DarkCardMinimal

/**
 * Floating bottom navigation dock in pill shape.
 *
 * Implements BitChord Liquid Glass dock architecture:
 * - Mathematically equal size for every tab item (72dp x 48dp)
 * - Animated sliding pill indicator with spring physics
 * - Shared outer pill-shaped glass container
 * - Official Material rounded icons
 */
@Composable
fun FloatingBottomDock(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    language: AppLanguage,
    accentColor: AppAccentColor,
    isMaterial3: Boolean,
    modifier: Modifier = Modifier
) {
    val dockShape = RoundedCornerShape(percent = 50)
    val itemShape = RoundedCornerShape(percent = 50)

    val tabWidth = 72.dp
    val tabHeight = 48.dp
    val dockPadding = 6.dp

    val selectedIndex = AppTab.entries.indexOf(currentTab).coerceAtLeast(0)
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 380f
        ),
        label = "dock_sliding_pill"
    )

    val dockModifier = if (isMaterial3) {
        Modifier
            .shadow(elevation = 16.dp, shape = dockShape, spotColor = Color.Black.copy(alpha = 0.6f))
            .clip(dockShape)
            .background(DarkCardMinimal)
            .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, dockShape)
    } else {
        Modifier
            .shadow(elevation = 20.dp, shape = dockShape, spotColor = accentColor.glow.copy(alpha = 0.25f))
            .clip(dockShape)
            .liquidGlass(dockShape, isMaterial3 = false)
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .then(dockModifier)
            .padding(dockPadding)
    ) {
        // 1. Sliding pill indicator behind the active tab
        val indicatorOffset = tabWidth * animatedIndex
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .height(tabHeight)
                .clip(itemShape)
                .background(
                    if (isMaterial3) accentColor.primary.copy(alpha = 0.16f)
                    else accentColor.primary.copy(alpha = 0.20f)
                )
                .border(
                    GlassTokens.EDGE_WIDTH,
                    if (isMaterial3) accentColor.primary.copy(alpha = 0.40f)
                    else accentColor.primary.copy(alpha = 0.50f),
                    itemShape
                )
        )

        // 2. Tab Items Row (mathematically equal sizes)
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTab.entries.forEach { tab ->
                val isSelected = tab == currentTab

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        accentColor.primary
                    } else {
                        Color.White.copy(alpha = 0.45f)
                    },
                    animationSpec = spring(stiffness = 500f),
                    label = "tab_content_color"
                )

                val icon = when (tab) {
                    AppTab.SPEEDOMETER -> SpeedyIcons.Speedometer
                    AppTab.STATS -> SpeedyIcons.Stats
                    AppTab.CUSTOMIZATION -> SpeedyIcons.Palette
                    AppTab.SETTINGS -> SpeedyIcons.Settings
                }

                val label = when (tab) {
                    AppTab.SPEEDOMETER -> AppStrings.tabSpeed(language)
                    AppTab.STATS -> AppStrings.tabStats(language)
                    AppTab.CUSTOMIZATION -> AppStrings.tabAppearance(language)
                    AppTab.SETTINGS -> AppStrings.tabSettings(language)
                }

                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .height(tabHeight)
                        .clip(itemShape)
                        .glassPressEffect(targetScale = 0.94f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            color = contentColor,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }
    }
}

package com.speedy.app.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speedy.app.core.glass.GlassTokens
import com.speedy.app.core.glass.glassPressEffect
import com.speedy.app.core.glass.liquidGlass
import com.speedy.app.core.location.SpeedData
import com.speedy.app.core.location.SpeedTracker
import com.speedy.app.core.settings.AppAccentColor
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppStrings
import com.speedy.app.ui.components.LanguageSelector
import com.speedy.app.ui.components.SpeedyIcons
import com.speedy.app.ui.components.UnitSelector
import com.speedy.app.ui.theme.DarkCardBorder
import com.speedy.app.ui.theme.DarkCardMinimal

@Composable
fun SettingsScreen(
    speedData: SpeedData,
    language: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    accentColor: AppAccentColor,
    isMaterial3: Boolean,
    hasLocationPermission: Boolean,
    hasOverlayPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Header
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
                    imageVector = SpeedyIcons.Settings,
                    contentDescription = null,
                    tint = accentColor.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = AppStrings.settingsHeader(language),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        // Section 1: Speed Units
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.unitTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            UnitSelector(
                selectedUnit = speedData.unit,
                onUnitSelected = { unit -> SpeedTracker.setUnit(unit) },
                isMaterial3 = isMaterial3,
                accentColor = accentColor
            )
        }

        // Section 2: Test Mode / Simulation
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.simulationTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            val simCardShape = RoundedCornerShape(20.dp)
            val simModifier = if (isMaterial3) {
                Modifier
                    .fillMaxWidth()
                    .clip(simCardShape)
                    .background(DarkCardMinimal)
                    .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, simCardShape)
                    .glassPressEffect(targetScale = 0.98f)
                    .clickable { SpeedTracker.toggleSimulation() }
                    .padding(16.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .clip(simCardShape)
                    .liquidGlass(simCardShape, isMaterial3 = false)
                    .glassPressEffect(targetScale = 0.98f)
                    .clickable { SpeedTracker.toggleSimulation() }
                    .padding(16.dp)
            }

            Box(modifier = simModifier) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (speedData.isSimulating) accentColor.primary else Color.White.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (speedData.isSimulating) AppStrings.simulationActive(language) else AppStrings.testMode(language),
                            color = if (speedData.isSimulating) accentColor.primary else Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val switchShape = RoundedCornerShape(percent = 50)
                    Box(
                        modifier = Modifier
                            .clip(switchShape)
                            .background(if (speedData.isSimulating) accentColor.primary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                            .border(GlassTokens.EDGE_WIDTH, if (speedData.isSimulating) accentColor.primary else Color.White.copy(alpha = 0.2f), switchShape)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = if (speedData.isSimulating) AppStrings.disableAction(language) else AppStrings.enableAction(language),
                            color = if (speedData.isSimulating) accentColor.primary else Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Section 3: Interface Language
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.languageTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            LanguageSelector(
                selectedLanguage = language,
                onLanguageSelected = onLanguageSelected,
                isMaterial3 = isMaterial3,
                accentColor = accentColor,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Section 4: System Permissions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = AppStrings.permissionsTitle(language),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            PermissionTile(
                title = AppStrings.permLocationTitle(language),
                isGranted = hasLocationPermission,
                grantedText = AppStrings.statusGranted(language),
                actionText = AppStrings.statusRequired(language),
                isMaterial3 = isMaterial3,
                accentColor = accentColor,
                onClick = onRequestLocationPermission
            )

            PermissionTile(
                title = AppStrings.permOverlayTitle(language),
                isGranted = hasOverlayPermission,
                grantedText = AppStrings.statusGranted(language),
                actionText = AppStrings.statusRequired(language),
                isMaterial3 = isMaterial3,
                accentColor = accentColor,
                onClick = onRequestOverlayPermission
            )
        }

        // Section 5: About App & GitHub Link
        val aboutShape = RoundedCornerShape(20.dp)
        val aboutModifier = if (isMaterial3) {
            Modifier
                .fillMaxWidth()
                .clip(aboutShape)
                .background(DarkCardMinimal)
                .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, aboutShape)
                .padding(16.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .clip(aboutShape)
                .liquidGlass(aboutShape, isMaterial3 = false)
                .padding(16.dp)
        }

        Box(modifier = aboutModifier) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPEEDY",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "v0.2",
                        color = accentColor.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Clickable GitHub Link Row
                val ghShape = RoundedCornerShape(14.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ghShape)
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(GlassTokens.EDGE_WIDTH, Color.White.copy(alpha = 0.12f), ghShape)
                        .glassPressEffect(targetScale = 0.97f)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/misiek02/Speedy"))
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "github.com/misiek02/Speedy",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = "GitHub",
                        tint = accentColor.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun PermissionTile(
    title: String,
    isGranted: Boolean,
    grantedText: String,
    actionText: String,
    isMaterial3: Boolean,
    accentColor: AppAccentColor,
    onClick: () -> Unit
) {
    val tileShape = RoundedCornerShape(18.dp)
    val tileModifier = if (isMaterial3) {
        Modifier
            .fillMaxWidth()
            .clip(tileShape)
            .background(DarkCardMinimal)
            .border(GlassTokens.EDGE_WIDTH, DarkCardBorder, tileShape)
            .glassPressEffect(targetScale = 0.98f)
            .clickable(enabled = !isGranted) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .clip(tileShape)
            .liquidGlass(tileShape, isMaterial3 = false)
            .glassPressEffect(targetScale = 0.98f)
            .clickable(enabled = !isGranted) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    }

    Box(modifier = tileModifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            val badgeShape = RoundedCornerShape(percent = 50)
            val badgeBg by animateColorAsState(
                targetValue = if (isGranted) accentColor.primary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
                label = "perm_badge_bg"
            )
            val badgeText by animateColorAsState(
                targetValue = if (isGranted) accentColor.primary else Color.White.copy(alpha = 0.6f),
                label = "perm_badge_text"
            )

            Box(
                modifier = Modifier
                    .clip(badgeShape)
                    .background(badgeBg)
                    .border(GlassTokens.EDGE_WIDTH, badgeText.copy(alpha = 0.4f), badgeShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isGranted) grantedText else actionText,
                    color = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

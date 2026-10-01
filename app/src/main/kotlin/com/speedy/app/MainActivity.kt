package com.speedy.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.speedy.app.core.glass.LocalHazeState
import com.speedy.app.core.glass.LocalLiquidGlassEnabled
import com.speedy.app.core.location.SpeedTracker
import com.speedy.app.core.settings.AppLanguage
import com.speedy.app.core.settings.AppSettings
import com.speedy.app.core.settings.AppThemeStyle
import com.speedy.app.service.SpeedOverlayService
import com.speedy.app.ui.MainScreen
import com.speedy.app.ui.theme.DarkBackground
import com.speedy.app.ui.theme.SpeedyTheme
import dev.chrisbanes.haze.rememberHazeState

class MainActivity : ComponentActivity() {

    private var hasLocationPermission by mutableStateOf(false)
    private var hasOverlayPermission by mutableStateOf(false)

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasLocationPermission = fineGranted || coarseGranted

        if (hasLocationPermission) {
            SpeedTracker.startTracking(this)
        }
    }

    private val overlaySettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermissions()
        if (hasOverlayPermission) {
            SpeedOverlayService.start(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()

        if (hasLocationPermission) {
            SpeedTracker.startTracking(this)
        } else {
            requestLocationPermissions()
        }

        setContent {
            val accentColor by AppSettings.accentColor.collectAsState()
            SpeedyTheme(accentColor = accentColor) {
                val hazeState = rememberHazeState()

                val speedData by SpeedTracker.speedData.collectAsState()
                val isOverlayActive by SpeedOverlayService.isRunning.collectAsState()
                val language by AppSettings.language.collectAsState()
                val themeStyle by AppSettings.themeStyle.collectAsState()
                val currentTab by AppSettings.currentTab.collectAsState()

                CompositionLocalProvider(
                    LocalLiquidGlassEnabled provides (themeStyle == AppThemeStyle.LIQUID_GLASS),
                    LocalHazeState provides hazeState
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = DarkBackground
                    ) { innerPadding ->
                        MainScreen(
                            speedData = speedData,
                            isOverlayActive = isOverlayActive,
                            language = language,
                            themeStyle = themeStyle,
                            currentTab = currentTab,
                            accentColor = accentColor,
                            onTabSelected = { AppSettings.setTab(it) },
                            onLanguageSelected = { AppSettings.setLanguage(it) },
                            onThemeStyleSelected = { AppSettings.setThemeStyle(it) },
                            onAccentColorSelected = { AppSettings.setAccentColor(it) },
                            hasLocationPermission = hasLocationPermission,
                            hasOverlayPermission = hasOverlayPermission,
                            onRequestLocationPermission = { requestLocationPermissions() },
                            onRequestOverlayPermission = { requestOverlayPermission() },
                            onToggleOverlay = {
                                if (isOverlayActive) {
                                    SpeedOverlayService.stop(this@MainActivity)
                                } else {
                                    if (hasOverlayPermission) {
                                        SpeedOverlayService.start(this@MainActivity)
                                    } else {
                                        requestOverlayPermission()
                                    }
                                }
                            },
                            innerPadding = innerPadding,
                            hazeState = hazeState
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
        if (hasLocationPermission) {
            SpeedTracker.startTracking(this)
        }
    }

    private fun checkPermissions() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        hasLocationPermission = fine || coarse

        hasOverlayPermission = Settings.canDrawOverlays(this)
    }

    private fun requestLocationPermissions() {
        val perms = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        locationPermissionLauncher.launch(perms.toTypedArray())
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        overlaySettingsLauncher.launch(intent)
    }
}

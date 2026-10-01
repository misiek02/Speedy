package com.speedy.app.core.settings

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val label: String) {
    PL("pl", "PL"),
    EN("en", "EN")
}

enum class AppThemeStyle(val labelPl: String, val labelEn: String) {
    LIQUID_GLASS("Aura", "Aura"),
    MATERIAL3("Minimal", "Minimal");

    fun label(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> labelPl
        AppLanguage.EN -> labelEn
    }
}

enum class AppTab(val labelPl: String, val labelEn: String) {
    SPEEDOMETER("Licznik", "Speed"),
    STATS("Statystyki", "Stats"),
    CUSTOMIZATION("Personalizacja", "Style"),
    SETTINGS("Ustawienia", "Settings");

    fun label(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> labelPl
        AppLanguage.EN -> labelEn
    }
}

enum class AppAccentColor(
    val id: String,
    val labelPl: String,
    val labelEn: String,
    val primary: Color,
    val secondary: Color,
    val glow: Color
) {
    CYAN("cyan", "Cyjan", "Cyan", Color(0xFF00E5FF), Color(0xFF00B0FF), Color(0xFF00E5FF)),
    BLUE("blue", "Kobalt", "Cobalt", Color(0xFF2979FF), Color(0xFF0039CB), Color(0xFF2962FF)),
    MINT("mint", "Mięta", "Mint", Color(0xFF00E676), Color(0xFF00C853), Color(0xFF00E676)),
    AMBER("amber", "Bursztyn", "Amber", Color(0xFFFFB300), Color(0xFFFF8F00), Color(0xFFFFB300)),
    VIOLET("violet", "Fiolet", "Violet", Color(0xFFD500F9), Color(0xFF7C4DFF), Color(0xFFD500F9)),
    CRIMSON("crimson", "Karmin", "Crimson", Color(0xFFFF1744), Color(0xFFD50000), Color(0xFFFF1744));

    fun label(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> labelPl
        AppLanguage.EN -> labelEn
    }
}

object AppSettings {
    private val _language = MutableStateFlow(AppLanguage.PL)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeStyle = MutableStateFlow(AppThemeStyle.LIQUID_GLASS)
    val themeStyle: StateFlow<AppThemeStyle> = _themeStyle.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.SPEEDOMETER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _accentColor = MutableStateFlow(AppAccentColor.CYAN)
    val accentColor: StateFlow<AppAccentColor> = _accentColor.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setThemeStyle(style: AppThemeStyle) {
        _themeStyle.value = style
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setAccentColor(color: AppAccentColor) {
        _accentColor.value = color
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.PL) AppLanguage.EN else AppLanguage.PL
    }

    fun toggleThemeStyle() {
        _themeStyle.value = if (_themeStyle.value == AppThemeStyle.LIQUID_GLASS) AppThemeStyle.MATERIAL3 else AppThemeStyle.LIQUID_GLASS
    }
}

object AppStrings {
    fun appTitle(lang: AppLanguage): String = "SPEEDY"

    fun simulationActive(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "SYMULACJA AKTYWNA"
        AppLanguage.EN -> "SIMULATION ACTIVE"
    }

    fun testMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "TRYB TESTOWY"
        AppLanguage.EN -> "TEST MODE"
    }

    fun locationPermissionTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Wymagane uprawnienie do lokalizacji"
        AppLanguage.EN -> "Location Permission Required"
    }

    fun locationPermissionDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Dotknij, aby włączyć GPS do precyzyjnego pomiaru prędkości."
        AppLanguage.EN -> "Tap to enable GPS for accurate speed tracking."
    }

    fun overlayPermissionTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Wymagane uprawnienie nakładki"
        AppLanguage.EN -> "Overlay Permission Required"
    }

    fun overlayPermissionDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Dotknij, aby zezwolić na wyświetlanie pływającego kółka nad innymi aplikacjami."
        AppLanguage.EN -> "Tap to allow displaying the floating bubble over other apps."
    }

    fun bubbleActiveTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Pływające kółko aktywne"
        AppLanguage.EN -> "Floating bubble active"
    }

    fun bubbleInactiveTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Włącz pływające kółko"
        AppLanguage.EN -> "Enable floating bubble"
    }

    fun bubbleActiveDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Dotknij, aby ukryć kółko"
        AppLanguage.EN -> "Tap to hide floating bubble"
    }

    fun bubbleInactiveDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Wyświetl kółko na ekranie telefonu"
        AppLanguage.EN -> "Display speed bubble over apps"
    }

    fun disableAction(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "WYŁĄCZ"
        AppLanguage.EN -> "DISABLE"
    }

    fun enableAction(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "WŁĄCZ"
        AppLanguage.EN -> "ENABLE"
    }

    fun maxSpeed(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Maksymalna"
        AppLanguage.EN -> "Max Speed"
    }

    fun distance(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Dystans"
        AppLanguage.EN -> "Distance"
    }

    fun gpsAccuracy(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Dokładność GPS"
        AppLanguage.EN -> "GPS Accuracy"
    }

    fun satellites(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Satelity"
        AppLanguage.EN -> "Satellites"
    }

    fun resetStats(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "ZERUJ STATYSTYKI TRASY"
        AppLanguage.EN -> "RESET TRIP STATS"
    }

    fun gaugeStatusSimulating(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "TRYB SYMULACJI"
        AppLanguage.EN -> "SIMULATION MODE"
    }

    fun gaugeStatusGpsLock(lang: AppLanguage, count: Int): String = when (lang) {
        AppLanguage.PL -> "GPS LOCK: $count SAT"
        AppLanguage.EN -> "GPS LOCK: $count SATS"
    }

    fun gaugeStatusSearching(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "SZUKANIE GPS..."
        AppLanguage.EN -> "SEARCHING GPS..."
    }

    fun gaugeStatusDisabled(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "GPS WYŁĄCZONY"
        AppLanguage.EN -> "GPS DISABLED"
    }

    fun notificationText(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Pływające kółko prędkości jest aktywne"
        AppLanguage.EN -> "Floating speedometer bubble is active"
    }

    fun closeAction(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Zamknij"
        AppLanguage.EN -> "Close"
    }

    // Tabs
    fun tabSpeed(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Licznik"
        AppLanguage.EN -> "Speed"
    }

    fun tabStats(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Statystyki"
        AppLanguage.EN -> "Stats"
    }

    fun tabAppearance(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Styl"
        AppLanguage.EN -> "Style"
    }

    fun tabSettings(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Ustawienia"
        AppLanguage.EN -> "Settings"
    }

    // Stats Screen
    fun statsHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "STATYSTYKI TRASY"
        AppLanguage.EN -> "TRIP STATISTICS"
    }

    fun statsSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Telemetryczne podsumowanie sesji GPS"
        AppLanguage.EN -> "Live GPS telemetry metrics summary"
    }

    fun avgSpeed(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Średnia prędkość"
        AppLanguage.EN -> "Average Speed"
    }

    // Customization Screen
    fun customizationHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "PERSONALIZACJA"
        AppLanguage.EN -> "PERSONALIZATION"
    }

    fun themeStyleTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "STYL WIZUALNY"
        AppLanguage.EN -> "VISUAL STYLE"
    }

    fun auraDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Optyczne szkło z dynamicznym rozmyciem, pryzmatem i neonowymi refleksami"
        AppLanguage.EN -> "Optical liquid glass with dynamic blur, refraction and neon speculars"
    }

    fun minimalDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Czysty, płaski styl Material 3 z wysokim kontrastem i ciemną estetyką"
        AppLanguage.EN -> "Clean, flat Material 3 surface with high contrast and dark geometry"
    }

    fun accentColorTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "KOLOR AKCENTU"
        AppLanguage.EN -> "ACCENT COLOR"
    }

    fun glowIntensityTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "POŚWIATA TŁA (AURA)"
        AppLanguage.EN -> "AMBIENT GLOW (AURA)"
    }

    fun previewTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "PODGLĄD NA ŻYWO"
        AppLanguage.EN -> "LIVE PREVIEW"
    }

    // Settings Screen
    fun settingsHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "USTAWIENIA"
        AppLanguage.EN -> "SETTINGS"
    }

    fun unitTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "JEDNOSTKA PRĘDKOŚCI"
        AppLanguage.EN -> "SPEED UNIT"
    }

    fun simulationTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "TRYB TESTOWY"
        AppLanguage.EN -> "TEST MODE"
    }

    fun simulationDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Symuluje przyspieszenie i hamowanie bez konieczności jazdy"
        AppLanguage.EN -> "Simulates acceleration and braking without moving"
    }

    fun languageTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "JĘZYK INTERFEJSU"
        AppLanguage.EN -> "APP LANGUAGE"
    }

    fun permissionsTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "UPRAWNIENIA SYSTEMOWE"
        AppLanguage.EN -> "PERMISSIONS"
    }

    fun permLocationTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Lokalizacja GPS"
        AppLanguage.EN -> "GPS Location"
    }

    fun permLocationGranted(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Dostęp do precyzyjnej lokalizacji aktywny"
        AppLanguage.EN -> "Fine location access granted"
    }

    fun permLocationDenied(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Wymagany do odczytu prędkości"
        AppLanguage.EN -> "Required for speed measurement"
    }

    fun permOverlayTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Pływające kółko (Overlay)"
        AppLanguage.EN -> "Floating Bubble (Overlay)"
    }

    fun permOverlayGranted(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Uprawnienie do wyświetlania nad aplikacjami aktywne"
        AppLanguage.EN -> "Display over other apps granted"
    }

    fun permOverlayDenied(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "Wymagany do wyświetlania kółka"
        AppLanguage.EN -> "Required for floating bubble"
    }

    fun statusGranted(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "AKTYWNE"
        AppLanguage.EN -> "GRANTED"
    }

    fun statusRequired(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "WYMAGANE"
        AppLanguage.EN -> "REQUIRED"
    }

    fun aboutTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.PL -> "O APLIKACJI"
        AppLanguage.EN -> "ABOUT"
    }

    fun appVersion(lang: AppLanguage): String = "Speedy v0.1 • Jetpack Compose • Liquid Glass Engine"
}

package com.speedy.app.core.settings

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

object AppSettings {
    private val _language = MutableStateFlow(AppLanguage.PL)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeStyle = MutableStateFlow(AppThemeStyle.LIQUID_GLASS)
    val themeStyle: StateFlow<AppThemeStyle> = _themeStyle.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setThemeStyle(style: AppThemeStyle) {
        _themeStyle.value = style
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
}

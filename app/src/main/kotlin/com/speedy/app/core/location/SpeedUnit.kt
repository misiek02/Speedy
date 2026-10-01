package com.speedy.app.core.location

import java.util.Locale

enum class SpeedUnit(val label: String, val multiplier: Float) {
    KMH("km/h", 3.6f),
    MPH("mph", 2.23694f),
    MS("m/s", 1.0f);

    fun convert(speedInMps: Float): Float = speedInMps * multiplier

    fun format(speedInMps: Float): String {
        val converted = convert(speedInMps).coerceAtLeast(0f)
        return when {
            converted >= 100f -> String.format(Locale.US, "%.0f", converted)
            converted >= 10f -> String.format(Locale.US, "%.1f", converted)
            else -> String.format(Locale.US, "%.1f", converted)
        }
    }

    fun formatInt(speedInMps: Float): String {
        val converted = convert(speedInMps).coerceAtLeast(0f)
        return String.format(Locale.US, "%.0f", converted)
    }
}

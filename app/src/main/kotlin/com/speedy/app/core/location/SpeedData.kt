package com.speedy.app.core.location

data class SpeedData(
    val currentSpeedMps: Float = 0f,
    val maxSpeedMps: Float = 0f,
    val avgSpeedMps: Float = 0f,
    val totalDistanceMeters: Float = 0f,
    val accuracyMeters: Float = 0f,
    val altitudeMeters: Double = 0.0,
    val bearingDegrees: Float = 0f,
    val satellitesCount: Int = 0,
    val isGpsFixed: Boolean = false,
    val isTracking: Boolean = false,
    val isSimulating: Boolean = false,
    val unit: SpeedUnit = SpeedUnit.KMH
) {
    val displaySpeed: String
        get() = unit.format(currentSpeedMps)

    val displaySpeedInt: String
        get() = unit.formatInt(currentSpeedMps)

    val displayMaxSpeed: String
        get() = unit.format(maxSpeedMps)

    val displayAvgSpeed: String
        get() = unit.format(avgSpeedMps)

    val displayUnit: String
        get() = unit.label

    val displayDistance: String
        get() = when (unit) {
            SpeedUnit.MPH -> {
                val miles = totalDistanceMeters / 1609.344f
                if (miles >= 1f) String.format(java.util.Locale.US, "%.2f mi", miles)
                else String.format(java.util.Locale.US, "%.0f ft", totalDistanceMeters * 3.28084f)
            }
            else -> {
                if (totalDistanceMeters >= 1000f) {
                    String.format(java.util.Locale.US, "%.2f km", totalDistanceMeters / 1000f)
                } else {
                    String.format(java.util.Locale.US, "%.0f m", totalDistanceMeters)
                }
            }
        }
}

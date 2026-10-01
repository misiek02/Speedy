package com.speedy.app.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Core GPS and simulation tracking engine.
 * Dispatches real-time [SpeedData] updates to UI and overlay service.
 */
object SpeedTracker {

    private val _speedData = MutableStateFlow(SpeedData())
    val speedData: StateFlow<SpeedData> = _speedData.asStateFlow()

    private var locationManager: LocationManager? = null
    private var lastLocation: Location? = null
    private var lastLocationTimestampNanos: Long = 0L

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private var gnssCallback: GnssStatus.Callback? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            handleNewLocation(location)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            _speedData.update { it.copy(isGpsFixed = false) }
        }
    }

    fun setUnit(unit: SpeedUnit) {
        _speedData.update { it.copy(unit = unit) }
    }

    fun resetStats() {
        _speedData.update {
            it.copy(
                maxSpeedMps = 0f,
                avgSpeedMps = 0f,
                totalDistanceMeters = 0f
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking(context: Context) {
        if (_speedData.value.isTracking) return

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val lm = locationManager ?: return

        try {
            // Register for GPS provider updates
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    300L, // 300ms min interval
                    0f,   // 0m min distance
                    locationListener
                )
            }

            // Register GNSS callback on Android 7.0+
            gnssCallback = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    var fixCount = 0
                    val total = status.satelliteCount
                    for (i in 0 until total) {
                        if (status.usedInFix(i)) {
                            fixCount++
                        }
                    }
                    _speedData.update {
                        it.copy(
                            satellitesCount = fixCount,
                            isGpsFixed = fixCount >= 4
                        )
                    }
                }
            }
            lm.registerGnssStatusCallback(context.mainExecutor, gnssCallback!!)

            _speedData.update { it.copy(isTracking = true) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopTracking() {
        locationManager?.let { lm ->
            try {
                lm.removeUpdates(locationListener)
                gnssCallback?.let { lm.unregisterGnssStatusCallback(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        stopSimulation()
        _speedData.update { it.copy(isTracking = false, isGpsFixed = false) }
    }

    fun toggleSimulation(enable: Boolean? = null) {
        val shouldSimulate = enable ?: !_speedData.value.isSimulating
        if (shouldSimulate) {
            startSimulation()
        } else {
            stopSimulation()
        }
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        _speedData.update { it.copy(isSimulating = true, isGpsFixed = true, satellitesCount = 14) }

        simulationJob = scope.launch {
            var simTime = 0.0
            var simDistance = _speedData.value.totalDistanceMeters
            var maxSpeed = _speedData.value.maxSpeedMps

            while (isActive) {
                simTime += 0.1
                // Dynamic speed pattern: accelerating, cruising, highway spurt, braking
                // Peaks around 22 m/s (~80 km/h)
                val base = 12.0 + 8.0 * sin(simTime * 0.15)
                val variation = 3.5 * sin(simTime * 0.45)
                val speed = (base + variation).toFloat().coerceAtLeast(0f)

                if (speed > maxSpeed) {
                    maxSpeed = speed
                }
                simDistance += speed * 0.1f

                _speedData.update {
                    it.copy(
                        currentSpeedMps = speed,
                        maxSpeedMps = maxSpeed,
                        totalDistanceMeters = simDistance,
                        accuracyMeters = 2.4f,
                        bearingDegrees = ((simTime * 15) % 360).toFloat()
                    )
                }
                delay(100)
            }
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        _speedData.update {
            it.copy(
                isSimulating = false,
                currentSpeedMps = 0f
            )
        }
    }

    private fun handleNewLocation(location: Location) {
        if (_speedData.value.isSimulating) return

        var rawSpeed = if (location.hasSpeed()) location.speed else 0f

        // Calculate fallback speed if hardware speed is omitted
        val prev = lastLocation
        val nowNanos = location.elapsedRealtimeNanos
        if (rawSpeed <= 0f && prev != null && lastLocationTimestampNanos > 0) {
            val deltaSec = (nowNanos - lastLocationTimestampNanos) / 1_000_000_000.0f
            if (deltaSec > 0.2f && deltaSec < 5f) {
                val distance = prev.distanceTo(location)
                rawSpeed = distance / deltaSec
            }
        }

        // Noise gate: jitter below 0.6 m/s (~2.1 km/h) is typically GPS drift
        if (rawSpeed < 0.6f) {
            rawSpeed = 0f
        }

        // Update stats
        val distanceDelta = if (prev != null) prev.distanceTo(location) else 0f
        lastLocation = location
        lastLocationTimestampNanos = nowNanos

        _speedData.update { current ->
            val newTotalDistance = current.totalDistanceMeters + distanceDelta
            val newMax = if (rawSpeed > current.maxSpeedMps) rawSpeed else current.maxSpeedMps

            current.copy(
                currentSpeedMps = rawSpeed,
                maxSpeedMps = newMax,
                totalDistanceMeters = newTotalDistance,
                accuracyMeters = if (location.hasAccuracy()) location.accuracy else 0f,
                altitudeMeters = if (location.hasAltitude()) location.altitude else 0.0,
                bearingDegrees = if (location.hasBearing()) location.bearing else current.bearingDegrees,
                isGpsFixed = true
            )
        }
    }
}

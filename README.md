# Speedy

Speedy is a GPS speedometer for Android built with Jetpack Compose. It features a system overlay floating speed bubble that stays on top of any navigation or mapping app, styled using the Liquid Glass optical design system.

## Overview

Speedy solves a common problem when driving with navigation apps: standard navigation interfaces often obscure current speed or update with high latency. Speedy runs a compact floating glass bubble over the system window manager, allowing users to position the speedometer anywhere on screen while keeping full visibility of maps and road conditions.

## Key Features

- **Draggable Floating Overlay**:
  - Runs as an Android Foreground Service (`SpeedOverlayService`) using `TYPE_APPLICATION_OVERLAY`.
  - The circular speed indicator can be dragged to any coordinate on the screen.
  - Tapping the bubble cycles through speed units or brings the main dashboard to the foreground.
  - Persistent low-priority notification keeps the service active during background execution on Android 14 and 16.

- **Liquid Glass Optical Pipeline**:
  - Resolution scaling optimization at 0.33x (`HazeInputScale.Fixed(0.33f)`), processing approximately 10.9% of full display pixels to preserve GPU headroom and guarantee 120 FPS rendering.
  - Directional 45-degree specular rim reflection using additive blending (`BlendMode.Plus`).
  - 0.5dp hairline border for high contrast against diverse app backgrounds.
  - Adaptive binary typography (`glassContentColor`) ensuring text legibility over changing backdrops.
  - 16-stop non-linear cubic scrim (`t^2.2`) preventing color banding on mobile OLED panels.

- **Visual Themes & Customization**:
  - Two switchable visual styles: Aura (liquid glass with optical backdrop effects) and Minimal (clean Material 3 surface).
  - Bilingual interface: seamless switching between English and Polish.

- **GPS & GNSS Telemetry Engine**:
  - Native `LocationManager` integration with `GnssStatus.Callback` for real-time satellite fix counts and accuracy metrics in meters.
  - Speed filtering with a 0.6 m/s noise gate to eliminate stationary GPS drift.
  - Trip statistics: maximum speed, average speed, total distance traveled.
  - Supported units: km/h, mph, m/s.

- **Drive Simulation Mode**:
  - Built-in test provider simulating realistic acceleration, cruising, and deceleration profiles for testing indoors or in development without physical movement.

## Architecture

```
app/src/main/kotlin/com/speedy/app/
├── MainActivity.kt               # Entry point, permission coordination, HazeState provider
├── core/
│   ├── glass/
│   │   ├── GlassTokens.kt       # Optical constants (0.33x scale, 45° angle, 0.5dp border)
│   │   ├── LayerBackdrop.kt     # Shared backdrop infrastructure
│   │   ├── LiquidGlass.kt       # Primary and lightweight Compose modifiers
│   │   ├── Shaders.kt           # AGSL runtime shaders (lens refraction, chromatic dispersion)
│   │   └── FadeScrims.kt        # 16-stop OLED anti-banding bottom scrim
│   └── location/
│       ├── SpeedTracker.kt      # Location engine, GNSS callback, simulation generator
│       ├── SpeedData.kt         # Immutable speed and telemetry state
│       └── SpeedUnit.kt         # Unit conversion logic and string formatters
├── service/
│   └── SpeedOverlayService.kt   # LifecycleService managing WindowManager overlay
└── ui/
    ├── MainScreen.kt            # Main dashboard, gauge, metrics grid, control actions
    ├── components/
    │   ├── SpeedBubble.kt       # Floating glass bubble composable
    │   ├── SpeedGauge.kt        # Circular speedometer gauge with dynamic neon arcs
    │   └── GlassControls.kt     # Metric cards, unit selector, toggle button
    └── theme/
        ├── Color.kt             # Dark obsidian glass color tokens
        └── Theme.kt             # Material theme configuration
```

## Permissions

The app requests the following Android permissions:

- `android.permission.ACCESS_FINE_LOCATION`: Required for raw GPS updates.
- `android.permission.ACCESS_COARSE_LOCATION`: Fallback network-based location provider.
- `android.permission.SYSTEM_ALERT_WINDOW`: Required to draw the floating bubble over other apps.
- `android.permission.FOREGROUND_SERVICE_LOCATION`: Required on Android 14+ for background location tracking.
- `android.permission.POST_NOTIFICATIONS`: Required on Android 13+ for foreground service notifications.

## Requirements

- Android 8.0 (API level 26) or higher.
- Java 21 / OpenJDK 21.
- Android SDK Platform 34 or 37.

## Build and Installation

### Build Debug APK

```bash
./gradlew assembleDebug
```

The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install via ADB

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Grant permissions via ADB (Optional for automated testing)

```bash
adb shell pm grant com.speedy.app android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.speedy.app android.permission.ACCESS_COARSE_LOCATION
adb shell pm grant com.speedy.app android.permission.POST_NOTIFICATIONS
adb shell appops set com.speedy.app SYSTEM_ALERT_WINDOW allow
```

### Launch Application

```bash
adb shell am start -n com.speedy.app/.MainActivity
```

# Speedy

**Speedy** is a modern, lightweight GPS speedometer for Android built with Jetpack Compose. It features an interactive floating speed bubble that stays on top of any navigation or mapping app, with pure OLED contrast and fluid Liquid Glass aesthetics.

## Features

- **Floating Bubble Overlay**: Always-on-top draggable speedometer running via Android foreground service with live speed and telemetry LED.
- **Visual Styles & Customization**:
  - **Aura**: Atmospheric Liquid Glass with dynamic optical blur, specular reflections, and ambient glows.
  - **Minimal**: Ultra-clean deep OLED obsidian black surface (`#030508`) with razor-sharp contrast.
  - **6 Accent Colors**: Cyan, Cobalt, Mint, Amber, Violet, Crimson — harmonized across all controls, telemetry units, and badges.
- **Bottom Navigation Dock**: Floating pill dock with spring physics connecting Dashboard, Statistics, Style, and Settings.
- **Accurate Telemetry**: Real-time GNSS satellite tracking, stationary drift filtering, distance, average and max speed calculations.
- **Supported Units**: km/h, mph, m/s with instant switching.
- **Drive Simulation Mode**: Built-in test generator for development and indoor testing.
- **Bilingual Interface**: English & Polish.

## Requirements

- Android 8.0 (API 26) or higher.
- JDK 21.

## Build & Install

```bash
# Build debug APK
./gradlew assembleDebug

# Install via ADB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Permissions

- `ACCESS_FINE_LOCATION`: High-precision GNSS positioning.
- `SYSTEM_ALERT_WINDOW`: Floating speedometer overlay over other apps.
- `FOREGROUND_SERVICE_LOCATION` & `POST_NOTIFICATIONS`: Continuous background telemetry tracking.

# Mobile POS

A point-of-sale Android application built with Jetpack Compose, Room, Hilt, and Kotlin.

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose with Material3
- **Database:** Room
- **DI:** Hilt
- **Barcode:** ZXing Android Embedded + CameraX
- **Charts:** Vico (Compose-native)

## Build

```bash
# Assemble debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run lint checks
./gradlew lintDebug
```

## Architecture

| Layer | Path | Description |
|-------|------|-------------|
| Data | `data/local/` | Room entities, DAOs, Database |
| Repository | `data/repository/` | Business logic, data access |
| Domain | `domain/` | Domain models, utilities |
| UI | `ui/` | Compose screens, ViewModels, navigation |

The app uses a single-activity architecture with Jetpack Compose navigation.
Three main screens: Inventory, Sale, Report — accessible via adaptive bottom/side navigation.

## Requirements
- Android 6.0+ (API 23)
- Camera (optional — for barcode scanning)

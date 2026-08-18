# TeleChat

Mobile Telegram Bot Live Chat Management app — real-time conversations, auto-replies, keyword triggers, and broadcast messaging. Built with Jetpack Compose, Room, and Navigation Compose.

## Build

This repo builds automatically via GitHub Actions on every push. The debug APK is uploaded as a workflow artifact (Actions tab → latest run → Artifacts → `app-debug-apk`).

To build locally (JDK 17 required, no Android Studio needed):

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Stack

- Kotlin 2.0.21, AGP 8.5.2, Gradle 8.7
- Jetpack Compose + Material 3
- Room (local persistence)
- Navigation Compose

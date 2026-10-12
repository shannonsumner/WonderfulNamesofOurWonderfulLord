# Wonderful Names of Our Wonderful Lord

An Android app built with Kotlin and Jetpack Compose. The app opens on a curtain inspired by the companion web app, with custom typography, decorative artwork, and a matching Android launch splash screen.

The curtain adapts to the available app-window width, using the web app's breakpoints in Android density-independent pixels (`dp`): phone at up to 600 dp, tablet at 601–1024 dp, and a centered large-window layout above 1024 dp. Font sizes, spacing, stripes, and artwork sizing change with the layout, including when rotating or resizing the window. Landscape windows no more than 600 dp tall use smaller text, tighter spacing, and height-limited artwork; the text can scroll if the available height or enlarged system font requires it. Portrait and taller landscape layouts are unchanged. `CurtainScreen.kt` includes previews for each layout.

## Requirements

- Android Studio with Android SDK platform 37 installed
- JDK 11 or a compatible JDK supported by the Gradle wrapper

## Build and run

Open the project in Android Studio, allow Gradle to sync, and run the `app` configuration on an emulator or Android device.

You can also build a debug APK from the command line:

```sh
./gradlew assembleDebug
```

On Windows, use:

```bat
gradlew.bat assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Project structure

- `app/` — Android application module
- `app/src/main/java/` — Kotlin activity and Compose screens
- `app/src/main/res/` — strings, themes, colors, and launcher assets
- `gradle/libs.versions.toml` — dependency and plugin versions

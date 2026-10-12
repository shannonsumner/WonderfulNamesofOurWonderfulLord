# Wonderful Names of Our Wonderful Lord

An Android app project built with Kotlin and Jetpack Compose. The current app is a minimal starter screen; its content is “Hello World!” while the project is being developed.

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
- `app/src/main/java/` — Kotlin and Compose UI
- `app/src/main/res/` — strings, themes, colors, and launcher assets
- `gradle/libs.versions.toml` — dependency and plugin versions

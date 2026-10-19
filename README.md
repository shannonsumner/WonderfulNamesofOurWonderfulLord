# Wonderful Names of Our Wonderful Lord

An Android app built with Kotlin and Jetpack Compose. The app opens on a curtain inspired by the companion web app, with custom typography, decorative artwork, and a matching Android launch splash screen.

The curtain adapts to the available app-window width, using the web app's breakpoints in Android density-independent pixels (`dp`): phone at up to 600 dp, tablet at 601–1024 dp, and a centered large-window layout above 1024 dp. Font sizes, spacing, stripes, and artwork sizing change with the layout, including when rotating or resizing the window. Landscape windows no more than 600 dp tall use smaller text, tighter spacing, and height-limited artwork; the text can scroll if the available height or enlarged system font requires it. Portrait and taller landscape layouts are unchanged. `CurtainScreen.kt` includes previews for each layout.

On launch, the curtain covers `DailyReadingPanel`, defined in its own Kotlin
file. Matching the site's `DailyReading.css`, it shows the curtain's striped
background with an empty translucent panel (up to 960 dp wide, 6 dp corners,
soft drop shadow). The panel stays clear of the system bars and has 24 dp of
outer padding on phones and tablets (40 dp above 1024 dp wide).

Matching the companion site's `Curtain.css`, the curtain waits 1 second,
then moves upward by 110% of its height over 3.5 seconds with linear easing and
a soft, 28 dp bottom-edge gradient shadow using the site's 28%-black color.
The explicit gradient keeps the shadow consistent across Android versions
without relying on platform elevation lighting.
The animation follows Android's system animator-duration scale
(including skipping motion when animations are disabled). Once raised, the
curtain is removed and stays dismissed across activity recreation, such as
rotation. A fresh launch without restored state shows the curtain again.

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

## Devotional database

The app bundles all 366 devotionals as data in
`app/src/main/assets/databases/daily-devotionals.json`, not as a SQLite database.
Room creates a new database in the app's private database directory on first use
and imports the JSON in its database-creation transaction. If importing fails,
creation rolls back and the error propagates rather than leaving a partial database.
Database access is available through:

```kotlin
val dao = DevotionalDatabase.getInstance(context).dailyDevotionalDao()
val devotional = dao.getByDayOfYear(1) // Call from a coroutine; days are 1 through 366.
```

Import `com.shannonsumner.namesofjesus.data.DevotionalDatabase`. The DAO also
provides `getAll()`, ordered by day. Nullable source fields remain nullable.
Day numbers are preserved exactly as supplied; calendar/leap-year mapping is
the caller's responsibility.

The JSON was exported from the companion site's `data/daily-devotional.sqlite`
without modifying the original. All row values are unchanged. Room owns the
version-1 schema, including the non-null `day_of_year` primary key, and its
generated versioned schema is stored in `app/schemas/`. Room still uses SQLite
internally, but no source SQLite file is shipped or opened by the app.

Future schema changes require a Room version increment and a migration.
The seed runs only when creating a database; changing the JSON alone does not
update existing installations.

## KJV Bible database

`KjvDatabase` is a separate version-1 Room database. On first database access,
Room creates its tables and imports 31,102 verses from
`app/src/main/assets/databases/kjv-verses.jsonl` (one JSON object per line)
and all source metadata from `kjv-metadata.json`. Verses are streamed using a
reusable insert statement inside Room's creation transaction. Import errors
propagate and roll back creation. No source SQLite file is bundled or modified.

```kotlin
val bible = KjvDatabase.getInstance(context).bibleDao()
val verse = bible.getVerse(book = 43, chapter = 3, verse = 16)
val chapter = bible.getChapter(book = 43, chapter = 3)
val passage = bible.getPassage(book = 43, chapter = 3, firstVerse = 16, lastVerse = 18)
```

Import `com.shannonsumner.namesofjesus.data.KjvDatabase` and call DAO methods
from a coroutine. Book numbers are the source's 1-based IDs (1 = Genesis,
43 = John, 66 = Revelation). Verse IDs, text (including any markup), metadata,
and the four source verse indexes are preserved. Metadata is available through
`getMetadata()`, including the source's copyright statement and usage restrictions.
The source states that this Bible is public domain in most parts of the world,
but under perpetual Crown copyright in the United Kingdom.

The Room schema is exported to `app/schemas/` under the `KjvDatabase` directory.
As with the devotional database, later schema changes need migrations, and seed
changes alone do not update existing databases. The curtain UI does not yet
read either database.

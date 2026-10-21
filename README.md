# Wonderful Names of Our Wonderful Lord

An Android app built with Kotlin and Jetpack Compose. The app opens on a curtain inspired by the companion web app, with custom typography, decorative artwork, and a matching Android launch splash screen.

The curtain adapts to the available app-window width, using the web app's breakpoints in Android density-independent pixels (`dp`): phone at up to 600 dp, tablet at 601–1024 dp, and a centered large-window layout above 1024 dp. Font sizes, spacing, stripes, and artwork sizing change with the layout, including when rotating or resizing the window. Landscape windows no more than 600 dp tall use smaller text, tighter spacing, and height-limited artwork; the text can scroll if the available height or enlarged system font requires it. Portrait and taller landscape layouts are unchanged. `ui/curtain/CurtainScreen.kt` includes previews for each layout.

On launch, the curtain covers `DailyReadingPanel`, defined in its own Kotlin
file. Matching the site's `DailyReading.css`, it shows the curtain's striped
background with a translucent panel holding the date controls and the day's
devotional (up to 960 dp wide, 6 dp corners,
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

## Daily reading

The daily reading mirrors the companion site's `DailyReading.tsx`. Behavior,
layout, and styling are kept in separate files (Compose has no CSS, so the
usual Android split is a ViewModel for logic, stateless composables for layout,
and style objects for appearance):

| Concern | File | Web equivalent |
| --- | --- | --- |
| Date → devotional lookup, leap years | `data/DevotionalRepository.kt` | `lib/dailyDevotional.ts` |
| HTML sanitizing and parsing ([Jsoup](https://jsoup.org)) | `data/DevotionalHtml.kt` | `sanitize-html`, `entities` |
| Screen state (date, devotional, tapped scripture) | `ui/dailyreading/DailyReadingViewModel.kt` | `useState`/`useEffect` |
| Layout | `ui/dailyreading/DailyReading.kt`, `DailyReadingPanel.kt` | JSX |
| Fonts, sizes, colors, spacing, link styling | `ui/dailyreading/DailyReadingStyle.kt` | `DailyReading.css` |
| Verse selection parsing and passage lookup | `data/ScriptureRepository.kt` | `lib/kjv.ts`, `/api/scripture` |
| Scripture popup state, layout, and style | `ui/scripture/ScriptureDialogState.kt`, `ScriptureDialog.kt`, `ScriptureDialogStyle.kt` | `ScriptureModal.tsx`, `Modal.tsx`, CSS |

**Leap years.** The data has 366 days and no year. Every date is looked up as
if it were in 2024 (a leap year), so February 29 is always day 60 and March 1
is always day 61. In common years, the February 29 reading is skipped.

**HTML.** Jsoup parses the HTML, decodes entities, and removes disallowed tags
with a `Safelist`; `DevotionalHtml` then turns the result into styled text.
(Compose's `AnnotatedString.fromHtml` isn't used because it collapses poetry
whitespace and can't turn `button[data-scripture]` into scripture links.) Like the site, only `b`/`strong`, `i`/`em`, `u`, `sup`, `sub`, `br`,
and `button[data-scripture]` are kept; other tags are removed but their text is
kept (`script`, `style`, and similar tags are removed with their content).
Entities such as `&hellip;` are decoded. If a field contains single line breaks
(poetry), its spacing and line breaks are kept exactly, like CSS
`white-space: pre-wrap`; otherwise paragraphs are split on blank lines and
whitespace is collapsed. Scripture buttons become tappable links (for example,
`data-scripture="50:2:9"` becomes Philippians 2:9). Tapping a link or the main
scripture opens the scripture popup. Share buttons are not implemented yet.

**Scripture popup.** The website's modal is a standard Material `AlertDialog`
showing the reference and each KJV verse with a superscript verse number. Back,
tapping outside, or **Close** dismisses it. The ViewModel shows a spinner while
the verses load and a message if the passage can't be found. Verse selections
are parsed like the website (`16`, `16-18`, `4-5,9`; verses 1–176; selection
order; no duplicates), and the whole passage is unavailable if any verse is
missing. Unlike the website, a leading chapter matching the reference's chapter
is accepted, because one source entry (2 Timothy 4) uses `4:1-2`. As on the
website, a leading `¶` is removed and other KJV markers (`[ ]`, `‹ ›`) are
shown as-is.

Returning to the app resets the date to today, and the reading scrolls back to
the top when the day changes.

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
- `app/src/main/res/` — strings, fonts, the XML splash/launcher theme, and launcher assets
- `gradle/libs.versions.toml` — dependency and plugin versions

Kotlin sources live in `app/src/main/java/com/shannonsumner/namesofjesus/`.
Each screen keeps logic, layout, and style in separate files, the usual
Jetpack Compose split (the web app's equivalent is TS logic, JSX, and CSS):

- **Logic**: ViewModels, state holders, and plain functions with no Compose UI,
  so they can be unit tested.
- **Layout**: `@Composable` functions that arrange content and pass events up.
- **Style**: `*Style.kt` files and `ui/theme/` hold colors, fonts, sizes, and
  text styles. Layout files don't hard-code these values.

| Package | Contents |
| --- | --- |
| `WonderfulNamesApplication.kt` | Application class that starts Hilt |
| `MainActivity.kt` | Entry point; applies `WonderfulNamesTheme` |
| `di/` | Hilt module that provides the databases, repositories, and clock |
| `data/` | Room databases, repositories (devotional lookup, scripture passages), and HTML parsing |
| `ui/theme/` | App colors (`Color.kt`), font families (`Type.kt`), web breakpoints (`WindowWidth.kt`), `WonderfulNamesTheme` |
| `ui/components/` | Striped page background shared by the curtain and the reading page |
| `ui/main/` | `MainScreen` and the curtain-raise animation (`MainScreenStyle.kt` holds its timing and shadow) |
| `ui/curtain/` | `CurtainScreen` (layout) and `CurtainStyle.kt` (sizes and text styles per window size) |
| `ui/datenav/` | `DateNav` (layout), `DateNavStyle.kt`, and `CalendarMonth.kt` (calendar grid, date selection, and the saveable `DatePickerState`) |
| `ui/dailyreading/` | Daily reading ViewModel, layout, and style (see above) |
| `ui/scripture/` | Scripture popup state, `ScriptureDialog` (layout), and `ScriptureDialogStyle.kt` |

### Dependency injection (Hilt)

The app uses [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
to create shared objects, so classes ask for what they need in their constructor
instead of building it themselves:

- `WonderfulNamesApplication` (`@HiltAndroidApp`) starts Hilt, and `MainActivity`
  (`@AndroidEntryPoint`) lets its composables use Hilt.
- `di/DataModule.kt` tells Hilt how to build each database (one instance per app),
  which `DevotionalRepository` and `ScriptureRepository` implementations to use
  (the Room-backed classes), and which `Clock` to use for "today".
- `DailyReadingViewModel` is a `@HiltViewModel` with an `@Inject` constructor;
  `DailyReadingPanel` gets it with `hiltViewModel()`.

Because the ViewModel depends on repository interfaces and a `Clock`, unit tests
pass in simple fakes. Device tests that show `hiltViewModel()` screens are
`@HiltAndroidTest` classes that use `HiltAndroidRule` and the debug-only
`HiltTestActivity`; `HiltTestRunner` runs all device tests in Hilt's test application.

Colors are defined in `ui/theme/Color.kt`. The only XML color is
`curtain_background`, used by the splash screen and launcher icon; keep it in
sync with `CurtainGreen`.

## Devotional database

The app bundles all 366 devotionals as data in
`app/src/main/assets/databases/daily-devotionals.json`, not as a SQLite database.
Room creates a new database in the app's private database directory on first use
and imports the JSON in its database-creation transaction. If importing fails,
creation rolls back and the error propagates rather than leaving a partial database.
Hilt provides one database instance for the app. To use the DAO, ask for it in
an `@Inject` constructor:

```kotlin
class MyRepository @Inject constructor(private val dao: DailyDevotionalDao) {
    suspend fun firstDay() = dao.getByDayOfYear(1) // Days are 1 through 366.
}
```

The DAO also
provides `getAll()`, ordered by day. Nullable source fields remain nullable.
Day numbers are preserved exactly as supplied. To look up by calendar date, use
`DevotionalRepository` (see [Daily reading](#daily-reading)), which handles leap years.
Tests can build their own copy with `Room.inMemoryDatabaseBuilder` and
`DevotionalDatabase.seedCallback(context)`.

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
// bible is a BibleDao requested in an @Inject constructor.
val verses = bible.getVerses(book = 43, chapter = 3, verses = listOf(16, 17, 18))
val count = bible.getVerseCount()
```

Call DAO methods from a coroutine. Book numbers are the source's 1-based IDs (1 = Genesis,
43 = John, 66 = Revelation). Verse IDs, text (including any markup), metadata,
and the four source verse indexes are preserved. `getVerses` returns rows in
database order, so callers sort or reorder them as needed. The source metadata,
including its copyright statement and usage restrictions, is stored in the
`meta` table. The app doesn't read it, so `BibleDao` has no query for it.
The source states that this Bible is public domain in most parts of the world,
but under perpetual Crown copyright in the United Kingdom.

The Room schema is exported to `app/schemas/` under the `KjvDatabase` directory.
As with the devotional database, later schema changes need migrations, and seed
changes alone do not update existing databases. The scripture popup reads it
through `ScriptureRepository`. The daily reading screen opens the database when
it starts, so the first-run import usually finishes before a passage is tapped.

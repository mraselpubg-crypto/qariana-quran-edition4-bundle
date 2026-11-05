# Qariana Quran — Multi-Edition & Hafezi Vector Architecture
## Master Prompt & Technical Brief for Claude 3.7 Sonnet / Advanced AI Architect

> **Instructions for the User:**
> Copy this entire prompt and submit it to Claude AI along with the GitHub link:
> `https://github.com/mraselpubg-crypto/qariana-quran-edition4-bundle`

---

```markdown
# Role & Mission
You are a Principal Android Architect (Kotlin + Jetpack Compose) and Senior Systems Engineer.
You are tasked with providing the exact, production-ready, fully-tested code diffs and architectural implementation for integrating the **4th Edition (Hafezi 15-Line & 14-Line Vector Mushaf)** and future Quran editions into the **Official Qariana Publications & Quran Reader Application** (`com.karianaquran.reader`).

---

## 1. Resolution of Blockers (Everything is Provided in the Repository!)

In response to your initial review regarding the missing buildable project and datasets:

1. **Full Buildable Android Studio Project (Provided in `3_android_app_source/`):**
   - You now have the **complete, buildable Gradle project tree**:
     - `build.gradle.kts` (Root project build script)
     - `settings.gradle.kts` (Configured with `:app` module)
     - `gradle.properties` (JVM & Android optimization flags)
     - `gradle/wrapper/` & `gradlew` / `gradlew.bat`
     - `app/build.gradle.kts` (All Compose, Hilt, Firebase, and Kotlin dependencies)
     - `app/src/main/AndroidManifest.xml` (Full manifest with `FLAG_SECURE` rules)
     - `app/src/main/java/com/karianaquran/reader/` (Complete Kotlin codebase across `ui/reader/`, `ui/settings/`, `ui/home/`, `data/`, `viewmodel/`)
     - `app/src/main/res/` and `app/src/main/assets/` (Full drawables, fonts, strings, layouts, and page caches).

2. **Complete 611-Page 15-Line Hafezi Mushaf Dataset (Provided in `2_web_reader_codebase/data/qudratullah_pages/`):**
   - Contains all **611 JSON page files** (`1.json` through `611.json`), covering the entire Quran from Surah Al-Fatiha to Surah An-Nas.
   - Each page contains line-by-line Arabic words, glyphs, tajweed classifications, and Bengali translations.

3. **Status of the 14-Line Layout (`layout14/`):**
   - The 15-line layout (`qudratullah_pages`, 611 pages) is the primary printed Quran standard.
   - The 14-line layout is an optional view mode generated via `tools/build_layout_pack.py` using `data/layout14/info.json`, `surah-pages.json`, `juzs.json`, and template SVGs (`assets/bg/page-bg-standard-14-*.svg`). The template SVGs and configuration are already fully present.

---

## 2. Core Architectural Directive: Standalone Web vs. In-App Viewport

Please inspect the two reference images in `4_screenshots_reference/`:
- **Image 1 (`image_1_full_web_reader.png`):** The standalone web reader interface with top chrome, Surah/Juz dropdowns, mode buttons, 15/14 line buttons, and bottom dock.
- **Image 2 (`image_2_target_mushaf_frame_only.png`):** The **TARGET IN-APP VIEWPORT**. In the Android app, the WebView must **ONLY** render the inner Mushaf page SVG container/template frame (`#mushaf-viewport`).

> ⚠️ **CRITICAL DIRECTIVE:**
> Inside Android:
> - The WebView must **NEVER** render top headers, bottom pagination, or web dropdowns.
> - **Native Android Jetpack Compose** handles all UI:
>   - Compose `ReaderTopBar`: Surah/Para selection, Bookmark, Audio, Search.
>   - Compose `ReaderBottomDock`: Page number slider, Pager, Zoom controls.
>   - Compose `SettingsSheet`: 
>     - **Line Switch**: হাফেজী ১৫ লাইন vs হাফেজী ১৪ লাইন.
>     - **Mode Switch**: শুধু আরবি vs আরবি + বাংলা (শব্দার্থ) vs আরবি + উচ্চারণ.
>     - **Tajweed Switch**: রঙিন তাজবীদ অন/অফ.
> - The Android WebView communicates via a high-performance `@JavascriptInterface` bridge (`AndroidBridge` ↔ `window.QuranPageRenderer`).

---

## 3. What You Must Provide (Complete, Tested Code Diffs)

Now that you have the complete buildable Android project and full 611-page dataset, please provide the exact code changes and file implementations:

### A. Clean WebView Viewport Engine
- Exact changes to `assets_mushaf/` (HTML/JS/CSS) to render in pure headless frame mode when invoked from Android, exposing:
  - `window.QuranPageRenderer.renderPage(pageNumber, config)`
  - `window.QuranPageRenderer.setLayout(lineCount)` (15 vs 14)
  - `window.QuranPageRenderer.setMode(readingMode)` (ARABIC_ONLY, ARABIC_MEANING, ARABIC_TRANSLIT)
  - `window.QuranPageRenderer.setTajweed(isEnabled)`
  - Bi-directional callbacks to Kotlin: `onWordClicked(wordData)`, `onAyahClicked(surah, ayah)`, `onPageRendered()`.

### B. Kotlin / Jetpack Compose Integration (Against `3_android_app_source/`)
Provide exact file-by-file code diffs for:
1. `app/src/main/java/com/karianaquran/reader/ui/reader/ReaderScreen.kt`:
   - Connecting the 4th Edition (Hafezi 15/14-line) to the Compose Pager.
   - Adding 15-Line / 14-Line toggles and Mode toggles to the Compose `SettingsSheet` or Top/Bottom controls.
2. `app/src/main/java/com/karianaquran/reader/ui/reader/MushafWebView.kt`:
   - Supporting the new JavaScript interface commands and word tap events.
3. `app/src/main/java/com/karianaquran/reader/ui/reader/QuranEditionSheet.kt`:
   - Showcasing all 4 Editions cleanly, with architecture ready for Edition 5, 6, etc.
4. `app/src/main/java/com/karianaquran/reader/data/model/Models.kt` & `SettingsStore.kt`:
   - Updating `ReadingMode` and persistent user preferences.

### C. Subscription & 10-Page Free Preview Enforcement
- Ensuring Pages 1–10 are accessible for guest users without credentials.
- When swiping to Page 11+, smoothly displaying the Native Credential Login / Purchase BottomSheet, preventing any visual bypass.

### D. Mandatory Update Fail-Safe
- Review `UpdateRepository.kt` and confirm it strictly forces the non-dismissible update dialog whenever `latestVersionCode > currentVersionCode` and `mandatory == true`.

Please provide production-ready Kotlin and JavaScript code diffs that can be applied directly to the codebase.
```

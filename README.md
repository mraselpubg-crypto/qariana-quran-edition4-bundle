# 📖 Qariana Quran — Edition 4 Architecture & Codebase Bundle

[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Web-emerald)](https://karianaquran.com)
[![Language](https://img.shields.io/badge/Language-Kotlin%20%7C%20Compose%20%7C%20JavaScript-blue)](https://karianaquran.com)
[![License](https://img.shields.io/badge/License-Proprietary-gold)](https://karianaquran.com)

This repository contains the complete codebase, reference UI screenshots, APK builds, and architectural specifications for integrating **Edition 4 (Hafezi 15-Line & 14-Line Vector Mushaf)** and future Quran editions into the **Official Qariana Quran Native Android Application** (`com.karianaquran.reader`).

---

## 📌 Quick Access Links for AI Reviewers (Claude 3.7 / External AI)

- 🤖 **[PROMPT_FOR_CLAUDE_AI.md](./PROMPT_FOR_CLAUDE_AI.md)**: **The Primary Master Prompt** to feed into Claude AI.
- 📐 **[PROJECT_ARCHITECTURE_AND_ROADMAP.md](./PROJECT_ARCHITECTURE_AND_ROADMAP.md)**: Exhaustive architectural roadmap, database schema, and bridge design.
- 📱 **[Android App Kotlin Source](./3_android_app_source/)**: Native Jetpack Compose UI, ViewModels, and Compose-to-WebView bridge.
- 🌐 **[Web Reader Codebase](./2_web_reader_codebase/)**: High-performance SVG vector Mushaf renderer with 15/14 lines, Tajweed colors, Word-by-Word Bengali meanings, and dynamic layout scaling.
- 🖼️ **[Screenshots & Visual Specs](./4_screenshots_reference/)**: Visual comparison showing how the Web Reader transforms into Native Android Compose UI.
- 📦 **[APKs](./1_APKs/)**: Previous release (v1.3.11) and current build (v1.3.12).

---

## 🎯 The Core Architectural Mission

### Visual Comparison: Standalone Web vs. Native Android Viewport

| Standalone Web Reader (Image 1) | Target Android Compose Viewport (Image 2) |
| :---: | :---: |
| ![Full Web Reader](./4_screenshots_reference/image_1_full_web_reader.png) | ![Target In-App Frame Only](./4_screenshots_reference/image_2_target_mushaf_frame_only.png) |
| **All controls on web page** (Surah/Para dropdowns, 15/14 line buttons, Mode buttons, bottom pagination) | **ZERO web controls**. ONLY the pure Mushaf SVG page frame renders. Native Jetpack Compose handles TopBar, BottomDock, and Sheets! |

### Fundamental Rules:
1. **Clean Viewport Inside Android**: In the Android app, the WebView must **NEVER** render top headers, bottom pagination bars, or HTML modal popups. It must ONLY render the beautiful SVG Mushaf page frame (`#mushaf-viewport`).
2. **Native Controls**:
   - 15-Line vs 14-Line switch: Controlled via Native Jetpack Compose Bottom Bar / Settings Sheet.
   - Reading Mode (Arabic Only / Arabic + Meaning / Arabic + Pronunciation): Controlled via Native Compose UI.
   - Color Tajweed Toggle: Controlled via Native Compose UI.
   - Surah / Para / Page jumps: Handled by Compose Top Bar and Surah Index Bottom Sheet.
3. **Multi-Edition Scalability**: The app currently has 3 editions (`ortho_quran`, `tajweed_quran`, `word_by_word`). We are integrating **Edition 4 (`hafezi_15_14_lines`)** and setting up a modular architecture for Edition 5, Edition 6, etc.
4. **Subscription & 10-Page Free Preview**: Pages 1 to 10 are free to read. Swiping to Page 11 displays the native purchase/unlock modal unless unlocked with user credentials (`Mobile Number + License Key KQ-XXXX-XXXX-XXXX`).
5. **Mandatory In-App Update**: Whenever a new APK version is released, previous versions must strictly enforce a mandatory update dialog blocking access until updated.

---

## 📂 Repository Structure

```
qariana-quran-edition4-bundle/
├── README.md                                  # Repository overview (this file)
├── PROMPT_FOR_CLAUDE_AI.md                    # Master prompt for Claude AI review
├── PROJECT_ARCHITECTURE_AND_ROADMAP.md        # Complete technical specification
├── 1_APKs/                                    # Android APK builds
│   ├── QarianaQuran-v1.3.11-stable-previous.apk  (41.7 MB - v1.3.11 stable)
│   └── QarianaQuran-v1.3.12-latest-build.apk     (39.8 MB - v1.3.12 latest)
├── 2_web_reader_codebase/                     # Standalone 15/14 Line Vector Engine
│   ├── index.html                             # Web reader entrypoint
│   ├── assets/
│   │   ├── css/                               # Responsive Mushaf styles & animations
│   │   ├── fonts/                             # Custom glyph Arabic & Kalpurush fonts
│   │   ├── js/                                # Dynamic layout generator & SVG renderer
│   │   └── mushaf_bg/                         # 12 high-res SVG decorative borders
│   └── data/                                  # Quran metadata & Word-by-word JSON datasets
├── 3_android_app_source/                      # Native Android Kotlin App Code
│   ├── ReaderScreen.kt                        # Primary Compose Reader UI
│   ├── MushafWebView.kt                       # Android WebView wrapper & JS Interface
│   ├── QuranEditionSheet.kt                   # 4-Edition Selector BottomSheet
│   ├── SettingsSheet.kt                       # Native Reading Mode & Line Count switches
│   ├── SettingsStore.kt                       # DataStore persistence for user preferences
│   ├── Models.kt                              # Domain models (ReadingMode, Edition, etc.)
│   ├── UpdateRepository.kt                    # Remote version check & mandatory update logic
│   ├── build.gradle.kts                       # App Gradle configuration
│   └── assets_mushaf/                         # Embedded offline web assets in Android
└── 4_screenshots_reference/                   # 10 High-Resolution UI Screenshots
    ├── image_1_full_web_reader.png            # Full web reader reference
    ├── image_2_target_mushaf_frame_only.png   # Target in-app viewport reference
    ├── p1_fatiha_opening_arch.png             # Surah Al-Fatiha ornamental arch
    ├── p2_baqarah_opening_arch.png            # Surah Al-Baqarah ornamental arch
    ├── p6_15line_arabic_only.png              # Standard 15-line Hafezi Mushaf page
    ├── p6_14line_mode.png                     # Dynamic 14-line layout mode
    ├── p6_arabic_plus_meaning.png             # In-line Bengali word meaning layout
    ├── p6_word_selected.png                   # Interactive word tap & pronunciation
    ├── side_tajweed_legend_desktop.png        # Tajweed color legend design
    └── mobile_responsive_view.png             # Mobile responsive scaling
```

---

## 🚀 How to Run the Web Reader Locally

To preview the 15/14-line vector engine locally:
```bash
cd 2_web_reader_codebase
# Start a simple HTTP server (Python 3)
python -m http.server 8000
# Open in browser: http://localhost:8000
```

---

## 🔒 Copyright & Rights
© 2026 **Qariana Quran & Publications**. All rights reserved.
Developed by Rasel Gazi.

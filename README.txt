# Qariana Quran — AI Architecture Bundle

This bundle contains everything needed for Claude 3.7 Sonnet (or other leading AI) to analyze, architect, and provide the exact implementation for integrating the 4th Edition (Hafezi 15 & 14-Line Vector Mushaf) and future editions into the Native Android App.

## Structure:
- `PROMPT_FOR_CLAUDE_AI.md`: Master prompt to copy-paste into Claude along with this ZIP.
- `PROJECT_ARCHITECTURE_AND_ROADMAP.md`: Detailed technical specification of the project.
- `1_APKs/`: Stable previous APK (v1.3.11) and new APK (v1.3.12).
- `2_web_reader_codebase/`: Complete, functioning 15/14-line Hafezi web reader engine with SVG layouts, tajweed colors, transliteration, and Bengali meanings.
- `3_android_app_source/`: Full Kotlin Jetpack Compose code, ViewModels, WebView bridges, and assets.
- `4_screenshots_reference/`:
  - `image_1_full_web_reader.png`: Standalone web reader UI.
  - `image_2_target_mushaf_frame_only.png`: TARGET VIEW inside the Android app (ONLY the inner Mushaf page frame, with all controls handled by Native Compose UI).
  - 7 High-resolution screenshots of opening arches, 15-line, 14-line, modes, and mobile view.

### Key Requirement:
In the Android APK, the in-page reader area MUST ONLY show the pure SVG Mushaf page (image 2). All layout toggles (15 vs 14 lines), mode switches (Arabic / Meaning / Transliteration), and Tajweed switches must live in the Native Android Compose Top Bar and Settings Sheets!

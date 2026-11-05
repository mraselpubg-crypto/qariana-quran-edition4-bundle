# Qariana Quran — Multi-Edition & Hafezi Vector Architecture
## Master Prompt & Technical Brief for Claude 3.7 Sonnet / Advanced AI Architect

> **Instructions for the User:**
> Copy this entire prompt and attach the provided `qariana_edition4_ai_bundle.zip` file, then submit to Claude or your preferred AI.

---

```markdown
# Role & Mission
You are a Principal Android Architect (Kotlin + Jetpack Compose) and Senior Systems Engineer.
You are tasked with architecting and implementing a major milestone update for the **Official Qariana Publications & Quran Reader Application** (`com.karianaquran.reader`), an international-grade Islamic reading application deployed for Android (and future Desktop).

---

## 1. Project Background & Current State
The project consists of two core components:
1. **Native Android Application (`com.karianaquran.reader`):**
   - Built with **100% Kotlin + Jetpack Compose (Material 3)**.
   - Enforces kernel-level `FLAG_SECURE` (zero screenshots, zero screen recording, black-out on AnyDesk/TeamViewer/Zoom).
   - Enforces Hardware Device Binding (`MediaDrm` UUID + `ANDROID_ID`) allowing 1 active device per license key (`KQ-XXXX-XXXX-XXXX`).
   - Guest Mode: Pages 1–10 are 100% Free Preview. Pages 11+ are strictly locked behind License Login / In-App Checkout.
   - Currently features **3 Editions (Reading Modes)**:
     - **Edition 1 (জনপ্রিয় মুদ্রিত সংস্করণ - ۹৬৩ পৃষ্ঠা):** 1:1 InDesign scanned book replica, edge-to-edge cropped view, 7-color tajweed (`ReadingMode.CLASSIC`).
     - **Edition 2 (নতুন ভেক্টর সংস্করণ - ৬১০ পৃষ্ঠা):** Crisp InDesign vector Mushaf with word-by-word Bangla meanings and transliteration (`ReadingMode.PAGE`).
     - **Edition 3 (ডিজিটাল সংস্করণ - ৬,২৩৬ আয়াত):** Interactive Ayah card list with audio recitation, pronunciation, and expandable Tafsir (`ReadingMode.AYAH`).

2. **The New 4th Edition — Web Vector Reader Engine (`qariana-web-reader`):**
   - A standalone web reader built with pure HTML5, CSS3, ES Modules, and responsive SVG page frames.
   - Supports **Traditional Hafezi 15-Line** and **Hafezi 14-Line** layouts (610 pages, Qudratullah layout standard).
   - Supports **3 Reading Modes**:
     1. **শুধু আরবি (Arabic Only)**
     2. **আরবি + বাংলা (Arabic + Word-by-Word Bangla Meaning)**
     3. **আরবি + উচ্চারণ (Arabic + Pronunciation/Transliteration)**
   - Features dynamic **Tajweed Coloring** with responsive sidebar legends (desktop side panels, auto-hidden on mobile).
   - Word-level tap inspection with ayah audio and multi-source Bengali Tafsir.

---

## 2. The Core Problem & Architectural Challenge

### A. UI/UX Separation Dilemma (Image 1 vs Image 2)
Please inspect the two reference images provided in the bundle:
- **Image 1 (`web_full_view.png`):** Shows the standalone web reader interface with its own top chrome, Surah/Juz dropdowns, mode tabs, 15/14-line layout buttons, and bottom dock.
- **Image 2 (`target_inner_mushaf_frame_only.png`):** Shows **ONLY the inner Mushaf page SVG container/template frame**.

> ⚠️ **CRITICAL ARCHITECTURAL DIRECTIVE:**
> When the 4th Edition (and future editions) runs inside the Android App, **THE READER VIEWPORT MUST ONLY DISPLAY THE PURE MUSHAF PAGE SVG FRAME (Image 2)**.
> **DO NOT** embed the web reader's top bars, mode tabs, or bottom docks inside the Android reader viewport!
> In the Android app, all navigation, buttons, and switches must be **Native Android Jetpack Compose** components:
> 1. The **Top Bar** (`ReaderTopBar` in Compose) handles Surah/Para selection, Bookmark, Audio, and Quick Actions.
> 2. The **Bottom Dock** (`ReaderBottomDock` in Compose) handles page numbers, swipe/pager, and native zoom buttons.
> 3. The **Settings Drawer / Sheet** (`SettingsSheet` in Compose) handles:
>    - Layout switching (হাফেজী ১৫ লাইন vs হাফেজী ১৪ লাইন).
>    - Mode switching (শুধু আরবি vs আরবি+বাংলা vs আরবি+উচ্চারণ).
>    - Tajweed toggle (রঙিন তাজবীদ অন/অফ).
>    - Font size / zoom scaling.
> The Android `WebView` should act strictly as a **headless/dumb vector page canvas** controlled via a clean, high-performance bidirectional JavaScript interface (`AndroidBridge` ↔ `window.QuranApp`).

### B. Scalable Multi-Edition Engine (Extensible to Edition 5, 6, and Beyond)
Currently, `ReadingMode` is a hardcoded enum: `CLASSIC, PAGE, AYAH, HAFEZI`.
We need a **scalable, modular architecture** where future books and editions (e.g., Ampara, Qaida, 16-Line Mushaf, Tajweed Guide) can be added dynamically or with minimal boilerplate:
- Unified Edition Interface: `QuranEdition` (id, title, pageCount, layoutType, features, freePagesLimit, assetProvider).
- Asset Storage & Decryption: Offline page bundles, encrypted vaults, memory caching.
- Unified Pager Adapter: A Compose `HorizontalPager` that seamlessly switches rendering strategies based on the active edition.

### C. Unbreakable Mandatory In-App Update Enforcement
In previous releases, users running older builds (e.g. `v1.3.11`) did not receive the mandatory update blocker when `v1.3.12` was released.
- We discovered that `auto_send_apk_to_telegram.py` was using lowercase `-p 65002` for SCP (which failed silently because SCP requires capital `-P`), leaving the live server's `app-version.json` at older version 42.
- However, from an architectural standpoint, the Android App's `UpdateRepository.kt` must be **100% fail-safe and un-bypassable**:
  - If `latestVersionCode > currentVersionCode` and `isMandatory == true`, the app must display a non-dismissible dialog (no back press, no tap outside, no bypass) that immediately forces download/update.
  - Multi-endpoint fallback (`https://karianaquran.com/download/app-version.json`, Firebase Remote Config, and mirror API).

---

## 3. What You Must Provide
Please provide a comprehensive, production-ready architectural implementation:

1. **System Architecture & Data Flow Diagram (Mermaid):**
   - Show how Compose UI, `AppViewModel`, `PageRepository`, `WebViewAssetLoader`, and the inner SVG renderer interact.

2. **The Bidirectional Android-WebView Bridge Specification:**
   - Define the exact JavaScript methods exposed on `window.QuranPageRenderer` (e.g., `renderPage(pageJson, config)`, `setLayout(lines)`, `setMode(mode)`, `setTajweed(enabled)`, `setTheme(theme)`).
   - Define the exact `@JavascriptInterface` callbacks sent to Kotlin (e.g., `onWordClick(wordId, arabic, meaning)`, `onAyahClick(surah, ayah)`, `onPageReady()`, `onSwipeNext()`, `onSwipePrev()`).

3. **Optimized Stripped-Down SVG Canvas HTML/CSS/JS Template:**
   - Create the clean, stripped-down `hafezi_page_view.html` that contains **ONLY** the SVG frame, font rendering, and tajweed logic (zero extraneous header/footer controls).
   - Ensure 60fps pinch-to-zoom, hardware acceleration, and font fidelity for `qariana_secret_font.ttf`, `indopak_qul.woff2`, and `kalpurush.ttf`.

4. **Kotlin / Jetpack Compose Implementation:**
   - Updated `ReaderScreen.kt` showing how the Native Compose Top Bar and Settings Sheet control the Hafezi 15/14-line and 3 reading modes.
   - Updated `HafeziWebView.kt` (or generalized `VectorMushafWebView.kt`).
   - Clean `QuranEdition` data model to allow future editions (Edition 5, 6...) effortlessly.

5. **Security & Subscription Enforcement:**
   - Verify that guest users cannot view page 11+ in Hafezi mode without authentication.
   - Verify kernel `FLAG_SECURE` and memory eviction on app backgrounding.

6. **Step-by-Step Implementation Roadmap:**
   - Ordered list of files to create, edit, or remove, with exact code snippets.

---
```

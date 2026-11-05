# 🌟 Qariana Quran — Master Architecture, UI Polish & Multi-Edition Sync Prompt

> **Public GitHub Repository Link for Kimi AI:**
> `https://github.com/mraselpubg-crypto/qariana-quran-edition4-bundle`

---

```markdown
# Role & Project Scope
You are a Principal Android UI/UX Architect (Kotlin + Jetpack Compose) and Senior Mobile Systems Engineer.
You are tasked with providing the complete UI overhaul, multi-edition synchronization engine, and exact production-grade code diffs for the **Official Qariana Publications & Quran Reader Application** (`com.karianaquran.reader`).

---

## 1. Real-World Issues to Fix (Directly from User's Device Screenshots)

Please inspect the two screenshots in `4_screenshots_reference/`:

### 📸 Screenshot 1: Overview Screen (`user_device_screen_overview.jpg`)
1. **Top Bar Badge Clipping**: The yellow badge currently truncates to `নতুন সংস্ক` on mobile. Make the Top Bar cleanly responsive with zero clipping across all screen sizes.
2. **Centerpiece Edition Selector**: Currently only shows 3 editions (`জনপ্রিয়`, `নতুন`, `ডিজিটাল`). Add the 4th Edition: **`হাফেজী সংস্করণ` (১৫ ও ১৪ লাইন ভেক্টর)** and update the subtitle dynamically to `ক্বারীয়ানা পাবলিকেশন্স • ৪টি পূর্ণাঙ্গ সংস্করণ`.
3. **Surah & Para Sync**: When the user taps `সূরাসমূহ (১১৪)` or `পারাসমূহ (৩০)`, it must open the exact starting page for the currently active edition (610-page, 654-page, or 963-page layout).
4. **Membership Card Polish**: Elevate `ক্বারীয়ানা মেম্বারশিপ প্যাকেজসমূহ` (`বেসিক` | `স্ট্যান্ডার্ড` | `ভিআইপি`) with pristine Material 3 responsive styling and Kalpurush Bengali numbers (`০-৯`).

---

### 📸 Screenshot 2: Reader Settings & Theme Sheet (`user_device_screen_settings.jpg`)
1. **Cramped & Awkward Edition Selector under `পড়ার ভিউ মোড:`**:
   - Currently, 4 large buttons (`জনপ্রিয় সংস্করণ`, `নতুন সংস্করণ`, `ডিজিটাল সংস্করণ`, `হাফেজী সংস্করণ`) are squashed side-by-side in a single row.
   - Text is forced into awkward 2-line wraps and boxes are narrow and uneven.
   - **Requirement**: Replace this with an elegant **2x2 Grid** or sleek **Material 3 Segmented Filter Chips** with subtle icons, clean typography, and balanced touch targets so text never wraps unnaturally.
2. **Missing Dynamic Controls for Hafezi Edition**:
   - When **`হাফেজী সংস্করণ`** is active (or selected in this sheet), the sheet MUST dynamically reveal:
     - **লাইন বিন্যাস (Line Layout)**: Two clean pill buttons: `[ ১৫ লাইন (৬১০ পৃষ্ঠা) ]` | `[ ১৪ লাইন (৬৫৪ পৃষ্ঠা) ]`.
     - **পাঠের ধরন (Reading Mode)**: Three pill buttons: `[ শুধু আরবি ]` | `[ আরবি + বাংলা (শব্দার্থ) ]` | `[ আরবি + উচ্চারণ ]`.
     - **রঙিন তাজবীদ (Color Tajweed)**: A dedicated toggle switch with descriptive subtitle (`তাজবীদের নিয়ম অনুযায়ী রঙিন হরফ প্রদর্শন`).
3. **Pristine Typography & Themes**:
   - All Bengali numerals (`১০০%`, `১২৫%`, `১৫ লাইন`, `৬১০ পৃষ্ঠা`) must strictly use `Kalpurush` font.
   - Retain the 6 theme presets (`ডার্ক`, `লাইট`, `সেপিয়া`, `এমেরাল্ড`, `মিডনাইট`, `ক্রিম`) and custom color pickers.

---

## 2. Core Architectural Paradigm: The Unified Multi-Edition Web Canvas

### Visual Separation of Concerns (Image 1 vs Image 2 Target):
- Inside the Android Reader, the WebView acts strictly as a **headless vector page canvas** (`#mushaf-viewport`).
- **ZERO web controls** (no top navbar, no bottom pagination, no web dropdowns inside the WebView).
- **All Controls are 100% Native Jetpack Compose**:
  - `ReaderTopBar`: Home, Edition Badge, Surah/Para Selector, Bookmark, Audio Recitation, Settings.
  - `ReaderBottomDock`: Native page slider and zoom buttons.
  - `SettingsSheet`: 15 vs 14 Line toggle, 3 Reading Modes (Arabic Only / Arabic+Meaning / Arabic+Translit), Color Tajweed toggle, and Theme cycle.
  - Native Shell Enforcements: Kernel-level `FLAG_SECURE` (unbreakable anti-screenshot), Hardware Device Binding (`MediaDrm`), and SMS Sync Paywall.

---

## 3. Strict Entitlement & Guest Swipe Protection

1. **Guest Swipe Sentinel Fix (`pagerState.settledPage`)**:
   - Free users have access to Pages 1–10. Page 11 is the locked paywall sentinel.
   - When a guest swipes from Page 10 to Page 11, the app must wait until `pagerState.settledPage == 10` (0-indexed 10 = Page 11), then:
     - Set `showPaywallSheet = true` (opening the native Paywall BottomSheet).
     - Instantly scroll the pager back to `page = 9` (Page 10), preventing any blank or trapped state.
2. **PageRepository Entitlement Enforcement**:
   - `PageRepository.getPage()` and `prefetchSurrounding()` must strictly verify `session?.isUnlocked == true` before returning or caching Page 11+.

---

## 4. What You Must Provide (Complete, Tested Code Diffs)

Please output complete, production-grade Kotlin and Web/JS code diffs for:
1. **`SettingsSheet.kt`**:
   - Elegant 2x2 grid / segmented chip selector for the 4 editions.
   - Dynamic Hafezi section: 15/14 line toggle, 3 reading modes, and color tajweed switch.
2. **`QuranOverviewScreen.kt`**:
   - Non-clipping responsive TopBar.
   - 4-Edition Selector in the centerpiece hero card.
   - Synchronized Surah & Para list index.
   - Polished Membership card.
3. **`SurahParaIndexMapper.kt`**:
   - Canonical mapper linking all 114 Surahs and 30 Paras across 610, 654, and 963-page editions.
4. **`ReaderScreen.kt` & `MushafWebView.kt`**:
   - Single-row responsive Top Bar, settled-page paywall swipe fix, and bidirectional JavaScript bridge communicating with `#mushaf-viewport`.
```

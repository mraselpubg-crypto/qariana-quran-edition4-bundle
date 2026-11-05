# 🌟 Qariana Quran — Master Architecture, UI Polish & Multi-Edition Sync Prompt

> **Public GitHub Repository Link for Kimi AI:**
> `https://github.com/mraselpubg-crypto/qariana-quran-edition4-bundle`

---

```markdown
# Role & Project Scope
You are a Principal Android UI/UX Architect (Kotlin + Jetpack Compose) and Senior Mobile Systems Engineer.
You are tasked with providing the complete UI overhaul, multi-edition synchronization engine, and exact production-grade code diffs for the **Official Qariana Publications & Quran Reader Application** (`com.karianaquran.reader`).

---

## 1. Real-World Issues to Fix (Directly from User's Device Screenshot)

Please review the user's active mobile screen (`QuranOverviewScreen.kt`):

### Issue A: Top Bar Badge Clipping & Clutter
- Currently, the yellow badge in the top bar truncates to **`নতুন সংস্ক`** because of insufficient flex space and fixed width.
- The top bar has: `[← Back]`, Title `ক্বারীয়ানা অর্থ-কুরআন`, Yellow Badge, `[Info (i)]`, `[Download ↓]`, `[Search 🔍]`.
- **Requirement**:
  - Make the Top Bar cleanly responsive with zero text truncation.
  - Separate actions clearly into distinct icon buttons with adequate touch targets (48dp).
  - Use a sleek, non-clipping Edition Pill Badge (e.g. `[📖 নতুন সংস্করণ]` with auto-shrink font or icon-first badge) that never clips on any device screen width.

### Issue B: Centerpiece Edition Selector Missing 4th Edition
- The centerpiece card currently shows only 3 buttons:
  `[জনপ্রিয় সংস্করণ]` | `[নতুন সংস্করণ]` | `[ডিজিটাল সংস্করণ]`.
- Subtitle states: `ক্বারীয়ানা পাবলিকেশন্স • ৩টি পূর্ণাঙ্গ সংস্করণ`.
- **Requirement**:
  - Add **Edition 4: `হাফেজী সংস্করণ` (১৫ ও ১৪ লাইন ভেক্টর)** as a 4th tab.
  - Update subtitle dynamically to: `ক্বারীয়ানা পাবলিকেশন্স • ৪টি পূর্ণাঙ্গ সংস্করণ`.
  - Tapping `হাফেজী সংস্করণ` updates the 3D book spread preview and switches the active edition.

### Issue C: Canonical Index Sync (`সূরাসমূহ ১১৪` and `পারাসমূহ ৩০`)
- Under the top bar, there are 3 tabs: `ওভারভিউ` | `সূরাসমূহ (১১৪)` | `পারাসমূহ (৩০)`.
- **Requirement**:
  - The Surah and Para lists must be **100% synchronized** with whichever edition is currently selected.
  - When the user taps a Surah or Para, the reader must open at the **exact starting page** for that specific edition:
    - **610-page editions** (Edition 2 & Edition 4 15-line): Juz 30 opens at **Page 582**.
    - **654-page edition** (Edition 4 14-line): Juz 30 opens at **Page 621**.
    - **963-page edition** (Edition 1): Juz 30 opens at **Page 910**.
  - When switching editions from `সংস্করণ বিবরণ` (Edition Detail Screen), the current reading location (Surah & Ayah) must seamlessly map to the new edition's page.

### Issue D: Membership Card UI & Responsive Polish
- The bottom card `ক্বারীয়ানা মেম্বারশিপ প্যাকেজসমূহ` (`বেসিক` | `স্ট্যান্ডার্ড` | `ভিআইপি`):
  - Ensure pristine Material 3 card styling, golden/emerald gradients, consistent typography using `Kalpurush` for all Bengali numerals (`০-৯`), and smooth package selection without layout shifts.

---

## 2. Core Architectural Paradigm: The Unified Multi-Edition Web Canvas

### Visual Separation of Concerns (Image 1 vs Image 2 Target):
- Inside the Android Reader, the WebView acts strictly as a **headless/dumb vector page canvas** (`#mushaf-viewport`).
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

Please output complete, production-grade code for:
1. **`QuranOverviewScreen.kt`**:
   - Fixed TopBar with non-clipping edition pill badge and cleanly spaced icons.
   - 4-Edition Selector Tab Row (`জনপ্রিয়`, `নতুন`, `ডিজিটাল`, `হাফেজী`).
   - Synchronized Surah (`সূরাসমূহ ১১৪`) & Para (`পারাসমূহ ৩০`) tabs.
   - Polished `ক্বারীয়ানা মেম্বারশিপ প্যাকেজসমূহ` card.
2. **`SurahParaIndexMapper.kt`**:
   - Canonical mapper linking all 114 Surahs and 30 Paras across 610, 654, and 963-page editions.
3. **`ReaderScreen.kt` & `SettingsSheet.kt`**:
   - Single-row responsive Top Bar, settled-page paywall swipe fix, and dynamic settings (15/14-line and mode toggles).
4. **`MushafWebView.kt` & `mushaf_frame.html`**:
   - Headless frame-only vector canvas exposing `window.QuranPageRenderer.renderPage(payload)` and sending `@JavascriptInterface` callbacks.
```

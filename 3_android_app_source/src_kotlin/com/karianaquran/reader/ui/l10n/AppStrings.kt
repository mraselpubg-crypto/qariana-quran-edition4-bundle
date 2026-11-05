package com.karianaquran.reader.ui.l10n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

data class AppStrings(
    val isEnglish: Boolean,

    // Navigation / Bottom Bar
    val navHome: String,
    val navLibrary: String,
    val navQuran: String,
    val navProfile: String,

    // Top Bar
    val menu: String,
    val language: String,
    val settings: String,
    val premium: String,
    val bangladesh: String,

    // Prayer & Hero
    val listenAzan: String,
    val stopAzan: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val allPrayers: String,
    val prayerRemaining: (name: String, hours: Int, minutes: Int, seconds: Int) -> String,
    val nextPrayerLabel: (name: String, timeStr: String) -> String,

    // Ramadan Countdown
    val ramadanCountdown: String,
    val allTimings: String,
    val days: String,
    val hours: String,
    val minutes: String,
    val seconds: String,

    // Flagship Book Showcase
    val flagshipTitle: String,
    val flagshipBadge: String,
    val flagshipSubtitle: String,
    val flagshipIndicator: String,

    // 8-Feature Grid
    val featureQuran: String,
    val featurePrayer: String,
    val featureNames: String,
    val featureTasbih: String,
    val featureQibla: String,
    val featureDuas: String,
    val featurePillars: String,
    val featureMore: String,

    // Last Read & Tracker
    val lastReadTitle: String,
    val continueReading: String,
    val pageLabel: (num: Int) -> String,
    val dailyTrackerTitle: String,
    val history: String,
    val trackerCompleted: (done: Int, total: Int) -> String,

    // Publications
    val ourPublications: String,
    val viewAllBooks: String,
    val comingSoon: String,
    val comingSoonNotice: String,
    val ok: String,
    val cancel: String,

    // Drawer Menu
    val drawerTitle: String,
    val drawerSubtitle: String,
    val drawerHome: String,
    val drawerIndex: String,
    val drawerPrayer: String,
    val drawerQibla: String,
    val drawerTasbih: String,
    val drawerDua: String,
    val drawerHadith: String,
    val drawerNames: String,
    val drawerLibrary: String,
    val drawerSettings: String,
    val drawerAboutUs: String,
    val drawerSupport: String,
    val drawerVersion: String,

    // Surah Index Screen
    val indexTitle: String,
    val tabSurah: String,
    val tabPara: String,
    val tabPage: String,
    val searchPlaceholder: String,
    val makki: String,
    val madani: String,
    val ayahsCount: (count: Int) -> String,
    val paraLabel: (num: Int) -> String,

    // Profile & Settings
    val profileTitle: String,
    val accountInfo: String,
    val darkTheme: String,
    val darkThemeActive: String,
    val lightThemeActive: String,
    val readingMode: String,
    val modePage: String,
    val modeAyah: String,
    val modeClassic: String,
    val appLanguageTitle: String,
    val arabicFontTitle: String,
    val logout: String,
    val logoutConfirm: String,
)

val BnStrings = AppStrings(
    isEnglish = false,
    navHome = "হোম",
    navLibrary = "বইসমূহ",
    navQuran = "আল-কুরআন",
    navProfile = "প্রোফাইল",

    menu = "মেনু",
    language = "ভাষা",
    settings = "সেটিংস",
    premium = "প্রিমিয়াম",
    bangladesh = "বাংলাদেশ",

    listenAzan = "আযান শুনুন",
    stopAzan = "আযান থামান",
    fajr = "ফজর",
    sunrise = "সূর্যোদয়",
    dhuhr = "যোহর",
    asr = "আসর",
    maghrib = "মাগরিব",
    isha = "এশা",
    allPrayers = "সব ওয়াক্ত",
    prayerRemaining = { name, h, m, s ->
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        fun toBn(n: Int) = n.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
        "$name বাকি: ${toBn(h)}ঘ ${toBn(m)}মি ${toBn(s)}সে"
    },
    nextPrayerLabel = { name, timeStr -> "পরবর্তী ওয়াক্ত: $name ($timeStr)" },

    ramadanCountdown = "রমজান কাউন্টডাউন",
    allTimings = "সকল সময়",
    days = "দিন",
    hours = "ঘণ্টা",
    minutes = "মিনিট",
    seconds = "সেকেন্ড",

    flagshipTitle = "ক্বারীয়ানা অর্থো-কুরআন শরীফ",
    flagshipBadge = "৩০ পারা পূর্ণাঙ্গ",
    flagshipSubtitle = "সাংকেতিক চিহ্নসহ ক্বারীয়ানা কুরআন",
    flagshipIndicator = "বই পরিচিতি ও সংস্করণ",

    featureQuran = "আল-কুরআন",
    featurePrayer = "নামাজের সময়",
    featureNames = "৯৯ নাম",
    featureTasbih = "তাসবীহ",
    featureQibla = "কিবলা",
    featureDuas = "দোয়া",
    featurePillars = "স্তম্ভ",
    featureMore = "আরও",

    lastReadTitle = "সর্বশেষ পঠিত",
    continueReading = "পড়া চালিয়ে যান",
    pageLabel = { num ->
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val bn = num.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
        "পৃষ্ঠা $bn"
    },
    dailyTrackerTitle = "দৈনিক নামাজ ট্র্যাকার",
    history = "ইতিহাস",
    trackerCompleted = { done, total ->
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val d = done.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
        val t = total.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
        "${t}টি ওয়াক্তের মধ্যে ${d}টি সম্পন্ন"
    },

    ourPublications = "আমাদের অন্যান্য প্রকাশনাসমূহ",
    viewAllBooks = "সকল বই",
    comingSoon = "শীঘ্রই আসছে",
    comingSoonNotice = "ইনশাআল্লাহ বইটি বর্তমানে প্রকাশনার কাজ চলছে।",
    ok = "ঠিক আছে",
    cancel = "বাতিল",

    drawerTitle = "ক্বারীয়ানা কুরআন ও পাবলিকেশনস",
    drawerSubtitle = "ডিজিটাল সংস্করণ ও ইসলামিক প্ল্যাটফর্ম",
    drawerHome = "হোম",
    drawerIndex = "সূচিপত্র",
    drawerPrayer = "নামাজের সময়",
    drawerQibla = "কিবলা কম্পাস",
    drawerTasbih = "ডিজিটাল তাসবীহ",
    drawerDua = "দৈনন্দিন দোয়া",
    drawerHadith = "নির্বাচিত হাদিস",
    drawerNames = "আল্লাহর ৯৯টি নাম",
    drawerLibrary = "আমাদের প্রকাশনাসমূহ",
    drawerSettings = "অ্যাপ সেটিংস",
    drawerAboutUs = "আমাদের সম্পর্কে",
    drawerSupport = "সহায়তা ও যোগাযোগ",
    drawerVersion = "ভার্সন",

    indexTitle = "কুরআন সূচিপত্র",
    tabSurah = "সূরা",
    tabPara = "পারা",
    tabPage = "পৃষ্ঠা",
    searchPlaceholder = "সূরা, পারা বা অর্থ খুঁজুন...",
    makki = "মাক্কী",
    madani = "মাদানী",
    ayahsCount = { count ->
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val bn = count.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
        "$bn আয়াত"
    },
    paraLabel = { num ->
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val bn = num.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
        "পারা $bn"
    },

    profileTitle = "আমার অ্যাকাউন্ট ও সেটিংস",
    accountInfo = "অ্যাকাউন্ট তথ্য",
    darkTheme = "ডার্ক মোড",
    darkThemeActive = "ডার্ক মোড সক্রিয় রয়েছে",
    lightThemeActive = "লাইট মোড সক্রিয় রয়েছে",
    readingMode = "ডিফল্ট পাঠ মোড",
    modePage = "পৃষ্ঠা ভিউ",
    modeAyah = "ডিজিটাল সংস্করণ",
    modeClassic = "ক্লাসিক হাফেজী",
    appLanguageTitle = "অ্যাপের ভাষা",
    arabicFontTitle = "আরবি ক্যালিগ্রাফি ফন্ট",
    logout = "লগআউট",
    logoutConfirm = "আপনি কি নিশ্চিতভাবে লগআউট করতে চান?"
)

val EnStrings = AppStrings(
    isEnglish = true,
    navHome = "Home",
    navLibrary = "Library",
    navQuran = "Al-Quran",
    navProfile = "Profile",

    menu = "Menu",
    language = "Language",
    settings = "Settings",
    premium = "Premium",
    bangladesh = "Bangladesh",

    listenAzan = "Listen Azan",
    stopAzan = "Stop Azan",
    fajr = "Fajr",
    sunrise = "Sunrise",
    dhuhr = "Dhuhr",
    asr = "Asr",
    maghrib = "Maghrib",
    isha = "Isha",
    allPrayers = "All Prayers",
    prayerRemaining = { name, h, m, s ->
        "$name in: ${h}h ${m}m ${s}s"
    },
    nextPrayerLabel = { name, timeStr -> "Next Waqt: $name ($timeStr)" },

    ramadanCountdown = "Ramadan Countdown",
    allTimings = "All Timings",
    days = "Days",
    hours = "Hours",
    minutes = "Minutes",
    seconds = "Seconds",

    flagshipTitle = "Qariana Ortho-Quran Shareef",
    flagshipBadge = "Complete 30 Paras",
    flagshipSubtitle = "Qariana Quran with Notation Symbols",
    flagshipIndicator = "Book Overview & Editions",

    featureQuran = "Al-Quran",
    featurePrayer = "Prayer Times",
    featureNames = "99 Names",
    featureTasbih = "Tasbih",
    featureQibla = "Qibla",
    featureDuas = "Duas",
    featurePillars = "Pillars",
    featureMore = "More",

    lastReadTitle = "Last Read",
    continueReading = "Continue Reading",
    pageLabel = { num -> "Page $num" },
    dailyTrackerTitle = "Daily Prayer Tracker",
    history = "History",
    trackerCompleted = { done, total -> "$done of $total prayers completed" },

    ourPublications = "Our Publications",
    viewAllBooks = "All Books",
    comingSoon = "Coming Soon",
    comingSoonNotice = "InshaAllah this publication is currently in press.",
    ok = "OK",
    cancel = "Cancel",

    drawerTitle = "Qariana Quran & Publications",
    drawerSubtitle = "Digital Edition & Islamic Platform",
    drawerHome = "Home",
    drawerIndex = "Index",
    drawerPrayer = "Prayer Times",
    drawerQibla = "Qibla Compass",
    drawerTasbih = "Digital Tasbih",
    drawerDua = "Daily Duas",
    drawerHadith = "Selected Hadith",
    drawerNames = "99 Names of Allah",
    drawerLibrary = "Our Publications",
    drawerSettings = "App Settings",
    drawerAboutUs = "About Us",
    drawerSupport = "Support & Contact",
    drawerVersion = "Version",

    indexTitle = "Quran Index",
    tabSurah = "Surah",
    tabPara = "Para",
    tabPage = "Page",
    searchPlaceholder = "Search Surah, Para or meaning...",
    makki = "Makki",
    madani = "Madani",
    ayahsCount = { count -> "$count Ayahs" },
    paraLabel = { num -> "Para $num" },

    profileTitle = "My Account & Settings",
    accountInfo = "Account Information",
    darkTheme = "Dark Theme",
    darkThemeActive = "Dark theme active",
    lightThemeActive = "Light theme active",
    readingMode = "Default Reading Mode",
    modePage = "Page View",
    modeAyah = "Digital Edition",
    modeClassic = "Classic Hafezi",
    appLanguageTitle = "App Language",
    arabicFontTitle = "Arabic Calligraphy Font",
    logout = "Logout",
    logoutConfirm = "Are you sure you want to log out?"
)

val LocalAppStrings = staticCompositionLocalOf { BnStrings }

object L10n {
    val current: AppStrings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStrings.current
}


package com.karianaquran.reader.data.model

/** কুরআন পৃষ্ঠার একটি সারি (Mushaf line) */
data class QuranRow(
    val index: Int,
    val arabic: String,
    val meaning: String,
    val translit: String,
    val ayahRef: String,
    val surahName: String,
    val words: List<String>,
    /** ঐচ্ছিক: প্রতিটি শব্দের আলাদা উচ্চারণ/অর্থ (ডেটাসেটে থাকলে) */
    val wordDetails: List<WordDetail> = emptyList(),
)

data class WordDetail(val translit: String, val meaning: String)

/** আয়াত ও তাফসীর ভিউয়ের জন্য আয়াত */
data class Verse(
    val ayah: Int,
    val ayahRef: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val words: List<String>,
    val tafsir: String?,
)

data class QuranPage(
    val page: Int,
    val juz: Int,
    val surahNameAr: String,
    val surahNameBn: String,
    val place: String,
    val headerLeftSub: String,
    val rows: List<QuranRow>,
    val verses: List<Verse>,
)

/** শব্দ বুকমার্কের ধরন */
enum class WordMarkType(val key: String, val emoji: String, val label: String) {
    STUCK("stuck", "", "আটকানো শব্দ"),
    TAJWEED("tajweed", "", "তাজবীদ লক্ষণীয়"),
    REVISION("revision", "", "রিভিশন");

    companion object {
        fun from(key: String?): WordMarkType? = entries.firstOrNull { it.key == key }
    }
}

/** শব্দ চিহ্নিত করার ইউনিক আইডি: পৃষ্ঠা_সারি_শব্দ */
data class WordRef(val page: Int, val row: Int, val word: Int) {
    val id: String get() = "${page}_${row}_$word"

    companion object {
        fun parse(id: String): WordRef? {
            val p = id.split("_")
            if (p.size != 3) return null
            return WordRef(p[0].toIntOrNull() ?: return null, p[1].toIntOrNull() ?: return null, p[2].toIntOrNull() ?: return null)
        }
    }
}

/** লগইন সেশন */
data class Session(val mobile: String, val licenseKey: String, val deviceId: String, val loginAt: Long)

/** প্রকাশিত বই */
data class Publication(
    val id: String,
    val titleBn: String,
    val subtitleBn: String,
    val comingSoon: Boolean,
    val totalPages: Int = 0,
    val titleAr: String = "",
    val priceBn: String = "১,০০০ ৳ / $10",
    val priceBd: String = "১,০০০ ৳",
    val priceIntl: String = "$10 USD",
    val badge: String = "প্রধান গ্রন্থ",
    val primaryColor: Long = 0xFF064E3B,
    val accentColor: Long = 0xFFD4AF37,
    val titleEn: String = "",
    val subtitleEn: String = "",
    val coverDrawableRes: Int? = null,
    val originalPriceBn: String = "",
    val discountPercent: String = "",
    val paperType: String = "",
    val orderUrl: String = "https://karianaquran.com/kariana-quran-all-books/",
) {
    fun title(isEn: Boolean) = if (isEn && titleEn.isNotEmpty()) titleEn else titleBn
    fun subtitle(isEn: Boolean) = if (isEn && subtitleEn.isNotEmpty()) subtitleEn else subtitleBn
}

object Publications {
    const val ORTHO_QURAN_ID = "ortho_quran"
    const val FREE_PREVIEW_LAST_PAGE = 10
    const val FREE_PREVIEW_CLASSIC_LAST_PAGE = 10
    const val ORTHO_TOTAL_PAGES = 610
    const val MUSHAF_TOTAL_PAGES = 610
    const val CLASSIC_TOTAL_PAGES = 963
    // প্রথম ১০ পৃষ্ঠা ভূমিকা/সূচিপত্র বাদ দিয়ে পৃষ্ঠা ১১ (কুরআনের মূল পৃষ্ঠা ১) থেকে শুরু
    const val CLASSIC_START_PAGE = 11
    const val CLASSIC_FIRST_SURAH_PAGE = 11
    const val CLASSIC_PAGE_OFFSET = 10

    /** রিডিং মোড অনুযায়ী মোট পৃষ্ঠা সংখ্যা (ক্লাসিকে প্রথম ১০ পৃষ্ঠা বাদ দিয়ে ৯৫৩ পৃষ্ঠা) */
    fun totalPagesForMode(mode: com.karianaquran.reader.data.ReadingMode? = null): Int =
        if (mode == com.karianaquran.reader.data.ReadingMode.CLASSIC) (CLASSIC_TOTAL_PAGES - CLASSIC_PAGE_OFFSET) else MUSHAF_TOTAL_PAGES

    /** রিডিং মোড অনুযায়ী ফ্রি পৃষ্ঠার সীমা: মুসহাফে ১০ পৃষ্ঠা, ক্লাসিকে ২০ পৃষ্ঠা */
    fun freePagesForMode(mode: com.karianaquran.reader.data.ReadingMode? = null): Int =
        FREE_PREVIEW_LAST_PAGE

    val ortho = Publication(
        id = ORTHO_QURAN_ID,
        titleBn = "ক্বারীয়ানা কুরআনুল মাজীদ (৩০ পারা)",
        titleAr = "الْقُرْآنُ الْكَرِيمُ",
        subtitleBn = "আর্ট পেপার, ৬১২ পৃষ্ঠা | রঙিন তাজবীদ, উচ্চারণ ও শব্দার্থসহ ইনডিজাইন প্রিন্ট সংস্করণ",
        comingSoon = false,
        totalPages = 612,
        priceBn = "৳ ১০০০",
        priceBd = "১,০০০ ৳",
        priceIntl = "$10 USD",
        originalPriceBn = "৳ ১২০০",
        discountPercent = "Hot Sale -17%",
        paperType = "আর্ট পেপার",
        badge = "হট সেল -১৭%",
        primaryColor = 0xFF064E3B,
        accentColor = 0xFFF59E0B,
        coverDrawableRes = com.karianaquran.reader.R.drawable.qariana_ortho_cover,
        titleEn = "Qariana Quranul Majeed (30 Paras)",
        subtitleEn = "Art Paper Edition with Word-by-Word Meaning & Tajweed",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books-price-in-bangladesh/",
    )

    val shikkha = Publication(
        id = "quran_shikkha",
        titleBn = "তাজবীদ সহ ক্বারীয়ানা কুরআন শিক্ষা",
        titleAr = "تَجْوِيدُ الْقُرْآنِ",
        subtitleBn = "পেপারব্যাক, ৪৩ পৃষ্ঠা | মাখরাজ, সিফাত ও সহজ পদ্ধতিতে রঙিন তাজবীদ সহ সহীহ কুরআন শিক্ষা",
        comingSoon = true,
        totalPages = 43,
        priceBn = "৳ ১০০",
        priceBd = "১০০ ৳",
        originalPriceBn = "৳ ১৫০",
        discountPercent = "Hot Sale -33%",
        paperType = "পেপারব্যাক",
        badge = "হট সেল -৩৩%",
        primaryColor = 0xFF1E3A8A,
        accentColor = 0xFF60A5FA,
        coverDrawableRes = com.karianaquran.reader.R.drawable.qariana_shikkha_cover,
        titleEn = "Tajweed Shoho Qariana Quran Shikkha",
        subtitleEn = "Easy Scientific Method for Quran Learning (43 Pages)",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books-price-in-bangladesh/",
    )

    val orthoSara = Publication(
        id = "ortho_sara",
        titleBn = "ক্বারীয়ানা কুরআনুল মাজীদ - অফসেট",
        titleAr = "الْقُرْآنُ الْكَرِيمُ",
        subtitleBn = "অফসেট পেপার, ৬১২ পৃষ্ঠা | অফসেট পেপারে মুদ্রিত পূর্ণাঙ্গ ৩০ পারা ক্বারীয়ানা কুরআনুল মাজীদ",
        comingSoon = true,
        totalPages = 612,
        priceBn = "৳ ৯০০",
        priceBd = "৯০০ ৳",
        originalPriceBn = "৳ ১১০০",
        discountPercent = "Hot Sale -16%",
        paperType = "অফসেট পেপার",
        badge = "হট সেল -১৬%",
        primaryColor = 0xFF065F46,
        accentColor = 0xFF34D399,
        coverDrawableRes = com.karianaquran.reader.R.drawable.qariana_ortho_sara_cover,
        titleEn = "Qariana Quranul Majeed - Offset",
        subtitleEn = "Offset Paper Complete 30 Paras Edition (612 Pages)",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books-price-in-bangladesh/",
    )

    val namaz = Publication(
        id = "namaz_shikkha",
        titleBn = "ভিডিওসহ ক্বারীয়ানা নামাজ শিক্ষা",
        titleAr = "الصَّلَاةُ وَالأَدْعِيَةُ",
        subtitleBn = "পেপারব্যাক, ৩৬ পৃষ্ঠা | ভিডিও ও কিউআর কোডসহ সচিত্র সুন্নাহভিত্তিক সহীহ নামাজ শিক্ষা",
        comingSoon = true,
        totalPages = 36,
        priceBn = "৳ ১০০",
        priceBd = "১০০ ৳",
        originalPriceBn = "৳ ১৫০",
        discountPercent = "Hot Sale -25%",
        paperType = "পেপারব্যাক",
        badge = "হট সেল -২৫%",
        primaryColor = 0xFF3B0764,
        accentColor = 0xFFC084FC,
        coverDrawableRes = com.karianaquran.reader.R.drawable.qariana_namaz_cover,
        titleEn = "Video Shoho Qariana Namaz Shikkha",
        subtitleEn = "Illustrated Sunnah Salah & Daily Masnoon Duas (36 Pages)",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books-price-in-bangladesh/",
    )

    val gojol = Publication(
        id = "gojol",
        titleBn = "ক্বারীয়ানা ইসলামী গজল ও ছন্দ",
        titleAr = "الأَنَاشِيدُ الإِسْلَامِيَّةُ",
        subtitleBn = "পেপারব্যাক, ৭২ পৃষ্ঠা | শিশু-কিশোর ও সবার জন্য হৃদয়গ্রাহী ইসলামিক গজল ও নাশিদ সংকলন",
        comingSoon = true,
        totalPages = 72,
        priceBn = "১০০ ৳",
        priceBd = "১০০ ৳",
        originalPriceBn = "১২০ ৳",
        discountPercent = "Hot Sale -17%",
        paperType = "পেপারব্যাক",
        badge = "হট সেল -১৭%",
        primaryColor = 0xFF78350F,
        accentColor = 0xFFFBBF24,
        titleEn = "Kariana Islamic Gojol & Poems",
        subtitleEn = "Inspiring Islamic Nasheed & Rhymes (72 Pages)",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books/",
    )

    val hifz = Publication(
        id = "hifz",
        titleBn = "ক্বারীয়ানা হিফযুল কুরআন ও মাখরাজ গাইড",
        titleAr = "حِفْظُ الْقُرْآنِ",
        subtitleBn = "হাফেজ ও ক্বারীদের জন্য বিশেষ তাজবীদ মুসহাফ ও মুতাশাবিহাত নির্দেশিকা",
        comingSoon = true,
        totalPages = 320,
        priceBn = "৩৫০ ৳",
        badge = "হিফয বিভাগ",
        primaryColor = 0xFF065F46,
        accentColor = 0xFF34D399,
        titleEn = "Kariana Hifzul Quran & Makhraj Guide",
        subtitleEn = "Special Tajweed Mushaf & Mutashabihat Guide for Huffaz",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books/",
    )

    val tafsir = Publication(
        id = "tafsir",
        titleBn = "ক্বারীয়ানা তাফসীরুল কুরআন সংকলন",
        titleAr = "تَفْسِيرُ الْقُرْآنِ",
        subtitleBn = "আয়াতভিত্তিক সংক্ষিপ্ত তাফসীর, শানে নুযূল ও জীবনঘনিষ্ঠ শিক্ষা",
        comingSoon = true,
        totalPages = 450,
        priceBn = "৪৫০ ৳",
        badge = "তাফসীর সিরিজ",
        primaryColor = 0xFF881337,
        accentColor = 0xFFFB7185,
        titleEn = "Kariana Tafsirul Quran",
        subtitleEn = "Ayah-by-Ayah Brief Tafsir & Lessons",
        orderUrl = "https://karianaquran.com/kariana-quran-all-books/",
    )

    val qaida = shikkha
    val ampara = gojol

    val upcoming = listOf(shikkha, orthoSara, namaz, gojol, hifz, tafsir)

    val all = listOf(ortho, shikkha, orthoSara, namaz, gojol, hifz, tafsir)
}

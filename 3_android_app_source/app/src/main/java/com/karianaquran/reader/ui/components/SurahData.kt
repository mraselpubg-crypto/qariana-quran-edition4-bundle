package com.karianaquran.reader.ui.components

import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.data.model.Publications

data class SurahMeta(
    val number: Int,
    val nameBn: String,
    val nameAr: String,
    val totalAyahs: Int,
    val startPage: Int, // ডিজিটাল ও নতুন ইনডিজাইন সংস্করণ পৃষ্ঠা (১-৬১০)
    val juzNumber: Int,
    val nameEn: String = "",
    val classicStartPage: Int = 2 // জনপ্রিয় মুদ্রিত সংস্করণ পৃষ্ঠা (১-৯৬৩)
) {
    val juz: Int get() = juzNumber
    fun name(isEn: Boolean) = if (isEn && nameEn.isNotEmpty()) nameEn else nameBn
}

data class JuzMeta(
    val number: Int,
    val nameBn: String,
    val nameAr: String = "",
    val startPage: Int,
    val surahsBn: String = "",
    val classicStartPage: Int = 2,
    val nameEn: String = "",
) {
    fun name(isEn: Boolean) = if (isEn && nameEn.isNotEmpty()) nameEn else nameBn
}

object QuranMetadata {

    fun getSurahNameEn(number: Int): String {
        return surahList.getOrNull(number - 1)?.nameEn ?: "Surah $number"
    }

    val introSurahMeta = SurahMeta(
        number = 1,
        nameBn = "সূরাতুল ফাতিহাহ্",
        nameAr = "الفاتحة",
        totalAyahs = 7,
        startPage = 1,
        juzNumber = 1,
        classicStartPage = 2
    )

    // =========================================================================
    // ১. জনপ্রিয় মুদ্রিত সংস্করণের (Classic 963-Page) ৩০টি পারার সুনির্দিষ্ট পৃষ্ঠা ম্যাপিং
    // =========================================================================
    val classicParaPages = intArrayOf(
        2,   42,  74,  106, 138, 170, 202, 234, 266, 298,
        330, 362, 394, 426, 458, 490, 522, 554, 586, 618,
        650, 682, 714, 746, 778, 810, 842, 874, 895, 929
    )

    fun classicJuzForPage(page: Int): Int {
        for (i in classicParaPages.indices.reversed()) {
            if (page >= classicParaPages[i]) return i + 1
        }
        return 1
    }

    // =========================================================================
    // ২. ডিজিটাল ও নতুন ভেক্টর সংস্করণের (Standard 610-Page) ৩০টি পারার পৃষ্ঠা ম্যাপিং
    // =========================================================================
    val digitalParaPages = intArrayOf(
        1,   22,  42,  62,  82,  102, 122, 142, 162, 182,
        202, 222, 242, 262, 282, 302, 322, 342, 362, 382,
        402, 422, 442, 462, 482, 502, 522, 542, 562, 582
    )

    fun juzForPage(page: Int, mode: ReadingMode = ReadingMode.CLASSIC): JuzMeta {
        val mapping = if (mode == ReadingMode.CLASSIC) classicParaPages else digitalParaPages
        var foundIdx = 0
        for (i in mapping.indices) {
            if (page >= mapping[i]) {
                foundIdx = i
            } else {
                break
            }
        }
        return juzList[foundIdx]
    }

    fun startPageForJuz(juzNumber: Int, mode: ReadingMode = ReadingMode.CLASSIC): Int {
        val idx = (juzNumber - 1).coerceIn(0, 29)
        return if (mode == ReadingMode.CLASSIC) classicParaPages[idx] else digitalParaPages[idx]
    }

    fun juzStartPage(juzNum: Int, mode: ReadingMode): Int = startPageForJuz(juzNum, mode)

    fun startPageForSurah(surahNumber: Int, mode: ReadingMode = ReadingMode.CLASSIC): Int {
        val surah = surahList.firstOrNull { it.number == surahNumber } ?: surahList[0]
        return if (mode == ReadingMode.CLASSIC) surah.classicStartPage else surah.startPage
    }

    fun targetPageForMode(currentPage: Int, currentMode: ReadingMode, targetMode: ReadingMode): Int {
        if (currentMode == targetMode) return currentPage
        val currentJuz = juzForPage(currentPage, currentMode)
        return startPageForJuz(currentJuz.number, targetMode)
    }

    fun targetPageForMode(digitalPage: Int, mode: ReadingMode): Int =
        targetPageForMode(digitalPage, ReadingMode.PAGE, mode)

    fun toDigitalPage(page: Int, mode: ReadingMode = ReadingMode.PAGE): Int =
        if (mode == ReadingMode.CLASSIC) {
            val juz = juzForPage(page, ReadingMode.CLASSIC)
            startPageForJuz(juz.number, ReadingMode.PAGE)
        } else {
            page.coerceIn(1, Publications.MUSHAF_TOTAL_PAGES)
        }

    fun surahForPage(page: Int, mode: ReadingMode = ReadingMode.PAGE): SurahMeta {
        if (mode == ReadingMode.CLASSIC) {
            return surahList.lastOrNull { it.classicStartPage <= page } ?: surahList.first()
        }
        return surahList.lastOrNull { it.startPage <= page } ?: surahList.first()
    }

    fun totalPagesForMode(mode: ReadingMode): Int =
        if (mode == ReadingMode.CLASSIC) Publications.CLASSIC_TOTAL_PAGES else Publications.MUSHAF_TOTAL_PAGES

    val juzList = listOf(
        JuzMeta(1, "আলিফ লাম মীম", "الم", 1, "আল-ফাতিহা (১) - আল-বাকারা (১৪১)", 2, "Alif Lam Meem"),
        JuzMeta(2, "সায়াকুলু", "سيقول", 22, "আল-বাকারা (১৪২-২৫২)", 42, "Sayaqulu"),
        JuzMeta(3, "তিলকার রুসুল", "تلك الرسل", 42, "আল-বাকারা (২৫৩) - আল-ইমরান (৯২)", 74, "Tilkar Rusul"),
        JuzMeta(4, "লান তানালু", "لن تنالوا", 62, "আল-ইমরান (৯৩) - আন-নিসা (২৩)", 106, "Lan Tanalu"),
        JuzMeta(5, "ওয়াল মুহসানাতু", "والمحصنات", 82, "আন-নিসা (২৪-১৪৭)", 138, "Wal Muhsanatu"),
        JuzMeta(6, "লা ইউহিব্বুল্লাহ", "لا يحب الله", 102, "আন-নিসা (১৪৮) - আল-মায়িদাহ (৮১)", 170, "La Yuhibbullah"),
        JuzMeta(7, "ওয়া ইজা সামিউ", "وإذا سمعوا", 122, "আল-মায়িদাহ (৮২) - আল-আনআম (১১০)", 202, "Wa Iza Sami'u"),
        JuzMeta(8, "ওয়া লাও আন্নানা", "ولو أننا", 142, "আল-আনআম (১১১) - আল-আরাফ (৮৭)", 234, "Wa Law Annana"),
        JuzMeta(9, "ক্বলাল মালাউ", "قال الملأ", 162, "আল-আরাফ (৮৮) - আল-আনফাল (৪০)", 266, "Qalal Mala'u"),
        JuzMeta(10, "ওয়া'লামু", "واعلموا", 182, "আল-আনফাল (৪১) - আত-তাওবাহ (৯২)", 298, "Wa'lamu"),
        JuzMeta(11, "ইয়া'তাজিরুন", "يعتذرون", 202, "আত-তাওবাহ (৯৩) - হূদ (৫)", 330, "Ya'tazirun"),
        JuzMeta(12, "ওয়া মা মিন দাব্বাহ", "وما من دابة", 222, "হূদ (৬) - ইউসুফ (৫২)", 362, "Wa Ma Min Dabbah"),
        JuzMeta(13, "ওয়া মা উবাররিউ", "وما أبرئ", 242, "ইউসুফ (৫৩) - ইবরাহীম (৫২)", 394, "Wa Ma Ubarri'u"),
        JuzMeta(14, "রুবামা", "ربما", 262, "আল-হিজর (১) - আন-নাহল (১২৮)", 426, "Rubama"),
        JuzMeta(15, "সুবহানাল্লাজি", "سبحان الذي", 282, "বনি ইসরাঈল (১) - আল-কাহফ (৭৪)", 458, "Subhanallazi"),
        JuzMeta(16, "ক্বলা আলাম", "قال ألم", 302, "আল-কাহফ (৭৫) - ত্বা-হা (১৩৫)", 490, "Qala Alam"),
        JuzMeta(17, "ইক্বতারা kines", "اقترب", 322, "আল-আম্বিয়া (১) - আল-হাজ্জ (৭৮)", 513, "Iqtaraba"),
        JuzMeta(18, "ক্বাদ আফলাহা", "قد أفلح", 342, "আল-মুমিনুন (১) - আল-ফুরকান (২০)", 543, "Qad Aflaha"),
        JuzMeta(19, "ওয়া ক্বলাল্লাজিনা", "وقال الذين", 362, "আল-ফুরকান (২১) - আন-নামল (৫৫)", 571, "Wa Qalallazina"),
        JuzMeta(20, "আম্মান খালাক্বা", "أمن خلق", 382, "আন-নামল (৫৬) - আল-আনকাবুত (৪৫)", 598, "Amman Khalaqa"),
        JuzMeta(21, "উতলু মা উহিয়া", "اتل ما أوحي", 402, "আল-আনকাবুত (৪৬) - আল-আহযাব (৩০)", 629, "Utlu Ma Oohiya"),
        JuzMeta(22, "ওয়া মাই ইয়াক্বনুত", "ومن يقنت", 422, "আল-আহযাব (৩১) - ইয়াসীন (২৭)", 662, "Wa Man Yaqnut"),
        JuzMeta(23, "ওয়া মা লিয়া", "وما لي", 442, "ইয়াসীন (২৮) - আয-যুমার (৩১)", 698, "Wa Ma Liya"),
        JuzMeta(24, "ফামান আজলামু", "فمن أظلم", 462, "আয-যুমার (৩২) - ফুসসিলাত (৪৬)", 728, "Faman Azlamu"),
        JuzMeta(25, "ইলাইহি ইউরাদ্দু", "إليه يرد", 482, "ফুসসিলাত (৪৭) - আল-জাসিয়াহ (৩৭)", 768, "Ilaihi Yuraddu"),
        JuzMeta(26, "হা-মীম", "حم", 502, "আল-আহক্বাফ (১) - আয-যারিয়াত (৩০)", 799, "Ha-Meem"),
        JuzMeta(27, "ক্বলা ফামা খাতবুকুম", "قال فما خطبكم", 522, "আয-যারিয়াত (৩১) - আল-হাদীদ (২৯)", 828, "Qala Fama Khatbukum"),
        JuzMeta(28, "ক্বাদ সামিআল্লাহু", "قد سمع الله", 542, "আল-মুজাদালাহ (১) - আত-তাহরীম (১২)", 863, "Qad Sami' Allahu"),
        JuzMeta(29, "তাবারাকাল্লাজি", "تبارك الذي", 562, "আল-মুলক (১) - আল-মুরসালাত (৫০)", 895, "Tabarakallazi"),
        JuzMeta(30, "আম্মা ইয়াতাসা'আলুন", "عم يتساءلون", 582, "আন-নাবা (১) - আন-নাস (৬)", 929, "Amma Yatasa'alun")
    )

    // ১১৪টি সূরার শতভাগ প্রামাণ্য মুদ্রিত পৃষ্ঠা ম্যাপিং তালিকা
    val surahList = listOf(
        SurahMeta(1, "সূরাতুল ফাতিহাহ্", "الفاتحة", 7, 1, 1, "Al-Fatihah", 2),
        SurahMeta(2, "সূরাতুল বাক্বারাহ্", "البقرة", 286, 2, 1, "Al-Baqarah", 3),
        SurahMeta(3, "সূরাতু আলি ইমরান", "آل عمران", 200, 50, 3, "Ali 'Imran", 79),
        SurahMeta(4, "সূরাতুন নিসা", "النساء", 176, 77, 4, "An-Nisa'", 123),
        SurahMeta(5, "সূরাতুল মা'ইদাহ্", "المائدة", 120, 106, 6, "Al-Ma'idah", 170),
        SurahMeta(6, "সূরাতুল আন্'আম", "الأنعام", 165, 128, 7, "Al-An'am", 204),
        SurahMeta(7, "সূরাতুল আ'রাফ", "الأعراف", 206, 151, 8, "Al-A'raf", 241),
        SurahMeta(8, "সূরাতুল আন্ফাল", "الأنفال", 75, 177, 9, "Al-Anfal", 283),
        SurahMeta(9, "সূরাতুত তাওবাহ্", "التوبة", 129, 187, 10, "At-Tawbah", 298),
        SurahMeta(10, "সূরাতু ইউনূস", "يونس", 109, 208, 11, "Yunus", 330),
        SurahMeta(11, "সূরাতু হৃদ", "هود", 123, 221, 11, "Hud", 352),
        SurahMeta(12, "সূরাতু ইউসুফ", "يوسف", 111, 235, 12, "Yusuf", 374),
        SurahMeta(13, "সূরাতুর র'য়দ", "الرعد", 43, 249, 13, "Ar-Ra'd", 395),
        SurahMeta(14, "সূরাতু ইব্রাহীম", "إبراهيم", 52, 255, 13, "Ibrahim", 406),
        SurahMeta(15, "সূরাতুল হিজ্র", "الحجر", 99, 262, 14, "Al-Hijr", 416),
        SurahMeta(16, "সূরাতুন নাহল", "النحل", 128, 267, 14, "An-Nahl", 425),
        SurahMeta(17, "সূরাতু বানী ইসরাঈল", "الإسراء", 111, 282, 15, "Al-Isra", 449),
        SurahMeta(18, "সূরাতুল কাহ্ফ", "الكهف", 110, 293, 15, "Al-Kahf", 467),
        SurahMeta(19, "সূরাতু মারইয়াম", "مريم", 98, 305, 16, "Maryam", 486),
        SurahMeta(20, "সূরাতু ত্বা-হা", "طه", 135, 312, 16, "Ta-Ha", 497),
        SurahMeta(21, "সূরাতুল আম্বিয়া", "الأنبياء", 112, 322, 17, "Al-Anbiya", 513),
        SurahMeta(22, "সূরাতুল হাজ্জ", "الحج", 78, 332, 17, "Al-Hajj", 528),
        SurahMeta(23, "সূরাতুল মু'মিনূন", "المؤمنون", 118, 342, 18, "Al-Mu'minun", 543),
        SurahMeta(24, "সূরাতুন নূর", "النور", 64, 350, 18, "An-Nur", 555),
        SurahMeta(25, "সূরাতুল ফুরক্বান", "الفرقان", 77, 359, 18, "Al-Furqan", 571),
        SurahMeta(26, "সূরাতুশ শু'আরাঅ", "الشعراء", 227, 367, 19, "Ash-Shu'ara", 582),
        SurahMeta(27, "সূরাতুন নামল", "النمل", 93, 377, 19, "An-Naml", 598),
        SurahMeta(28, "সূরাতুল ক্বাছাছ", "القصص", 88, 385, 20, "Al-Qasas", 612),
        SurahMeta(29, "সূরাতুল 'আনকাবূত", "العنكبوت", 69, 396, 20, "Al-'Ankabut", 629),
        SurahMeta(30, "সূরাতুর রূম", "الروم", 60, 404, 21, "Ar-Rum", 641),
        SurahMeta(31, "সূরাতু লুক্বমান", "لقمان", 34, 411, 21, "Luqman", 651),
        SurahMeta(32, "সূরাতুস্ সাজদাহ্", "السجدة", 30, 415, 21, "As-Sajdah", 658),
        SurahMeta(33, "সূরাতুল আহ্্যাব", "الأحزاب", 73, 418, 21, "Al-Ahzab", 662),
        SurahMeta(34, "সূরাতু সাবা", "سبأ", 54, 428, 22, "Saba'", 678),
        SurahMeta(35, "সূরাতু ফাত্বির", "فاطر", 45, 434, 22, "Fatir", 689),
        SurahMeta(36, "সূরাতু ইয়াসীন", "يس", 83, 440, 22, "Ya-Sin", 698),
        SurahMeta(37, "সূরাতুছ ছফফাত", "الصافات", 182, 446, 23, "As-Saffat", 707),
        SurahMeta(38, "সূরাতু ছ্বদ", "ص", 88, 453, 23, "Sad", 719),
        SurahMeta(39, "সূরাতুজ্ জুমার", "الزمر", 75, 458, 23, "Az-Zumar", 728),
        SurahMeta(40, "সূরাতুল মু'মিন", "غافر", 85, 467, 24, "Ghafir", 743),
        SurahMeta(41, "সূরাতু হা-মীম সাজদাহ্", "فصلت", 54, 477, 24, "Fussilat", 758),
        SurahMeta(42, "সূরাতুশ শুরা", "الشورى", 53, 483, 25, "Ash-Shura", 768),
        SurahMeta(43, "সূরাতুজ্ জুখরুফ্", "الزخرف", 89, 489, 25, "Az-Zukhruf", 778),
        SurahMeta(44, "সূরাতুদ্ দুখান", "الدخان", 59, 496, 25, "Ad-Dukhan", 788),
        SurahMeta(45, "সূরাতুজ্ জাছিয়াহ্", "الجاثية", 37, 499, 25, "Al-Jathiyah", 793),
        SurahMeta(46, "সূরাতুল আহ্ ক্বাফ", "الأحقاف", 35, 502, 26, "Al-Ahqaf", 799),
        SurahMeta(47, "সূরাতু মুহাম্মাদ", "محمد", 38, 507, 26, "Muhammad", 806),
        SurahMeta(48, "সূরাতুল ফাতহ্", "الفتح", 29, 511, 26, "Al-Fath", 813),
        SurahMeta(49, "সূরাতুল হুজরাত", "الحجرات", 18, 515, 26, "Al-Hujurat", 820),
        SurahMeta(50, "সূরাতু ক্বাফ", "ق", 45, 518, 26, "Qaf", 824),
        SurahMeta(51, "সূরাতুয্ যারিয়াত", "الذاريات", 60, 520, 26, "Adh-Dhariyat", 828),
        SurahMeta(52, "সূরাতুত ত্বূর", "الطور", 49, 523, 27, "At-Tur", 833),
        SurahMeta(53, "সূরাতুন নাজম", "النجم", 62, 526, 27, "An-Najm", 837),
        SurahMeta(54, "সূরাতুল ক্বমার", "القمر", 55, 528, 27, "Al-Qamar", 841),
        SurahMeta(55, "সূরাতুর রহমান", "الرحمن", 78, 531, 27, "Ar-Rahman", 846),
        SurahMeta(56, "সূরাতুল ওয়াক্বিয়াহ্", "الواقعة", 96, 534, 27, "Al-Waqi'ah", 850),
        SurahMeta(57, "সূরাতুল হাদীদ", "الحديد", 29, 537, 27, "Al-Hadid", 856),
        SurahMeta(58, "সূরাতুল মুজাদালাহ্", "المجادلة", 22, 542, 28, "Al-Mujadilah", 863),
        SurahMeta(59, "সূরাতুল হাশর", "الحشر", 24, 545, 28, "Al-Hashr", 868),
        SurahMeta(60, "সূরাতুল মুমতাহিনাহ্", "الممتحنة", 13, 549, 28, "Al-Mumtahanah", 874),
        SurahMeta(61, "সূরাতুছ ছফ", "الصف", 14, 551, 28, "As-Saff", 878),
        SurahMeta(62, "সূরাতুল জুমু'আহ্", "الجمعة", 11, 553, 28, "Al-Jumu'ah", 881),
        SurahMeta(63, "সূরাতুল মুনাফিকূন", "المنافقون", 11, 554, 28, "Al-Munafiqun", 883),
        SurahMeta(64, "সূরাতুত তাগাবুন", "التغابن", 18, 556, 28, "At-Taghabun", 885),
        SurahMeta(65, "সূরাতুত ত্বলাক্ব", "الطلاق", 12, 558, 28, "At-Talaq", 888),
        SurahMeta(66, "সূরাতুত তাহরীম", "التحريم", 12, 560, 28, "At-Tahrim", 892),
        SurahMeta(67, "সূরাতুল মুলক", "الملك", 30, 562, 29, "Al-Mulk", 895),
        SurahMeta(68, "সূরাতুল ক্বলম", "القلم", 52, 564, 29, "Al-Qalam", 899),
        SurahMeta(69, "সূরাতুল হাক্বহ্", "الحاقة", 52, 566, 29, "Al-Haqqah", 903),
        SurahMeta(70, "সূরাতুল মা'আরিজ", "المعارج", 44, 568, 29, "Al-Ma'arij", 906),
        SurahMeta(71, "সূরাতু নূহ", "نوح", 28, 570, 29, "Nuh", 909),
        SurahMeta(72, "সূরাতুল জ্বিন", "الجن", 28, 572, 29, "Al-Jinn", 912),
        SurahMeta(73, "সূরাতুল মুজ্জাম্মিল", "المزمل", 20, 574, 29, "Al-Muzzammil", 915),
        SurahMeta(74, "সূরাতুল মুদ্দাচ্ছির", "المدثر", 56, 575, 29, "Al-Muddaththir", 917),
        SurahMeta(75, "সূরাতুল ক্বিয়ামাহ্", "القيامة", 40, 577, 29, "Al-Qiyamah", 921),
        SurahMeta(76, "সূরাতুদ্ দাহ্র", "الإنسان", 31, 578, 29, "Al-Insan", 923),
        SurahMeta(77, "সূরাতুল মুরসালাত", "المرسلات", 50, 580, 29, "Al-Mursalat", 926),
        SurahMeta(78, "সূরাতুন নাবাঅ", "النبأ", 40, 582, 30, "An-Naba'", 929),
        SurahMeta(79, "সূরাতুন নাযি'য়াত", "النازعات", 46, 583, 30, "An-Nazi'at", 931),
        SurahMeta(80, "সূরাতু 'আবাসা", "عبس", 42, 585, 30, "'Abasa", 933),
        SurahMeta(81, "সূরাতুত তাকবীর", "التكوير", 29, 586, 30, "At-Takwir", 935),
        SurahMeta(82, "সূরাতুল ইনফিত্বার", "الانفطار", 19, 587, 30, "Al-Infitar", 937),
        SurahMeta(83, "সূরাতুল মুত্বাফ্ফিফীন", "المطففين", 36, 587, 30, "Al-Mutaffifin", 938),
        SurahMeta(84, "সূরাতুল ইন্শিক্বাক্ব", "الانشقاق", 25, 589, 30, "Al-Inshiqaq", 940),
        SurahMeta(85, "সূরাতুল বুরুজ", "البروج", 22, 590, 30, "Al-Buruj", 942),
        SurahMeta(86, "সূরাতুত ত্বারিক্ব", "الطارق", 17, 591, 30, "At-Tariq", 943),
        SurahMeta(87, "সূরাতুল আ'লা", "الأعلى", 19, 591, 30, "Al-A'la", 944),
        SurahMeta(88, "সূরাতুল গাশিয়াহ্", "الغاشية", 26, 592, 30, "Al-Ghashiyah", 945),
        SurahMeta(89, "সূরাতুল ফাজর", "الفجر", 30, 593, 30, "Al-Fajr", 946),
        SurahMeta(90, "সূরাতুল বালাদ", "البلد", 20, 594, 30, "Al-Balad", 948),
        SurahMeta(91, "সূরাতুশ শামস", "الشمس", 15, 595, 30, "Ash-Shams", 949),
        SurahMeta(92, "সূরাতুল লাইল", "الليل", 21, 595, 30, "Al-Layl", 950),
        SurahMeta(93, "সূরাতুদ্ব দুহা", "الضحى", 11, 596, 30, "Ad-Duha", 951),
        SurahMeta(94, "সূরাতু আলাম নাশরাহ্", "الشرح", 8, 596, 30, "Ash-Sharh", 952),
        SurahMeta(95, "সূরাতুত তীন", "التين", 8, 597, 30, "At-Tin", 952),
        SurahMeta(96, "সূরাতুল 'আলাক্ব", "العلق", 19, 597, 30, "Al-'Alaq", 953),
        SurahMeta(97, "সূরাতুল ক্বদর", "القدر", 5, 598, 30, "Al-Qadr", 954),
        SurahMeta(98, "সূরাতুল বাইয়িনাহ্", "البينة", 8, 598, 30, "Al-Bayyinah", 954),
        SurahMeta(99, "সূরাতুজ্ জিলজাল", "الزلزلة", 8, 599, 30, "Az-Zalzalah", 956),
        SurahMeta(100, "সূরাতুল 'আদিয়াত", "العاديات", 11, 599, 30, "Al-'Adiyat", 956),
        SurahMeta(101, "সূরাতুল ক্বারিয়াহ্", "القارعة", 11, 600, 30, "Al-Qari'ah", 957),
        SurahMeta(102, "সূরাতুত তাকাসুর", "التكاثر", 8, 600, 30, "At-Takathur", 957),
        SurahMeta(103, "সূরাতুল 'আছর", "العصر", 3, 601, 30, "Al-'Asr", 958),
        SurahMeta(104, "সূরাতুল হুমাযাহ্", "الهمزة", 9, 601, 30, "Al-Humazah", 958),
        SurahMeta(105, "সূরাতুল ফীল", "الفيل", 5, 601, 30, "Al-Fil", 959),
        SurahMeta(106, "সূরাতু কুরাইশ", "قريش", 4, 602, 30, "Quraysh", 959),
        SurahMeta(107, "সূরাতুল মা'উন", "الماعون", 7, 602, 30, "Al-Ma'un", 960),
        SurahMeta(108, "সূরাতুল কাওছার", "الكوثر", 3, 602, 30, "Al-Kawthar", 960),
        SurahMeta(109, "সূরাতুল কাফিরুন", "الكافرون", 6, 603, 30, "Al-Kafirun", 960),
        SurahMeta(110, "সূরাতুন নাছর", "النصر", 3, 603, 30, "An-Nasr", 961),
        SurahMeta(111, "সূরাতুল লাহাব", "المسد", 5, 603, 30, "Al-Masad", 961),
        SurahMeta(112, "সূরাতুল ইখলাছ", "الإخلاص", 4, 604, 30, "Al-Ikhlas", 962),
        SurahMeta(113, "সূরাতুল ফালাক্ব", "الفلق", 5, 604, 30, "Al-Falaq", 962),
        SurahMeta(114, "সূরাতুন নাস", "الناس", 6, 604, 30, "An-Nas", 962)
    )
}

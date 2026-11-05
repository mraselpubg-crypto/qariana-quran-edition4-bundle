package com.karianaquran.reader.ui.reader

/** তাজবীদ লক্ষণ (শব্দ-স্তরের অনুমান — ডেটাসেটে নিয়ম-ট্যাগ থাকলে সেটিই অগ্রাধিকার পাবে) */
enum class TajweedRule(val nameBn: String, val descBn: String, val colorArgb: Long) {
    GHUNNAH("গুন্নাহ", "নাসিকা থেকে ২ হরকত পরিমাণ টেনে পড়তে হবে।", 0xFF22C55E),
    QALQALAH("কলকলাহ", "সাকিন অবস্থায় আওয়াজে কম্পন/প্রতিধ্বনি আনতে হবে।", 0xFF3B82F6),
    MADD("মাদ্দ", "নির্দিষ্ট পরিমাণ টেনে পড়তে হবে।", 0xFFEF4444),
    IKHFA("ইখফা", "নুন সাকিন/তানবীন গোপন করে নাকের আওয়াজসহ পড়তে হবে।", 0xFFF59E0B),
    IDGHAM("ঈদগাম", "নুন সাকিন/তানবীন পরের হরফের সাথে মিলিয়ে পড়তে হবে।", 0xFFA855F7),
}

object TajweedAnalyzer {
    private const val SHADDA = '\u0651'
    private const val SUKUN = '\u0652'
    private const val MADDAH = '\u0653'
    private val TANWEEN = setOf('\u064B', '\u064C', '\u064D')
    private const val QALQALAH_LETTERS = "قطبجد"
    private const val IKHFA_LETTERS = "تثجدذزسشصضطظفقك"
    private const val IDGHAM_LETTERS = "يرملون"

    private fun isArabicLetter(c: Char) = c in '\u0621'..'\u064A'

    private fun firstLetter(word: String?): Char? = word?.firstOrNull { isArabicLetter(it) }

    private fun classifyNoon(after: Char): TajweedRule? = when {
        after in IKHFA_LETTERS -> TajweedRule.IKHFA
        after in IDGHAM_LETTERS -> TajweedRule.IDGHAM
        else -> null
    }

    fun analyze(word: String, next: String? = null): List<TajweedRule> {
        val rules = LinkedHashSet<TajweedRule>()
        for (i in word.indices) {
            val c = word[i]
            val nx = word.getOrNull(i + 1)
            if ((c == 'ن' || c == 'م') && nx == SHADDA) rules.add(TajweedRule.GHUNNAH)
            if (c in QALQALAH_LETTERS && nx == SUKUN) rules.add(TajweedRule.QALQALAH)
            if (c == 'ن' && nx == SUKUN) {
                val after = word.substring(i + 2).firstOrNull { isArabicLetter(it) }
                if (after != null) classifyNoon(after)?.let { rules.add(it) }
            }
        }
        if (word.contains(MADDAH) || word.contains('آ')) rules.add(TajweedRule.MADD)
        if (word.any { it in TANWEEN }) {
            firstLetter(next)?.let { l -> classifyNoon(l)?.let { rules.add(it) } }
        }
        return rules.toList()
    }
}

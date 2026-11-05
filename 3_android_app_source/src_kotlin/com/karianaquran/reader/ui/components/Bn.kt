package com.karianaquran.reader.ui.components

private val BN_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

/** ইংরেজি ডিজিটকে বাংলা সংখ্যায় রূপান্তর (দেখানোর সময় কালপুরুষ ফন্টে রেন্ডার হয়) */
fun String.toBn(): String {
    val sb = StringBuilder(length)
    for (c in this) sb.append(if (c in '0'..'9') BN_DIGITS[c - '0'] else c)
    return sb.toString()
}

fun Int.toBn(): String = toString().toBn()

/** ভাষা-সচেতন সংখ্যা রূপান্তর: ইংরেজি হলে ইংরেজি ডিজিট, বাংলা হলে বাংলা ডিজিট */
fun Int.toLang(isEn: Boolean): String = if (isEn) toString() else toBn()
fun String.toLang(isEn: Boolean): String = if (isEn) this else toBn()

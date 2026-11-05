package com.karianaquran.reader.data.model

import com.karianaquran.reader.service.QuranAudioItem
import com.karianaquran.reader.service.QuranAudioPlayer
import com.karianaquran.reader.ui.components.QuranMetadata

data class PageVerseRange(
    val surahNumber: Int,
    val surahNameBn: String,
    val startAyah: Int,
    val endAyah: Int,
)

object ClassicVerseMapping {

    /**
     * ক্লাসিক মুদ্রিত মুসহাফের (১–৯৬৩ পৃষ্ঠা) নির্দিষ্ট পৃষ্ঠার আয়াত তালিকা
     */
    fun getVersesForClassicPage(page: Int): List<QuranAudioItem> {
        val reciter = QuranAudioPlayer.currentReciter.value
        if (page < 11) {
            // ভূমিকা ও কভার পেজে সূরা আল-ফাতিহা
            return listOf(QuranAudioItem(1, 1, "আল-ফাতিহা", reciter))
        }

        if (page in 11..13) {
            // পৃষ্ঠা ১১, ১২, ১৩: সম্পূর্ণ সূরা আল-ফাতিহা (১-৭ আয়াত)
            return (1..7).map { a ->
                QuranAudioItem(1, a, "আল-ফাতিহা", reciter)
            }
        }

        val surahList = QuranMetadata.surahList
        val currentSurah = surahList.lastOrNull { it.classicStartPage <= page } ?: surahList[0]
        val currentIdx = surahList.indexOf(currentSurah)
        val nextSurah = surahList.getOrNull(currentIdx + 1)
        val nextStartPage = nextSurah?.classicStartPage ?: (Publications.CLASSIC_TOTAL_PAGES + 1)

        val totalPagesForSurah = (nextStartPage - currentSurah.classicStartPage).coerceAtLeast(1)
        val pageOffset = (page - currentSurah.classicStartPage).coerceIn(0, totalPagesForSurah - 1)

        val startAyah = ((pageOffset.toFloat() / totalPagesForSurah) * currentSurah.totalAyahs).toInt() + 1
        val endAyah = (((pageOffset + 1).toFloat() / totalPagesForSurah) * currentSurah.totalAyahs).toInt()
            .coerceIn(startAyah, currentSurah.totalAyahs)

        return (startAyah..endAyah).map { a ->
            QuranAudioItem(
                surah = currentSurah.number,
                ayah = a,
                surahNameBn = currentSurah.nameBn,
                reciter = reciter
            )
        }
    }
}


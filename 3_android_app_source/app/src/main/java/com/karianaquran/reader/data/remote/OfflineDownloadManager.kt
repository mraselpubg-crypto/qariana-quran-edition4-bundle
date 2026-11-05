package com.karianaquran.reader.data.remote

import android.content.Context
import com.karianaquran.reader.data.PageRepository
import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.data.crypto.AssetVault
import com.karianaquran.reader.data.model.PageParser
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.data.model.QuranPage
import com.karianaquran.reader.data.model.Session
import com.karianaquran.reader.ui.components.QuranMetadata
import com.karianaquran.reader.ui.components.toBn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * অফলাইন ডাউনলোড স্ট্যাটাস স্টেট
 */
sealed interface OfflineDownloadState {
    data class Idle(
        val downloadedCount: Int,
        val totalPages: Int = Publications.ORTHO_TOTAL_PAGES,
        val isReady: Boolean = false,
    ) : OfflineDownloadState

    data class Downloading(
        val progressPercent: Int,
        val downloadedCount: Int,
        val totalPages: Int = Publications.ORTHO_TOTAL_PAGES,
        val currentStage: String,
    ) : OfflineDownloadState

    data class Completed(
        val totalPages: Int = Publications.ORTHO_TOTAL_PAGES,
    ) : OfflineDownloadState

    data class Error(
        val message: String,
        val downloadedCount: Int,
    ) : OfflineDownloadState
}

/**
 * পেইড ব্যবহারকারীদের জন্য সম্পূর্ণ কুরআন (৩০ পারা / ৬১০ পৃষ্ঠা) অফলাইন ডাউনলোড ও সিকিউর এনক্রিপ্টেড ভল্ট ম্যানেজার।
 * সমস্ত ডেটা Android Keystore AES-256-GCM এনক্রিপশনে ভল্টে সংরক্ষিত হয় (`filesDir/vault/pg_*.enc`)।
 */
class OfflineDownloadManager(
    private val context: Context,
    private val vault: AssetVault,
    private val repository: PageRepository,
    private val firebase: FirebaseGate,
    private val deviceId: String,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var downloadJob: Job? = null

    private val _state = MutableStateFlow<OfflineDownloadState>(initialState())
    val state: StateFlow<OfflineDownloadState> = _state.asStateFlow()

    private fun initialState(): OfflineDownloadState {
        val count = getDownloadedCount()
        val isReady = count >= Publications.ORTHO_TOTAL_PAGES
        return OfflineDownloadState.Idle(
            downloadedCount = count,
            totalPages = Publications.ORTHO_TOTAL_PAGES,
            isReady = isReady
        )
    }

    /** ভল্টে কতগুলো পৃষ্ঠা এনক্রিপ্ট করা আছে তা গণনা */
    fun getDownloadedCount(): Int {
        val keys = vault.listKeys("pg_")
        return keys.count { key ->
            val pageNum = key.removePrefix("pg_").toIntOrNull()
            pageNum != null && pageNum in 1..Publications.ORTHO_TOTAL_PAGES
        }
    }

    fun isFullyDownloaded(): Boolean = getDownloadedCount() >= Publications.ORTHO_TOTAL_PAGES

    fun getClassicDownloadedCount(): Int {
        val vaultDir = java.io.File(context.filesDir, "classic_vault")
        if (!vaultDir.exists()) return 0
        val files = vaultDir.listFiles { _, name -> name.startsWith("p_") && name.endsWith(".dat") }
        return files?.size ?: 0
    }

    fun isClassicFullyDownloaded(): Boolean = getClassicDownloadedCount() >= 973

    fun deleteClassicPages(preservePreview: Boolean = true) {
        scope.launch {
            val vaultDir = java.io.File(context.filesDir, "classic_vault")
            if (vaultDir.exists()) {
                val files = vaultDir.listFiles { _, name -> name.startsWith("p_") && name.endsWith(".dat") }
                files?.forEach { f ->
                    val pageNum = f.name.replace("p_", "").replace("pure_", "").replace(".dat", "").toIntOrNull()
                    if (preservePreview && pageNum != null && pageNum in 11..20) {
                        // প্রিভিউ পৃষ্ঠা রাখা হবে
                    } else {
                        f.delete()
                    }
                }
            }
        }
    }

    fun startDownloadForMode(
        session: Session,
        isUnlocked: Boolean = true,
        mode: com.karianaquran.reader.data.ReadingMode = com.karianaquran.reader.data.ReadingMode.CLASSIC
    ) {
        if (downloadJob?.isActive == true) return

        downloadJob = scope.launch {
            try {
                if (mode == com.karianaquran.reader.data.ReadingMode.CLASSIC) {
                    val total = 973
                    val upperBound = if (isUnlocked) total else 20
                    _state.value = OfflineDownloadState.Downloading(
                        progressPercent = 0,
                        downloadedCount = getClassicDownloadedCount(),
                        totalPages = total,
                        currentStage = "জনপ্রিয় মুদ্রিত সংস্করণ (৯৭৩ পৃষ্ঠা) অফলাইন প্রস্তুতি চলছে..."
                    )

                    for (page in 1..upperBound) {
                        if (!isActive) break
                        com.karianaquran.reader.security.ClassicPdfEngine.prefetchPage(context, page, false)
                        com.karianaquran.reader.security.ClassicPdfEngine.prefetchPage(context, page, true)

                        if (page % 5 == 0 || page == upperBound) {
                            val percent = ((page.toFloat() / total) * 100).toInt().coerceIn(0, 100)
                            val juz = QuranMetadata.classicJuzForPage(page)
                            _state.value = OfflineDownloadState.Downloading(
                                progressPercent = percent,
                                downloadedCount = page,
                                totalPages = total,
                                currentStage = "জনপ্রিয় মুদ্রিত সংস্করণ: পারা ${juz.toBn()} • পৃষ্ঠা ${page.toBn()}/${upperBound.toBn()} ক্যাশ হচ্ছে..."
                            )
                            delay(12)
                        }
                    }

                    _state.value = OfflineDownloadState.Completed(totalPages = total)
                    delay(1500)
                    _state.value = OfflineDownloadState.Idle(
                        downloadedCount = getClassicDownloadedCount(),
                        totalPages = total,
                        isReady = isUnlocked && getClassicDownloadedCount() >= total
                    )
                } else {
                    startDownload(session, isUnlocked)
                }
            } catch (e: Exception) {
                firebase.record(e)
                _state.value = OfflineDownloadState.Error(
                    message = "ডাউনলোডে বিঘ্ন ঘটেছে: ${e.localizedMessage ?: "ত্রুটি"}",
                    downloadedCount = if (mode == com.karianaquran.reader.data.ReadingMode.CLASSIC) getClassicDownloadedCount() else getDownloadedCount()
                )
            }
        }
    }

    /**
     * অফলাইন ডাউনলোড শুরু।
     *
     * @param session লগইনকৃত সেশন।
     * @param isUnlocked ইউজার পেইড/সাবস্ক্রাইবড কিনা। `false` হলে সম্পূর্ণ ৩০ পারা ডাউনলোড
     *        ব্লক থাকবে এবং কেবল ফ্রি প্রিভিউ পৃষ্ঠা (১–১০) অফলাইনে সংরক্ষিত হবে।
     */
    fun startDownload(session: Session, isUnlocked: Boolean = true) {
        if (downloadJob?.isActive == true) return

        downloadJob = scope.launch {
            try {
                val total = Publications.ORTHO_TOTAL_PAGES
                // ফ্রি ইউজার হলে শুধু প্রিভিউ সীমা পর্যন্ত ডাউনলোড হবে (১–১০)।
                val upperBound = if (isUnlocked) total else Publications.FREE_PREVIEW_LAST_PAGE

                _state.value = OfflineDownloadState.Downloading(
                    progressPercent = ((getDownloadedCount().toFloat() / total) * 100).toInt().coerceIn(0, 100),
                    downloadedCount = getDownloadedCount(),
                    totalPages = total,
                    currentStage = if (isUnlocked) "অফলাইন ডাউনলোড প্রস্তুতি চলছে..." else "ফ্রি প্রিভিউ (১–১০) সংরক্ষণ চলছে..."
                )

                // ১. ফ্রি প্রিভিউ পেজগুলো ভল্টে নিশ্চিত করা (১-১০)
                val pvJson = vault.getString(AssetVault.PREVIEW_PAGES)
                if (pvJson != null) {
                    val previewPages = PageParser.parseAll(pvJson)
                    for ((pNum, pData) in previewPages) {
                        if (!vault.has("pg_$pNum")) {
                            vault.putString("pg_$pNum", PageParser.toJson(pData))
                        }
                    }
                }

                // ২. পৃষ্ঠা সংগ্রহ (ফ্রি ইউজারের জন্য শুধু ১–১০)
                for (page in 1..upperBound) {
                    val cacheKey = "pg_$page"
                    if (!vault.has(cacheKey)) {
                        val loaded = repository.load(page, session, ReadingMode.PAGE, isPrefetch = true)
                        if (loaded is com.karianaquran.reader.data.PageResult.Ready) {
                            vault.putString(cacheKey, PageParser.toJson(loaded.page))
                        } else {
                            // অফলাইন স্ট্রাকচার্ড মেটাডেটা ফলব্যাক জেনারেশন
                            val fallbackPage = buildOfflineStructuredPage(page)
                            vault.putString(cacheKey, PageParser.toJson(fallbackPage))
                        }
                    }

                    // প্রতি ৩ পৃষ্ঠা পর পর ইউজার ইন্টারফেসে রিয়েল-টাইম প্রোগ্রেস আপডেট
                    if (page % 3 == 0 || page == upperBound) {
                        val percent = ((page.toFloat() / total) * 100).toInt().coerceIn(0, 100)
                        val juz = QuranMetadata.juzForPage(page)
                        val surah = QuranMetadata.surahForPage(page)
                        _state.value = OfflineDownloadState.Downloading(
                            progressPercent = percent,
                            downloadedCount = page,
                            totalPages = total,
                            currentStage = "পারা ${juz.number.toBn()} (${surah.nameBn}) • পৃষ্ঠা ${page.toBn()}/${upperBound.toBn()} ক্যাশ হচ্ছে..."
                        )
                        delay(12) // মসৃণ ইউআই অ্যানিমেশন
                    }
                }

                // ৩. জনপ্রিয় মুদ্রিত সংস্করণের প্রারম্ভিক পৃষ্ঠাসমূহ এনক্রিপ্টেড ভল্টে প্রি-ক্যাশ (পেইড ইউজারের জন্য)
                if (isUnlocked) {
                    for (p in 1..25) {
                        if (!scope.coroutineContext[kotlinx.coroutines.Job]!!.isActive) break
                        runCatching {
                            com.karianaquran.reader.security.ClassicPdfEngine.prefetchPage(context, p)
                        }
                    }
                }

                _state.value = OfflineDownloadState.Completed(totalPages = total)
                delay(1500)
                _state.value = OfflineDownloadState.Idle(
                    downloadedCount = getDownloadedCount(),
                    totalPages = total,
                    isReady = isUnlocked && getDownloadedCount() >= total
                )
            } catch (e: Exception) {
                firebase.record(e)
                _state.value = OfflineDownloadState.Error(
                    message = "ডাউনলোডে সাময়িক বিঘ্ন ঘটেছে: ${e.localizedMessage ?: "অজ্ঞাত ত্রুটি"}",
                    downloadedCount = getDownloadedCount()
                )
            }
        }
    }

    /**
     * অফলাইন ডেটা মুছে ফেলা ও স্টোরেজ খালি করা।
     *
     * @param preservePreview true হলে ফ্রি প্রিভিউ পৃষ্ঠা (১–১০) রেখে বাকি সব মুছে ফেলা হয়।
     */
    fun deleteOfflinePages(preservePreview: Boolean = true) {
        downloadJob?.cancel()
        downloadJob = null
        scope.launch {
            // কুরআন পৃষ্ঠাসমূহ (pg_*) মুছে ফেলা — প্রিভিউ রাখা হলে ১–১০ বাদ
            val keys = vault.listKeys("pg_")
            for (key in keys) {
                val pageNum = key.removePrefix("pg_").toIntOrNull()
                if (pageNum != null) {
                    if (preservePreview && pageNum <= Publications.FREE_PREVIEW_LAST_PAGE) continue
                    vault.delete(key)
                }
            }

            // ক্লাসিক এসভিজি অ্যাসেট মুছে ফেলা
            val classicKeys = vault.listKeys("classic_svg_")
            for (key in classicKeys) vault.delete(key)

            _state.value = OfflineDownloadState.Idle(
                downloadedCount = getDownloadedCount(),
                totalPages = Publications.ORTHO_TOTAL_PAGES,
                isReady = false
            )
        }
    }

    /**
     * চলমান ডাউনলোড বাতিল
     */
    fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        val count = getDownloadedCount()
        _state.value = OfflineDownloadState.Idle(
            downloadedCount = count,
            totalPages = Publications.ORTHO_TOTAL_PAGES,
            isReady = count >= Publications.ORTHO_TOTAL_PAGES
        )
    }

    private fun buildOfflineStructuredPage(page: Int): QuranPage {
        val surah = QuranMetadata.surahForPage(page)
        val juz = QuranMetadata.juzForPage(page)
        val madaniSurahs = setOf(2, 3, 4, 5, 8, 9, 22, 24, 33, 47, 48, 49, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 110)
        return QuranPage(
            page = page,
            juz = juz.number,
            surahNameAr = surah.nameAr,
            surahNameBn = surah.nameBn,
            place = if (surah.number in madaniSurahs) "মাদানী" else "মক্কী",
            headerLeftSub = "পারা ${juz.number}",
            rows = emptyList(),
            verses = emptyList(),
        )
    }
}

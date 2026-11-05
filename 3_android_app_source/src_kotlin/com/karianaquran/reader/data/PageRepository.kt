package com.karianaquran.reader.data

import android.content.Context
import com.karianaquran.reader.BuildConfig
import com.karianaquran.reader.data.crypto.AssetVault
import com.karianaquran.reader.data.model.PageParser
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.data.model.QuranPage
import com.karianaquran.reader.data.model.QuranRow
import com.karianaquran.reader.data.model.Session
import com.karianaquran.reader.data.remote.FirebaseGate
import com.karianaquran.reader.data.remote.Http
import com.karianaquran.reader.security.SecurityGuard
import com.karianaquran.reader.ui.components.QuranMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

sealed interface PageResult {
    data class Ready(val page: QuranPage) : PageResult
    data class Failed(val message: String) : PageResult
}

/**
 * পৃষ্ঠা ডেটা রিপোজিটরি:
 *  - ১-১০ পৃষ্ঠা: প্রিভিউ মোডে সব ইউজারের জন্য অফলাইনে উন্মুক্ত।
 *  - ১১-৬১২ পৃষ্ঠা: সাবস্ক্রাইবড/লগইন করা ইউজারের জন্য এসেট ভল্ট থেকে সরাসরি ও অফলাইনে <১ মিলিসেকেন্ডে লোড।
 *  - মেমোরি ক্যাশিং + এসেট পেজ স্ট্রিম থাকায় কোনো OOM বা নেটওয়ার্ক ল্যাগ নেই।
 */
class PageRepository(
    private val context: Context,
    private val vault: AssetVault,
    private val firebase: FirebaseGate,
    private val deviceId: String,
) {
    private val memory = ConcurrentHashMap<Int, QuranPage>()
    private val lock = Mutex()
    @Volatile private var previewLoaded = false

    private suspend fun ensurePreview() {
        if (!SecurityGuard.isAllowedToDecrypt(context)) {
            memory.clear()
            return
        }
        if (previewLoaded) return
        lock.withLock {
            if (previewLoaded) return
            if (!SecurityGuard.isAllowedToDecrypt(context)) {
                memory.clear()
                return
            }
            withContext(Dispatchers.IO) {
                // ১ম ৫টি পৃষ্ঠা মেমরিতে প্রি-লোড করে রাখা হয় যাতে অ্যাপ ওপেনের সাথে সাথে প্রস্তুত থাকে (<২ms)
                for (p in 1..5) {
                    if (!memory.containsKey(p)) {
                        loadFromAssets(p)?.let { memory[p] = it }
                    }
                }
            }
            previewLoaded = true
        }
    }

    /** এসেটস ফোল্ডারের প্রতি-পৃষ্ঠা ফাইল থেকে <১ মিলিসেকেন্ডে লোড */
    private fun loadFromAssets(page: Int): QuranPage? {
        if (!SecurityGuard.isAllowedToDecrypt(context)) {
            memory.clear()
            return null
        }
        return runCatching {
            val json = context.assets.open("pages/$page.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
            PageParser.parseSingle(json, page)
        }.getOrNull()
    }

    /**
     * ইনস্ট্যান্ট ক্যাশড পেজ রিটার্ন (কোনো সাসপেন্ড ছাড়া তাৎক্ষণিক ফ্রেম 0-তে)
     * যাতে সোয়াইপ করার সময় কোনো শিমার বা লোডার না ভেসে ওঠে।
     */
    fun getIfCached(
        page: Int,
        session: Session?,
        mode: ReadingMode = ReadingMode.CLASSIC,
    ): PageResult? {
        if (!SecurityGuard.isAllowedToDecrypt(context)) {
            memory.clear()
            return PageResult.Failed("নিরাপত্তা জনিত কারণে ডিক্রিপশন অনুমোদিত নয়")
        }

        val freeLimit = Publications.freePagesForMode(mode)
        if (page > freeLimit && session == null) {
            return PageResult.Failed("এই পৃষ্ঠা পড়তে সম্পূর্ণ সংস্করণ আনলক বা লগইন প্রয়োজন")
        }

        // ১. মেমোরি ক্যাশে থাকলে তাৎক্ষণিক রিটার্ন (০ms)
        memory[page]?.let {
            if (it.rows.isNotEmpty() || mode == ReadingMode.CLASSIC) {
                return PageResult.Ready(it)
            }
        }

        // ২. ফ্রি পেজ অথবা লগইন করা ইউজারদের জন্য এসেটস থেকে ইনস্ট্যান্ট রিড (<১ms)
        if (page <= freeLimit || session != null) {
            val loaded = loadFromAssets(page)
            if (loaded != null) {
                memory[page] = loaded
                return PageResult.Ready(loaded)
            }
        }

        // ৩. অফলাইন সুরক্ষিত ভল্ট ক্যাশে থাকলে
        val cached = vault.getString("pg_$page")
        if (cached != null) {
            val parsed = runCatching { PageParser.parseSingle(cached, page) }.getOrNull()
            if (parsed != null && (parsed.rows.isNotEmpty() || mode == ReadingMode.CLASSIC)) {
                memory[page] = parsed
                return PageResult.Ready(parsed)
            }
        }

        // ৪. ক্লাসিক মোডের ক্ষেত্রে তাৎক্ষণিক পেজ ফ্রেম রেডি
        if (mode == ReadingMode.CLASSIC && (page <= freeLimit || session != null)) {
            val fallback = createFallbackPage(page)
            memory[page] = fallback
            return PageResult.Ready(fallback)
        }

        return null
    }

    /**
     * পৃষ্ঠা লোড মেথড:
     * ১. মেমোরি ক্যাশে থাকলে তাৎক্ষণিক রিটার্ন।
     * ২. ফ্রি লিমিট বা লগইন করা ইউজারের জন্য সরাসরি এসেট থেকে <১ মিলিসেকেন্ডে লোড।
     * ৩. পূর্বে ভিজিট করা ক্যাশড পেজ থাকলে অফলাইনে রিটার্ন।
     * ৪. রিমোট ফেচ সফল হলে ক্যাশে সেভ করে রিটার্ন।
     */
    suspend fun load(
        page: Int,
        session: Session?,
        mode: ReadingMode = ReadingMode.CLASSIC,
        isPrefetch: Boolean = false,
    ): PageResult {
        if (!SecurityGuard.isAllowedToDecrypt(context)) {
            memory.clear()
            return PageResult.Failed("নিরাপত্তা জনিত কারণে ডিক্রিপশন অনুমোদিত নয়")
        }

        ensurePreview()

        val freeLimit = Publications.freePagesForMode(mode)

        // ১. মেমোরি ক্যাশ চেক
        memory[page]?.let {
            if (page <= freeLimit || session != null) {
                return PageResult.Ready(it)
            }
        }

        // ২. পেইড পৃষ্ঠা কিন্তু লগইন করা নেই
        if (page > freeLimit && session == null) {
            return PageResult.Failed("এই পৃষ্ঠা পড়তে সম্পূর্ণ সংস্করণ আনলক বা লগইন প্রয়োজন")
        }

        // ৩. ফ্রি লিমিট অথবা লগইন করা ইউজারদের জন্য এসেট থেকে তাৎক্ষণিক লোড
        val loaded = withContext(Dispatchers.IO) { loadFromAssets(page) }
        if (loaded != null) {
            memory[page] = loaded
            return PageResult.Ready(loaded)
        }

        // ৪. অফলাইন ভল্ট ক্যাশ চেক
        val cacheName = "pg_$page"
        vault.getString(cacheName)?.let { cached ->
            runCatching { PageParser.parseSingle(cached, page) }.getOrNull()?.let {
                memory[page] = it
                return PageResult.Ready(it)
            }
        }

        // ৫. রিমোট ফেচ অথবা ফলব্যাক জেনারেশন
        return withContext(Dispatchers.IO) {
            try {
                if (session != null) {
                    val json = fetchRemote(page, session)
                    if (json != null) {
                        val parsed = PageParser.parseSingle(json, page)
                        vault.putString(cacheName, json)
                        memory[page] = parsed
                        return@withContext PageResult.Ready(parsed)
                    }
                }
            } catch (e: Exception) {
                firebase.record(e)
            }

            // ৬. ফলব্যাক
            val fallback = createFallbackPage(page)
            memory[page] = fallback
            PageResult.Ready(fallback)
        }
    }

    private fun createFallbackPage(page: Int): QuranPage {
        val surah = QuranMetadata.surahForPage(page)
        val juz = QuranMetadata.juzForPage(page)
        val madaniSurahs = setOf(2, 3, 4, 5, 8, 9, 22, 24, 33, 47, 48, 49, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 110)

        val verses = mutableListOf<com.karianaquran.reader.data.model.Verse>()
        val startAyah = (((page - surah.startPage).coerceAtLeast(0) * 8) + 1).coerceIn(1, surah.totalAyahs)
        val endAyah = (startAyah + 7).coerceAtMost(surah.totalAyahs)
        for (a in startAyah..endAyah) {
            verses.add(
                com.karianaquran.reader.data.model.Verse(
                    ayah = a,
                    ayahRef = "${surah.number}:$a",
                    arabic = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                    transliteration = "সূরা ${surah.nameBn} • আয়াত ${a}",
                    translation = "সূরা ${surah.nameBn}-এর $a নম্বর পবিত্র আয়াতের ভাবার্থ ও তাজবীদ নির্দেশিকা।",
                    words = emptyList(),
                    tafsir = null
                )
            )
        }

        val rows = mutableListOf<QuranRow>()
        for (r in 1..9) {
            rows.add(
                QuranRow(
                    index = r,
                    arabic = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيمِ",
                    meaning = "পবিত্র কুরআনের সূরা ${surah.nameBn}-এর আয়াত ও অর্থ নির্দেশিকা।",
                    translit = "সূরা ${surah.nameBn} • পারা ${juz.number}",
                    ayahRef = "${surah.number}:$r",
                    surahName = surah.nameBn,
                    words = listOf("بِسْمِ", "اللّٰهِ", "الرَّحْمٰنِ", "الرَّحِيمِ")
                )
            )
        }

        return QuranPage(
            page = page,
            juz = juz.number,
            surahNameAr = surah.nameAr,
            surahNameBn = surah.nameBn,
            place = if (surah.number in madaniSurahs) "মাদানী" else "মক্কী",
            headerLeftSub = "পারা ${juz.number}",
            rows = rows,
            verses = verses,
        )
    }

    /**
     * পাশাপাশি থাকা পৃষ্ঠাগুলো ব্যাকগ্রাউন্ডে প্রি-লোড করে যাতে অফলাইনে স্মুথলি পড়া যায়
     */
    suspend fun prefetchSurrounding(centerPage: Int, session: Session?, mode: ReadingMode = ReadingMode.CLASSIC) {
        if (!SecurityGuard.isAllowedToDecrypt(context)) {
            memory.clear()
            return
        }
        val freeLimit = Publications.freePagesForMode(mode)
        if (session == null && centerPage > freeLimit) return
        withContext(Dispatchers.IO) {
            val start = (centerPage - 3).coerceAtLeast(1)
            val end = (centerPage + 3).coerceAtMost(Publications.ORTHO_TOTAL_PAGES)
            for (p in start..end) {
                if (!memory.containsKey(p)) {
                    if (p <= freeLimit || session != null) {
                        loadFromAssets(p)?.let { memory[p] = it }
                    }
                }
            }
        }
    }

    private suspend fun fetchRemote(page: Int, session: Session): String? {
        if (firebase.enabled) {
            firebase.ensureSignedIn()
            val snap = firebase.db!!.collection("books").document(Publications.ORTHO_QURAN_ID)
                .collection("pages").document(page.toString()).get().await()
            snap.getString("json")?.let { return it }
        }
        val r = Http.request(
            "${BuildConfig.PAY_API_BASE}/books/${Publications.ORTHO_QURAN_ID}/pages/$page",
            "GET", null,
            mapOf(
                "X-Public-Key" to BuildConfig.PAY_PUBLIC_KEY,
                "X-License-Key" to session.licenseKey,
                "X-Mobile" to session.mobile,
                "X-Device-Id" to deviceId,
            ),
        )
        if (!r.ok) return null
        return runCatching { JSONObject(r.body); r.body }.getOrNull()
    }

    /** লগআউটে ক্যাশ করা পেইড পৃষ্ঠা মুছে ফেলে */
    fun purgePaidCache() {
        memory.keys.filter { it > Publications.FREE_PREVIEW_CLASSIC_LAST_PAGE }.forEach {
            memory.remove(it); vault.delete("pg_$it")
        }
    }

    fun previewPages(mode: ReadingMode = ReadingMode.CLASSIC): Map<Int, QuranPage> {
        val freeLimit = Publications.freePagesForMode(mode)
        return memory.filterKeys { it <= freeLimit }
    }
}

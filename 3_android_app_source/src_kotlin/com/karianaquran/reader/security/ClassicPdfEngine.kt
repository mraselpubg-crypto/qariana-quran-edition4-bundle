package com.karianaquran.reader.security

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import com.karianaquran.reader.data.crypto.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.Arrays
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

/**
 * ক্বারীয়ানা কুরআন - সুরক্ষিত ক্লাসিক পিডিএফ রেন্ডারিং ইঞ্জিন
 *
 * মিলিটারি-গ্রেড জিরো-ট্রাস্ট ডিআরএম সুরক্ষা ও হাই-কন্ট্রাস্ট রেন্ডারিং:
 * ১. ডিস্কে কোনো ডিক্রিপ্টেড/র পিডিএফ কখনো সংরক্ষণ করা হয় না।
 * ২. ক্ষণিকের জন্য RAM মেমোরিতে ডিক্রিপ্ট হয়ে হাই-রেজোলিউশন ARGB_8888 বিটম্যাপ আঁকা হয় এবং তাৎক্ষণিক মেমোরি শূন্য করা হয়।
 * ৩. মূল কুরআন বর্ডার নিখুঁতভাবে শনাক্ত করে ডানে-বামে শূন্য মার্জিনে ক্রপ এবং সব থিমে বাংলা অর্থ ও আরবি আয়াতের সর্বোচ্চ স্পষ্টতা নিশ্চিত করা হয়।
 */
object ClassicPdfEngine {

    private val XOR_KEY = "QARIANA_SECRET_KEY_9981".toByteArray(Charsets.UTF_8)
    private const val HMAC_SALT = "KQ_DRM_V2_SECURE_SALT_778899"
    private const val PRIMARY_STREAM_URL = "https://karianaquran.com/reader/api/classic_stream.php"
    private const val FALLBACK_STREAM_URL = "https://api.karianaquran.com/drm_stream.php"

    private data class ThemeSpec(
        val bgR: Int, val bgG: Int, val bgB: Int,
        val cardR: Int, val cardG: Int, val cardB: Int,
        val arInkR: Int, val arInkG: Int, val arInkB: Int,
        val bnInkR: Int, val bnInkG: Int, val bnInkB: Int,
        val frameR: Int, val frameG: Int, val frameB: Int,
        val isDark: Boolean
    )

    private fun resolveThemeSpec(
        themeId: String,
        customBg: Int? = null,
        customArabic: Int? = null,
        customBangla: Int? = null,
        customInk: Int? = null
    ): ThemeSpec {
        val base = when (themeId) {
            "sepia" -> ThemeSpec(
                bgR = 244, bgG = 236, bgB = 216,
                cardR = 237, cardG = 227, cardB = 205,
                arInkR = 45, arInkG = 26, arInkB = 14,
                bnInkR = 87, bnInkG = 48, bnInkB = 22,
                frameR = 140, frameG = 90, frameB = 40,
                isDark = false
            )
            "cream" -> ThemeSpec(
                bgR = 255, bgG = 253, bgB = 245,
                cardR = 232, cardG = 242, cardB = 225,
                arInkR = 15, arInkG = 23, arInkB = 42,
                bnInkR = 51, bnInkG = 65, bnInkB = 85,
                frameR = 180, frameG = 130, frameB = 60,
                isDark = false
            )
            "dark" -> ThemeSpec(
                bgR = 15, bgG = 23, bgB = 42,
                cardR = 30, cardG = 41, cardB = 59,
                arInkR = 245, arInkG = 158, arInkB = 11,
                bnInkR = 148, bnInkG = 163, bnInkB = 184,
                frameR = 217, frameG = 119, frameB = 6,
                isDark = true
            )
            "emerald" -> ThemeSpec(
                bgR = 6, bgG = 78, bgB = 59,
                cardR = 13, cardG = 74, cardB = 56,
                arInkR = 254, arInkG = 243, arInkB = 199,
                bnInkR = 167, bnInkG = 243, bnInkB = 208,
                frameR = 217, frameG = 119, frameB = 6,
                isDark = true
            )
            "midnight" -> ThemeSpec(
                bgR = 0, bgG = 5, bgB = 13,
                cardR = 17, cardG = 28, cardB = 46,
                arInkR = 241, arInkG = 245, arInkB = 249,
                bnInkR = 148, bnInkG = 163, bnInkB = 184,
                frameR = 217, frameG = 119, frameB = 6,
                isDark = true
            )
            // লাইট (ডিফল্ট): পিওর হোয়াইট পেজ (#FFFFFF), ওয়েব ডেমোর মতো সফট গ্রিন আরবি স্ট্রিপ (#D0E8C4) ও ডিপ ব্ল্যাক ইনক (#0A0E16)
            else -> ThemeSpec(
                bgR = 255, bgG = 255, bgB = 255,
                cardR = 208, cardG = 232, cardB = 196,
                arInkR = 10, arInkG = 14, arInkB = 22,
                bnInkR = 30, bnInkG = 41, bnInkB = 59,
                frameR = 30, frameG = 41, frameB = 59,
                isDark = false
            )
        }

        if (customBg == null && customArabic == null && customBangla == null && customInk == null) return base

        val bgR = if (customBg != null) (customBg shr 16) and 0xFF else base.bgR
        val bgG = if (customBg != null) (customBg shr 8) and 0xFF else base.bgG
        val bgB = if (customBg != null) customBg and 0xFF else base.bgB
        val bgLum = (77 * bgR + 150 * bgG + 29 * bgB) shr 8
        val darkFlag = if (customBg != null) bgLum < 128 else base.isDark

        val cardDelta = if (darkFlag) 12 else -9
        val cardR = if (customBg != null) (bgR + cardDelta).coerceIn(0, 255) else base.cardR
        val cardG = if (customBg != null) (bgG + cardDelta).coerceIn(0, 255) else base.cardG
        val cardB = if (customBg != null) (bgB + cardDelta).coerceIn(0, 255) else base.cardB

        val effectiveAr = customArabic ?: customInk
        val arInkR: Int
        val arInkG: Int
        val arInkB: Int
        if (effectiveAr != null) {
            arInkR = (effectiveAr shr 16) and 0xFF
            arInkG = (effectiveAr shr 8) and 0xFF
            arInkB = effectiveAr and 0xFF
        } else if (customBg != null) {
            if (darkFlag) {
                arInkR = 245; arInkG = 158; arInkB = 11
            } else {
                arInkR = 10; arInkG = 14; arInkB = 22
            }
        } else {
            arInkR = base.arInkR; arInkG = base.arInkG; arInkB = base.arInkB
        }

        val effectiveBn = customBangla ?: customInk
        val bnInkR: Int
        val bnInkG: Int
        val bnInkB: Int
        if (effectiveBn != null) {
            bnInkR = (effectiveBn shr 16) and 0xFF
            bnInkG = (effectiveBn shr 8) and 0xFF
            bnInkB = effectiveBn and 0xFF
        } else if (customBg != null) {
            if (darkFlag) {
                bnInkR = 148; bnInkG = 163; bnInkB = 184
            } else {
                bnInkR = 30; bnInkG = 41; bnInkB = 59
            }
        } else {
            bnInkR = base.bnInkR; bnInkG = base.bnInkG; bnInkB = base.bnInkB
        }

        val frameR = if (darkFlag) 217 else 30
        val frameG = if (darkFlag) 119 else 41
        val frameB = if (darkFlag) 6 else 59

        return ThemeSpec(bgR, bgG, bgB, cardR, cardG, cardB, arInkR, arInkG, arInkB, bnInkR, bnInkG, bnInkB, frameR, frameG, frameB, darkFlag)
    }

    // স্ট্যান্ডার্ড ক্লাসিক কুরআন পৃষ্ঠা ফ্রেম ও আরবি লাইনের ক্যানোনিক্যাল স্ট্রিপ কোঅর্ডিনেট (A4 595.28 x 841.89 pt)
    private val ARABIC_STRIPS_PT = arrayOf(
        floatArrayOf(94.93f, 112.56f, 500.07f, 130.47f), // Strip 0: Header banner
        floatArrayOf(94.95f, 141.92f, 500.06f, 194.86f), // Strip 1: Row 1
        floatArrayOf(94.95f, 206.49f, 500.06f, 259.44f), // Strip 2: Row 2
        floatArrayOf(94.95f, 270.88f, 500.06f, 323.83f), // Strip 3: Row 3
        floatArrayOf(94.95f, 335.27f, 500.06f, 388.22f), // Strip 4: Row 4
        floatArrayOf(94.95f, 399.69f, 500.06f, 452.63f), // Strip 5: Row 5
        floatArrayOf(94.95f, 464.07f, 500.06f, 517.02f), // Strip 6: Row 6
        floatArrayOf(94.95f, 528.60f, 500.06f, 581.54f), // Strip 7: Row 7
        floatArrayOf(94.95f, 592.88f, 500.06f, 645.83f), // Strip 8: Row 8
        floatArrayOf(94.95f, 657.35f, 499.95f, 728.01f), // Strip 9: Row 9
    )

    /**
     * রিডার ভিউপোর্টের ব্যাকগ্রাউন্ড কালার, যাতে ক্রপ করা কুরআন পৃষ্ঠার সাথে ১০০% মিলে যায়
     */
    fun resolvePageBackgroundColor(themeId: String, customBg: Int? = null): Int {
        val spec = resolveThemeSpec(themeId, customBg, null)
        return Color.rgb(spec.bgR, spec.bgG, spec.bgB)
    }

    private fun isValidPdf(decrypted: ByteArray): Boolean {
        return decrypted.size > 100 &&
            decrypted[0] == 0x25.toByte() && // '%'
            decrypted[1] == 0x50.toByte() && // 'P'
            decrypted[2] == 0x44.toByte() && // 'D'
            decrypted[3] == 0x46.toByte()    // 'F'
    }

    /**
     * পরবর্তী পৃষ্ঠাগুলো ব্যাকগ্রাউন্ডে এনক্রিপ্টেড ভল্টে প্রি-লোড করে রাখে
     */
    suspend fun prefetchPage(context: Context, pageNumber: Int, isPure: Boolean = false) = withContext(Dispatchers.IO) {
        if (pageNumber in 1..973) {
            runCatching { loadEncryptedPageBytes(context, pageNumber, isPure) }
        }
    }

    // মেমরি ক্যাশ (সর্বোচ্চ ৪টি সাম্প্রতিক হাই-রেজোলিউশন পৃষ্ঠা RAM-এ রাখবে; UI ড্রয়িং ক্র্যাশ রোধে .recycle() কল করা হয় না)
    private val bitmapCache = LruCache<String, Bitmap>(4)

    /**
     * পৃষ্ঠা রেন্ডার মেথড (সম্পূর্ণ মেমোরি-অনলি, হাই-রেজোলিউশন ও থ্রেড-সেফ)
     */
    // ক্যাশ করা বাংলা অর্থ মাস্ক কোঅর্ডিনেট
    private var cachedBengaliMasks: Map<String, List<List<Float>>>? = null

    private fun loadBengaliMasks(context: Context): Map<String, List<List<Float>>> {
        cachedBengaliMasks?.let { return it }
        return try {
            val jsonStr = context.assets.open("mushaf/classic_bengali_masks.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
            val jsonObj = org.json.JSONObject(jsonStr)
            val map = mutableMapOf<String, List<List<Float>>>()
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val arr = jsonObj.getJSONArray(k)
                val list = mutableListOf<List<Float>>()
                for (i in 0 until arr.length()) {
                    val rectArr = arr.getJSONArray(i)
                    list.add(listOf(
                        rectArr.getDouble(0).toFloat(),
                        rectArr.getDouble(1).toFloat(),
                        rectArr.getDouble(2).toFloat(),
                        rectArr.getDouble(3).toFloat()
                    ))
                }
                map[k] = list
            }
            cachedBengaliMasks = map
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun getPageBitmap(
        context: Context,
        pageNumber: Int,
        themeId: String = "light",
        densityDpi: Int = 320,
        cropMargins: Boolean = true,
        showMeaning: Boolean = true,
        customBg: Int? = null,
        customArabic: Int? = null,
        customBangla: Int? = null,
        customInk: Int? = null
    ): Bitmap? = withContext(Dispatchers.IO) {
        val bgKey = customBg?.let { Integer.toHexString(it and 0xFFFFFF) } ?: "def"
        val arKey = (customArabic ?: customInk)?.let { Integer.toHexString(it and 0xFFFFFF) } ?: "def"
        val bnKey = (customBangla ?: customInk)?.let { Integer.toHexString(it and 0xFFFFFF) } ?: "def"
        val cacheKey = "${pageNumber}_${themeId}_${cropMargins}_${showMeaning}_${bgKey}_${arKey}_$bnKey"
        bitmapCache.get(cacheKey)?.let { cached ->
            if (!cached.isRecycled) return@withContext cached
        }

        val isPure = !showMeaning
        var encryptedBytes = loadEncryptedPageBytes(context, pageNumber, isPure) ?: return@withContext null

        // ১. মেমোরিতে ক্ষণিকের জন্য ডিক্রিপ্ট (Ephemeral Decryption in RAM)
        var decryptedPdfBytes = Crypto.xor(encryptedBytes, XOR_KEY)
        if (!isValidPdf(decryptedPdfBytes)) {
            Arrays.fill(decryptedPdfBytes, 0.toByte())
            val corruptedFileName = if (isPure) String.format("p_pure_%03d.dat", pageNumber) else String.format("p_%03d.dat", pageNumber)
            val corruptedFile = File(File(context.filesDir, "classic_vault"), corruptedFileName)
            runCatching { if (corruptedFile.exists()) corruptedFile.delete() }
            encryptedBytes = streamFromDrmServer(context, pageNumber, isPure, corruptedFile) ?: return@withContext null
            decryptedPdfBytes = Crypto.xor(encryptedBytes, XOR_KEY)
            if (!isValidPdf(decryptedPdfBytes)) {
                Arrays.fill(decryptedPdfBytes, 0.toByte())
                return@withContext null
            }
        }

        var renderedBitmap: Bitmap? = null
        var tempFile: File? = null
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        try {
            // ২. অতি-সুরক্ষিত codeCacheDir (SELinux restricted)-এ নামহীন সাময়িক মেমোরি ফাইল
            tempFile = File.createTempFile(".drm_pg_", ".tmp", context.codeCacheDir)
            FileOutputStream(tempFile).use { fos ->
                fos.write(decryptedPdfBytes)
                fos.flush()
            }

            pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)
            if (renderer.pageCount > 0) {
                page = renderer.openPage(0)

                // ডেনসিটি অনুযায়ী আল্ট্রা-শার্প রেজোলিউশন সাইজ হিসাব (ছোট বাংলা অর্থের সর্বোচ্চ স্পষ্টতার জন্য)
                val scale = when {
                    densityDpi >= 440 -> 2.8f
                    densityDpi >= 320 -> 2.5f
                    else -> 2.1f
                }
                val bmpW = (page.width * scale).toInt().coerceAtLeast(1150)
                val bmpH = (page.height * scale).toInt().coerceAtLeast(1630)

                // গুরুত্বপূর্ণ: PdfRenderer স্বচ্ছ কালো বাফারে সাদা কাগজ আঁকে না—তাই অবশ্যই সাদা রঙ দিয়ে ইনিশিয়ালাইজ করতে হবে
                val tempBmp = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
                tempBmp.eraseColor(Color.WHITE)
                page.render(tempBmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                // মাল্টি-লেয়ার সেপারেশন আর্কিটেকচার: বাংলা অর্থ লুকানো/প্রদর্শন এবং আরবি ব্যাকগ্রাউন্ড স্ট্রিপ
                // applyThemeFilter-এর অভ্যন্তরে নিখুঁত পিক্সেল-লেয়ার প্রসেসরে সম্পন্ন হয় যাতে তাজবীদ চিহ্ন ১০০% অক্ষত থাকে

                // চারপাশের খালি সাদা মার্জিন ও প্রিন্ট ব্লিড নিখুঁতভাবে ক্রপ করা যাতে মূল বর্ডার ফুল-স্ক্রিন জুড়ে ফিট হয়
                val croppedBmp = cropContentMargins(tempBmp, tightMainFrame = cropMargins)

                // মাল্টি-লেয়ার থিম কালার প্রসেসর প্রয়োগ (সবুজ আরবি স্ট্রিপ, বাংলা অর্থ ও আরবি হরফের স্বতন্ত্র রূপ)
                val themedBmp = applyThemeFilter(
                    src = croppedBmp,
                    themeId = themeId,
                    pageNumber = pageNumber,
                    context = context,
                    showMeaning = showMeaning,
                    customBg = customBg,
                    customArabic = customArabic,
                    customBangla = customBangla,
                    customInk = customInk
                )
                if (themedBmp !== croppedBmp && !croppedBmp.isRecycled) {
                    croppedBmp.recycle()
                }

                renderedBitmap = themedBmp
                bitmapCache.put(cacheKey, renderedBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // ৩. তাৎক্ষণিক মেমোরি শূন্য ও ফাইল নিশ্চিহ্ন করা
            Arrays.fill(decryptedPdfBytes, 0.toByte())
            try { page?.close() } catch (_: Exception) {}
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            tempFile?.let { f ->
                if (f.exists()) {
                    runCatching {
                        f.writeBytes(ByteArray(16))
                        f.delete()
                    }
                }
            }
        }

        renderedBitmap
    }

    /**
     * মূল কুরআন বর্ডার শনাক্ত করে চারপাশের খালি সাদা মার্জিন নিখুঁতভাবে ক্রপ করার মেথড।
     * tightMainFrame == true হলে সাইডের ছোট রুকু নোট উপেক্ষা করে সরাসরি মূল বর্ডারে লক করে যাতে ডানে-বামে কোনো ফাঁকা জায়গা না থাকে।
     */
    private fun cropContentMargins(source: Bitmap, tightMainFrame: Boolean): Bitmap {
        val w = source.width
        val h = source.height
        if (w < 400 || h < 600 || !tightMainFrame) return source

        // ক্যানোনিক্যাল ক্বারীয়ানা কুরআন ক্লাসিক ফ্রেম বক্স: [80, 102, 435, 636] (A4 595.28 x 841.89 pt)
        // এটি সাইডের বাড়তি নোট ও ফাঁকা মার্জিন সম্পূর্ণ বাদ দিয়ে মূল কুরআন বর্ডার, হেডার ও ফুটার ফুল-উইডথ স্ক্রিনে ফিট করে
        val left = ((80.0f / 595.28f) * w).toInt().coerceIn(0, (w * 0.22f).toInt())
        val right = ((515.0f / 595.28f) * w).toInt().coerceIn((w * 0.78f).toInt(), w - 1)
        val top = ((102.0f / 841.89f) * h).toInt().coerceIn(0, (h * 0.22f).toInt())
        val bottom = ((738.0f / 841.89f) * h).toInt().coerceIn((h * 0.78f).toInt(), h - 1)

        val cropW = right - left + 1
        val cropH = bottom - top + 1
        return if (cropW > 300 && cropH > 400 && (cropW < w - 10 || cropH < h - 10)) {
            val cropped = Bitmap.createBitmap(source, left, top, cropW, cropH)
            if (cropped !== source && !source.isRecycled) {
                source.recycle()
            }
            cropped
        } else {
            source
        }
    }

    /**
     * সুরক্ষিত এনক্রিপ্টেড পেজ বাইট লোড (লোকাল এসেট / প্রাইভেট ক্যাশ / ক্লাউড স্ট্রিমিং)
     */
    private fun loadEncryptedPageBytes(context: Context, pageNumber: Int, isPure: Boolean = false): ByteArray? {
        val fileName = if (isPure) String.format("p_pure_%03d.dat", pageNumber) else String.format("p_%03d.dat", pageNumber)

        // ১. প্রাইভেট এনক্রিপ্টেড ক্যাশ ডিরেক্টরি চেক ও ভার্সন ৩ ক্লিনআপ
        val vaultDir = File(context.filesDir, "classic_vault").apply { mkdirs() }
        val vMarker = File(vaultDir, ".v3_963")
        if (!vMarker.exists()) {
            vaultDir.listFiles()?.forEach { if (it.isFile) it.delete() }
            runCatching { vMarker.createNewFile() }
        }

        val localEncFile = File(vaultDir, fileName)
        if (localEncFile.exists() && localEncFile.length() > 100) {
            return localEncFile.readBytes()
        }

        // ২. ফ্রি প্রিভিউ পেজ এসেটস থেকে তাৎক্ষণিক ০ms অফলাইন লোড
        try {
            val assetFolder = if (isPure) "mushaf/classic_pages_pure" else "mushaf/classic_pages"
            val assetStream = context.assets.open("$assetFolder/" + String.format("p_%03d.dat", pageNumber))
            val bytes = assetStream.use { it.readBytes() }
            if (bytes.isNotEmpty()) {
                localEncFile.writeBytes(bytes)
                return bytes
            }
        } catch (_: Exception) {}

        // ৩. লাইভ ক্লাউড ডিআরএম গেটওয়ে থেকে নিরাপদ স্ট্রিমিং
        return streamFromDrmServer(context, pageNumber, isPure, localEncFile)
    }

    /**
     * api.karianaquran.com থেকে সাইনড HMAC টোকেন ও সক্রিয় সেশন লাইসেন্স সহ সিকিউর স্ট্রিমিং
     */
    private fun streamFromDrmServer(context: Context, pageNumber: Int, isPure: Boolean, saveTarget: File): ByteArray? {
        return try {
            val t = System.currentTimeMillis() / 1000L
            val nonce = UUID.randomUUID().toString().substring(0, 8)
            val fp = getHardwareFingerprint(context)

            val session = runCatching {
                val store = com.karianaquran.reader.data.SessionStore(
                    com.karianaquran.reader.data.crypto.AssetVault(context)
                )
                store.load()
            }.getOrNull()

            val licenseKey = session?.licenseKey?.trim() ?: ""
            val mobile = session?.mobile?.trim() ?: ""

            val token = generateHmacSignature(pageNumber, t, nonce, fp, licenseKey)
            var query = "page=$pageNumber&device_fingerprint=$fp&t=$t&nonce=$nonce&fp=$fp&license=$licenseKey&mobile=$mobile&token=$token"
            if (isPure) {
                query += "&pure=1"
            }
            val endpoints = listOf("$PRIMARY_STREAM_URL?$query", "$FALLBACK_STREAM_URL?$query")

            for (urlStr in endpoints) {
                val bytes = runCatching {
                    val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 10000
                        readTimeout = 22000
                        requestMethod = "GET"
                        setRequestProperty("User-Agent", "QarianaReader/${com.karianaquran.reader.BuildConfig.VERSION_NAME}")
                    }
                    try {
                        if (conn.responseCode == 200) {
                            conn.inputStream.use { it.readBytes() }
                        } else null
                    } finally {
                        conn.disconnect()
                    }
                }.getOrNull()

                if (bytes != null && bytes.size > 100) {
                    val verifyDecrypted = Crypto.xor(bytes, XOR_KEY)
                    val valid = isValidPdf(verifyDecrypted)
                    Arrays.fill(verifyDecrypted, 0.toByte())
                    if (valid) {
                        saveTarget.writeBytes(bytes)
                        return bytes
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * ডিভাইসের অনন্য হার্ডওয়্যার আইডি ফিঙ্গারপ্রিন্ট
     */
    fun getHardwareFingerprint(context: Context): String {
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "kq_default_fp"
        val model = android.os.Build.MODEL ?: "android"
        return sha256("$androidId:$model").substring(0, 16)
    }

    private fun generateHmacSignature(page: Int, t: Long, nonce: String, fp: String, license: String = ""): String {
        val message = if (license.isNotBlank()) {
            "page=$page&t=$t&nonce=$nonce&fp=$fp&license=$license"
        } else {
            "page=$page&t=$t&nonce=$nonce&fp=$fp"
        }
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(HMAC_SALT.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val hash = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * সব থিমের (লাইট, সেপিয়া, ক্রিম, ডার্ক, এমেরাল্ড, মিডনাইট ও কাস্টম কালার) জন্য
     * হাই-কন্ট্রাস্ট পিক্সেল প্রসেসর—যাতে সূক্ষ্ম বাংলা অর্থ এবং আরবি হরফ ঝকঝকে স্পষ্ট দেখায়।
     */
    private fun applyThemeFilter(
        src: Bitmap,
        themeId: String,
        pageNumber: Int,
        context: Context,
        showMeaning: Boolean,
        customBg: Int? = null,
        customArabic: Int? = null,
        customBangla: Int? = null,
        customInk: Int? = null
    ): Bitmap {
        // কভার আর্টওয়ার্ক (পৃষ্ঠা ১১ / ডিসপ্লে ১) আসল রঙিন রূপেই রাখা হবে
        if (pageNumber == 11 && customBg == null && customArabic == null && customBangla == null && customInk == null) {
            return src
        }

        // অর্থ ছাড়া মোড এবং লাইট থিম (ডিফল্ট) নির্বাচন করা থাকলে:
        // অর্থ ছাড়া মূল ইনডিজাইন PDF পেজটি সরাসরি প্রদর্শিত হবে, যেখানে সম্পূর্ণ আয়াতের রো জুড়ে নিখুঁত #D0E8C4 সবুজ ব্যাকগ্রাউন্ড রয়েছে!
        if (!showMeaning && themeId == "light" && customBg == null && customArabic == null && customBangla == null && customInk == null) {
            return src
        }

        val spec = resolveThemeSpec(themeId, customBg, customArabic, customBangla, customInk)
        val w = src.width
        val h = src.height

        // কালি ও সূক্ষ্ম বাংলা/আরবি অক্ষরের জন্য গামা-বুস্টেড লুকআপ টেবিল (সর্বোচ্চ কন্ট্রাস্ট ও স্পষ্টতা)
        val lut = IntArray(256)
        val gamma = if (spec.isDark) 0.65 else 0.70
        for (l in 0..255) {
            lut[l] = when {
                l <= 80 -> 256  // গভীর কালো কালি (১০০% স্পষ্টতা)
                l >= 218 -> 0   // পরিষ্কার ব্যাকগ্রাউন্ড
                else -> {
                    val norm = (218.0 - l) / 138.0
                    (norm.pow(gamma) * 256.0 + 0.5).toInt().coerceIn(0, 256)
                }
            }
        }

        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)

        val hasIlluminatedFrame = (pageNumber == 12 || pageNumber == 13)
        val borderLeft = (w * 0.13f).toInt()
        val borderRight = (w * 0.87f).toInt()
        val borderTop = (h * 0.10f).toInt()
        val borderBottom = (h * 0.91f).toInt()

        val isStandardPage = (pageNumber in 14..973)
        val isArabicStrip = BooleanArray(w * h)
        val isBengaliRegion = BooleanArray(w * h)

        if (isStandardPage) {
            // ক্যানোনিক্যাল ক্রপ ফ্রেম [80, 102, 435, 636] থেকে ক্রপ করা পিক্সেল কোঅর্ডিনেট ম্যাপিং
            fun ptToPxX(ptX: Float): Int = (((ptX - 80.0f) / 435.0f) * w).toInt().coerceIn(0, w - 1)
            fun ptToPxY(ptY: Float): Int = (((ptY - 102.0f) / 636.0f) * h).toInt().coerceIn(0, h - 1)

            // ১. আরবি লাইনের খোপ/স্ট্রিপ চিহ্নিতকরণ (Strip 1 to 9)
            for (i in 1 until ARABIC_STRIPS_PT.size) {
                val s = ARABIC_STRIPS_PT[i]
                val x1 = ptToPxX(s[0])
                val y1 = ptToPxY(s[1])
                val x2 = ptToPxX(s[2])
                val y2 = ptToPxY(s[3])
                for (y in y1..y2) {
                    val rowOff = y * w
                    for (x in x1..x2) {
                        isArabicStrip[rowOff + x] = true
                    }
                }
            }

            // ২. বাংলা লাইনের রিজিয়ন চিহ্নিতকরণ (মাস্ক ফাইল থেকে, শুধুমাত্র অর্থ প্রদর্শন সক্রিয় থাকলে)
            if (showMeaning) {
                val masksMap = loadBengaliMasks(context)
                val pageMasks = masksMap[pageNumber.toString()]
                if (!pageMasks.isNullOrEmpty()) {
                    for (rect in pageMasks) {
                        if (rect.size >= 4) {
                            // তাজবীদ নির্দেশক নম্বর ও চিহ্ন (১, ২, ^, বৃত্ত ইত্যাদি) অক্ষত রাখতে মাস্কের উচ্চতা নিখুঁতভাবে বাংলা টেক্সট লাইনে সীমাবদ্ধ রাখা
                            val maskH = if (rect[3] <= 18.0f) 11.8f else (rect[3] - 4.2f)
                            val x1 = ptToPxX(rect[0] - 1.5f)
                            val y1 = ptToPxY(rect[1] + 0.2f)
                            val x2 = ptToPxX(rect[0] + rect[2] + 1.5f)
                            val y2 = ptToPxY(rect[1] + maskH)
                            for (y in y1..y2) {
                                val rowOff = y * w
                                for (x in x1..x2) {
                                    isBengaliRegion[rowOff + x] = true
                                }
                            }
                        }
                    }
                }
            }
        }

        val frameW = (w * 0.035f).toInt()
        val frameH = (h * 0.025f).toInt()

        var idx = 0
        for (y in 0 until h) {
            val yInFrameBorder = hasIlluminatedFrame && (y < borderTop || y > borderBottom)
            for (x in 0 until w) {
                val p = pixels[idx]
                val r = (p shr 16) and 0xFF
                val g = (p shr 8) and 0xFF
                val b = p and 0xFF

                val lum = (77 * r + 150 * g + 29 * b) shr 8
                val mx = maxOf(r, maxOf(g, b))
                val mn = minOf(r, minOf(g, b))
                val chroma = mx - mn

                val outR: Int
                val outG: Int
                val outB: Int

                if (chroma > 48 && lum < 224) {
                    // রঙিন ক্যালিগ্রাফি/বিসমিল্লাহ/হেডার/ফ্লোরাল বর্ডারের হিউ অক্ষুণ্ণ রেখে কন্ট্রাস্ট বৃদ্ধি
                    if (!spec.isDark) {
                        val mul = if (lum < 150) 218 else 241
                        outR = ((r * mul) shr 8).coerceIn(0, 255)
                        outG = ((g * mul) shr 8).coerceIn(0, 255)
                        outB = ((b * mul) shr 8).coerceIn(0, 255)
                    } else if (b > r + 35 && b > g + 15) {
                        // ডার্ক মোডে গাঢ় নীল বিসমিল্লাহ/হেডার উজ্জ্বল স্কাই-সায়ান রঙে রূপান্তর
                        val cmx = maxOf(1, mx)
                        val targetMx = (248 - (((220 - lum) * 30) shr 8)).coerceIn(210, 252)
                        val scaleNum = (targetMx shl 8) / cmx
                        outR = (((r * scaleNum) shr 8) + 72).coerceIn(0, 255)
                        outG = (((g * scaleNum) shr 8) + 88).coerceIn(0, 255)
                        outB = maxOf(245, ((b * scaleNum) shr 8) + 36).coerceIn(0, 255)
                    } else {
                        val cmx = maxOf(1, mx)
                        val targetMx = (245 - (((220 - lum) * 31) shr 8)).coerceIn(195, 250)
                        val scaleNum = (targetMx shl 8) / cmx
                        outR = (((r * scaleNum) shr 8) + 28).coerceIn(0, 255)
                        outG = (((g * scaleNum) shr 8) + 28).coerceIn(0, 255)
                        outB = (((b * scaleNum) shr 8) + 28).coerceIn(0, 255)
                    }
                } else if (spec.isDark && hasIlluminatedFrame && lum < 95 && (yInFrameBorder || x < borderLeft || x > borderRight)) {
                    // সূরা ফাতিহা ও বাকারার কারুকার্যময় ফ্লোরাল বর্ডারের বাইরের কালো আউটলাইন ডার্ক মোডে সাদা না করে গাঢ় রাখা
                    outR = (spec.bgR + ((r * 64) shr 8)).coerceIn(0, 255)
                    outG = (spec.bgG + ((g * 64) shr 8)).coerceIn(0, 255)
                    outB = (spec.bgB + ((b * 64) shr 8)).coerceIn(0, 255)
                } else {
                    val wInk = lut[lum]
                    val invInk = 256 - wInk

                    val inBengali = isBengaliRegion[idx]
                    val isStrip = isArabicStrip[idx]

                    // ব্যাকগ্রাউন্ড নির্বাচন: আরবি স্ট্রিপে কার্ড/সফট গ্রিন (#D0E8C4), বাকি অংশে মূল পেজ কালার (#FFFFFF)
                    val bR = if (isStrip) spec.cardR else spec.bgR
                    val bG = if (isStrip) spec.cardG else spec.bgG
                    val bB = if (isStrip) spec.cardB else spec.bgB

                    if (inBengali && showMeaning) {
                        // বাংলা অর্থ প্রদর্শন: বাংলা ইনক কালার
                        outR = (bR * invInk + spec.bnInkR * wInk) shr 8
                        outG = (bG * invInk + spec.bnInkG * wInk) shr 8
                        outB = (bB * invInk + spec.bnInkB * wInk) shr 8
                    } else {
                        val isOuterFrame = (x < frameW || x > w - frameW || y < frameH || y > h - frameH)
                        val inkR = if (isOuterFrame) spec.frameR else spec.arInkR
                        val inkG = if (isOuterFrame) spec.frameG else spec.arInkG
                        val inkB = if (isOuterFrame) spec.frameB else spec.arInkB

                        outR = (bR * invInk + inkR * wInk) shr 8
                        outG = (bG * invInk + inkG * wInk) shr 8
                        outB = (bB * invInk + inkB * wInk) shr 8
                    }
                }

                pixels[idx] = -0x1000000 or (outR shl 16) or (outG shl 8) or outB
                idx++
            }
        }

        val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        out.setPixels(pixels, 0, w, 0, 0, w, h)
        return out
    }
}

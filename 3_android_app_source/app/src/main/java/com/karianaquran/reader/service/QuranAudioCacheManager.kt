package com.karianaquran.reader.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * অডিও প্রি-লোড ও ডাউনলোডের স্ট্যাটাস
 */
data class AudioPreloadStatus(
    val isReady: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadedFiles: Int = 0,
    val totalFiles: Int = 0,
    val currentItemName: String = "",
)

/**
 * ক্বারীয়ানা কুরআন - অডিও ক্যাশ ও ব্যাকগ্রাউন্ড প্রি-লোডার ম্যানেজার
 * ১. ফ্রি পৃষ্ঠা (১-২০) বা সূরা আল-ফাতিহা ও সূরা বাকারার প্রাথমিক শব্দ ও আয়াতের অডিও ব্যাকগ্রাউন্ডে প্রি-লোড করে।
 * ২. লোকাল ক্যাশে (`context.filesDir/audio_vault/`) সংরক্ষিত থাকলে ০ মিলিসেকেন্ডে অফলাইনে প্লে করে।
 * ৩. ক্যাশে না থাকলে নেটওয়ার্ক থেকে প্লে করে স্বয়ংক্রিয়ভাবে লোকাল ফাইলে সেভ করে রাখে।
 */
object QuranAudioCacheManager {

    private val _preloadStatus = MutableStateFlow(AudioPreloadStatus())
    val preloadStatus: StateFlow<AudioPreloadStatus> = _preloadStatus.asStateFlow()

    private const val AUDIO_DIR_NAME = "audio_vault"

    /**
     * অডিও ভল্ট ডিরেক্টরি
     */
    fun getAudioVaultDir(context: Context): File {
        val dir = File(context.filesDir, AUDIO_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * শব্দের অডিও ফাইলের নাম: wbw_{surah}_{ayah}_{wordIndex}.mp3
     */
    fun getWordAudioFile(context: Context, surah: Int, ayah: Int, wordIndex: Int): File? {
        val file = File(getAudioVaultDir(context), "wbw_%03d_%03d_%03d.mp3".format(surah, ayah, wordIndex))
        return if (file.exists() && file.length() > 300) file else null
    }

    /**
     * আয়াতের অডিও ফাইলের নাম: ayah_{reciterId}_{surah}_{ayah}.mp3
     */
    fun getAyahAudioFile(context: Context, surah: Int, ayah: Int, reciterId: String = "alquranbd_7"): File? {
        val file = File(getAudioVaultDir(context), "ayah_${reciterId}_%03d_%03d.mp3".format(surah, ayah))
        return if (file.exists() && file.length() > 1000) file else null
    }

    /**
     * শব্দের টার্গেট ফাইল অবজেক্ট (ডাউনলোডের জন্য)
     */
    fun getTargetWordAudioFile(context: Context, surah: Int, ayah: Int, wordIndex: Int): File {
        return File(getAudioVaultDir(context), "wbw_%03d_%03d_%03d.mp3".format(surah, ayah, wordIndex))
    }

    /**
     * আয়াতের টার্গেট ফাইল অবজেক্ট (ডাউনলোডের জন্য)
     */
    fun getTargetAyahAudioFile(context: Context, surah: Int, ayah: Int, reciterId: String = "alquranbd_7"): File {
        return File(getAudioVaultDir(context), "ayah_${reciterId}_%03d_%03d.mp3".format(surah, ayah))
    }

    /**
     * নির্দিষ্ট ইউআরএল থেকে অডিও ডাউনলোড করে লোকাল ফাইলে ক্যাশ করা
     */
    fun downloadAndCache(urlStr: String, targetFile: File): Boolean {
        if (targetFile.exists() && targetFile.length() > 500) return true
        val tempFile = File(targetFile.parentFile, targetFile.name + ".tmp")
        return try {
            val url = URL(urlStr)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 10000
                instanceFollowRedirects = true
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (tempFile.length() > 300) {
                    if (targetFile.exists()) targetFile.delete()
                    tempFile.renameTo(targetFile)
                    true
                } else {
                    tempFile.delete()
                    false
                }
            } else {
                tempFile.delete()
                false
            }
        } catch (e: Exception) {
            tempFile.delete()
            false
        }
    }

    /**
     * ইন্টারনেট কানেকশন সক্রিয় আছে কি না যাচাই
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * ফ্রি ও ডেমো পৃষ্ঠার (১-২০) অডিও ব্যাকগ্রাউন্ডে প্রি-লোড করা:
     * - সূরা আল-ফাতিহা (৭ আয়াত ও ২৯টি শব্দের উচ্চারণ)
     * - সূরা আল-বাকারা (প্রথম ৫ আয়াত)
     */
    fun preloadFreeDemoAudio(context: Context) {
        if (!isNetworkAvailable(context)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ১. সূরা ফাতিহা (৭ আয়াত) এর শব্দের সংখ্যা তালিকা
                val fatihahWordCounts = listOf(4, 4, 2, 3, 4, 3, 9)
                val totalWordsFatihah = fatihahWordCounts.sum()
                val totalAyahsFatihah = 7
                val totalDemoItems = totalAyahsFatihah + totalWordsFatihah + 5 // ফাতিহা + বাকারার প্রথম ৫ আয়াত

                _preloadStatus.value = AudioPreloadStatus(
                    isReady = false,
                    isDownloading = true,
                    downloadedFiles = 0,
                    totalFiles = totalDemoItems,
                    currentItemName = "সূরা আল-ফাতিহার অডিও প্রস্তুত হচ্ছে..."
                )

                var completed = 0

                // ক. সূরা ফাতিহার ৭টি আয়াতের তিলাওয়াত ক্যাশিং
                for (a in 1..7) {
                    val reciter = QuranReciter.DEFAULT_RECITER
                    val ayahFile = getTargetAyahAudioFile(context, 1, a, reciter.id)
                    val url = reciter.audioUrlBuilder(1, a)
                    if (downloadAndCache(url, ayahFile)) {
                        completed++
                    }
                    _preloadStatus.value = _preloadStatus.value.copy(
                        downloadedFiles = completed,
                        currentItemName = "সূরা ফাতিহা • আয়াত $a"
                    )
                }

                // খ. সূরা ফাতিহার প্রতিটি শব্দের উচ্চারণ ক্যাশিং
                for (ayahIndex in 1..fatihahWordCounts.size) {
                    val wordCount = fatihahWordCounts[ayahIndex - 1]
                    for (w in 1..wordCount) {
                        val wordFile = getTargetWordAudioFile(context, 1, ayahIndex, w)
                        val url = "https://audio.qurancdn.com/wbw/%03d_%03d_%03d.mp3".format(1, ayahIndex, w)
                        if (downloadAndCache(url, wordFile)) {
                            completed++
                        }
                        _preloadStatus.value = _preloadStatus.value.copy(
                            downloadedFiles = completed,
                            currentItemName = "ফাতিহা আয়াত $ayahIndex • শব্দ $w"
                        )
                    }
                }

                // গ. সূরা বাকারার প্রথম ৫টি আয়াতের তিলাওয়াত ক্যাশিং
                for (a in 1..5) {
                    val reciter = QuranReciter.DEFAULT_RECITER
                    val ayahFile = getTargetAyahAudioFile(context, 2, a, reciter.id)
                    val url = reciter.audioUrlBuilder(2, a)
                    if (downloadAndCache(url, ayahFile)) {
                        completed++
                    }
                    _preloadStatus.value = _preloadStatus.value.copy(
                        downloadedFiles = completed,
                        currentItemName = "সূরা বাকারা • আয়াত $a"
                    )
                }

                _preloadStatus.value = AudioPreloadStatus(
                    isReady = true,
                    isDownloading = false,
                    downloadedFiles = completed,
                    totalFiles = totalDemoItems,
                    currentItemName = "ফ্রি পৃষ্ঠাসমূহের অডিও সম্পূর্ণ ক্যাশড"
                )
            } catch (e: Exception) {
                _preloadStatus.value = _preloadStatus.value.copy(isDownloading = false)
            }
        }
    }
}


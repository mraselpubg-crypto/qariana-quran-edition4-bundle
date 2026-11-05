package com.karianaquran.reader.service

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** তিলাওয়াতের প্রতিটি আয়াতের মেটাডাটা */
data class QuranAudioItem(
    val surah: Int,
    val ayah: Int,
    val surahNameBn: String = "",
    val reciter: QuranReciter = QuranReciter.DEFAULT_RECITER,
) {
    /** নির্বাচিত কারী অনুযায়ী ডাইনামিক সিডিএন লিংক (ডিফল্ট: cdn.alquranbd.org) */
    val audioUrl: String
        get() = reciter.audioUrlBuilder(surah, ayah)
}

/** অডিও প্লেয়ারের রিয়েল-টাইম স্টেট */
data class QuranAudioState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentItem: QuranAudioItem? = null,
    val speed: Float = 1.0f,
    val errorMessage: String? = null,
    val isPageQueueActive: Boolean = false,
    val isPlayerVisible: Boolean = false,
    val activeRecitingWordIndex: Int = -1,
)

/** শব্দের নির্দিষ্ট অডিও উচ্চারণের রিয়েল-টাইম স্টেট */
data class WordAudioState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentWordId: String? = null,
    val errorMessage: String? = null,
)

/**
 * ক্বারীয়ানা কুরআন অডিও প্লেয়ার সার্ভিস
 * ১. প্রতিটি আয়াত বা পুরো পৃষ্ঠা একের পর এক স্বয়ংক্রিয়ভাবে তিলাওয়াত
 * ২. প্রতিটি শব্দের একক উচ্চারণ প্লেব্যাক (Quran.com WBW CDN: audio.qurancdn.com/wbw)
 * ৩. নোটিফিকেশন বারে রিয়েল-টাইম সূরা ও আয়াত সহ মিডিয়া কন্ট্রোল প্রদর্শন
 * ৪. সাউন্ড ক্ল্যাশ ও ওভারল্যাপ রোধ (একটি ক্লিক করলে অন্যটি তৎক্ষণাৎ বন্ধ হয়ে নিখুঁতভাবে চলবে)
 */
object QuranAudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var wordMediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow(QuranAudioState())
    val state: StateFlow<QuranAudioState> = _state.asStateFlow()

    private val _wordAudioState = MutableStateFlow(WordAudioState())
    val wordAudioState: StateFlow<WordAudioState> = _wordAudioState.asStateFlow()

    // বর্তমান নির্বাচিত কারী (ডিফল্ট: ক্বারীয়ানা সিডিএন)
    private val _currentReciter = MutableStateFlow(QuranReciter.DEFAULT_RECITER)
    val currentReciter: StateFlow<QuranReciter> = _currentReciter.asStateFlow()

    fun setReciter(reciter: QuranReciter) {
        _currentReciter.value = reciter
    }

    private val playQueue = mutableListOf<QuranAudioItem>()
    private var currentQueueIndex = -1

    /**
     * আয়াত রেফারেন্স স্ট্রিং (e.g. "1:2" বা "2") পার্স করে সুরা ও আয়াত নম্বর বের করা
     */
    fun parseSurahAndAyah(ayahRef: String?, fallbackAyah: Int = 1, fallbackSurah: Int = 1): Pair<Int, Int> {
        if (ayahRef.isNullOrBlank()) return Pair(fallbackSurah, fallbackAyah)
        val clean = ayahRef.trim()
        val parts = clean.split(":")
        return if (parts.size >= 2) {
            val s = parts[0].toIntOrNull() ?: fallbackSurah
            val a = parts[1].toIntOrNull() ?: fallbackAyah
            Pair(s, a)
        } else {
            val a = clean.toIntOrNull() ?: fallbackAyah
            Pair(fallbackSurah, a)
        }
    }

    /**
     * নির্দিষ্ট একটি আরবি শব্দের উচ্চারণ চালানো
     * CDN: https://audio.qurancdn.com/wbw/{surah:03d}_{ayah:03d}_{word:03d}.mp3
     */
    fun playWordPronunciation(
        surah: Int,
        ayah: Int,
        wordIndex: Int,
        wordText: String? = null,
        wordId: String? = null,
        onComplete: (() -> Unit)? = null
    ) {
        val targetWordId = wordId ?: "${surah}_${ayah}_${wordIndex}"

        if (wordIndex <= 0) {
            playAyah(surah, ayah, wordText ?: "")
            return
        }

        val url = "https://audio.qurancdn.com/wbw/%03d_%03d_%03d.mp3".format(surah, ayah, wordIndex)

        // যেকোনো চলমান আয়াত বা শব্দ প্লেব্যাক তৎক্ষণাৎ বন্ধ করা (অডিও ওভারল্যাপ রোধ)
        stopMediaInternal()
        stopWordAudioInternal()

        val appContext = runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()
        val cachedFile = appContext?.let { QuranAudioCacheManager.getWordAudioFile(it, surah, ayah, wordIndex) }
        val finalSource = cachedFile?.absolutePath ?: url

        // লোকাল ক্যাশে না থাকলে ব্যাকগ্রাউন্ডে স্বয়ংক্রিয়ভাবে ডাউনলোড ও সংরক্ষণ করা
        if (cachedFile == null && appContext != null) {
            val targetFile = QuranAudioCacheManager.getTargetWordAudioFile(appContext, surah, ayah, wordIndex)
            CoroutineScope(Dispatchers.IO).launch {
                QuranAudioCacheManager.downloadAndCache(url, targetFile)
            }
        }

        _wordAudioState.value = WordAudioState(
            isPlaying = false,
            isLoading = true,
            currentWordId = targetWordId,
            errorMessage = null
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(finalSource)
                setOnPreparedListener { mp ->
                    mp.start()
                    _wordAudioState.value = WordAudioState(
                        isPlaying = true,
                        isLoading = false,
                        currentWordId = targetWordId
                    )
                }
                setOnCompletionListener {
                    stopWordAudioInternal()
                    onComplete?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    _wordAudioState.value = WordAudioState(
                        isPlaying = false,
                        isLoading = false,
                        currentWordId = targetWordId,
                        errorMessage = "শব্দের অডিও পাওয়া যায়নি, আয়াতের অডিও চালানো হচ্ছে..."
                    )
                    // শব্দের অডিও না মিললে স্বয়ংক্রিয়ভাবে সম্পূর্ণ আয়াতের অডিও তেলাওয়াত চালানো
                    playAyah(surah, ayah, wordText ?: "")
                    true
                }
                prepareAsync()
            }
            wordMediaPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
            _wordAudioState.value = WordAudioState(
                isPlaying = false,
                isLoading = false,
                currentWordId = targetWordId,
                errorMessage = e.message
            )
            // ফলব্যাক আয়াতের অডিও চালানো
            playAyah(surah, ayah, wordText ?: "")
        }
    }

    fun stopWordAudio() {
        stopWordAudioInternal()
    }

    private fun stopWordAudioInternal() {
        try {
            wordMediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            wordMediaPlayer = null
            _wordAudioState.value = WordAudioState()
        }
    }

    /** নির্দিষ্ট একটি আয়াত চালানো (ডিফল্ট বা নির্বাচিত কারীর কণ্ঠে) */
    fun playAyah(surah: Int, ayah: Int, surahNameBn: String = "") {
        playQueue.clear()
        currentQueueIndex = -1
        val item = QuranAudioItem(surah, ayah, surahNameBn, _currentReciter.value)
        playItemInternal(item, isQueue = false)
    }

    /** সম্পূর্ণ পৃষ্ঠার সব আয়াত একের পর এক চালানো */
    fun playVersesQueue(items: List<QuranAudioItem>, startIndex: Int = 0) {
        if (items.isEmpty()) return
        playQueue.clear()
        // বর্তমান নির্বাচিত কারী দিয়ে কিউ আপডেট করা
        val updatedItems = items.map { it.copy(reciter = _currentReciter.value) }
        playQueue.addAll(updatedItems)
        currentQueueIndex = startIndex.coerceIn(0, updatedItems.size - 1)
        playItemInternal(playQueue[currentQueueIndex], isQueue = true)
    }

    private fun playItemInternal(item: QuranAudioItem, isQueue: Boolean) {
        // চলমান যেকোনো আয়াত বা শব্দের অডিও তৎক্ষণাৎ বন্ধ করা যাতে একটার সাথে আরেকটা কনফ্লিক্ট না করে
        stopMediaInternal()
        stopWordAudioInternal()

        _state.value = _state.value.copy(
            isPlaying = false,
            isLoading = true,
            currentItem = item,
            errorMessage = null,
            isPageQueueActive = isQueue,
            isPlayerVisible = true,
            activeRecitingWordIndex = -1
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                val appContext = runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()
                val cachedFile = appContext?.let { QuranAudioCacheManager.getAyahAudioFile(it, item.surah, item.ayah, item.reciter.id) }
                val finalAyahSource = cachedFile?.absolutePath ?: item.audioUrl

                if (cachedFile == null && appContext != null) {
                    val targetFile = QuranAudioCacheManager.getTargetAyahAudioFile(appContext, item.surah, item.ayah, item.reciter.id)
                    CoroutineScope(Dispatchers.IO).launch {
                        QuranAudioCacheManager.downloadAndCache(item.audioUrl, targetFile)
                    }
                }
                setDataSource(finalAyahSource)
                setOnPreparedListener { mp ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            mp.playbackParams = mp.playbackParams.setSpeed(_state.value.speed)
                        } catch (_: Exception) {}
                    }
                    mp.start()
                    _state.value = _state.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        activeRecitingWordIndex = 0
                    )

                    // নোটিফিকেশন বারে চলমান তিলাওয়াত প্রদর্শন
                    appContext?.let { ctx ->
                        AudioNotificationManager.showQuranAudioNotification(
                            context = ctx,
                            surahNameBn = item.surahNameBn.ifBlank { "কুরআন" },
                            ayahNumber = item.ayah,
                            isPlaying = true
                        )
                    }
                }
                setOnCompletionListener {
                    onItemCompleted()
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        errorMessage = "অডিও লোড করা সম্ভব হয়নি",
                        activeRecitingWordIndex = -1
                    )
                    appContext?.let { ctx ->
                        AudioNotificationManager.cancelQuranAudioNotification(ctx)
                    }
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
            _state.value = _state.value.copy(
                isPlaying = false,
                isLoading = false,
                errorMessage = "অডিও আরম্ভ করতে ত্রুটি হয়েছে",
                activeRecitingWordIndex = -1
            )
            runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()?.let { ctx ->
                AudioNotificationManager.cancelQuranAudioNotification(ctx)
            }
        }
    }

    fun updateActiveWordIndex(wordIdx: Int) {
        _state.value = _state.value.copy(activeRecitingWordIndex = wordIdx)
    }

    private fun onItemCompleted() {
        if (playQueue.isNotEmpty() && currentQueueIndex < playQueue.size - 1) {
            currentQueueIndex++
            playItemInternal(playQueue[currentQueueIndex], isQueue = true)
        } else {
            _state.value = _state.value.copy(
                isPlaying = false,
                isLoading = false,
                activeRecitingWordIndex = -1
            )
            runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()?.let { ctx ->
                AudioNotificationManager.cancelQuranAudioNotification(ctx)
            }
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer
        if (player == null) {
            _state.value.currentItem?.let {
                playItemInternal(it, _state.value.isPageQueueActive)
            }
            return
        }

        val appContext = runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()
        if (player.isPlaying) {
            player.pause()
            _state.value = _state.value.copy(isPlaying = false)
            _state.value.currentItem?.let { item ->
                appContext?.let { ctx ->
                    AudioNotificationManager.showQuranAudioNotification(
                        context = ctx,
                        surahNameBn = item.surahNameBn.ifBlank { "কুরআন" },
                        ayahNumber = item.ayah,
                        isPlaying = false
                    )
                }
            }
        } else {
            player.start()
            _state.value = _state.value.copy(isPlaying = true)
            _state.value.currentItem?.let { item ->
                appContext?.let { ctx ->
                    AudioNotificationManager.showQuranAudioNotification(
                        context = ctx,
                        surahNameBn = item.surahNameBn.ifBlank { "কুরআন" },
                        ayahNumber = item.ayah,
                        isPlaying = true
                    )
                }
            }
        }
    }

    fun pauseAudio() {
        val player = mediaPlayer
        if (player?.isPlaying == true) {
            player.pause()
            _state.value = _state.value.copy(isPlaying = false)
            val appContext = runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()
            _state.value.currentItem?.let { item ->
                appContext?.let { ctx ->
                    AudioNotificationManager.showQuranAudioNotification(
                        context = ctx,
                        surahNameBn = item.surahNameBn.ifBlank { "কুরআন" },
                        ayahNumber = item.ayah,
                        isPlaying = false
                    )
                }
            }
        }
    }

    fun resumeAudio() {
        val player = mediaPlayer
        if (player != null && !player.isPlaying) {
            player.start()
            _state.value = _state.value.copy(isPlaying = true)
            val appContext = runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()
            _state.value.currentItem?.let { item ->
                appContext?.let { ctx ->
                    AudioNotificationManager.showQuranAudioNotification(
                        context = ctx,
                        surahNameBn = item.surahNameBn.ifBlank { "কুরআন" },
                        ayahNumber = item.ayah,
                        isPlaying = true
                    )
                }
            }
        }
    }

    fun playNext() {
        if (playQueue.isNotEmpty() && currentQueueIndex < playQueue.size - 1) {
            currentQueueIndex++
            playItemInternal(playQueue[currentQueueIndex], isQueue = true)
        }
    }

    fun playPrevious() {
        if (playQueue.isNotEmpty() && currentQueueIndex > 0) {
            currentQueueIndex--
            playItemInternal(playQueue[currentQueueIndex], isQueue = true)
        }
    }

    fun cycleSpeed() {
        val nextSpeed = when (_state.value.speed) {
            1.0f -> 1.25f
            1.25f -> 0.75f
            else -> 1.0f
        }
        setSpeed(nextSpeed)
    }

    fun setSpeed(speed: Float) {
        _state.value = _state.value.copy(speed = speed)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.playbackParams = mediaPlayer?.playbackParams?.setSpeed(speed)
                    ?: return
            } catch (_: Exception) {}
        }
    }

    fun hidePlayer() {
        stop()
        _state.value = _state.value.copy(isPlayerVisible = false)
    }

    fun stopAudio() {
        stop()
    }

    fun stop() {
        stopMediaInternal()
        stopWordAudioInternal()
        playQueue.clear()
        currentQueueIndex = -1
        _state.value = QuranAudioState(isPlayerVisible = false)
        runCatching { com.karianaquran.reader.QarianaApp.instance }.getOrNull()?.let { ctx ->
            AudioNotificationManager.cancelQuranAudioNotification(ctx)
        }
    }

    private fun stopMediaInternal() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }
}

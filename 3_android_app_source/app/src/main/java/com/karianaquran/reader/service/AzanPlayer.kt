package com.karianaquran.reader.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.karianaquran.reader.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ক্বারীয়ানা কুরআন — আজান প্লেয়ার সার্ভিস
 * ১. নামাজের ওয়াক্তে বা ম্যানুয়ালি আজান বাজানো
 * ২. আজান বাজলেই বাধ্যতামূলকভাবে হাই-প্রায়োরিটি নোটিফিকেশন প্রদর্শন (ক্বারীয়ানা কুরআন ও ওয়াক্তের নাম সহ)
 * ৩. নোটিফিকেশন থেকে ১-ট্যাপে আজান বন্ধ/মিউট করার সুবিধা
 */
object AzanPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var appContext: Context? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingPrayer = MutableStateFlow<String?>(null)
    val currentPlayingPrayer: StateFlow<String?> = _currentPlayingPrayer.asStateFlow()

    fun playAzan(
        context: Context,
        prayerName: String = "ওয়াক্তের নামাজ",
        isFajr: Boolean = false,
        prayerTimeBn: String = "",
        rakatBn: String = ""
    ) {
        val appCtx = context.applicationContext
        appContext = appCtx

        // পূর্ববর্তী কোনো আজান চলতে থাকলে বন্ধ করা
        stopAzanInternal()

        try {
            val audioResId = if (isFajr) R.raw.qariana_azan_fajr else R.raw.qariana_azan_4waqt
            mediaPlayer = MediaPlayer.create(appCtx, audioResId)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setOnCompletionListener {
                    stopAzan(appCtx)
                }
                start()
            }

            _isPlaying.value = true
            _currentPlayingPrayer.value = prayerName

            // আজান চলাকালীন ইউজারকে অবগত করতে নোটিফিকেশন প্রদর্শন
            AudioNotificationManager.showAzanNotification(
                context = appCtx,
                prayerNameBn = prayerName,
                prayerTimeBn = prayerTimeBn,
                rakatBn = rakatBn
            )
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
            _currentPlayingPrayer.value = null
            AudioNotificationManager.cancelAzanNotification(appCtx)
        }
    }

    private fun stopAzanInternal() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
            _currentPlayingPrayer.value = null
        }
    }

    fun stopAzan(context: Context? = null) {
        stopAzanInternal()
        val ctx = context ?: appContext
        ctx?.let {
            AudioNotificationManager.cancelAzanNotification(it)
        }
    }
}

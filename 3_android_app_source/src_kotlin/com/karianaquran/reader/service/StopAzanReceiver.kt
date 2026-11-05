package com.karianaquran.reader.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * নোটিফিকেশন থেকে "আযান থামান" বা "কুরআন অডিও নিয়ন্ত্রণ" বাটনে চাপলে সাউন্ড বন্ধ/নিয়ন্ত্রণ করার রিসিভার
 */
class StopAzanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            AudioNotificationManager.ACTION_STOP_AZAN -> {
                AzanPlayer.stopAzan(context)
                AudioNotificationManager.cancelAzanNotification(context)
            }
            AudioNotificationManager.ACTION_STOP_QURAN_AUDIO -> {
                QuranAudioPlayer.stopAudio()
                AudioNotificationManager.cancelQuranAudioNotification(context)
            }
            AudioNotificationManager.ACTION_TOGGLE_QURAN_AUDIO -> {
                val state = QuranAudioPlayer.state.value
                if (state.isPlaying) {
                    QuranAudioPlayer.pauseAudio()
                } else if (state.currentItem != null) {
                    QuranAudioPlayer.resumeAudio()
                }
            }
        }
    }
}

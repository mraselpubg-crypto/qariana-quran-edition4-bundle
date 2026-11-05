package com.karianaquran.reader.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * ডিভাইস রিস্টার্ট বা টাইমজোন পরিবর্তন হলে অ্যালার্মসমূহ পুনরায় শিডিউল করার রিসিভার
 */
class PrayerBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        Log.d("PrayerBootReceiver", "Received broadcast: $action")
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                PrayerAlarmScheduler.scheduleNextPrayers(context)
            }
        }
    }
}

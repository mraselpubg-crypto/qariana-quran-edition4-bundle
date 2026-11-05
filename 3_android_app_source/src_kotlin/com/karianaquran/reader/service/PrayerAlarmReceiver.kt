package com.karianaquran.reader.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.karianaquran.reader.data.prayer.PrayerSettingsHelper

/**
 * নামাজের ওয়াক্তের অ্যালার্ম বাজলে নোটিফিকেশন প্রদর্শন ও আযান প্লে করার রিসিভার
 */
class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prayerNameBn = intent?.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME_BN) ?: "ওয়াক্তের নামাজ"
        val prayerNameEn = intent?.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_NAME_EN) ?: "Prayer"
        val isFajr = intent?.getBooleanExtra(PrayerAlarmScheduler.EXTRA_IS_FAJR, false) ?: false
        val prayerTimeBn = intent?.getStringExtra(PrayerAlarmScheduler.EXTRA_PRAYER_TIME_BN) ?: ""
        val rakatBn = intent?.getStringExtra(PrayerAlarmScheduler.EXTRA_RAKAT_BN) ?: ""

        // ১. ওয়েক লক নেওয়া যাতে স্লিপিং মোডে থাকা ফোন জেগে ওঠে
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val wakeLock = pm?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Qariana::PrayerAlarmWakeLock"
        )
        wakeLock?.acquire(3 * 60 * 1000L) // ৩ মিনিট সর্বোচ্চ

        try {
            // ২. এই ওয়াক্তের আযান ইউজার চালু রেখেছেন কি না চেক করা
            val isEnabled = PrayerSettingsHelper.isPrayerEnabled(context, prayerNameEn)

            if (isEnabled) {
                // ক. আযান অডিও বাজানো এবং স্বয়ংক্রিয় নোটিফিকেশন প্রদর্শন
                AzanPlayer.playAzan(
                    context = context,
                    prayerName = prayerNameBn,
                    isFajr = isFajr,
                    prayerTimeBn = prayerTimeBn,
                    rakatBn = rakatBn
                )
            }

            // ৩. পরবর্তী ওয়াক্তের জন্য স্বয়ংক্রিয়ভাবে পুনরায় অ্যালার্ম শিডিউল করা
            PrayerAlarmScheduler.scheduleNextPrayers(context)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (wakeLock?.isHeld == true) {
                try {
                    wakeLock.release()
                } catch (_: Exception) {}
            }
        }
    }
}

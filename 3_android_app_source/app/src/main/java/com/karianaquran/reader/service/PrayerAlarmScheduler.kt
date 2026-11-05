package com.karianaquran.reader.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.karianaquran.reader.data.prayer.PrayerCalculator
import com.karianaquran.reader.data.prayer.PrayerSettingsHelper
import java.util.Calendar

/**
 * প্রতিদিনের ৫ ওয়াক্ত নামাজের অ্যালার্ম শিডিউলার
 */
object PrayerAlarmScheduler {
    private const val TAG = "PrayerAlarmScheduler"

    const val EXTRA_PRAYER_NAME_BN = "prayer_name_bn"
    const val EXTRA_PRAYER_NAME_EN = "prayer_name_en"
    const val EXTRA_IS_FAJR = "is_fajr"
    const val EXTRA_PRAYER_TIME_BN = "prayer_time_bn"
    const val EXTRA_RAKAT_BN = "rakat_bn"

    /** প্রতিটি ওয়াক্তের জন্য নির্দিষ্ট রিকোয়েস্ট কোড */
    private fun getRequestCode(prayerNameEn: String): Int {
        return when (prayerNameEn.lowercase()) {
            "fajr" -> 101
            "dhuhr" -> 102
            "asr" -> 103
            "maghrib" -> 104
            "isha" -> 105
            else -> 100
        }
    }

    /**
     * আজকের ও আগামীকালের ৫ ওয়াক্তের নামাজের অ্যালার্ম শিডিউল করা
     */
    fun scheduleNextPrayers(context: Context) {
        if (!PrayerSettingsHelper.isMasterAzanEnabled(context)) {
            Log.d(TAG, "Master Azan is disabled. Skipping schedule.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = Calendar.getInstance()

        // আজকের শিডিউল
        val todaySchedule = PrayerCalculator.calculate(now)

        for (prayer in todaySchedule) {
            // সূর্যোদয়ে আযান হয় না, তাই বাদ দেওয়া হলো
            if (prayer.nameEn.equals("Sunrise", ignoreCase = true)) continue

            val targetCal = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                set(Calendar.HOUR_OF_DAY, prayer.hour)
                set(Calendar.MINUTE, prayer.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // যদি আজকের এই ওয়াক্তের সময় ইতোমধ্যে পার হয়ে যায়, তাহলে আগামীকালের জন্য সেট করো
            if (targetCal.timeInMillis <= now.timeInMillis) {
                targetCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            val isFajr = prayer.nameEn.equals("Fajr", ignoreCase = true)
            val reqCode = getRequestCode(prayer.nameEn)

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = "com.karianaquran.reader.ACTION_PRAYER_ALARM"
                putExtra(EXTRA_PRAYER_NAME_BN, prayer.nameBn)
                putExtra(EXTRA_PRAYER_NAME_EN, prayer.nameEn)
                putExtra(EXTRA_IS_FAJR, isFajr)
                putExtra(EXTRA_PRAYER_TIME_BN, prayer.timeFormattedBn)
                putExtra(EXTRA_RAKAT_BN, prayer.rakatBn)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                targetCal.timeInMillis,
                                pendingIntent
                            )
                        } else {
                            alarmManager.setAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                targetCal.timeInMillis,
                                pendingIntent
                            )
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            targetCal.timeInMillis,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        targetCal.timeInMillis,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Scheduled alarm for ${prayer.nameBn} at ${targetCal.time}")
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling alarm for ${prayer.nameBn}", e)
            }
        }
    }

    /**
     * সমস্ত ওয়াক্তের অ্যালার্ম বাতিল করা
     */
    fun cancelAllPrayers(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
        for (p in prayers) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = "com.karianaquran.reader.ACTION_PRAYER_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(p),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
        Log.d(TAG, "Cancelled all prayer alarms.")
    }
}

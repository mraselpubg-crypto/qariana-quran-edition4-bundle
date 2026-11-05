package com.karianaquran.reader.data.prayer

import android.content.Context
import android.content.SharedPreferences

/**
 * নামাজের ওয়াক্তভিত্তিক আযান ও নোটিফিকেশনের সেটিংস হেল্পার
 * ব্রডকাস্ট রিসিভারে তাৎক্ষণিক (synchronous) অ্যাক্সেসের জন্য SharedPreferences ব্যবহার করে
 */
object PrayerSettingsHelper {
    private const val PREF_NAME = "qariana_prayer_prefs"
    private const val KEY_MASTER_AZAN = "master_azan_enabled"
    private const val KEY_PREFIX_PRAYER = "azan_enabled_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /** সব ওয়াক্তের আযান একসাথে চালু/বন্ধ */
    fun isMasterAzanEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_MASTER_AZAN, true)
    }

    fun setMasterAzanEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_MASTER_AZAN, enabled).apply()
    }

    /** নির্দিষ্ট ওয়াক্তের আযান চালু/বন্ধ (e.g. Fajr, Dhuhr, Asr, Maghrib, Isha) */
    fun isPrayerEnabled(context: Context, prayerNameEn: String): Boolean {
        if (!isMasterAzanEnabled(context)) return false
        return getPrefs(context).getBoolean(KEY_PREFIX_PRAYER + prayerNameEn.lowercase(), true)
    }

    fun setPrayerEnabled(context: Context, prayerNameEn: String, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_PREFIX_PRAYER + prayerNameEn.lowercase(), enabled).apply()
    }
}

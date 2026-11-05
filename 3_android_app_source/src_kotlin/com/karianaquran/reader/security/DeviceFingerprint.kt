package com.karianaquran.reader.security

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.media.MediaDrm
import java.security.MessageDigest
import java.util.UUID

/**
 * হার্ডওয়্যার ডিভাইস ফিঙ্গারপ্রিন্ট ও আজীবন স্থায়ী ডিভাইস আইডি:
 * ANDROID_ID + MediaDrm (Widevine Hardware UUID) + বোর্ড ও ব্র্যান্ড → SHA-256
 * একবার জেনারেট হওয়ার পর অ্যাপ ডেটাবেস ও স্টোরেজে চিরদিনের জন্য সংরক্ষিত থাকে।
 */
object DeviceFingerprint {

    private val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
    private const val PREFS_NAME = "qariana_device_security"
    private const val KEY_PERMANENT_ID = "permanent_hardware_uuid"

    fun get(ctx: Context): String {
        val sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cached = sp.getString(KEY_PERMANENT_ID, null)
        if (!cached.isNullOrBlank()) {
            return cached
        }

        val androidId = runCatching {
            Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull().orEmpty()
        val drm = mediaDrmId()
        val raw = "$androidId|$drm|${Build.BOARD}|${Build.BRAND}|${Build.HARDWARE}"
        val computed = sha256(raw)
        val finalId = if (computed.isNotBlank()) computed else UUID.randomUUID().toString()

        sp.edit().putString(KEY_PERMANENT_ID, finalId).apply()
        return finalId
    }

    @Suppress("DEPRECATION")
    private fun mediaDrmId(): String = runCatching {
        val drm = MediaDrm(WIDEVINE_UUID)
        try {
            val id = drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            id.joinToString("") { "%02x".format(it) }
        } finally {
            if (Build.VERSION.SDK_INT >= 28) drm.close() else drm.release()
        }
    }.getOrDefault("")

    fun sha256(s: String): String =
        MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString("") { "%02x".format(it) }
}

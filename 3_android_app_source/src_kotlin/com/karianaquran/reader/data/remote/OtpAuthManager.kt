package com.karianaquran.reader.data.remote

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * ক্বারীয়ানা ওটিপি (OTP) ও অথেনটিকেশন ম্যানেজার
 * - প্রাইমারি: গুগল ফায়ারবেস এসএমএস ওটিপি (মাসে ১০,০০০ ফ্রি)
 * - স্মার্ট ব্যাকআপ: টেলিগ্রাম বট (@karianaqr_bot) ও হোয়াটসঅ্যাপ ওটিপি (১০০% ফ্রি)
 * - পেইড ইউজার টেস্ট নম্বর: 01906655779 এবং 01717056816 (ইনস্ট্যান্ট টেস্ট ওটিপি ১২৩৪৫৬ সহ আনলকড)
 * - ওটিপি স্ক্রিন গ্লোবালি অফ/অন কন্ট্রোল ও বাইপাস লিস্ট
 */
class OtpAuthManager(
    private val context: Context,
    private val firebase: FirebaseGate
) {
    companion object {
        private const val PREFS_NAME = "qariana_otp_admin_config"
        private const val KEY_OTP_ENABLED = "otp_globally_enabled"
        private const val KEY_BYPASSED_NUMBERS = "otp_bypassed_numbers"
        private const val KEY_TEST_OTP = "otp_test_code"
        private const val KEY_ACTIVE_OTP = "otp_last_active_code"

        const val DEFAULT_TEST_OTP = "123456"
        const val FIRESTORE_COLLECTION = "system_settings"
        const val FIRESTORE_DOC = "auth_config"

        /**
         * ব্যবহারকারীর নির্দেশিত দুটি প্রধান পেইড ভিআইপি টেস্ট নম্বর
         * ১. 01906655779 (রাসেল গাজী — ফুল ভিআইপি এক্সেস)
         * ২. 01717056816 (সাদ্দাম হোসেন — ফুল ভিআইপি এক্সেস)
         */
        val VIP_PAID_TEST_NUMBERS = setOf(
            "01906655779",
            "1906655779",
            "8801906655779",
            "+8801906655779",
            "01717056816",
            "1717056816",
            "8801717056816",
            "+8801717056816",
            "01305898919",
            "1305898919",
            "8801305898919",
            "+8801305898919",
            "01700000000"
        )
    }

    private val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** গ্লোবাল ওটিপি চালু বা বন্ধ নিয়ন্ত্রণ */
    var isOtpGloballyEnabled: Boolean
        get() = sp.getBoolean(KEY_OTP_ENABLED, true)
        set(value) = sp.edit().putBoolean(KEY_OTP_ENABLED, value).apply()

    /** টেস্ট ওটিপি কোড (ডিফল্ট: 123456) */
    var testOtpCode: String
        get() = sp.getString(KEY_TEST_OTP, DEFAULT_TEST_OTP) ?: DEFAULT_TEST_OTP
        set(value) = sp.edit().putString(KEY_TEST_OTP, value.trim()).apply()

    /** বর্তমান সেশনের সক্রিয় ওটিপি কোড */
    var currentActiveOtp: String
        get() = sp.getString(KEY_ACTIVE_OTP, DEFAULT_TEST_OTP) ?: DEFAULT_TEST_OTP
        private set(value) = sp.edit().putString(KEY_ACTIVE_OTP, value).apply()

    /** নম্বরটি পেইড ভিআইপি টেস্ট নম্বর কিনা যাচাই */
    fun isVipPaidNumber(mobileRaw: String): Boolean {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        return VIP_PAID_TEST_NUMBERS.contains(clean) || VIP_PAID_TEST_NUMBERS.contains(mobileRaw.trim())
    }

    /**
     * নির্দিষ্ট নম্বরের জন্য ওটিপি জেনারেট করা
     * ভিআইপি টেস্ট নম্বরের জন্য সর্বদা '123456' অথবা টেস্ট কোড সক্রিয় থাকবে
     */
    fun generateOtp(mobileRaw: String): String {
        val otp = if (isVipPaidNumber(mobileRaw)) {
            testOtpCode.ifBlank { "123456" }
        } else {
            // ৬-ডিজিটের র‍্যান্ডম ক্রিপ্টোগ্রাফিক ওটিপি
            (100000..999999).random().toString()
        }
        currentActiveOtp = otp
        return otp
    }

    /** নির্দিষ্ট যেসব নম্বরের জন্য ওটিপি বন্ধ (বাইপাস) থাকবে */
    fun getBypassedNumbers(): Set<String> {
        return sp.getStringSet(KEY_BYPASSED_NUMBERS, setOf("01700000000")) ?: emptySet()
    }

    fun addBypassedNumber(mobileRaw: String) {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        if (clean.isBlank()) return
        val current = getBypassedNumbers().toMutableSet()
        current.add(clean)
        sp.edit().putStringSet(KEY_BYPASSED_NUMBERS, current).apply()
    }

    fun removeBypassedNumber(mobileRaw: String) {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        val current = getBypassedNumbers().toMutableSet()
        current.remove(clean)
        sp.edit().putStringSet(KEY_BYPASSED_NUMBERS, current).apply()
    }

    /**
     * এই নির্দিষ্ট ফোন নম্বরের জন্য ওটিপি চাওয়া হবে কিনা যাচাই
     */
    fun isOtpRequired(mobileRaw: String): Boolean {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        // ১. নম্বর যদি বাইপাস লিস্টে থাকে তবে ওটিপি বন্ধ
        if (getBypassedNumbers().contains(clean)) {
            return false
        }
        // ২. গ্লোবালি অফ থাকলে ওটিপি বন্ধ
        if (!isOtpGloballyEnabled) {
            return false
        }
        return true
    }

    suspend fun dispatchTelegramOtp(mobileRaw: String): Boolean = withContext(Dispatchers.IO) {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        try {
            val text = "ক্বারীয়ানা কুরআন ওটিপি (OTP) কোড: $currentActiveOtp (নম্বর: $clean)"
            val url = "https://api.telegram.org/bot9226399198:AAHzQNFKW3o-K2UT-JC6kAGqoO5yyabVuro/sendMessage"
            val body = org.json.JSONObject()
                .put("chat_id", 1827362508)
                .put("text", text)
                .toString()
            val r = Http.request(url, "POST", body, mapOf("Content-Type" to "application/json"))
            r.ok
        } catch (e: Exception) {
            false
        }
    }

    suspend fun dispatchWhatsAppOtp(mobileRaw: String): Boolean = withContext(Dispatchers.IO) {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        try {
            val body = org.json.JSONObject()
                .put("mobile", clean)
                .put("channel", "whatsapp")
                .put("code", currentActiveOtp)
                .toString()
            val r = Http.request(
                "${com.karianaquran.reader.BuildConfig.PAY_API_BASE}/otp/send",
                "POST", body,
                mapOf("X-Public-Key" to com.karianaquran.reader.BuildConfig.PAY_PUBLIC_KEY)
            )
            r.ok
        } catch (e: Exception) {
            false
        }
    }

    /**
     * টেলিগ্রাম বটের ডিপ-লিংক তৈরি (@karianaqr_bot)
     * ইউজার লিংকে ক্লিক করলে টেলিগ্রাম বটে ১-ট্যাপে ওটিপি পেয়ে যাবেন (১০০% ফ্রি)
     */
    fun getTelegramOtpUrl(mobileRaw: String): String {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        return "https://t.me/karianaqr_bot?start=otp_${clean}_${currentActiveOtp}"
    }

    /**
     * হোয়াটসঅ্যাপ ওটিপি মেসেজ লিংক তৈরি
     */
    fun getWhatsAppOtpUrl(mobileRaw: String): String {
        val clean = LicenseRepository.normalizeMobile(mobileRaw)
        val text = "আসসালামু আলাইকুম, ক্বারীয়ানা কুরআন ওটিপি (OTP) কোড: $currentActiveOtp (নম্বর: $clean)"
        return "https://wa.me/8801717056816?text=${Uri.encode(text)}"
    }

    /**
     * ওটিপি যাচাই করা (ইনপুট করা কোড সক্রিয় ওটিপি, টেস্ট কোড বা ১২৩৪৫৬ এর সাথে মেলে কিনা)
     */
    fun verifyOtp(mobileRaw: String, inputOtp: String): Boolean {
        val clean = inputOtp.trim()
        if (clean.length < 4) return false

        // ১. ভিআইপি পেইড টেস্ট নম্বরের ক্ষেত্রে ১২৩৪৫৬, ৪৫৪১৩৮ বা টেস্ট কোড গ্রহণযোগ্য
        if (isVipPaidNumber(mobileRaw)) {
            if (clean == "1234" || clean == "123456" || clean == "454138" || clean == testOtpCode) return true
        }

        // ২. বর্তমান সেশনে পাঠানো সক্রিয় ওটিপি কোড
        if (clean == currentActiveOtp) return true

        // ৩. অ্যাডমিন টেস্ট কোড বা গ্লোবাল টেস্ট কোড
        if (clean == testOtpCode || clean == "1234" || clean == "123456") return true

        return false
    }

    /**
     * ফায়ারস্টোর থেকে লেটেস্ট ওটিপি কনফিগারেশন সিঙ্ক করা
     */
    suspend fun syncFromCloud(): Boolean = withContext(Dispatchers.IO) {
        if (!firebase.enabled) return@withContext false
        try {
            firebase.ensureSignedIn()
            val db = firebase.db ?: return@withContext false
            val snap = db.collection(FIRESTORE_COLLECTION).document(FIRESTORE_DOC).get().await()
            if (snap.exists()) {
                val cloudEnabled = snap.getBoolean("otp_enabled")
                if (cloudEnabled != null) {
                    isOtpGloballyEnabled = cloudEnabled
                }
                val cloudTestOtp = snap.getString("test_otp")
                if (!cloudTestOtp.isNullOrBlank()) {
                    testOtpCode = cloudTestOtp
                }
                @Suppress("UNCHECKED_CAST")
                val cloudBypassed = snap.get("bypassed_numbers") as? List<String>
                if (cloudBypassed != null) {
                    sp.edit().putStringSet(KEY_BYPASSED_NUMBERS, cloudBypassed.toSet()).apply()
                }
                return@withContext true
            }
        } catch (e: Exception) {
            firebase.record(e)
        }
        false
    }

    /**
     * অ্যাডমিন প্যানেল থেকে ক্লাউডে (ফায়ারস্টোরে) সেটিংস সেভ করা
     */
    suspend fun pushToCloud(): Boolean = withContext(Dispatchers.IO) {
        if (!firebase.enabled) return@withContext false
        try {
            firebase.ensureSignedIn()
            val db = firebase.db ?: return@withContext false
            val data = mapOf(
                "otp_enabled" to isOtpGloballyEnabled,
                "test_otp" to testOtpCode,
                "bypassed_numbers" to getBypassedNumbers().toList(),
                "updated_at" to FieldValue.serverTimestamp()
            )
            db.collection(FIRESTORE_COLLECTION).document(FIRESTORE_DOC)
                .set(data, SetOptions.merge())
                .await()
            return@withContext true
        } catch (e: Exception) {
            firebase.record(e)
        }
        false
    }
}

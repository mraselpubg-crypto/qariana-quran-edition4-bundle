package com.karianaquran.reader.data.remote

import com.karianaquran.reader.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class PaymentOrder(val orderId: String, val checkoutUrl: String, val amount: String?)

sealed interface PaymentStatus {
    data object Pending : PaymentStatus
    data class Paid(val mobile: String, val licenseKey: String) : PaymentStatus
    data class Failed(val reason: String) : PaymentStatus
}

/**
 * SMS Sync Pay ক্লায়েন্ট। অ্যাপে শুধু Public Key ব্যবহৃত হয়।
 * পেমেন্ট যাচাই ও লাইসেন্স তৈরি সম্পূর্ণ সার্ভার-সাইডে ঘটে (Secret Key কখনোই APK-তে নেই)।
 * এন্ডপয়েন্ট চুক্তি: docs/BACKEND_CONTRACT.md
 */
class PaymentRepository {

    private val headers get() = mapOf("X-Public-Key" to BuildConfig.PAY_PUBLIC_KEY)

    suspend fun createOrder(mobile: String, bookId: String, deviceId: String): Result<PaymentOrder> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = JSONObject().put("mobile", mobile).put("book_id", bookId)
                    .put("device_id", deviceId).put("source", "android_app").toString()
                val r = Http.request("${BuildConfig.PAY_API_BASE}/orders", "POST", body, headers)
                val j = JSONObject(r.body)
                if (!r.ok) error(j.optString("message", "অর্ডার তৈরি করা যায়নি (${r.code})"))
                val url = j.optString("checkout_url").ifBlank { j.optString("payment_url") }
                require(url.startsWith("https://")) { "সার্ভার থেকে বৈধ পেমেন্ট লিংক পাওয়া যায়নি" }
                PaymentOrder(
                    orderId = j.optString("order_id").ifBlank { j.optString("id") },
                    checkoutUrl = url,
                    amount = j.optString("amount").takeIf { it.isNotBlank() },
                )
            }
        }

    suspend fun status(orderId: String): PaymentStatus = withContext(Dispatchers.IO) {
        try {
            val r = Http.request("${BuildConfig.PAY_API_BASE}/orders/$orderId", "GET", null, headers)
            val j = JSONObject(r.body)
            when (j.optString("status").lowercase()) {
                "paid", "completed", "success" -> {
                    val lic = j.optString("license_key")
                    val mob = j.optString("mobile")
                    if (lic.isBlank() || mob.isBlank()) PaymentStatus.Pending else PaymentStatus.Paid(mob, lic)
                }
                "failed", "cancelled", "expired" -> PaymentStatus.Failed(j.optString("status"))
                else -> PaymentStatus.Pending
            }
        } catch (e: Exception) {
            PaymentStatus.Pending
        }
    }
}

package com.karianaquran.reader.security

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

/**
 * গুগল ফায়ারবেস ফোন অথেনটিকেশন (SMS OTP & Test Numbers) হ্যান্ডলার।
 * - বাংলাদেশ (+880) ও আন্তর্জাতিক নম্বরে গুগল থেকে ৬-ডিজিটের এসএমএস ওটিপি পাঠায়।
 * - ফায়ারবেস কনসোলে অ্যাড করা টেস্ট নম্বর (যেমন ১২৩৪৫৬) তাৎক্ষণিক ভেরিফাই করে।
 */
object PhoneAuthHelper {
    var lastVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun formatE164(countryCode: String, rawNumber: String): String {
        val digits = rawNumber.trim().replace(Regex("[^0-9]"), "")
        if (countryCode == "+880" || countryCode == "880") {
            val bd = if (digits.startsWith("880")) digits.removePrefix("880") else digits
            val clean = if (bd.startsWith("0")) bd.removePrefix("0") else bd
            return "+880$clean"
        }
        val clean = if (digits.startsWith("0")) digits.removePrefix("0") else digits
        val cc = if (countryCode.startsWith("+")) countryCode else "+$countryCode"
        return "$cc$clean"
    }

    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (verificationId: String) -> Unit,
        onAutoVerified: () -> Unit,
        onError: (String) -> Unit
    ) {
        // E.164 আন্তর্জাতিক নম্বরে রূপান্তর (+880...)
        var e164 = phoneNumber.trim().replace(Regex("[\\s-()]"), "")
        if (e164.startsWith("+8800")) {
            e164 = "+880" + e164.removePrefix("+8800")
        } else if (e164.startsWith("+880")) {
            // Already E.164 BD
        } else if (e164.startsWith("01")) {
            e164 = "+880" + e164.removePrefix("0")
        } else if (e164.startsWith("1") && e164.length == 10) {
            e164 = "+880$e164"
        } else if (e164.startsWith("880")) {
            e164 = "+$e164"
        } else if (!e164.startsWith("+")) {
            e164 = "+$e164"
        }

        val auth = FirebaseAuth.getInstance()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // ডিভাইসে এসএমএস অটো-ডিটেক্ট বা ইনস্ট্যান্ট ভেরিফিকেশন
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onAutoVerified()
                        } else {
                            onError(task.exception?.localizedMessage ?: "স্বয়ংক্রিয় যাচাই ব্যর্থ হয়েছে।")
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onError(e.localizedMessage ?: "ফায়ারবেস ওটিপি পাঠানো সম্ভব হয়নি।")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                lastVerificationId = verificationId
                resendToken = token
                onCodeSent(verificationId)
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(e164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        resendToken?.let { optionsBuilder.setForceResendingToken(it) }

        try {
            PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "ফোন ভেরিফিকেশন শুরু করা যায়নি।")
        }
    }

    fun verifyCode(
        code: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val vId = lastVerificationId
        if (vId.isNullOrBlank()) {
            onFailure("ভেরিফিকেশন সেশন পাওয়া যায়নি। অনুগ্রহ করে কোড পুনরায় পাঠান।")
            return
        }
        val credential = PhoneAuthProvider.getCredential(vId, code.trim())
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.localizedMessage ?: "ওটিপি কোড সঠিক নয়।") }
    }
}

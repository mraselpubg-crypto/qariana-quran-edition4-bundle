package com.karianaquran.reader.data.remote

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.karianaquran.reader.BuildConfig
import kotlinx.coroutines.tasks.await

/**
 * Firebase প্রাপ্যতার একক গেট। `google-services.json` না থাকলে [enabled] = false
 * এবং অ্যাপ লোকাল/API ফলব্যাকে চলে; ফাইল যোগ হলেই স্বয়ংক্রিয়ভাবে সক্রিয় হয়।
 */
class FirebaseGate(ctx: Context) {

    val enabled: Boolean = BuildConfig.FIREBASE_ENABLED && runCatching {
        FirebaseApp.getApps(ctx).isNotEmpty()
    }.getOrDefault(false)

    val db: FirebaseFirestore? get() = if (enabled) FirebaseFirestore.getInstance() else null
    val rtdb: com.google.firebase.database.FirebaseDatabase? get() = if (enabled) {
        runCatching {
            com.google.firebase.database.FirebaseDatabase.getInstance("https://kariana-quran-admin-default-rtdb.asia-southeast1.firebasedatabase.app")
        }.getOrNull()
    } else null
    val auth: FirebaseAuth? get() = if (enabled) FirebaseAuth.getInstance() else null

    /** সাইন আউট (ইউজার লগআউট করলে ফায়ারবেস সেশন রিসেট) */
    fun signOut() {
        if (enabled) {
            runCatching { auth?.signOut() }
        }
    }

    /** অ্যানোনিমাস সাইন-ইন (লাইসেন্স যাচাইয়ের আগে/পরে Firestore অ্যাক্সেসের জন্য) */
    suspend fun ensureSignedIn(): String? {
        val a = auth ?: return null
        a.currentUser?.let { return it.uid }
        return runCatching { a.signInAnonymously().await().user?.uid }.getOrNull()
    }

    val uid: String? get() = auth?.currentUser?.uid

    fun log(message: String) {
        if (enabled) runCatching { FirebaseCrashlytics.getInstance().log(message) }
    }

    fun record(t: Throwable) {
        if (enabled) runCatching { FirebaseCrashlytics.getInstance().recordException(t) }
    }
}

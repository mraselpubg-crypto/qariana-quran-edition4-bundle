package com.karianaquran.reader

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.karianaquran.reader.data.PageRepository
import com.karianaquran.reader.data.SessionStore
import com.karianaquran.reader.data.SettingsStore
import com.karianaquran.reader.data.crypto.AssetVault
import com.karianaquran.reader.data.remote.FirebaseGate
import com.karianaquran.reader.data.remote.LicenseRepository
import com.karianaquran.reader.data.remote.OfflineDownloadManager
import com.karianaquran.reader.data.remote.OtpAuthManager
import com.karianaquran.reader.data.remote.PaymentRepository
import com.karianaquran.reader.data.remote.SyncRepository
import com.karianaquran.reader.data.remote.UpdateRepository
import com.karianaquran.reader.security.DeviceFingerprint
import com.karianaquran.reader.ui.theme.AppFonts
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface

/** ম্যানুয়াল ডিপেন্ডেন্সি কন্টেইনার */
class AppContainer(app: Application) {
    val firebase = FirebaseGate(app)
    val vault = AssetVault(app)
    val deviceId: String = DeviceFingerprint.get(app)
    val settings = SettingsStore(app)
    val sessions = SessionStore(vault)
    val licenses = LicenseRepository(firebase, deviceId)
    val pages = PageRepository(app, vault, firebase, deviceId)
    val payments = PaymentRepository()
    val sync = SyncRepository(firebase)
    val updates = UpdateRepository()
    val offline = OfflineDownloadManager(app, vault, pages, firebase, deviceId)
    val otpManager = OtpAuthManager(app, firebase)

    val fonts: AppFonts

    init {
        // বান্ডেল অ্যাসেট প্রথম রানে এনক্রিপ্টেড ভল্টে (filesDir) যায়
        runCatching { vault.ensureInstalled() }.onFailure { firebase.record(it) }
        val arabic = runCatching {
            vault.loadTypeface(AssetVault.FONT_ARABIC)?.let { FontFamily(Typeface(it)) }
        }.getOrNull() ?: FontFamily.Default

        val bangla = runCatching {
            vault.loadTypeface(AssetVault.FONT_BANGLA)?.let { FontFamily(Typeface(it)) }
        }.getOrNull() ?: FontFamily.Default

        fonts = AppFonts(arabic, bangla)
    }
}

class QarianaApp : Application() {
    companion object {
        lateinit var instance: QarianaApp
            private set
    }

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        // স্বয়ংক্রিয়ভাবে ৫ ওয়াক্ত নামাজের অ্যালার্ম ও আযান শিডিউলিং
        runCatching {
            com.karianaquran.reader.service.PrayerAlarmScheduler.scheduleNextPrayers(this)
        }
        // ফ্রি ও ডেমো পৃষ্ঠাসমূহের অডিও এসেট স্বয়ংক্রিয় ব্যাকগ্রাউন্ড প্রি-লোডিং
        runCatching {
            com.karianaquran.reader.service.QuranAudioCacheManager.preloadFreeDemoAudio(this)
        }
        container = AppContainer(this)
        if (container.firebase.enabled) {
            runCatching {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            }
        }
    }
}

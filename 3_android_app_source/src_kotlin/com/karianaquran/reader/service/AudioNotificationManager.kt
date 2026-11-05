package com.karianaquran.reader.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.karianaquran.reader.MainActivity
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.components.toBn

/**
 * ক্বারীয়ানা কুরআন — কেন্দ্রীয় অডিও ও আযান নোটিফিকেশন ম্যানেজার
 * ১. যেকোনো ধরনের সাউন্ড চললে ইউজারকে রিয়েল-টাইমে নোটিফিকেশন প্রদর্শন
 * ২. আযান চলার সময় স্পষ্ট ওয়াক্তের নাম ও ১-ট্যাপে আযান বন্ধ করার বাটন
 * ৩. কুরআন তিলাওয়াত চলার সময় সূরা ও আয়াত নম্বর সহ প্লে/পজ ও স্টপ অ্যাকশন
 */
object AudioNotificationManager {

    const val CHANNEL_AZAN_ID = "qariana_azan_channel"
    const val CHANNEL_AZAN_NAME = "নামাজের ওয়াক্ত ও আযান"

    const val CHANNEL_AUDIO_ID = "qariana_audio_channel"
    const val CHANNEL_AUDIO_NAME = "কুরআন তিলাওয়াত ও অডিও"

    const val AZAN_NOTIFICATION_ID = 2001
    const val QURAN_AUDIO_NOTIFICATION_ID = 2002

    const val ACTION_STOP_AZAN = "com.karianaquran.reader.ACTION_STOP_AZAN"
    const val ACTION_STOP_QURAN_AUDIO = "com.karianaquran.reader.ACTION_STOP_QURAN_AUDIO"
    const val ACTION_TOGGLE_QURAN_AUDIO = "com.karianaquran.reader.ACTION_TOGGLE_QURAN_AUDIO"

    /**
     * ডিভাইসে অ্যাপের নোটিফিকেশন সক্রিয় কি না তা যাচাই করা
     */
    fun areNotificationsEnabled(context: Context): Boolean {
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val perm = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (perm != android.content.pm.PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    private fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // ১. আযান চ্যানেল (হাই প্রায়োরিটি, অ্যালার্ম ক্যাটাগরি)
            val azanChannel = NotificationChannel(
                CHANNEL_AZAN_ID,
                CHANNEL_AZAN_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "নামাজের ওয়াক্ত হলে আযান ও সতর্কবার্তা প্রদর্শন"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(null, null) // আজান অডিও সরাসরি বাজছে, তাই সিস্টেমের অতিরিক্ত বিপ বন্ধ রাখা
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            nm.createNotificationChannel(azanChannel)

            // ২. কুরআন অডিও চ্যানেল (স্বাভাবিক প্লেব্যাক)
            val audioChannel = NotificationChannel(
                CHANNEL_AUDIO_ID,
                CHANNEL_AUDIO_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "কুরআন তিলাওয়াত চলাকালীন প্লেয়ার কন্ট্রোল প্রদর্শন"
                setShowBadge(false)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            nm.createNotificationChannel(audioChannel)
        }
    }

    /**
     * আযান চলাকালীন নোটিফিকেশন প্রদর্শন (ওয়াক্তের নাম ও আযান বন্ধ করার অ্যাকশন সহ)
     */
    fun showAzanNotification(
        context: Context,
        prayerNameBn: String,
        prayerTimeBn: String = "",
        rakatBn: String = ""
    ) {
        ensureChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // অ্যাপ ওপেন করার ইনটেন্ট
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "namaz")
        }
        val openPending = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // আযান বন্ধ করার ইনটেন্ট (ব্রডকাস্ট)
        val stopIntent = Intent(context, StopAzanReceiver::class.java).apply {
            action = ACTION_STOP_AZAN
        }
        val stopPending = PendingIntent.getBroadcast(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "$prayerNameBn নামাজের আযান চলছে"
        val subtitle = if (prayerTimeBn.isNotBlank()) "ওয়াক্ত: $prayerNameBn • সময়: $prayerTimeBn" else "ওয়াক্ত: $prayerNameBn"
        val detailText = if (rakatBn.isNotBlank()) "$subtitle • $rakatBn" else subtitle

        val notification = NotificationCompat.Builder(context, CHANNEL_AZAN_ID)
            .setSmallIcon(R.drawable.ic_qariana_notif)
            .setContentTitle(title)
            .setContentText(detailText)
            .setSubText("ক্বারীয়ানা কুরআন ও সালাত")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "$title\n" +
                    "ক্বারীয়ানা কুরআন অ্যাপ থেকে আযান প্রচারিত হচ্ছে।\n" +
                    "⏰ সময়: $detailText\n\n" +
                    "সালাত কায়েম করুন, পরকালে শান্তি লাভ করুন।\n" +
                    "(আযান থামাতে নিচে 'আজান বন্ধ করুন' চাপুন)"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openPending)
            .setOngoing(true) // আযান বাজার সময় নোটিফিকেশন স্থির থাকবে
            .setAutoCancel(false)
            .setDeleteIntent(stopPending) // নোটিফিকেশন কেটে দিলেও আযান বন্ধ হবে
            .addAction(android.R.drawable.ic_media_pause, "আজান বন্ধ করুন", stopPending)
            .addAction(android.R.drawable.ic_lock_silent_mode, "মিউট", stopPending)
            .build()

        runCatching {
            nm.notify(AZAN_NOTIFICATION_ID, notification)
        }
    }

    /**
     * আযান নোটিফিকেশন বাতিল করা
     */
    fun cancelAzanNotification(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.cancel(AZAN_NOTIFICATION_ID)
    }

    /**
     * কুরআন তিলাওয়াত চলাকালীন নোটিফিকেশন প্রদর্শন (সূরা ও আয়াত নম্বর সহ)
     */
    fun showQuranAudioNotification(
        context: Context,
        surahNameBn: String,
        ayahNumber: Int,
        isPlaying: Boolean = true
    ) {
        ensureChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPending = PendingIntent.getActivity(
            context,
            10,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleIntent = Intent(context, StopAzanReceiver::class.java).apply {
            action = ACTION_TOGGLE_QURAN_AUDIO
        }
        val togglePending = PendingIntent.getBroadcast(
            context,
            11,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, StopAzanReceiver::class.java).apply {
            action = ACTION_STOP_QURAN_AUDIO
        }
        val stopPending = PendingIntent.getBroadcast(
            context,
            12,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "ক্বারীয়ানা কুরআন তিলাওয়াত"
        val body = "সূরা $surahNameBn • আয়াত ${ayahNumber.toBn()}"

        val builder = NotificationCompat.Builder(context, CHANNEL_AUDIO_ID)
            .setSmallIcon(R.drawable.ic_qariana_notif)
            .setContentTitle(title)
            .setContentText(body)
            .setSubText("ক্বারীয়ানা কুরআন ও প্রকাশনা")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openPending)
            .setOngoing(isPlaying)
            .setAutoCancel(false)
            .setDeleteIntent(stopPending)

        if (isPlaying) {
            builder.addAction(android.R.drawable.ic_media_pause, "⏸ বিরতি", togglePending)
        } else {
            builder.addAction(android.R.drawable.ic_media_play, "▶️ চালু", togglePending)
        }
        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "⏹ বন্ধ", stopPending)

        runCatching {
            nm.notify(QURAN_AUDIO_NOTIFICATION_ID, builder.build())
        }
    }

    /**
     * কুরআন অডিও নোটিফিকেশন বাতিল করা
     */
    fun cancelQuranAudioNotification(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.cancel(QURAN_AUDIO_NOTIFICATION_ID)
    }
}

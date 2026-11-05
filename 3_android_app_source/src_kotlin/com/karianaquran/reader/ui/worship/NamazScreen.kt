package com.karianaquran.reader.ui.worship

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.karianaquran.reader.data.location.District
import com.karianaquran.reader.data.location.LocationHelper
import com.karianaquran.reader.data.prayer.NextPrayerInfo
import com.karianaquran.reader.data.prayer.PrayerCalculator
import com.karianaquran.reader.data.prayer.PrayerSettingsHelper
import com.karianaquran.reader.data.prayer.PrayerTime
import com.karianaquran.reader.service.AzanPlayer
import com.karianaquran.reader.service.PrayerAlarmScheduler
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.components.toLang
import com.karianaquran.reader.ui.theme.LocalAppFonts
import java.util.Calendar
import java.util.Date

/**
 * নামাজের ওয়াক্ত ও নিষিদ্ধ সময়ের মডেল
 */
data class NamazItem(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val nameAr: String,
    val timeRangeBn: String,
    val timeRangeEn: String,
    val isProhibited: Boolean = false,
    val isCurrent: Boolean = false,
    val isPassed: Boolean = false,
    val icon: ImageVector,
    val totalRakatsBn: String = "",
    val totalRakatsEn: String = "",
    val rakatsBreakdownBn: List<Pair<String, String>> = emptyList(),
    val virtuesBn: String = "",
    val hadithRefBn: String = "",
    val prohibitedNoticeBn: String = "",
    val allowedDeedsBn: String = "",
)

private fun format12h(hour: Int, minute: Int, isEnglish: Boolean): String {
    val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    val period = if (hour < 12) "AM" else "PM"
    val mStr = String.format("%02d", minute)
    val res = "$h:$mStr $period"
    return res.toLang(isEnglish)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"

    // ১. জেলা নির্বাচন স্টেট
    var currentDistrict by remember { mutableStateOf(LocationHelper.getSelectedDistrict(context)) }
    var showDistrictDialog by remember { mutableStateOf(false) }

    // ২. লাইভ রিয়েল-টাইম টাইমার (প্রতি সেকেন্ডে ঘড়ি ও কাউন্টডাউন রিফ্রেশ)
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }

    val now = remember(currentTimeMillis) { Date(currentTimeMillis) }
    val cal = remember(currentTimeMillis) {
        Calendar.getInstance().apply { time = now }
    }
    val currentHour = cal.get(Calendar.HOUR_OF_DAY)
    val currentMinute = cal.get(Calendar.MINUTE)
    val currentSecond = cal.get(Calendar.SECOND)
    val currentSecOfDay = currentHour * 3600 + currentMinute * 60 + currentSecond

    // ৩. ওয়াক্তের তথ্য ও সময়সূচী
    val nextPrayerInfo = remember(currentTimeMillis, currentDistrict) {
        PrayerCalculator.getNextPrayer(
            now = now,
            latitude = currentDistrict.lat,
            longitude = currentDistrict.lon
        )
    }

    val schedule = nextPrayerInfo.schedule
    val fajr = schedule.getOrNull(0) ?: PrayerTime("ফজর", "Fajr", "الفجر", 5, 0, "০৫:০০", "৪ রাকাত")
    val sunrise = schedule.getOrNull(1) ?: PrayerTime("সূর্যোদয়", "Sunrise", "الشروق", 6, 15, "০৬:১৫", "")
    val dhuhr = schedule.getOrNull(2) ?: PrayerTime("যোহর", "Dhuhr", "الظهر", 12, 5, "১২:০৫", "১২ রাকাত")
    val asr = schedule.getOrNull(3) ?: PrayerTime("আসর", "Asr", "العصر", 16, 15, "০৪:১৫", "৮ রাকাত")
    val maghrib = schedule.getOrNull(4) ?: PrayerTime("মাগরিব", "Maghrib", "المغرب", 18, 0, "০৬:০০", "৭ রাকাত")
    val isha = schedule.getOrNull(5) ?: PrayerTime("ইশা", "Isha", "العشاء", 19, 15, "০৭:১৫", "১৭ রাকাত")

    // ৪. নোটিফিকেশন পারমিশন ও আযান অ্যালার্ম স্টেট
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            PrayerAlarmScheduler.scheduleNextPrayers(context)
        }
    }

    var isMasterAzanEnabled by remember {
        mutableStateOf(PrayerSettingsHelper.isMasterAzanEnabled(context))
    }

    val prayerAlarmStates = remember {
        mutableStateMapOf(
            "fajr" to PrayerSettingsHelper.isPrayerEnabled(context, "fajr"),
            "dhuhr" to PrayerSettingsHelper.isPrayerEnabled(context, "dhuhr"),
            "asr" to PrayerSettingsHelper.isPrayerEnabled(context, "asr"),
            "maghrib" to PrayerSettingsHelper.isPrayerEnabled(context, "maghrib"),
            "isha" to PrayerSettingsHelper.isPrayerEnabled(context, "isha"),
            "tahajjud" to PrayerSettingsHelper.isPrayerEnabled(context, "tahajjud"),
            "suhoor" to PrayerSettingsHelper.isPrayerEnabled(context, "suhoor"),
            "iftar" to PrayerSettingsHelper.isPrayerEnabled(context, "iftar"),
            "ishraq" to PrayerSettingsHelper.isPrayerEnabled(context, "ishraq")
        )
    }

    val isAzanPlaying by AzanPlayer.isPlaying.collectAsState()

    // ৫. লাইভ ঘড়ি টেক্সট (HH:MM:SS AM/PM)
    val liveClockText = remember(currentTimeMillis, isEnglish) {
        val displayH = if (currentHour == 0) 12 else if (currentHour > 12) currentHour - 12 else currentHour
        val hStr = String.format("%02d", displayH)
        val mStr = String.format("%02d", currentMinute)
        val sStr = String.format("%02d", currentSecond)
        val period = if (currentHour < 12) "AM" else "PM"
        val timeFormatted = "$hStr:$mStr:$sStr $period"
        timeFormatted.toLang(isEnglish)
    }

    val currentPrayerName = nextPrayerInfo.currentPrayerNameBn

    val fStart = format12h(fajr.hour, fajr.minute, isEnglish)
    val sStart = format12h(sunrise.hour, sunrise.minute, isEnglish)
    val sEndM = (sunrise.minute + 20) % 60
    val sEndH = sunrise.hour + (sunrise.minute + 20) / 60
    val sEnd = format12h(sEndH, sEndM, isEnglish)

    val dStart = format12h(dhuhr.hour, dhuhr.minute, isEnglish)
    val zawwalStartM = if (dhuhr.minute >= 15) dhuhr.minute - 15 else 60 + (dhuhr.minute - 15)
    val zawwalStartH = if (dhuhr.minute >= 15) dhuhr.hour else dhuhr.hour - 1
    val zawwalStart = format12h(zawwalStartH, zawwalStartM, isEnglish)

    val aStart = format12h(asr.hour, asr.minute, isEnglish)
    val mStart = format12h(maghrib.hour, maghrib.minute, isEnglish)
    val sunsetStartM = if (maghrib.minute >= 16) maghrib.minute - 16 else 60 + (maghrib.minute - 16)
    val sunsetStartH = if (maghrib.minute >= 16) maghrib.hour else maghrib.hour - 1
    val sunsetStart = format12h(sunsetStartH, sunsetStartM, isEnglish)

    val iStart = format12h(isha.hour, isha.minute, isEnglish)

    val timelineItems = listOf(
        NamazItem(
            id = "fajr",
            nameBn = "ফজর",
            nameEn = "Fajr",
            nameAr = "الفجر",
            timeRangeBn = "$fStart - $sStart",
            timeRangeEn = "$fStart - $sStart",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("ফজর"),
            isPassed = currentSecOfDay > (sunrise.hour * 3600 + sunrise.minute * 60),
            icon = Icons.Default.WbTwilight,
            totalRakatsBn = "৪ রাকাত",
            totalRakatsEn = "4 Rakats",
            rakatsBreakdownBn = listOf("২ রাকাত সুন্নতে মুয়াক্কাদা" to "ফরজের পূর্বে", "২ রাকাত ফরজ" to "ফরজ নামাজ"),
            virtuesBn = "রাসুলুল্লাহ (সা.) ইরশাদ করেছেন: 'ফজরের দুই রাকাত সুন্নাত দুনিয়া ও এর মধ্যকার সমস্ত কিছুর চেয়ে উত্তম।' (সহীহ মুসলিম: ৭২৫)"
        ),
        NamazItem(
            id = "forbidden_sunrise",
            nameBn = "সূর্যোদয় (নামাজ নিষিদ্ধ)",
            nameEn = "Sunrise (Prohibited)",
            nameAr = "الشروق",
            timeRangeBn = "$sStart - $sEnd",
            timeRangeEn = "$sStart - $sEnd",
            isProhibited = true,
            isCurrent = currentPrayerName.contains("সূর্যোদয়") || (currentSecOfDay in (sunrise.hour * 3600 + sunrise.minute * 60)..(sEndH * 3600 + sEndM * 60)),
            isPassed = currentSecOfDay > (sEndH * 3600 + sEndM * 60),
            icon = Icons.Default.Warning,
            hadithRefBn = "সহীহ মুসলিম: ৮৩১",
            prohibitedNoticeBn = "সূর্য ওঠার সময় প্রায় ১৫-২০ মিনিট পর্যন্ত যেকোনো নামাজ পড়া সম্পূর্ণ নিষেধ।",
            allowedDeedsBn = "জিকির, তাসবীহ, দরূদ ও তিলাওয়াত সম্পূর্ণ বৈধ ও সওয়াবের কাজ।"
        ),
        NamazItem(
            id = "ishraq",
            nameBn = "ইশরাক ও চাশত (নফল)",
            nameEn = "Ishraq & Duha",
            nameAr = "الإشراق والضحى",
            timeRangeBn = "$sEnd - $zawwalStart",
            timeRangeEn = "$sEnd - $zawwalStart",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("ইশরাক") || (currentSecOfDay in (sEndH * 3600 + sEndM * 60)..(zawwalStartH * 3600 + zawwalStartM * 60)),
            isPassed = currentSecOfDay > (zawwalStartH * 3600 + zawwalStartM * 60),
            icon = Icons.Default.WbSunny,
            totalRakatsBn = "২ থেকে ৮ রাকাত",
            totalRakatsEn = "2 to 8 Rakats",
            rakatsBreakdownBn = listOf("২ রাকাত করে ২, ৪ বা ৮ রাকাত" to "নফল সালাতুল দুহা"),
            virtuesBn = "রাসুলুল্লাহ (সা.) বলেছেন: 'যে ব্যক্তি ফজরের পর বসে আল্লাহর জিকির করে সূর্যোদয়ের পর দুই রাকাত নামাজ পড়ে, সে একটি পূর্ণ হজ্জ ও উমরার সওয়াব পায়।' (তিরমিযী: ৫৮৬)"
        ),
        NamazItem(
            id = "forbidden_zawwal",
            nameBn = "দ্বিপ্রহর (নামাজ নিষিদ্ধ)",
            nameEn = "Midday Zawwal (Prohibited)",
            nameAr = "الزوال",
            timeRangeBn = "$zawwalStart - $dStart",
            timeRangeEn = "$zawwalStart - $dStart",
            isProhibited = true,
            isCurrent = currentPrayerName.contains("দ্বিপ্রহর") || (currentSecOfDay in (zawwalStartH * 3600 + zawwalStartM * 60)..(dhuhr.hour * 3600 + dhuhr.minute * 60)),
            isPassed = currentSecOfDay > (dhuhr.hour * 3600 + dhuhr.minute * 60),
            icon = Icons.Default.Warning,
            hadithRefBn = "সহীহ মুসলিম: ৮৩১",
            prohibitedNoticeBn = "ঠিক দ্বিপ্রহরে সূর্য যখন মাথার উপর থাকে, তখন যোহরের ওয়াক্ত শুরু হওয়ার পূর্ব পর্যন্ত নামাজ পড়া নিষেধ।",
            allowedDeedsBn = "তিলাওয়াত ও আল্লাহর জিকির অব্যাহত রাখা যায়।"
        ),
        NamazItem(
            id = "dhuhr",
            nameBn = "যোহর",
            nameEn = "Dhuhr",
            nameAr = "الظهر",
            timeRangeBn = "$dStart - $aStart",
            timeRangeEn = "$dStart - $aStart",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("যোহর"),
            isPassed = currentSecOfDay > (asr.hour * 3600 + asr.minute * 60),
            icon = Icons.Default.WbSunny,
            totalRakatsBn = "১২ রাকাত",
            totalRakatsEn = "12 Rakats",
            rakatsBreakdownBn = listOf("৪ রাকাত সুন্নতে মুয়াক্কাদা" to "ফরজের পূর্বে", "৪ রাকাত ফরজ" to "মূল ফরজ", "২ রাকাত সুন্নতে মুয়াক্কাদা" to "ফরজের পরে", "২ রাকাত নফল" to "ঐচ্ছিক"),
            virtuesBn = "যোহরের পূর্বের চার রাকাত সম্পর্কে রাসুল (সা.) বলেন: এ সময়ে আসমানের দরজাসমূহ উন্মুক্ত করা হয়। (তিরমিযী: ৪৭৮)"
        ),
        NamazItem(
            id = "asr",
            nameBn = "আসর",
            nameEn = "Asr",
            nameAr = "العصر",
            timeRangeBn = "$aStart - $sunsetStart",
            timeRangeEn = "$aStart - $sunsetStart",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("আসর"),
            isPassed = currentSecOfDay > (sunsetStartH * 3600 + sunsetStartM * 60),
            icon = Icons.Default.WbTwilight,
            totalRakatsBn = "৮ রাকাত",
            totalRakatsEn = "8 Rakats",
            rakatsBreakdownBn = listOf("৪ রাকাত গায়রে মুয়াক্কাদা" to "ফরজের পূর্বে নফল সুন্নত", "৪ রাকাত ফরজ" to "মূল ফরজ নামাজ"),
            virtuesBn = "রাসুলুল্লাহ (সা.) ইরশাদ করেন: 'যে ব্যক্তি দুই শীতল সময়ের (ফজর ও আসর) নামাজ পড়বে, সে জান্নাতে প্রবেশ করবে।' (সহীহ বুখারী: ৫৭৪)"
        ),
        NamazItem(
            id = "forbidden_sunset",
            nameBn = "সূর্যাস্ত (নামাজ নিষিদ্ধ)",
            nameEn = "Sunset (Prohibited)",
            nameAr = "الغروب",
            timeRangeBn = "$sunsetStart - $mStart",
            timeRangeEn = "$sunsetStart - $mStart",
            isProhibited = true,
            isCurrent = currentPrayerName.contains("সূর্যাস্ত") || (currentSecOfDay in (sunsetStartH * 3600 + sunsetStartM * 60)..(maghrib.hour * 3600 + maghrib.minute * 60)),
            isPassed = currentSecOfDay > (maghrib.hour * 3600 + maghrib.minute * 60),
            icon = Icons.Default.Warning,
            hadithRefBn = "সহীহ বুখারী: ৫৫১",
            prohibitedNoticeBn = "সূর্য যখন অস্ত যাওয়ার প্রাক্কালে লালচে বর্ণ ধারণ করে, তখন মাগরিব শুরু হওয়া পর্যন্ত নামাজ পড়া নিষেধ।",
            allowedDeedsBn = "সন্ধ্যার মাসনুন দোয়া ও ইস্তিগফার পাঠ করুন।"
        ),
        NamazItem(
            id = "maghrib",
            nameBn = "মাগরিব ও ইফতার",
            nameEn = "Maghrib & Iftar",
            nameAr = "المغرب",
            timeRangeBn = "$mStart - $iStart",
            timeRangeEn = "$mStart - $iStart",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("মাগরিব"),
            isPassed = currentSecOfDay > (isha.hour * 3600 + isha.minute * 60),
            icon = Icons.Default.NightsStay,
            totalRakatsBn = "৭ রাকাত",
            totalRakatsEn = "7 Rakats",
            rakatsBreakdownBn = listOf("৩ রাকাত ফরজ" to "মূল ফরজ", "২ রাকাত সুন্নতে মুয়াক্কাদা" to "ফরজের পরে", "২ রাকাত নফল (আওয়াবিন)" to "ঐচ্ছিক"),
            virtuesBn = "মাগরিবের আযান দেওয়ার সাথে সাথে রোযাদারদের ইফতার করা এবং নামাজে বিলম্ব না করা সুন্নাত।"
        ),
        NamazItem(
            id = "isha",
            nameBn = "ইশা ও বিতর",
            nameEn = "Isha & Witr",
            nameAr = "العشاء",
            timeRangeBn = "$iStart - রাত ১২:০০",
            timeRangeEn = "$iStart - 12:00 AM",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("ইশা"),
            isPassed = false,
            icon = Icons.Default.NightsStay,
            totalRakatsBn = "১৭ রাকাত",
            totalRakatsEn = "17 Rakats",
            rakatsBreakdownBn = listOf("৪ রাকাত সুন্নত" to "ফরজের পূর্বে", "৪ রাকাত ফরজ" to "মূল ফরজ", "২ রাকাত সুন্নতে মুয়াক্কাদা" to "ফরজের পরে", "২ রাকাত নফল" to "ঐচ্ছিক", "৩ রাকাত বিতর" to "ওয়াজিব", "২ রাকাত নফল" to "বিতরের পরে"),
            virtuesBn = "রাসুল (সা.) বলেন: 'যে ব্যক্তি জামাতে ইশার নামাজ পড়ল, সে যেন অর্ধরাত পর্যন্ত নামাজ পড়ল।' (সহীহ মুসলিম: ৬৫৬)"
        ),
        NamazItem(
            id = "tahajjud",
            nameBn = "তাহাজ্জুদ ও সাহরি",
            nameEn = "Tahajjud & Suhoor",
            nameAr = "التهجد والسحور",
            timeRangeBn = "রাত ১২:০০ - $fStart",
            timeRangeEn = "12:00 AM - $fStart",
            isProhibited = false,
            isCurrent = currentPrayerName.contains("তাহাজ্জুদ") || currentPrayerName.contains("সাহরি"),
            isPassed = false,
            icon = Icons.Default.NightsStay,
            totalRakatsBn = "৪ থেকে ১২ রাকাত",
            totalRakatsEn = "4 to 12 Rakats",
            rakatsBreakdownBn = listOf("২ রাকাত করে ৪, ৮ বা ১২ রাকাত" to "রাতের শেষ তৃতীয়াংশে নফল সালাত"),
            virtuesBn = "আল্লাহ তাআলা বলেন: 'রাত্রির কিছু অংশে তাহাজ্জুদ পড়ুন, যা আপনার জন্য এক অতিরিক্ত নেক আমল।' (সূরা বনী ইসরাঈল: ৭৯)"
        )
    )

    var selectedDetailItem by remember { mutableStateOf<NamazItem?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "clockPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseGlow"
    )

    // ৮. ১০০% স্ক্রলেবল মূল কন্টেইনার
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) Color(0xFF07120E) else Color(0xFFF1F5F9),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ১. শীর্ষ লাইভ ইসলামিক ক্লক ও হিরো সেকশন
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                if (isDark) {
                                    listOf(Color(0xFF061A14), Color(0xFF0A2B20), Color(0xFF0D382C))
                                } else {
                                    listOf(Color(0xFF064E3B), Color(0xFF0F766E), Color(0xFF14B8A6))
                                }
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // ক. টপ বার: ব্যাক বাটন ও জেলা পিল
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                modifier = Modifier.size(38.dp)
                            ) {
                                IconButton(onClick = { vm.back() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "পেছনে",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isEnglish) "Prayer Times & Azan" else "নামাজের সময়সূচী ও আযান",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 16.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "Live Dynamic Islamic Clock" else "লাইভ ইসলামিক ঘড়ি ও ওয়াক্ত",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            // জেলা পিল
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                modifier = Modifier.clickable { showDistrictDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isEnglish) currentDistrict.nameEn else currentDistrict.nameBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // খ. লাইভ ঘড়ি সেন্ট্রাল কার্ড
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.25f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // রানিং ওয়াক্ত ব্যাজ
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFD4AF37).copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = pulseGlow))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF34D399))
                                        )
                                        Text(
                                            text = if (isEnglish) "Active: $currentPrayerName" else "চলমান: $currentPrayerName",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                // লাইভ সেকেন্ডসহ ঘড়ির টেক্সট
                                Text(
                                    text = liveClockText,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )

                                Spacer(Modifier.height(4.dp))

                                // পরবর্তী ওয়াক্তের কাউন্টডাউন
                                val remStr = "${nextPrayerInfo.remainingHours.toBn()} ঘণ্টা ${nextPrayerInfo.remainingMinutes.toBn()} মি. ${nextPrayerInfo.remainingSeconds.toBn()} সে."
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "${nextPrayerInfo.nextPrayerNameBn} শুরু হতে বাকি: $remStr",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFDE047)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // গ. কুইক টাইম গ্রিড (সাহরি শেষ, সূর্যোদয়, ইফতার)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // সাহরি শেষ
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isEnglish) "Suhoor Ends" else "সাহরি শেষ",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        text = fStart,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // সূর্যোদয়
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isEnglish) "Sunrise" else "সূর্যোদয়",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        text = sStart,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE047)
                                    )
                                }
                            }

                            // ইফতার / মাগরিব
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isEnglish) "Sunset / Iftar" else "সূর্যাস্ত / ইফতার",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Text(
                                        text = mStart,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6EE7B7)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            // ২. হেডিং: নামাজের ওয়াক্তসমূহ ও নিষিদ্ধ সময়
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Daily Prayer Timeline & Rulings" else "ওয়াক্তসমূহ ও নামাজের সময়সূচী",
                        fontFamily = fonts.kalpurush,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = if (isEnglish) "Tap for Details" else "বিস্তারিত জানতে ট্যাপ করুন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        color = Color(0xFF0F6B56),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ৩. ওয়াক্তের কার্ডসমূহ
            items(timelineItems) { item ->
                NamazTimelineCard(
                    item = item,
                    isDark = isDark,
                    isEnglish = isEnglish,
                    onClick = { selectedDetailItem = item }
                )
                Spacer(Modifier.height(8.dp))
            }

            // ৪. ইসলামিক আযান ও অ্যালার্ম ম্যানেজার সেকশন
            item {
                Spacer(Modifier.height(12.dp))
                IslamicAlarmsSection(
                    isDark = isDark,
                    isEnglish = isEnglish,
                    hasPermission = hasNotificationPermission,
                    onRequestPermission = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                    isMasterEnabled = isMasterAzanEnabled,
                    onToggleMaster = { enabled ->
                        isMasterAzanEnabled = enabled
                        PrayerSettingsHelper.setMasterAzanEnabled(context, enabled)
                        if (enabled && hasNotificationPermission) {
                            PrayerAlarmScheduler.scheduleNextPrayers(context)
                        } else {
                            PrayerAlarmScheduler.cancelAllPrayers(context)
                        }
                    },
                    prayerAlarmStates = prayerAlarmStates,
                    onTogglePrayer = { key, enabled ->
                        prayerAlarmStates[key] = enabled
                        PrayerSettingsHelper.setPrayerEnabled(context, key, enabled)
                        if (isMasterAzanEnabled && hasNotificationPermission) {
                            PrayerAlarmScheduler.scheduleNextPrayers(context)
                        }
                    },
                    isAzanPlaying = isAzanPlaying,
                    onToggleTestAzan = {
                        if (isAzanPlaying) {
                            AzanPlayer.stopAzan()
                        } else {
                            AzanPlayer.playAzan(
                                context = context,
                                prayerName = currentPrayerName,
                                isFajr = currentPrayerName == "ফজর",
                                prayerTimeBn = liveClockText
                            )
                        }
                    }
                )
            }
        }
    }

    // ওয়াক্তের বিস্তারিত সেন্টার্ড ডায়ালগ (বটম শিট নয়)
    selectedDetailItem?.let { item ->
        Dialog(onDismissRequest = { selectedDetailItem = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF14241C) else Color.White,
                border = BorderStroke(1.dp, if (item.isProhibited) Color(0xFFEF4444) else Color(0xFF0F6B56)),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isEnglish) item.nameEn else item.nameBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isProhibited) Color(0xFFEF4444) else (if (isDark) Color.White else Color(0xFF0F6B56))
                            )
                            Text(
                                text = item.nameAr,
                                fontSize = 14.sp,
                                color = Color(0xFFD4AF37)
                            )
                        }

                        IconButton(onClick = { selectedDetailItem = null }) {
                            Icon(Icons.Rounded.Close, contentDescription = "বন্ধ করুন", tint = Color.Gray)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // সময় পরিসীমা
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.isProhibited) Color(0xFFFEE2E2) else (if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9))
                    ) {
                        Text(
                            text = "ওয়াক্ত: ${if (isEnglish) item.timeRangeEn else item.timeRangeBn}",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (item.isProhibited) Color(0xFFB91C1C) else (if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56)),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    if (item.isProhibited) {
                        Text(
                            text = "হাদীস নির্দেশিকা ও হুকুম:",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.prohibitedNoticeBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                            lineHeight = 17.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "অনুমোদিত আমল: ${item.allowedDeedsBn}",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = Color(0xFF059669),
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        if (item.totalRakatsBn.isNotEmpty()) {
                            Text(
                                text = "মোট রাকাত: ${item.totalRakatsBn}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            Spacer(Modifier.height(6.dp))
                            item.rakatsBreakdownBn.forEach { (rTitle, rDesc) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "• $rTitle", fontFamily = fonts.kalpurush, fontSize = 12.sp, color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B))
                                    Text(text = rDesc, fontFamily = fonts.kalpurush, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }

                        if (item.virtuesBn.isNotEmpty()) {
                            Text(
                                text = "ফজিলত ও হাদিস:",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.virtuesBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { selectedDetailItem = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ঠিক আছে", fontFamily = fonts.kalpurush, color = Color.White)
                    }
                }
            }
        }
    }

    // জেলা পরিবর্তনের ডায়ালগ
    if (showDistrictDialog) {
        var districtSearchQuery by remember { mutableStateOf("") }
        val filteredDistricts = remember(districtSearchQuery) {
            if (districtSearchQuery.isBlank()) LocationHelper.districts
            else LocationHelper.districts.filter {
                it.nameBn.contains(districtSearchQuery.trim(), ignoreCase = true) ||
                it.nameEn.contains(districtSearchQuery.trim(), ignoreCase = true)
            }
        }

        Dialog(onDismissRequest = { showDistrictDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF14241C) else Color.White,
                modifier = Modifier.padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isEnglish) "Select District" else "জেলা নির্বাচন করুন (৬৪ জেলা)",
                        fontFamily = fonts.kalpurush,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F6B56)
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = districtSearchQuery,
                        onValueChange = { districtSearchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isEnglish) "Search district..." else "জেলার নাম লিখে খুঁজুন...",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFFFDF79) else Color(0xFF0F6B56),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) Color(0xFF10B981) else Color(0xFF0F6B56),
                            unfocusedBorderColor = if (isDark) Color(0xFF2E533C) else Color(0xFFE2E8F0),
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B)
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    LazyColumn(modifier = Modifier.height(280.dp)) {
                        items(filteredDistricts) { dist ->
                            val isSelected = dist.id == currentDistrict.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentDistrict = dist
                                        LocationHelper.saveSelectedDistrict(context, dist)
                                        showDistrictDialog = false
                                    },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF0F6B56).copy(alpha = 0.15f) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isEnglish) dist.nameEn else dist.nameBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF0F6B56) else (if (isDark) Color.White else Color(0xFF1E293B))
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0F6B56), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * ওয়াক্তের স্বতন্ত্র কার্ড
 */
@Composable
private fun NamazTimelineCard(
    item: NamazItem,
    isDark: Boolean,
    isEnglish: Boolean,
    onClick: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val borderColor = if (item.isCurrent) Color(0xFFD4AF37) else if (item.isProhibited) Color(0xFFEF4444).copy(alpha = 0.4f) else (if (isDark) Color(0xFF1E2E25) else Color(0xFFE2E8F0))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCurrent) {
                if (isDark) Color(0xFF1B3D30) else Color(0xFFE8F5E9)
            } else if (item.isProhibited) {
                if (isDark) Color(0xFF241515) else Color(0xFFFFF1F2)
            } else {
                if (isDark) Color(0xFF122019) else Color.White
            }
        ),
        border = BorderStroke(if (item.isCurrent) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (item.isProhibited) Color(0xFFFEE2E2) else (if (isDark) Color(0xFF1C3328) else Color(0xFFF1F5F9)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (item.isProhibited) Color(0xFFEF4444) else (if (item.isCurrent) Color(0xFF0F6B56) else Color(0xFF64748B)),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isEnglish) item.nameEn else item.nameBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isProhibited) Color(0xFFDC2626) else (if (isDark) Color.White else Color(0xFF0F172A))
                        )
                        if (item.isCurrent) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF0F6B56)) {
                                Text(
                                    text = "চলমান",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (item.totalRakatsBn.isNotEmpty()) item.totalRakatsBn else item.nameAr,
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            Text(
                text = if (isEnglish) item.timeRangeEn else item.timeRangeBn,
                fontFamily = fonts.kalpurush,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.isProhibited) Color(0xFFDC2626) else (if (isDark) Color(0xFFFDE68A) else Color(0xFF0F6B56))
            )
        }
    }
}

/**
 * ইসলামিক আযান ও অতিরিক্ত অ্যালার্ম সেটিংস সেকশন
 */
@Composable
private fun IslamicAlarmsSection(
    isDark: Boolean,
    isEnglish: Boolean,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    isMasterEnabled: Boolean,
    onToggleMaster: (Boolean) -> Unit,
    prayerAlarmStates: Map<String, Boolean>,
    onTogglePrayer: (String, Boolean) -> Unit,
    isAzanPlaying: Boolean,
    onToggleTestAzan: () -> Unit,
) {
    val fonts = LocalAppFonts.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF13221A) else Color.White),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF20382B) else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // শিরোনাম
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = Color(0xFF0F6B56),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isEnglish) "Azan & Islamic Alarms" else "আযান ও বিশেষ অ্যালার্ম সিস্টেম",
                        fontFamily = fonts.kalpurush,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                }

                // টেস্ট আযান বাটন
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAzanPlaying) Color(0xFFEF4444) else Color(0xFF0F6B56),
                    modifier = Modifier.clickable(onClick = onToggleTestAzan)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAzanPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isAzanPlaying) "থামান" else "টেস্ট আযান",
                            fontFamily = fonts.kalpurush,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // মাস্টার আযান সুইচ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDark) Color(0xFF1B3125) else Color(0xFFF1F5F9),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isEnglish) "Master Azan Notification" else "সকল ওয়াক্তের আযান নোটিফিকেশন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = if (isMasterEnabled) "সকল সক্রিয় ওয়াক্তে আযান বাজবে" else "আযান নোটিফিকেশন বন্ধ রয়েছে",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = isMasterEnabled,
                    onCheckedChange = { onToggleMaster(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF0F6B56)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // পারমিশন ওয়ার্নিং যদি না থাকে
            if (!hasPermission) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onRequestPermission)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Text(
                            text = "আযান বাজাতে নোটিফিকেশন অনুমতি প্রয়োজন। ট্যাপ করে অনুমতি দিন।",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // ৬টি ওয়াক্ত ও ৩টি বিশেষ অ্যালার্ম তালিকা
            val alarmList = listOf(
                Triple("fajr", "ফজর আযান", "Fajr Azan Alert"),
                Triple("dhuhr", "যোহর আযান", "Dhuhr Azan Alert"),
                Triple("asr", "আসর আযান", "Asr Azan Alert"),
                Triple("maghrib", "মাগরিব আযান", "Maghrib Azan Alert"),
                Triple("isha", "ইশা আযান", "Isha Azan Alert"),
                Triple("tahajjud", "তাহাজ্জুদ অ্যালার্ম", "Tahajjud Waking Alarm"),
                Triple("suhoor", "সাহরি সমাপ্তি অ্যালার্ম", "Suhoor Ends Alert"),
                Triple("iftar", "ইফতার স্মরণকারী অ্যালার্ম", "Iftar Reminder Alert"),
                Triple("ishraq", "ইশরাক সালাত অ্যালার্ম", "Ishraq Prayer Alert")
            )

            alarmList.forEach { (key, bnName, enName) ->
                val isChecked = prayerAlarmStates[key] ?: true
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isEnglish) enName else bnName,
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.5.sp,
                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isChecked) (if (isDark) Color.White else Color(0xFF1E293B)) else Color.Gray
                    )
                    Switch(
                        checked = isChecked && isMasterEnabled,
                        onCheckedChange = { onTogglePrayer(key, it) },
                        enabled = isMasterEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0F6B56)
                        )
                    )
                }
            }
        }
    }
}

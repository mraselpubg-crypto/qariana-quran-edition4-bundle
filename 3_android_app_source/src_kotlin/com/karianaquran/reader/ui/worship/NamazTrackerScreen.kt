package com.karianaquran.reader.ui.worship

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.prayer.DayPrayerRecord
import com.karianaquran.reader.data.prayer.PrayerTrackerRepository
import com.karianaquran.reader.data.prayer.TrackerPeriod
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.components.toLang
import com.karianaquran.reader.ui.theme.LocalAppFonts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class PrayerDisplayInfo(
    val key: String,
    val nameBn: String,
    val nameEn: String,
    val icon: ImageVector
)

private data class CalendarDayItem(
    val dayNum: Int,
    val dateStr: String,
    val isFri: Boolean
)

/**
 * ক্বারীয়ানা কুরআন - পূর্ণাঙ্গ নামাজ ট্র্যাকার ও ইতিহাস স্ক্রিন (NamazTrackerScreen)
 * আন্তর্জাতিক ডার্ক এমেরাল্ড ইসলামিক লাক্সারি থিম
 */
@Composable
fun NamazTrackerScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isEnglish = readerSettings.appLanguage == "en"

    LaunchedEffect(Unit) {
        PrayerTrackerRepository.init(context)
    }

    val recordsMap by PrayerTrackerRepository.recordsFlow.collectAsState()
    val todayDateStr = remember { PrayerTrackerRepository.getTodayDateString() }

    // নির্বাচিত তারিখ (ডিফল্ট: আজকের তারিখ)
    var selectedDateStr by remember { mutableStateOf(todayDateStr) }

    // নির্বাচিত ফিল্টার ট্যাব (This Day, This Week, This Month, This Year)
    var selectedPeriod by remember { mutableStateOf(TrackerPeriod.DAY) }

    // ক্যালেন্ডারের মাস ও বছর স্টেট
    var calendarYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var calendarMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) } // 0..11

    // নির্বাচিত দিনের রেকর্ড
    val currentDayRecord = recordsMap[selectedDateStr] ?: DayPrayerRecord(date = selectedDateStr)

    // ৫টি ওয়াক্তের মেটাডাটা
    val prayersList = remember {
        listOf(
            PrayerDisplayInfo("fajr", "ফজর", "Fajr", Icons.Default.WbTwilight),
            PrayerDisplayInfo("dhuhr", "যোহর", "Dhuhr", Icons.Default.WbSunny),
            PrayerDisplayInfo("asr", "আসর", "Asr", Icons.Default.WbCloudy),
            PrayerDisplayInfo("maghrib", "মাগরিব", "Maghrib", Icons.Default.NightsStay),
            PrayerDisplayInfo("isha", "এশা", "Isha", Icons.Default.NightsStay)
        )
    }

    // ওয়াক্ত শুরু হওয়ার স্ট্যাটাস মেমোইজেশন (বারংবার সূর্যের হিসাব এড়াতে)
    val now = remember { Calendar.getInstance() }
    val waqtStatusMap = remember(selectedDateStr) {
        prayersList.associate { it.key to PrayerTrackerRepository.isPrayerWaqtStarted(it.key, selectedDateStr, now) }
    }

    val isDark = readerSettings.themeId == "dark"

    // ক্বারীয়ানা ব্র্যান্ড থিম কালার প্যালেট (লাইট মোডে স্নিগ্ধ মিন্ট/সাদা এবং ডার্ক মোডে মিডনাইট এমারেল্ড)
    val bgPage = if (isDark) Color(0xFF0A1C16) else Color(0xFFF6FFF9)
    val cardBg = if (isDark) Color(0xFF112921) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
    val emeraldTeal = if (isDark) Color(0xFF107B5F) else Color(0xFF059669)
    val amberAccent = Color(0xFFD4AF37) // ক্বারীয়ানা সিগনেচার রয়েল গোল্ড
    val textPrimary = if (isDark) Color.White else Color(0xFF064E3B)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgPage,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // =========================================================================
            // ১. টপ বার: ব্যাক বাটন, টাইটেল ও ক্লাউড সিঙ্ক স্ট্যাটাস
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { vm.back() },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isEnglish) "Back" else "ফিরে যান",
                            tint = textPrimary
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isEnglish) "Namaz Tracker" else "নামাজ ট্র্যাকার ও ইতিহাস",
                        fontFamily = fonts.kalpurush,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }

                // সিঙ্ক স্ট্যাটাস পিল
                val isAllSynced = remember(recordsMap) {
                    recordsMap.isEmpty() || recordsMap.values.all { it.isSynced }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAllSynced) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (isAllSynced) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFF59E0B).copy(alpha = 0.4f)),
                    modifier = Modifier.clickable {
                        PrayerTrackerRepository.syncPendingRecords(context)
                        Toast.makeText(
                            context,
                            if (isEnglish) "Syncing with cloud..." else "ক্লাউডে সংরক্ষিত হচ্ছে...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAllSynced) Icons.Default.CloudDone else Icons.Default.Sync,
                            contentDescription = null,
                            tint = if (isAllSynced) Color(0xFF10B981) else Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isEnglish) (if (isAllSynced) "Cloud Synced" else "Syncing") else (if (isAllSynced) "সিঙ্কড" else "সিঙ্ক হচ্ছে"),
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAllSynced) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                    }
                }
            }

            // =========================================================================
            // ২. ফিল্টার ট্যাব রো: This Day, This Week, This Month, This Year
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    TrackerPeriod.DAY to (if (isEnglish) "This Day" else "আজকের দিন"),
                    TrackerPeriod.WEEK to (if (isEnglish) "This Week" else "এই সপ্তাহ"),
                    TrackerPeriod.MONTH to (if (isEnglish) "This Month" else "এই মাস"),
                    TrackerPeriod.YEAR to (if (isEnglish) "This Year" else "এই বছর")
                ).forEach { (period, label) ->
                    val isSelected = selectedPeriod == period
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) (if (isDark) Color(0xFF17382D) else Color(0xFFE6F4EA)) else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.5.dp, emeraldTeal) else BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPeriod = period }
                    ) {
                        Text(
                            text = label,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) (if (isDark) amberAccent else Color(0xFF064E3B)) else textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // =========================================================================
            // ৩. মূল কনটেন্ট (Day ভিউ বনাম Week/Month/Year সামারি ভিউ)
            // =========================================================================
            when (selectedPeriod) {
                TrackerPeriod.DAY -> {
                    // স্ক্রল্যাবল এরিয়া: ৫টি নামাজের কার্ড + কুইক টগল স্ট্রিপ + মাসিক ক্যালেন্ডার
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // ক. ৫টি নামাজের স্ট্যাটাস ও প্রগ্রেস কার্ড
                        items(prayersList) { pInfo ->
                            val isCompleted = currentDayRecord.isPrayerCompleted(pInfo.key)
                            val isWaqtStarted = waqtStatusMap[pInfo.key] ?: true

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCompleted) emeraldTeal.copy(alpha = 0.5f) else cardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val res = PrayerTrackerRepository.togglePrayer(context, pInfo.key, selectedDateStr, isEnglish)
                                        if (!res.first && res.second.isNotEmpty()) {
                                            Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // বামে আইকন
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCompleted) emeraldTeal.copy(alpha = 0.25f)
                                                else if (!isWaqtStarted) (if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9))
                                                else amberAccent.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (!isWaqtStarted) Icons.Default.Lock else pInfo.icon,
                                            contentDescription = null,
                                            tint = if (isCompleted) Color(0xFF10B981)
                                            else if (!isWaqtStarted) Color(0xFF94A3B8)
                                            else amberAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    // মাঝে: নাম এবং ফুল-উইথ প্রগ্রেস বার
                                    Column(modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(26.dp)
                                                .clip(RoundedCornerShape(13.dp))
                                                .background(
                                                    if (isCompleted) emeraldTeal
                                                    else if (!isWaqtStarted) (if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9))
                                                    else (if (isDark) Color(0xFF1F3D32) else Color(0xFFE2E8F0))
                                                )
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (isEnglish) pInfo.nameEn else pInfo.nameBn,
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCompleted) Color.White else textPrimary
                                                )

                                                // পার্সেন্টেজ ব্যাজ
                                                if (isCompleted) {
                                                    Text(
                                                        text = "100%".toLang(isEnglish),
                                                        fontFamily = fonts.kalpurush,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFA7F3D0)
                                                    )
                                                } else if (!isWaqtStarted) {
                                                    Text(
                                                        text = if (isEnglish) "Upcoming" else "অপেক্ষমান",
                                                        fontFamily = fonts.kalpurush,
                                                        fontSize = 10.5.sp,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                } else {
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = amberAccent.copy(alpha = 0.35f)
                                                    ) {
                                                        Text(
                                                            text = "0%".toLang(isEnglish),
                                                            fontFamily = fonts.kalpurush,
                                                            fontSize = 10.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = amberAccent,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    // ডানে রেশিও: 1/1 বা 0/1
                                    Text(
                                        text = (if (isCompleted) "1/1" else "0/1").toLang(isEnglish),
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCompleted) Color(0xFF10B981) else textSecondary
                                    )
                                }
                            }
                        }

                        // খ. ৫টি কুইক সুইচ পিল রো (রেফারেন্স ছবির মতো)
                        item {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                prayersList.forEach { pInfo ->
                                    val isCompleted = currentDayRecord.isPrayerCompleted(pInfo.key)
                                    val isWaqtStarted = waqtStatusMap[pInfo.key] ?: true

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable {
                                            val res = PrayerTrackerRepository.togglePrayer(context, pInfo.key, selectedDateStr, isEnglish)
                                            if (!res.first && res.second.isNotEmpty()) {
                                                Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = if (isEnglish) pInfo.nameEn else pInfo.nameBn,
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isCompleted) (if (isDark) amberAccent else Color(0xFF059669)) else textSecondary
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        // পিল সুইচ ডিজাইন
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = if (isCompleted) (if (isDark) Color(0xFF13362A) else Color(0xFFD1FAE5))
                                                    else if (!isWaqtStarted) (if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9))
                                                    else (if (isDark) Color(0xFF241C10) else Color(0xFFFEF3C7)),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isCompleted) Color(0xFF10B981).copy(alpha = 0.6f)
                                                else if (!isWaqtStarted) (if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1))
                                                else amberAccent.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.size(width = 54.dp, height = 26.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = if (isCompleted) Arrangement.SpaceBetween else Arrangement.Center
                                            ) {
                                                if (isCompleted) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color(0xFF10B981),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isDark) Color(0xFFE2E8F0) else Color(0xFF10B981))
                                                    )
                                                } else if (!isWaqtStarted) {
                                                    Icon(
                                                        imageVector = Icons.Default.Lock,
                                                        contentDescription = null,
                                                        tint = Color(0xFF94A3B8),
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(15.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isDark) Color(0xFFE2E8F0) else Color(0xFF94A3B8))
                                                    )
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(
                                                        text = "!",
                                                        fontFamily = fonts.kalpurush,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = amberAccent
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // গ. নিচের মাসিক ক্যালেন্ডার কার্ড
                        item {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                                color = cardBg,
                                border = BorderStroke(1.dp, cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    // বছর নেভিগেশন: < 2026 >
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(onClick = { calendarYear-- }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = textSecondary, modifier = Modifier.size(18.dp))
                                        }
                                        Text(
                                            text = calendarYear.toLang(isEnglish),
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        IconButton(onClick = { calendarYear++ }, modifier = Modifier.size(28.dp)) {
                                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = textSecondary, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    // মাস নেভিগেশন: < OCTOBER / অক্টোবর >
                                    val monthNamesEn = listOf("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")
                                    val monthNamesBn = listOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (calendarMonth > 0) calendarMonth--
                                                else {
                                                    calendarMonth = 11
                                                    calendarYear--
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = textSecondary, modifier = Modifier.size(18.dp))
                                        }
                                        Text(
                                            text = if (isEnglish) monthNamesEn[calendarMonth] else monthNamesBn[calendarMonth],
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) amberAccent else Color(0xFF059669),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(
                                            onClick = {
                                                if (calendarMonth < 11) calendarMonth++
                                                else {
                                                    calendarMonth = 0
                                                    calendarYear++
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = textSecondary, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    // সপ্তাহের দিনগুলোর হেডার: Su Mo Tu We Th Fr Sa
                                    val weekDaysEn = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                                    val weekDaysBn = listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহ", "শুক্র", "শনি")

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        for (i in 0..6) {
                                            val isFri = i == 5
                                            Text(
                                                text = if (isEnglish) weekDaysEn[i] else weekDaysBn[i],
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isFri) (if (isDark) amberAccent else Color(0xFF059669)) else textSecondary,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // ক্যালেন্ডার গ্রিড হিসাব প্রি-ক্যালকুলেশন (রি-কম্পোজিশনে কোনো অ্যালোকেশন ছাড়া দ্রুত রেন্ডারিং)
                                    val calendarDayItems = remember(calendarYear, calendarMonth) {
                                        val calHelper = Calendar.getInstance().apply {
                                            set(Calendar.YEAR, calendarYear)
                                            set(Calendar.MONTH, calendarMonth)
                                            set(Calendar.DAY_OF_MONTH, 1)
                                        }
                                        val firstDayOfWeek = calHelper.get(Calendar.DAY_OF_WEEK) - 1 // 0: Sunday .. 6: Saturday
                                        val daysInMonth = calHelper.getActualMaximum(Calendar.DAY_OF_MONTH)
                                        val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7
                                        val list = ArrayList<CalendarDayItem?>(totalCells)
                                        for (cellIndex in 0 until totalCells) {
                                            val dayNum = cellIndex - firstDayOfWeek + 1
                                            if (dayNum in 1..daysInMonth) {
                                                val mStr = if (calendarMonth + 1 < 10) "0${calendarMonth + 1}" else "${calendarMonth + 1}"
                                                val dStr = if (dayNum < 10) "0$dayNum" else "$dayNum"
                                                val dateStr = "$calendarYear-$mStr-$dStr"
                                                val isFri = (cellIndex % 7) == 5
                                                list.add(CalendarDayItem(dayNum, dateStr, isFri))
                                            } else {
                                                list.add(null)
                                            }
                                        }
                                        list
                                    }

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        val totalRows = calendarDayItems.size / 7
                                        for (row in 0 until totalRows) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp),
                                                horizontalArrangement = Arrangement.SpaceAround
                                            ) {
                                                for (col in 0..6) {
                                                    val itemIndex = row * 7 + col
                                                    val cellItem = calendarDayItems.getOrNull(itemIndex)
                                                    if (cellItem != null) {
                                                        val isSelected = cellItem.dateStr == selectedDateStr
                                                        val rec = recordsMap[cellItem.dateStr]
                                                        val isAllDone = rec?.isAllCompleted == true
                                                        val isPartial = (rec?.completedCount ?: 0) > 0 && !isAllDone

                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .aspectRatio(1f)
                                                                .clip(CircleShape)
                                                                .background(if (isSelected) amberAccent else Color.Transparent)
                                                                .clickable { selectedDateStr = cellItem.dateStr },
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text(
                                                                    text = cellItem.dayNum.toLang(isEnglish),
                                                                    fontFamily = fonts.kalpurush,
                                                                    fontSize = 12.5.sp,
                                                                    fontWeight = if (isSelected || isAllDone) FontWeight.Bold else FontWeight.Normal,
                                                                    color = if (isSelected) Color(0xFF091712)
                                                                    else if (cellItem.isFri) (if (isDark) amberAccent else Color(0xFF059669))
                                                                    else if (isAllDone) Color(0xFF10B981)
                                                                    else textPrimary
                                                                )

                                                                // নামাজ সম্পন্ন হওয়ার ডট ইন্ডিকেটর
                                                                if (!isSelected && (isAllDone || isPartial)) {
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(3.5.dp)
                                                                            .clip(CircleShape)
                                                                            .background(if (isAllDone) Color(0xFF10B981) else amberAccent)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        Spacer(modifier = Modifier.weight(1f))
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

                TrackerPeriod.WEEK, TrackerPeriod.MONTH, TrackerPeriod.YEAR -> {
                    // খ. সপ্তাহ/মাস/বছরের পূর্ণাঙ্গ স্ট্যাটিস্টিকস কার্ড
                    val stats = remember(selectedPeriod, recordsMap) {
                        PrayerTrackerRepository.getStats(selectedPeriod)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(1.dp, cardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = when (selectedPeriod) {
                                            TrackerPeriod.WEEK -> if (isEnglish) "Weekly Performance" else "সাপ্তাহিক নামাজ পর্যালোচনা"
                                            TrackerPeriod.MONTH -> if (isEnglish) "Monthly Summary" else "মাসিক নামাজ পর্যালোচনা"
                                            TrackerPeriod.YEAR -> if (isEnglish) "Yearly Consistency" else "বার্ষিক নামাজ ট্র্যাকিং"
                                            else -> ""
                                        },
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) amberAccent else Color(0xFF059669)
                                    )

                                    Spacer(Modifier.height(14.dp))

                                    // বড় শতাংশ
                                    Text(
                                        text = "${(stats.completionPercent * 100).toInt().toLang(isEnglish)}%",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399)
                                    )

                                    Text(
                                        text = if (isEnglish) "${stats.totalCompleted} of ${stats.totalRequired} prayers performed" else "${stats.totalCompleted.toBn()} / ${stats.totalRequired.toBn()} ওয়াক্ত আদায় হয়েছে",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.5.sp,
                                        color = textSecondary
                                    )

                                    Spacer(Modifier.height(14.dp))

                                    LinearProgressIndicator(
                                        progress = { stats.completionPercent },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF10B981),
                                        trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                                    )

                                    Spacer(Modifier.height(18.dp))

                                    // ৩টি স্ট্যাটাস বক্স
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, cardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = stats.allPrayedDaysCount.toLang(isEnglish),
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textPrimary
                                                )
                                                Text(
                                                    text = if (isEnglish) "Full Days" else "পূর্ণাঙ্গ দিন",
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 11.sp,
                                                    color = textSecondary
                                                )
                                            }
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, cardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = stats.streakDays.toLang(isEnglish),
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = amberAccent
                                                )
                                                Text(
                                                    text = if (isEnglish) "Day Streak" else "ধারাবাহিক দিন",
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 11.sp,
                                                    color = textSecondary
                                                )
                                            }
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        val missedCount = (stats.totalRequired - stats.totalCompleted).coerceAtLeast(0)
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, cardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = missedCount.toLang(isEnglish),
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (missedCount > 0) Color(0xFFF87171) else Color(0xFF34D399)
                                                )
                                                Text(
                                                    text = if (isEnglish) "Missed / Qaza" else "কাজা / বাদ",
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 11.sp,
                                                    color = textSecondary
                                                )
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
    }
}


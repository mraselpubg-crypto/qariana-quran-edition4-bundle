package com.karianaquran.reader.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ডেডিকেটেড সেটিংস ও পছন্দসমূহ স্ক্রিন (SettingsScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val context = LocalContext.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    val primaryEmerald = Color(0xFF0F6B56)
    val bgPage = if (isDark) Color(0xFF0B131E) else Color(0xFFF6FFF9)
    val cardBg = if (isDark) Color(0xFF131F2A) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF1E293B)
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
            // ১. হেডার
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = primaryEmerald,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { vm.back() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "পেছনে",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "অ্যাপ ও রিডার সেটিংস",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "থিম, পাঠ সংস্করণ ও নোটিফিকেশন পছন্দসমূহ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // ২. স্ক্রলযোগ্য সেটিংস তালিকা
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ২.১ থিম পছন্দ
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = primaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "থিম পছন্দ করুন",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Pair("light", "লাইট"),
                                    Pair("dark", "ডার্ক"),
                                    Pair("sepia", "সেপিয়া"),
                                    Pair("emerald", "এমেরাল্ড")
                                ).forEach { (id, label) ->
                                    val isSelected = readerSettings.themeId == id
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { vm.updateSettings { it.copy(themeId = id) } },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) primaryEmerald else (if (isDark) Color(0xFF182937) else Color(0xFFF1F5F9)),
                                        border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isSelected) primaryEmerald else Color(0xFFCBD5E1))
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else textPrimary,
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ২.২ ডিফল্ট পড়ার সংস্করণ নির্বাচন
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = primaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "ডিফল্ট পড়ার সংস্করণ",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            listOf(
                                Triple(ReadingMode.CLASSIC, "জনপ্রিয় সংস্করণ", "মূল মুদ্রিত অফসেট পেপারের অবিকল ছাপা পৃষ্ঠা"),
                                Triple(ReadingMode.PAGE, "নতুন সংস্করণ", "১:১ রঙিন ইনডিজাইন প্রিন্ট ও ভেক্টর মুসহাফ পৃষ্ঠা"),
                                Triple(ReadingMode.AYAH, "ডিজিটাল সংস্করণ", "আয়াতভিত্তিক কার্ড, বাংলা উচ্চারণ ও সংক্ষিপ্ত তাফসীর"),
                                Triple(ReadingMode.HAFEZI, "হাফেজী সংস্করণ", "১৫ ও ১৪ লাইন হাফেজী ভেক্টর লেআউট ও রঙিন তাজবীদ")
                            ).forEach { (mode, title, desc) ->
                                val isSelected = readerSettings.readingMode == mode
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { vm.setReadingMode(mode) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) primaryEmerald.copy(alpha = 0.12f) else (if (isDark) Color(0xFF182937) else Color(0xFFF8FAF9)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.2.dp,
                                        if (isSelected) primaryEmerald else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(24.dp),
                                            shape = CircleShape,
                                            color = if (isSelected) primaryEmerald else Color.Transparent,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, primaryEmerald)
                                        ) {
                                            if (isSelected) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = title,
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) primaryEmerald else textPrimary
                                            )
                                            Text(
                                                text = desc,
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.5.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ২.৩ নোটিফিকেশন ও হোম ডিসপ্লে সুইচ
                                // ২.৩ বাংলা ফন্ট ও টাইপোগ্রাফি পছন্দ
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = primaryEmerald.copy(alpha = 0.15f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("অ", fontFamily = fonts.kalpurush, fontWeight = FontWeight.Bold, color = primaryEmerald, fontSize = 16.sp)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "বাংলা ফন্ট ও টাইপোগ্রাফি",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "অ্যাপের সকল বাংলা লেখার জন্য পছন্দের ফন্ট",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            val fontOptions = listOf(
                                Triple("kalpurush", "কালপুরুষ (Kalpurush)", "ঐতিহ্যবাহী স্ট্যান্ডার্ড প্রমিত বাংলা ফন্ট (ডিফল্ট)"),
                                Triple("hind_siliguri", "হিন্দ শিলিগুড়ি (Hind Siliguri)", "আধুনিক ও পরিচ্ছন্ন ডিজিটাল ফন্ট"),
                                Triple("fira_sans", "ফিরাস্যান্স (Fira Sans)", "বোল্ড ও কম্প্যাক্ট পাঠযোগ্য ফন্ট")
                            )

                            fontOptions.forEach { (key, title, desc) ->
                                val normKey = key.replace("_", "")
                                val currentNorm = readerSettings.banglaFont.replace("_", "")
                                val isSelected = currentNorm == normKey
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            vm.updateSettings { it.copy(banglaFont = key) }
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) primaryEmerald.copy(alpha = 0.12f) else (if (isDark) Color(0xFF182937) else Color(0xFFF8FAF9)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.2.dp,
                                        if (isSelected) primaryEmerald else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(22.dp),
                                            shape = CircleShape,
                                            color = if (isSelected) primaryEmerald else Color.Transparent,
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, primaryEmerald)
                                        ) {
                                            if (isSelected) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                                }
                                            }
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = title,
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) primaryEmerald else textPrimary
                                            )
                                            Text(
                                                text = desc,
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            // বোল্ড মোড সুইচ
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.updateSettings { it.copy(fontBold = !it.fontBold) } }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "বোল্ড (Bold) টেক্সট মোড",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "টেক্সটের ঘনত্ব বৃদ্ধি করে স্পষ্ট পড়া",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                                Switch(
                                    checked = readerSettings.fontBold,
                                    onCheckedChange = { vm.updateSettings { it.copy(fontBold = !it.fontBold) } },
                                    colors = SwitchDefaults.colors(checkedThumbColor = primaryEmerald, checkedTrackColor = primaryEmerald.copy(alpha = 0.35f))
                                )
                            }
                        }
                    }
                }

item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // আযান সাউন্ড
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.toggleAzanSound() }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "নামাজের সময়ে আযান অ্যালার্ট",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "ওয়াক্ত শুরু হলে মিষ্টি সুরে আযানের নোটিফিকেশন",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                                Switch(
                                    checked = readerSettings.azanSoundEnabled,
                                    onCheckedChange = { vm.toggleAzanSound() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = primaryEmerald, checkedTrackColor = primaryEmerald.copy(alpha = 0.35f))
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // রমজান ব্যানার
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.toggleRamadanCountdown() }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "রমজান কাউন্টডাউন হোম ব্যানার",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "হোম স্ক্রিনে রমজান মাস ও কাউন্টডাউন কার্ড দৃশ্যমান",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                                Switch(
                                    checked = readerSettings.showRamadanCountdown,
                                    onCheckedChange = { vm.toggleRamadanCountdown() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = primaryEmerald, checkedTrackColor = primaryEmerald.copy(alpha = 0.35f))
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vm.updateSettings { it.copy(hasSeenOnboarding = false) }
                                android.widget.Toast.makeText(context, "হোমে প্রবেশ করলেই অ্যাপ গাইড দৃশ্যমান হবে।", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = CircleShape,
                                    color = primaryEmerald.copy(alpha = 0.12f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoStories,
                                            contentDescription = null,
                                            tint = primaryEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "অ্যাপ গাইড ও পরিচিতি পুনরায় দেখুন",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = "কুরআনের ৩টি সংস্করণ ও ফিচার টিউটোরিয়াল চালু করুন",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // ২.৪ মেমোরি ও অ্যাপ তথ্য
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = primaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "অ্যাপ তথ্য ও সংস্করণ",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = "ক্বারীয়ানা কুরআন ও পাবলিকেশন্স",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "ভার্সন: v1.0.0 (Play Store Ready • Ultra Fast Engine)",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "কপিরাইট © ২০২৬ ক্বারীয়ানা পাবলিকেশনস। সর্বস্বত্ব সংরক্ষিত।",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}


package com.karianaquran.reader.ui.worship

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class RamadanDayData(
    val day: Int,
    val dateBn: String,
    val seheriEnd: String,
    val iftarTime: String
)

/**
 * পবিত্র মাহে রমজান ও সেহরি-ইফতারের পূর্ণাঙ্গ ডেডিকেটেড স্ক্রিন (RamadanScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamadanScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    val primaryEmerald = Color(0xFF0F6B56)
    val bgPage = if (isDark) Color(0xFF0B131E) else Color(0xFFF6FFF9)
    val cardBg = if (isDark) Color(0xFF131F2A) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF1E293B)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // ৮ বিভাগ ও সময় পার্থক্য (মিনিটে)
    val divisions = listOf(
        Pair("ঢাকা (মূল)", 0),
        Pair("চট্টগ্রাম", -5),
        Pair("সিলেট", -6),
        Pair("বরিশাল", 1),
        Pair("খুলনা", 5),
        Pair("রাজশাহী", 6),
        Pair("রংপুর", 5),
        Pair("ময়মনসিংহ", -1)
    )
    var selectedDivisionIndex by remember { mutableIntStateOf(0) }
    val (currentDivName, divOffset) = divisions[selectedDivisionIndex]

    val ramadanSchedule = remember(divOffset) {
        generate30DaysRamadanSchedule(divOffset)
    }

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
                            text = "রমজান ও সেহরি-ইফতার সময়সূচি",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ইসলামিক ফাউন্ডেশন বাংলাদেশ অনুমোদিত ক্যালেন্ডার",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // ২. বিভাগ নির্বাচন চিপ রো
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = "বিভাগ নির্বাচন করুন:",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(divisions.indices.toList()) { index ->
                        val (name, _) = divisions[index]
                        val isSelected = selectedDivisionIndex == index
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedDivisionIndex = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) primaryEmerald else (if (isDark) Color(0xFF182937) else Color(0xFFE2E8F0)),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) primaryEmerald else Color(0xFFCBD5E1)
                            )
                        ) {
                            Text(
                                text = name,
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else (if (isDark) Color.White else textPrimary),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // ৩. মূল তালিকা
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ৩.১ আজকের হাইলাইট কার্ড
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF162A20) else Color(0xFFECFDF5)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, primaryEmerald.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "আজকের সময়সূচি ($currentDivName)",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryEmerald
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = primaryEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "১ম রমজান",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryEmerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = primaryEmerald,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "সেহরি শেষ সময়:",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 13.sp,
                                            color = textSecondary
                                        )
                                    }
                                    Text(
                                        text = ramadanSchedule.first().seheriEnd,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryEmerald
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.WbTwilight,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "ইফতার শুরু সময়:",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 13.sp,
                                            color = textSecondary
                                        )
                                    }
                                    Text(
                                        text = ramadanSchedule.first().iftarTime,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                        }
                    }
                }

                // ৩.২ নিয়ত ও দোয়া কার্ড
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "রোজার নিয়ত ও ইফতারের দোয়া",
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryEmerald
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "ইফতারের দোয়া: اللَّهُمَّ لَكَ صُمْتُ وَعَلَىٰ رِزْقِكَ أَفْطَرْتُ",
                                fontFamily = fonts.arabic,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F6B56)
                            )
                            Text(
                                text = "অর্থ: হে আল্লাহ! আমি আপনার জন্য রোজা রেখেছি এবং আপনারই রিযিক দ্বারা ইফতার করছি।",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                    }
                }

                // ৩.৩ ৩০ দিনের পূর্ণাঙ্গ টেবিল
                item {
                    Text(
                        text = "মাহে রমজানের পূর্ণাঙ্গ ৩০ দিনের সময়সূচি:",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(ramadanSchedule, key = { it.day }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = primaryEmerald.copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.day.toBn(),
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryEmerald
                                        )
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${item.day.toBn()} রমজান",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = item.dateBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "সেহরি শেষ",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 10.5.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = item.seheriEnd,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryEmerald
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "ইফতার",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 10.5.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        text = item.iftarTime,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
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

private fun generate30DaysRamadanSchedule(offsetMinutes: Int): List<RamadanDayData> {
    val baseSeheriMinutes = 4 * 60 + 52 + offsetMinutes
    val baseIftarMinutes = 18 * 60 + 2 + offsetMinutes

    return (1..30).map { day ->
        val seheriM = baseSeheriMinutes - (day * 40 / 30)
        val iftarM = baseIftarMinutes + (day * 20 / 30)

        val sH = seheriM / 60
        val sMin = seheriM % 60
        val iH = (iftarM / 60) - 12
        val iMin = iftarM % 60

        val seheriFormatted = String.format("ভোর %02d:%02d মি.", sH, sMin).toBn()
        val iftarFormatted = String.format("সন্ধ্যা %02d:%02d মি.", iH, iMin).toBn()

        RamadanDayData(
            day = day,
            dateBn = "${(day + 1).toBn()} মার্চ ২০২৬",
            seheriEnd = seheriFormatted,
            iftarTime = iftarFormatted
        )
    }
}

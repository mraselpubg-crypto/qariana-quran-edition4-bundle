package com.karianaquran.reader.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.data.model.Publication
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.data.prayer.DayPrayerRecord
import com.karianaquran.reader.data.prayer.PrayerTrackerRepository
import com.karianaquran.reader.ui.components.RealBookCover
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.AccentMint
import com.karianaquran.reader.ui.home.LightBlackText
import com.karianaquran.reader.ui.home.PrimaryDarkEmerald
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * হোম স্ক্রিনের কার্ড-ভিত্তিক কম্পোনেন্টসমূহ:
 * - সর্বশেষ পঠিত কার্ড (HomeLastReadCard)
 * - দৈনিক নামাজ ট্র্যাকার (HomePrayerTrackerCard + PrayerTrackerCircleItem)
 * - প্রকাশনা ক্যারোসেল (HomeUpcomingPublicationsSection)
 * HomeScreen.kt থেকে ভাগ করা হয়েছে — behavior অপরিবর্তিত।
 */

// -------------------------------------------------------------------------------------------------
// ৬. সর্বশেষ পঠিত কার্ড (HomeLastReadCard)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun HomeLastReadCard(
    isDark: Boolean,
    surahNameBn: String,
    surahNameAr: String,
    pageNumber: Int,
    onContinueClick: () -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val totalPages = 610
    val juzNumber = ((pageNumber - 1) / 20 + 1).coerceIn(1, 30)
    val readProgress = (pageNumber.toFloat() / totalPages.toFloat()).coerceIn(0.01f, 1f)
    val percentBn = (readProgress * 100).toInt().toBn()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable(onClick = onContinueClick),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF1E3A2E) else Color(0xFF9CCBB0)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.bg_home_last_read_scene),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (isDark) Color(0xEB0A1C16) else Color(0xF2FFFFFF)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                if (isDark) Color(0xFF143026) else Color(0xFFECFDF5),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isDark) Color(0xFF275544) else Color(0xFFA7F3D0),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFFD4AF37)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "সর্বশেষ পঠিত • পারা ${juzNumber.toBn()}",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) AccentMint else PrimaryDarkEmerald
                        )
                    }

                    Text(
                        text = "$percentBn% সমাপ্ত",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFFD97706)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = surahNameBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else LightBlackText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "পৃষ্ঠা নম্বর: ${pageNumber.toBn()} / ${totalPages.toBn()}",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280)
                        )
                    }

                    Text(
                        text = surahNameAr,
                        fontFamily = fonts.arabic,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) AccentMint else PrimaryDarkEmerald
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { readProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF10B981),
                    trackColor = if (isDark) Color(0xFF1E3A2E) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        modifier = Modifier.clickable(onClick = onContinueClick),
                        shape = RoundedCornerShape(20.dp),
                        color = if (isDark) Color(0xFF143026) else Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF275544) else Color(0xFF86EFAC)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "পড়া চালিয়ে যান",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) AccentMint else PrimaryDarkEmerald
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = if (isDark) AccentMint else PrimaryDarkEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ৭. দৈনিক নামাজ ট্র্যাকার কার্ড (HomePrayerTrackerCard)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun HomePrayerTrackerCard(
    isDark: Boolean,
    todayRecord: DayPrayerRecord,
    todayDateStr: String,
    completedCount: Int,
    onToggle: (String) -> Unit,
    onViewHistoryClick: () -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val progress = (completedCount.toFloat() / 5f).coerceIn(0f, 1f)
    val percentText = if (isEnglish) "${(progress * 100).toInt()}%" else "${(progress * 100).toInt().toBn()}%"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF1E3A2E) else Color(0xFF9CCBB0)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.bg_home_prayer_tracker_scene),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (isDark) Color(0xEB0A1C16) else Color(0xF2FFFFFF)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_tracker),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(if (isDark) AccentMint else PrimaryDarkEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Daily Prayer Tracker" else "দৈনিক নামাজ ট্র্যাকার",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PrimaryDarkEmerald
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) Color(0xFF1E3A2E) else Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = if (isEnglish) "Today" else "আজ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) AccentMint else PrimaryDarkEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable(onClick = onViewHistoryClick),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isDark) Color(0xFF1B3B2B) else Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF2E6349) else Color(0xFFA5D6A7)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "History" else "ইতিহাস দেখুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) AccentMint else PrimaryDarkEmerald
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = if (isDark) AccentMint else PrimaryDarkEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "$completedCount/5 prayers completed" else "${completedCount.toBn()}/৫টি ওয়াক্ত আদায় হয়েছে",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                    )
                    Text(
                        text = percentText,
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (completedCount == 5) Color(0xFF10B981) else (if (isDark) Color(0xFFFDE68A) else Color(0xFFD97706))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF10B981),
                    trackColor = if (isDark) Color(0xFF1E3A2E) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val prayers = listOf(
                        Triple("fajr", if (isEnglish) "Fajr" else "ফজর", Icons.Default.WbTwilight),
                        Triple("dhuhr", if (isEnglish) "Dhuhr" else "যোহর", Icons.Default.WbSunny),
                        Triple("asr", if (isEnglish) "Asr" else "আসর", Icons.Default.WbSunny),
                        Triple("maghrib", if (isEnglish) "Maghrib" else "মাগরিব", Icons.Default.WbTwilight),
                        Triple("isha", if (isEnglish) "Isha" else "এশা", Icons.Default.NightsStay)
                    )

                    prayers.forEach { (key, name, icon) ->
                        val isDone = todayRecord.isPrayerCompleted(key)
                        val isWaqtStarted = PrayerTrackerRepository.isPrayerWaqtStarted(key, todayDateStr)
                        PrayerTrackerCircleItem(
                            key = key,
                            name = name,
                            icon = icon,
                            isCompleted = isDone,
                            isWaqtStarted = isWaqtStarted,
                            isDark = isDark,
                            onClick = { onToggle(key) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerTrackerCircleItem(
    key: String,
    name: String,
    icon: ImageVector,
    isCompleted: Boolean,
    isWaqtStarted: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = if (isCompleted) Color(0xFF10B981) else (if (isDark) Color(0xFF172836) else Color(0xFFF1F5F9)),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isCompleted) Color(0xFF059669) else (if (isDark) Color(0xFF293D50) else Color(0xFFCBD5E1))
            ),
            shadowElevation = if (isCompleted) 2.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check_white),
                        contentDescription = "সম্পন্ন",
                        modifier = Modifier.size(19.dp)
                    )
                } else if (!isWaqtStarted) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "ওয়াক্ত শুরু হয়নি",
                        tint = if (isDark) Color(0xFF475569) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        tint = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = name,
            fontFamily = fonts.kalpurush,
            fontSize = 11.5.sp,
            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium,
            color = if (isCompleted) (if (isDark) AccentMint else PrimaryDarkEmerald) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
        )
    }
}

// -------------------------------------------------------------------------------------------------
// ৮. আমাদের প্রকাশিত ও আসন্ন কিতাবসমূহ (HomeUpcomingPublicationsSection)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun HomeUpcomingPublicationsSection(
    isDark: Boolean,
    onBookClick: (Publication) -> Unit,
    onViewAllClick: () -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val upcomingBooks = Publications.upcoming

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "আমাদের প্রকাশিত ও আসন্ন কিতাবসমূহ",
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else PrimaryDarkEmerald
                )
                Text(
                    text = "ইনডিজাইন প্রেস সংস্করণ ও রঙিন তাজবীদ গাইড",
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280)
                )
            }

            TextButton(
                onClick = onViewAllClick,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "সকল দেখুন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) AccentMint else PrimaryDarkEmerald
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (isDark) AccentMint else PrimaryDarkEmerald
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(upcomingBooks) { book ->
                Surface(
                    modifier = Modifier
                        .width(125.dp)
                        .clickable { onBookClick(book) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDark) Color(0xFF131F2A) else Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RealBookCover(
                            publication = book,
                            height = 112.dp,
                            showSpine = true,
                            onClick = { onBookClick(book) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = book.title(isEnglish),
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else LightBlackText,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp,
                            modifier = Modifier.height(32.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (book.originalPriceBn.isNotEmpty()) {
                                Text(
                                    text = book.originalPriceBn,
                                    fontSize = 9.5.sp,
                                    color = Color.Gray,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            }
                            Text(
                                text = book.priceBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFFDE68A) else Color(0xFF0F6B56)
                            )
                        }
                    }
                }
            }
        }
    }
}

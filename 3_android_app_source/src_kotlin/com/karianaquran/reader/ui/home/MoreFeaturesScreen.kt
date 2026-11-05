package com.karianaquran.reader.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.theme.LocalAppFonts

private data class FeatureGridEntry(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val destination: Screen
)

/**
 * সকল ইসলামিক ফিচারের পূর্ণাঙ্গ ডেডিকেটেড স্ক্রিন (MoreFeaturesScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreFeaturesScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"

    val primaryEmerald = Color(0xFF0F6B56)
    val bgPage = if (isDark) Color(0xFF0B131E) else Color(0xFFF6FFF9)
    val cardBg = if (isDark) Color(0xFF131F2A) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF1E293B)
    val textSecondary = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF64748B)

    val features = listOf(
        FeatureGridEntry("আল-কুরআন", "৩০ পারা উচ্চারণ ও অর্থ", R.drawable.ic_quran_new, Screen.QuranOverview(1)),
        FeatureGridEntry(if (isEnglish) "Prayer Times" else "নামাজের সময়", if (isEnglish) "Today's 5 Prayers" else "আজকের ৫ ওয়াক্ত নামাজ", R.drawable.prayericon, Screen.Namaz),
        FeatureGridEntry(if (isEnglish) "Namaz Tracker" else "নামাজ ট্র্যাকার", if (isEnglish) "Daily Log & History" else "দৈনিক হিসাব ও ইতিহাস", R.drawable.ic_tracker, Screen.NamazTracker),
        FeatureGridEntry("আল্লাহর ৯৯ নাম", "আসমাউল হুসনা ও অর্থ", R.drawable.allahicon, Screen.Names),
        FeatureGridEntry("কিবলা কম্পাস", "পবিত্র কাবার সঠিক দিক", R.drawable.compassicon, Screen.Qibla),
        FeatureGridEntry("ডিজিটাল তাসবীহ", "দৈনিক জিকির কাউন্টার", R.drawable.tasbeeicon, Screen.Tasbih),
        FeatureGridEntry("প্রয়োজনীয় দোয়া", "দৈনন্দিন মাসনূন দোয়া", R.drawable.duaaicon, Screen.Dua),
        FeatureGridEntry("সহীহ হাদিস", "প্রামাণ্য হাদিস ভাণ্ডার", R.drawable.ic_hadith, Screen.Hadith),
        FeatureGridEntry("ইসলামের স্তম্ভ", "৫ স্তম্ভ ও ৬ কালেমা", R.drawable.pillarsicon, Screen.Pillars),
        FeatureGridEntry("রমজান ক্যালেন্ডার", "সেহরি ও ইফতার সময়সূচি", R.drawable.ic_ramadantimes2, Screen.Ramadan),
        FeatureGridEntry("সূরা সূচিপত্র", "১১৪টি সূরা ও ৩০ পারা", R.drawable.ic_quran_translation_modern, Screen.SurahIndex(initialTab = 1)),
        FeatureGridEntry("অ্যাপ সেটিংস", "থিম ও প্রিফারেন্স", R.drawable.menu, Screen.Settings)
    )

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
                            text = "সকল ইসলামিক ফিচার",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ক্বারীয়ানা ইসলামিক অ্যাপ সার্ভিসেস",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // ২. গ্রিড ভিউ
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(features) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.navigate(item.destination) },
                        shape = RoundedCornerShape(16.dp),
                        color = cardBg,
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = primaryEmerald.copy(alpha = 0.1f),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Image(
                                        painter = painterResource(id = item.iconRes),
                                        contentDescription = item.title,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = item.title,
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text = item.subtitle,
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

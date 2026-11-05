package com.karianaquran.reader.ui.worship

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ইসলামের ৫টি স্তম্ভ ও ৬টি কালেমার ডেডিকেটেড স্ক্রিন (PillarsScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillarsScreen(
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

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("৫টি মৌলিক স্তম্ভ", "৬টি পবিত্র কালেমা")

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
                            text = "ইসলামের স্তম্ভ ও কালেমা",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "মৌলিক ঈমান, আকীদা ও আমল",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // ২. ট্যাব বার
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = cardBg,
                contentColor = primaryEmerald,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = primaryEmerald,
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            // ৩. কনটেন্ট তালিকা
            if (selectedTabIndex == 0) {
                PillarsListView(isDark = isDark, cardBg = cardBg, primaryEmerald = primaryEmerald, textPrimary = textPrimary, textSecondary = textSecondary)
            } else {
                KalimasListView(isDark = isDark, cardBg = cardBg, primaryEmerald = primaryEmerald, textPrimary = textPrimary, textSecondary = textSecondary)
            }
        }
    }
}

@Composable
private fun PillarsListView(
    isDark: Boolean,
    cardBg: Color,
    primaryEmerald: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val fonts = LocalAppFonts.current
    val pillars = listOf(
        Triple(
            "১. কালিমা (শাহাদাত)",
            "আল্লাহ ছাড়া কোনো ইলাহ নেই এবং মুহাম্মাদ (সা.) তাঁর বান্দা ও রাসূল—এ কথার মৌখিক স্বীকৃতি ও অন্তরে দৃঢ় বিশ্বাস স্থাপন করা।",
            "أَشْهَدُ أَنْ لَا إِلَٰهَ إِلَّا اللَّهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ"
        ),
        Triple(
            "২. সালাত (নামাজ)",
            "দৈনিক ৫ ওয়াক্ত নামাজ (ফজর, যোহর, আসর, মাগরিব, এশা) নির্ধারিত সময়ে রুকু, সিজদা ও খুশু-খুজুর সাথে আদায় করা।",
            "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَوْقُوتًا"
        ),
        Triple(
            "৩. সিয়াম (রোজা)",
            "পবিত্র মাহে রমজানের ৩০ দিন সুবহে সাদিক থেকে সূর্যাস্ত পর্যন্ত পানাহার ও পাপাচার থেকে বিরত থেকে আল্লাহর সন্তুষ্টি অর্জন করা।",
            "يَا أَيُّهَا الَّذِينَ آمَنُوا كُتِبَ عَلَيْكُمُ الصِّيَامُ"
        ),
        Triple(
            "৪. যাকাত",
            "সাহেবে নিসাব মুসলিমদের বাৎসরিক উদ্বৃত্ত ধন-সম্পদের ২.৫% দরিদ্র, মিসকিন ও নির্ধারিত ৮টি খাতের হকদারদের মাঝে বণ্টন করা।",
            "وَأَقِيمُوا الصَّلَاةَ وَآتُوا الزَّكَاةَ"
        ),
        Triple(
            "৫. হজ",
            "শারীরিক ও আর্থিকভাবে সক্ষম প্রাপ্তবয়স্ক মুসলিম নর-নারীর জীবনে অন্তত একবার পবিত্র মক্কা শরীফে উপস্থিত হয়ে হজ পালন করা।",
            "وَلِلَّهِ عَلَى النَّاسِ حِجُّ الْبَيْتِ مَنِ اسْتَطَاعَ إِلَيْهِ سَبِيلًا"
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pillars) { (title, desc, arabicAyah) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = CircleShape,
                            color = primaryEmerald.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = primaryEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = title,
                            fontFamily = fonts.kalpurush,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryEmerald
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = arabicAyah,
                        fontFamily = fonts.arabic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F6B56),
                        lineHeight = 26.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = desc,
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        color = textSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun KalimasListView(
    isDark: Boolean,
    cardBg: Color,
    primaryEmerald: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val fonts = LocalAppFonts.current
    data class KalimaData(val name: String, val ar: String, val translit: String, val meaning: String)

    val kalimas = listOf(
        KalimaData(
            "১. কালেমা তাইয়্যেবা",
            "لَا إِلَٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَّسُولُ اللَّهِ",
            "লা ইলাহা ইল্লাল্লাহু মুহাম্মাদুর রাসুলুল্লাহ।",
            "আল্লাহ ছাড়া কোনো ইলাহ বা উপাস্য নেই, হযরত মুহাম্মদ (সা.) আল্লাহর প্রেরিত রাসূল।"
        ),
        KalimaData(
            "২. কালেমা শাহাদাত",
            "أَشْهَدُ أَنْ لَّا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ وَأَشْهَدُ أَنَّ مُحَمَّدًا عَبْدُهُ وَرَسُولُهُ",
            "আশহাদু আল্লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু ওয়া আশহাদু আন্না মুহাম্মাদান আবদুহু ওয়া রাসূলুহু।",
            "আমি সাক্ষ্য দিচ্ছি যে, আল্লাহ ছাড়া কোনো উপাস্য নেই। তিনি একক, তাঁর কোনো অংশীদার নেই। আমি আরও সাক্ষ্য দিচ্ছি যে, মুহাম্মদ (সা.) তাঁর বান্দা ও প্রেরিত রাসূল।"
        ),
        KalimaData(
            "৩. কালেমা তাওহীদ",
            "لَا إِلَٰهَ إِلَّا أَنْتَ وَاحِدًا لَّا ثَانِيَ لَكَ مُحَمَّدٌ رَّسُولُ اللَّهِ إِمَامُ الْمُتَّقِينَ وَرَسُولُ رَبِّ الْعَالَمِينَ",
            "লা ইলাহা ইল্লা আনতা ওয়াহিদাল লা সানিয়া লাকা মুহাম্মাদুর রাসুলুল্লাহি ইমামুল মুত্তাক্বীন ওয়া রাসূলু রাব্বিল আলামীন।",
            "তুমি ছাড়া কোনো উপাস্য নেই, তুমি এক ও অদ্বিতীয়, তোমার কোনো সমকক্ষ নেই। মুহাম্মদ (সা.) আল্লাহর রাসূল, মুত্তাকীদের পথপ্রদর্শক ও বিশ্বজাহানের প্রতিপালকের প্রেরিত রাসূল।"
        ),
        KalimaData(
            "৪. কালেমা তামজীদ",
            "سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَلَا إِلَٰهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            "সুবহানাল্লাহি ওয়াল হামদুলিল্লাহি ওয়া লা ইলাহা ইল্লাল্লাহু ওয়াল্লাহু আকবার, ওয়া লা হাওলা ওয়া লা কুওয়াতা ইল্লা বিল্লাহিল আলিয়্যিল আযীম।",
            "আল্লাহ পবিত্র, সমস্ত প্রশংসা আল্লাহর জন্য, আল্লাহ ছাড়া কোনো উপাস্য নেই এবং আল্লাহ সর্বশ্রেষ্ঠ। সুমহান ও সর্বোচ্চ মর্যাদাবান আল্লাহর সাহায্য ছাড়া কোনো শক্তি বা সামর্থ্য নেই।"
        ),
        KalimaData(
            "৫. কালেমা আস্তাগফার",
            "أَسْتَغْفِرُ اللَّهَ رَبِّي مِنْ كُلِّ ذَنْبٍ أَذْنَبْتُهُ عَمْدًا أَوْ خَطَأً سِرًّا أَوْ عَلَانِيَةً وَأَتُوبُ إِلَيْهِ مِنَ الذَّنْبِ الَّذِي أَعْلَمُ وَمِنَ الذَّنْبِ الَّذِي لَا أَعْلَمُ إِنَّكَ أَنْتَ عَلَّامُ الْغُيُوبِ",
            "আস্তাগফিরুল্লাহা রাব্বী মিন কুল্লি যাম্বিন আযনাবতুহু আমাদান আও খাতাআন সিররান আও আলানিয়াহ, ওয়া আতূবু ইলাইহি মিনায যামবিল্লাযী আ'লামু ওয়া মিনায যামবিল্লাযী লা আ'লামু, ইন্নাকা আনতা আল্লামুল গুয়ূব।",
            "আমি আমার পালনকর্তা আল্লাহর নিকট ক্ষমা প্রার্থনা করছি আমার সকল পাপ থেকে—যা আমি স্বেচ্ছায় বা অনিচ্ছায়, গোপনে বা প্রকাশ্যে করেছি। আমি তাঁর নিকট তওবা করছি সেই পাপ থেকে যা আমি জানি এবং যা জানি না। নিশ্চয়ই আপনি অদৃশ্য বিষয়ে সর্বজ্ঞাত।"
        ),
        KalimaData(
            "৬. কালেমা রুদ্দে কুফর",
            "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ أَنْ أُشْرِكَ بِكَ شَيْئًا وَأَنَا أَعْلَمُ بِهِ وَأَسْتَغْفِرُكَ لِمَا لَا أَعْلَمُ بِهِ تُبْتُ عَنْهُ وَتَبَرَّأْتُ مِنَ الْكُفْرِ وَالشِّرْكِ وَالْكِذْبِ وَالْغِيبَةِ وَالْبِدْعَةِ وَالنَّمِيمَةِ",
            "আল্লাহুম্মা ইন্নী আউযুবিকা মিন আন উশরিকা বিকা শাইআন ওয়া আনা আ'লামু বিহী, ওয়া আসতাগফিরুকা লিমা লা আ'লামু বিহী, তুবতু আনহু ওয়া তাবাররাতু মিনাল কুফরি ওয়াশ শিরকি ওয়াল কিযবি ওয়াল গীবাতী ওয়াল বিদ'আতি ওয়ান নামীমাহ।",
            "হে আল্লাহ! নিশ্চয়ই আমি আপনার সাথে কোনো কিছুকে অংশীদার করা থেকে আশ্রয় চাই যা আমি জানি। এবং আপনার নিকট ক্ষমা প্রার্থনা করি সেই পাপের জন্য যা আমি জানি না। আমি তা থেকে তওবা করলাম এবং কুফরি, শিরক, মিথ্যা, পরনিন্দা, বিদআত ও চোগলখুরি থেকে নিজেকে মুক্ত ঘোষণা করলাম।"
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(kalimas) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.name,
                        fontFamily = fonts.kalpurush,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryEmerald
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = item.ar,
                        fontFamily = fonts.arabic,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F6B56),
                        lineHeight = 28.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "উচ্চারণ: ${item.translit}",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                        lineHeight = 19.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "অর্থ: ${item.meaning}",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

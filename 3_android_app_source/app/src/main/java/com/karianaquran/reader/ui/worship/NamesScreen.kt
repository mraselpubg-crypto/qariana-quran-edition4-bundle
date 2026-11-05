package com.karianaquran.reader.ui.worship

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

data class AllahName(
    val index: Int,
    val nameAr: String,
    val nameBn: String,
    val meaning: String,
    val benefit: String = "এই পবিত্র নামের জিকিরে অন্তরে প্রশান্তি ও আল্লাহর নৈকট্য লাভ হয়।"
)

/**
 * আল্লাহর ৯৯টি গুণবাচক নামের পূর্ণাঙ্গ ডেডিকেটেড স্ক্রিন (NamesScreen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamesScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    val primaryEmerald = Color(0xFF0F6B56)
    val accentGold = Color(0xFFD4AF37)
    val bgPage = if (isDark) Color(0xFF0B131E) else Color(0xFFF6FFF9)
    val cardBg = if (isDark) Color(0xFF131F2A) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF1E293B)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    var searchQuery by remember { mutableStateOf("") }
    val allNames = remember { getAll99Names() }
    val filteredNames = remember(searchQuery) {
        if (searchQuery.isBlank()) allNames
        else allNames.filter {
            it.nameBn.contains(searchQuery, ignoreCase = true) ||
            it.meaning.contains(searchQuery, ignoreCase = true) ||
            it.index.toString() == searchQuery ||
            it.index.toBn() == searchQuery
        }
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
                            text = "আল্লাহর ৯৯টি পবিত্র নাম",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "আসমাউল হুসনা, উচ্চারণ ও বঙ্গানুবাদ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // ২. সার্চ বার
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "নাম বা অর্থ দিয়ে খুঁজুন (যেমন: আর-রহমান)...",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = primaryEmerald
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "মুছুন")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedBorderColor = primaryEmerald,
                        unfocusedBorderColor = if (isDark) Color(0xFF263D54) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ৩. নামের তালিকা
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredNames, key = { it.index }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = primaryEmerald.copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.index.toBn(),
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryEmerald
                                        )
                                    }
                                }

                                Spacer(Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = item.nameBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = item.meaning,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.5.sp,
                                        color = textSecondary
                                    )
                                }
                            }

                            Text(
                                text = item.nameAr,
                                fontFamily = fonts.arabic,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getAll99Names(): List<AllahName> {
    return listOf(
        AllahName(1, "الرَّحْمَٰنُ", "আর-রহমান", "পরম দয়ালু ও করুণাময়"),
        AllahName(2, "الرَّحِيمُ", "আর-রাহীম", "অতিশয় মেহেরবান ও ক্ষমাশীল"),
        AllahName(3, "الْمَلِكُ", "আল-মালিক", "সর্বভৌম ক্ষমতার অধিকারী ও অধিপতি"),
        AllahName(4, "الْقُدُّوسُ", "আল-কুদ্দুস", "নিষ্কলঙ্ক, অতি পবিত্র"),
        AllahName(5, "السَّلَامُ", "আস-সালাম", "শান্তি প্রদানকারী ও নিরাপত্তাদাতা"),
        AllahName(6, "الْمُؤْمِنُ", "আল-মু'মিন", "নিরাপত্তা ও ঈমান দানকারী"),
        AllahName(7, "الْمُهَيْمِنُ", "আল-মুহাইমিন", "রক্ষাকর্তা ও তত্ত্বাবধায়ক"),
        AllahName(8, "الْعَزِيزُ", "আল-আযীয", "পরাক্রমশালী, অপরাজেয়"),
        AllahName(9, "الْجَبَّارُ", "আল-জাব্বার", "দুর্দণ্ড প্রতাপশালী, সর্বশক্তিমান"),
        AllahName(10, "الْمُتَكَبِّرُ", "আল-মুতাকাব্বির", "সর্বশ্রেষ্ঠ মর্যাদাবান ও অহংকারী"),
        AllahName(11, "الْخَالِقُ", "আল-খালিক্ব", "সৃষ্টিকর্তা ও উদ্ভাবক"),
        AllahName(12, "الْبَارِئُ", "আল-বারী", "সঠিক রূপদানকারী ও নির্মাণকারী"),
        AllahName(13, "الْمُصَوِّرُ", "আল-মুসাওয়ির", "আকৃতি দানকারী ও রূপকার"),
        AllahName(14, "الْغَفَّارُ", "আল-গাফফার", "মহাক্ষমাশীল ও মার্জনাকারী"),
        AllahName(15, "الْقَهَّارُ", "আল-ক্বাহহার", "মহাপ্রতাপশালী ও দমনকারী"),
        AllahName(16, "الْوَهَّابُ", "আল-ওয়াহহাব", "মহাদাতা ও অফুরন্ত কল্যাণকারী"),
        AllahName(17, "الرَّزَّاقُ", "আর-রাযযাক্ব", "রিযিকদাতা ও জীবিকা প্রদানকারী"),
        AllahName(18, "الْفَتَّاحُ", "আল-ফাত্তাহ", "বিজয়দানকারী ও দ্বার উন্মোচনকারী"),
        AllahName(19, "الْعَلِيمُ", "আল-আলীম", "সর্বজ্ঞাত ও মহাজ্ঞানী"),
        AllahName(20, "الْقَابِضُ", "আল-ক্বাবিদ", "সংকোচনকারী ও নিয়ন্ত্রণকারী"),
        AllahName(21, "الْبَاسِطُ", "আল-বাসিত", "প্রশস্তকারী ও প্রসারক"),
        AllahName(22, "الْخَافِضُ", "আল-খাফিদ", "অবনমিতকারী ও অধঃপতনকারী"),
        AllahName(23, "الرَّافِعُ", "আর-রাফি'", "উন্নতকারী ও মর্যাদা দানকারী"),
        AllahName(24, "الْمُعِزُّ", "আল-মু'ইযয", "সম্মানদাতা ও মর্যাদাকারী"),
        AllahName(25, "الْمُذِلُّ", "আল-মুযিল্ল", "অপমানকারী ও হীনকারী"),
        AllahName(26, "السَّمِيعُ", "আস-সামী'", "সর্বশ্রোতা, যিনি সব শোনেন"),
        AllahName(27, "الْبَصِيرُ", "আল-বাসীর", "সর্বদ্রষ্টা, যিনি সব দেখেন"),
        AllahName(28, "الْحَكَمُ", "আল-হাকাম", "মহাবিচারক ও মীমাংসাকারী"),
        AllahName(29, "الْعَدْلُ", "আল-আদল", "পরম ন্যায়পরায়ণ ও সুবিন্যস্ত"),
        AllahName(30, "اللَّطِيفُ", "আল-লাতীফ", "সুক্ষ্মদর্শী, স্নেহময় ও করুণাসিক্ত"),
        AllahName(31, "الْخَبِيرُ", "আল-খাবীর", "সর্ববিষয়ে ওয়াকিফহাল"),
        AllahName(32, "الْحَلِيمُ", "আল-হালীম", "অসীম ধৈর্যশীল ও সহনশীল"),
        AllahName(33, "الْعَظِيمُ", "আল-আযীম", "সুমহান, অসীম মর্যাদাবান"),
        AllahName(34, "الْغَفُورُ", "আল-গাফুর", "পরম ক্ষমাশীল"),
        AllahName(35, "الشَّكُورُ", "আশ-শাকূর", "গুণগ্রাহী ও কৃতজ্ঞতাপ্রিয়"),
        AllahName(36, "الْعَلِيُّ", "আল-আলী", "সর্বোচ্চ, সুউচ্চ মর্যাদাসম্পন্ন"),
        AllahName(37, "الْكَبِيرُ", "আল-কাবীর", "সুমহান ও সর্বশ্রেষ্ঠ"),
        AllahName(38, "الْحَفِيظُ", "আল-হাফীয", "মহাহেফাজতকারী ও সুরক্ষক"),
        AllahName(39, "الْمُقِيتُ", "আল-মুক্বীত", "শক্তি ও পুষ্টি প্রদানকারী"),
        AllahName(40, "الْحَسِيبُ", "আল-হাসীব", "হিসাব গ্রহণকারী"),
        AllahName(41, "الْجَلِيلُ", "আল-জালীল", "মহিমান্বিত ও প্রতাপশালী"),
        AllahName(42, "الْكَرِيمُ", "আল-কারীম", "মহা সম্মানিত ও অসীম দাতা"),
        AllahName(43, "الرَّقِيبُ", "আর-রাক্বীব", "তত্ত্বাবধায়ক ও পর্যবেক্ষণকারী"),
        AllahName(44, "الْمُجِيبُ", "আল-মুজিব", "দোয়া কবুলকারী ও সাড়া দানকারী"),
        AllahName(45, "الْوَاسِعُ", "আল-ওয়াসি'", "সর্বব্যাপী ও অসীম প্রাচুর্যময়"),
        AllahName(46, "الْحَكِيمُ", "আল-হাকীম", "মহাপ্রজ্ঞাবান ও সুবিজ্ঞ"),
        AllahName(47, "الْوَدُودُ", "আল-ওয়াদূদ", "প্রেমময় ও পরম স্নেহশীল"),
        AllahName(48, "الْمَجِيدُ", "আল-মাজীদ", "মহিমান্বিত ও গৌরবান্বিত"),
        AllahName(49, "الْبَاعِثُ", "আল-বা'ইস", "পুনরুজ্জীবিতকারী"),
        AllahName(50, "الشَّهِيدُ", "আশ-শাহীদ", "প্রত্যক্ষদর্শী ও সাক্ষী"),
        AllahName(51, "الْحَقُّ", "আল-হাক্বক্ব", "চিরন্তন সত্য"),
        AllahName(52, "الْوَكِيلُ", "আল-ওয়াকীল", "পরম অভিভাবক ও কার্যনির্বাহক"),
        AllahName(53, "الْقَوِيُّ", "আল-ক্বাবীয়", "মহাশক্তিশালী ও পরাক্রমী"),
        AllahName(54, "الْمَتِينُ", "আল-মাতীন", "সুদৃঢ় ও অটল"),
        AllahName(55, "الْوَلِيُّ", "আল-ওয়ালীয়", "পরম বন্ধু ও সাহায্যকারী"),
        AllahName(56, "الْحَمِيدُ", "আল-হামীদ", "সর্বপ্রশংসিত"),
        AllahName(57, "الْمُحْصِي", "আল-মুহসী", "সবকিছুর পুঙ্খানুপুঙ্খ হিসাবকারী"),
        AllahName(58, "الْمُبْدِئُ", "আল-মুবদি'", "প্রথমবার সৃষ্টিকারী"),
        AllahName(59, "الْمُعِيدُ", "আল-মু'ঈদ", "পুনরায় সৃষ্টিকারী"),
        AllahName(60, "الْمُحْيِي", "আল-মুহয়ী", "জীবনদানকারী"),
        AllahName(61, "الْمُمِيتُ", "আল-মুমীত", "মৃত্যুদানকারী"),
        AllahName(62, "الْحَيُّ", "আল-হাইয়্যু", "চিরঞ্জীব ও শাশ্বত"),
        AllahName(63, "الْقَيُّومُ", "আল-ক্বাইয়্যূম", "চিরধারক ও স্বনির্ভর"),
        AllahName(64, "الْوَاجِدُ", "আল-ওয়াজিদ", "অভাবহীন ও ধনী"),
        AllahName(65, "الْمَاجِدُ", "আল-মাজিদ", "মহিমান্বিত ও মর্যাদাবান"),
        AllahName(66, "الْوَاحِدُ", "আল-ওয়াহিদ", "এক ও অদ্বিতীয়"),
        AllahName(67, "الأَحَدُ", "আল-আহাদ", "একক সত্ত্বা"),
        AllahName(68, "الصَّمَدُ", "আস-সামাদ", "স্বনির্ভর ও অমুখাপেক্ষী"),
        AllahName(69, "الْقَادِرُ", "আল-ক্বাদির", "সর্বশক্তিমান ও সামর্থ্যবান"),
        AllahName(70, "الْمُقْتَدِرُ", "আল-মুক্বতাদির", "পূর্ণ শক্তির অধিকারী"),
        AllahName(71, "الْمُقَدِّمُ", "আল-মুক্বাদ্দিম", "অগ্রসরকারী"),
        AllahName(72, "الْمُؤَخِّرُ", "আল-মুয়াখখির", "বিলম্বকারী ও পশ্চাৎকারী"),
        AllahName(73, "الأَوَّلُ", "আল-আউয়াল", "অনাদি ও আদিহীন"),
        AllahName(74, "الآخِرُ", "আল-আখির", "অনন্ত ও অন্তহীন"),
        AllahName(75, "الظَّاهِرُ", "আয-যাহির", "প্রকাশ্য ও দৃশ্যমান"),
        AllahName(76, "الْبَاطِنُ", "আল-বাতিন", "গুপ্ত ও অদৃশ্য"),
        AllahName(77, "الْوَالِي", "আল-ওয়ালী", "শাসনকর্তা ও পরিচালক"),
        AllahName(78, "الْمُتَعَالِي", "আল-মুতা'আলী", "সর্বোচ্চ সুমহান"),
        AllahName(79, "الْبَرُّ", "আল-বার্র", "পরম কল্যাণকারী ও সদয়"),
        AllahName(80, "التَّوَّابُ", "আত-তাওয়াব", "তওবা কবুলকারী"),
        AllahName(81, "الْمُنْتَقِمُ", "আল-মুনতাক্বিম", "প্রতিশোধ গ্রহণকারী"),
        AllahName(82, "العَفُوُّ", "আল-আফুও", "মার্জনাকারী ও ক্ষান্তকারী"),
        AllahName(83, "الرَّؤُوفُ", "আর-রাউফ", "স্নেহশীল ও কোমল হৃদয়"),
        AllahName(84, "مَالِكُ الْمُلْكِ", "মালিকুল মুলক", "সার্বভৌম রাজত্বের মালিক"),
        AllahName(85, "ذُو الْجَلَالِ وَالإِكْرَامِ", "যুল জালালি ওয়াল ইকরাম", "মহিমা ও মর্যাদার অধিকারী"),
        AllahName(86, "الْمُقْسِطُ", "আল-মুক্বসিত", "ন্যায়পরায়ণ ও সুবিন্যস্তকারী"),
        AllahName(87, "الْجَامِعُ", "আল-জামি'", "একত্রকারী ও সমাবেশকারী"),
        AllahName(88, "الْغَنِيُّ", "আল-গানীয়্যু", "স্বয়ংসম্পূর্ণ ও অমুখাপেক্ষী"),
        AllahName(89, "الْمُغْنِي", "আল-মুগনী", "ধনীকারী ও প্রাচুর্যদাতা"),
        AllahName(90, "الْمَانِعُ", "আল-মানি'", "নিবারণকারী ও রক্ষক"),
        AllahName(91, "الضَّارُّ", "আদ-দার্র", "ক্ষতিসাধনকারী (পরীক্ষার্থে)"),
        AllahName(92, "النَّافِعُ", "আন-নাফি'", "উপকারকারী ও কল্যাণদাতা"),
        AllahName(93, "النُّورُ", "আন-নূর", "জ্যোতি ও আলোকময়"),
        AllahName(94, "الْهَادِي", "আল-হাদী", "পথপ্রদর্শক ও হেদায়াতদাতা"),
        AllahName(95, "الْبَدِيعُ", "আল-বাদী'", "অনুপম ও অপূর্ব স্রষ্টা"),
        AllahName(96, "الْبَاقِي", "আল-বাক্বী", "চিরস্থায়ী ও অবিনশ্বর"),
        AllahName(97, "الْوَارِثُ", "আল-ওয়ারিস", "সকল কিছুর চরম উত্তরাধিকারী"),
        AllahName(98, "الرَّشِيدُ", "আর-রাশীদ", "সঠিক পথনির্দেশক"),
        AllahName(99, "الصَّبُورُ", "আস-সাবূর", "মহাধৈর্যশীল")
    )
}

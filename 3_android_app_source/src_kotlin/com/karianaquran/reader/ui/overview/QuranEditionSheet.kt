package com.karianaquran.reader.ui.overview



import androidx.compose.animation.AnimatedVisibility

import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowForward

import androidx.compose.material.icons.automirrored.filled.MenuBook

import androidx.compose.material.icons.filled.AutoStories

import androidx.compose.material.icons.filled.Bookmark

import androidx.compose.material.icons.filled.CheckCircle

import androidx.compose.material.icons.filled.Close

import androidx.compose.material.icons.filled.FormatColorText

import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.material.icons.filled.Star

import androidx.compose.material.icons.filled.Translate

import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.ModalBottomSheet

import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.SheetState

import androidx.compose.material3.Surface

import androidx.compose.material3.Text

import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.karianaquran.reader.data.ReadingMode

import com.karianaquran.reader.data.model.Publications

import com.karianaquran.reader.ui.AppViewModel

import com.karianaquran.reader.ui.components.toBn

import com.karianaquran.reader.ui.theme.LocalAppFonts

import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun QuranEditionBottomSheet(

    onDismiss: () -> Unit,

    vm: AppViewModel,

    startPage: Int? = null,

    onSelectEdition: ((ReadingMode) -> Unit)? = null,

) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    QuranEditionBottomSheet(

        onDismiss = onDismiss,

        vm = vm,

        startPage = startPage,

        sheetState = sheetState,

        onSelectEdition = onSelectEdition

    )

}



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun QuranEditionBottomSheet(

    onDismiss: () -> Unit,

    vm: AppViewModel,

    startPage: Int? = null,

    sheetState: SheetState,

    onSelectEdition: ((ReadingMode) -> Unit)? = null,

) {

    val fonts = LocalAppFonts.current

    val settings by vm.settings.collectAsState()

    val userData by vm.userData.collectAsState()

    val isDark = settings.themeId == "dark"

    val scope = rememberCoroutineScope()



    val targetPage = startPage ?: userData.lastPage.coerceIn(1, Publications.ORTHO_TOTAL_PAGES)



    val handleSelect: (ReadingMode) -> Unit = { mode ->

        vm.setReadingMode(mode)

        scope.launch { sheetState.hide() }.invokeOnCompletion {

            onDismiss()

            if (onSelectEdition != null) {

                onSelectEdition(mode)

            } else {

                val effPage = if (mode == ReadingMode.CLASSIC && targetPage < Publications.CLASSIC_START_PAGE) {

                    Publications.CLASSIC_START_PAGE

                } else targetPage

                vm.openReader(effPage)

            }

        }

    }



    ModalBottomSheet(

        onDismissRequest = onDismiss,

        sheetState = sheetState,

        containerColor = if (isDark) Color(0xFF0D1612) else Color(0xFFF9FBF9),

        dragHandle = {

            Box(

                modifier = Modifier

                    .padding(vertical = 10.dp)

                    .width(42.dp)

                    .height(4.5.dp)

                    .clip(RoundedCornerShape(3.dp))

                    .background(if (isDark) Color(0xFF2E4036) else Color(0xFFD1DCD5))

            )

        }

    ) {

        Column(

            modifier = Modifier

                .fillMaxWidth()

                .padding(horizontal = 18.dp)

                .padding(bottom = 28.dp)

        ) {

            // ১. হেডার সেকশন

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(

                            modifier = Modifier

                                .size(32.dp)

                                .clip(CircleShape)

                                .background(Color(0xFF0F6B56).copy(alpha = 0.15f)),

                            contentAlignment = Alignment.Center

                        ) {

                            Icon(

                                imageVector = Icons.AutoMirrored.Filled.MenuBook,

                                contentDescription = null,

                                tint = Color(0xFF0F6B56),

                                modifier = Modifier.size(18.dp)

                            )

                        }

                        Spacer(Modifier.width(10.dp))

                        Text(

                            text = "পড়ার সংস্করণ নির্বাচন করুন",

                            fontFamily = fonts.kalpurush,

                            fontSize = 18.sp,

                            fontWeight = FontWeight.Bold,

                            color = if (isDark) Color.White else Color(0xFF0D1F17)

                        )

                    }

                    Spacer(Modifier.height(3.dp))

                    Text(

                        text = "আপনার সুবিধাজনক মুসহাফ বা তাফসীর ভিউ বেছে নিন",

                        fontFamily = fonts.kalpurush,

                        fontSize = 11.5.sp,

                        color = if (isDark) Color(0xFF9EABA3) else Color(0xFF5A6E63)

                    )

                }



                IconButton(

                    onClick = {

                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }

                    },

                    modifier = Modifier.size(36.dp)

                ) {

                    Icon(

                        imageVector = Icons.Default.Close,

                        contentDescription = "বন্ধ করুন",

                        tint = if (isDark) Color(0xFF9EABA3) else Color(0xFF5A6E63),

                        modifier = Modifier.size(20.dp)

                    )

                }

            }



            Spacer(Modifier.height(14.dp))



            // ২. সর্বশেষ পঠিত পৃষ্ঠার দ্রুত পাঠ বাটন

            Surface(

                modifier = Modifier

                    .fillMaxWidth()

                    .clip(RoundedCornerShape(14.dp))

                    .clickable {

                        scope.launch { sheetState.hide() }.invokeOnCompletion {

                            onDismiss()

                            vm.openReader(targetPage)

                        }

                    },

                color = Color(0xFF0F6B56),

                shadowElevation = 2.dp

            ) {

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(horizontal = 14.dp, vertical = 10.dp),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(

                            modifier = Modifier

                                .size(30.dp)

                                .clip(CircleShape)

                                .background(Color.White.copy(alpha = 0.2f)),

                            contentAlignment = Alignment.Center

                        ) {

                            Icon(

                                imageVector = Icons.Default.PlayArrow,

                                contentDescription = null,

                                tint = Color.White,

                                modifier = Modifier.size(18.dp)

                            )

                        }

                        Spacer(Modifier.width(10.dp))

                        Column {

                            Text(

                                text = "সর্বশেষ পঠিত পৃষ্ঠা থেকে পড়ুন",

                                fontFamily = fonts.kalpurush,

                                fontSize = 13.5.sp,

                                fontWeight = FontWeight.Bold,

                                color = Color.White

                            )

                            Text(

                                text = "পৃষ্ঠা ${targetPage.toBn()} • সরাসরি ক্বারীয়ানা কুরআন",

                                fontFamily = fonts.kalpurush,

                                fontSize = 11.sp,

                                color = Color.White.copy(alpha = 0.85f)

                            )

                        }

                    }



                    Icon(

                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,

                        contentDescription = null,

                        tint = Color.White,

                        modifier = Modifier.size(18.dp)

                    )

                }

            }



            Spacer(Modifier.height(14.dp))



            // ৩. পাঠ সংস্করণের ৩টি আকর্ষণীয় কার্ড

            LazyColumn(

                modifier = Modifier.fillMaxWidth(),

                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {

                // সংস্করণ ১: জনপ্রিয় সংস্করণ (৯৬৩ পৃষ্ঠা — শব্দে শব্দে অর্থ ও রঙিন তাজবীদ)
                item {
                    EditionOptionCard(
                        numberBn = "১",
                        titleBn = "জনপ্রিয় সংস্করণ (৯৬৩ পৃষ্ঠা)",
                        subtitleBn = "শব্দে-শব্দে অর্থ ও রঙিন তাজবীদ — ইনডিজাইন প্রিন্ট রেপ্লিকা, এজ-টু-এজ মার্জিন ক্রপড ভিউ ও ৭ রঙের তাজবীদ সংকেত।",
                        badgeText = "১০ পৃষ্ঠা ফ্রি প্রিভিউ • সর্বাধিক জনপ্রিয়",
                        badgeColor = Color(0xFF0F6B56),
                        icon = Icons.Default.AutoStories,
                        isActive = settings.readingMode == ReadingMode.CLASSIC,
                        isDark = isDark,
                        features = listOf("১–১০ পৃষ্ঠা সম্পূর্ণ ফ্রি", "১৫-লাইন হাফেজী বিন্যাস", "৯৬৩ মুদ্রিত পৃষ্ঠা ১:১"),
                        onSelect = { handleSelect(ReadingMode.CLASSIC) }
                    )
                }

                // সংস্করণ ২: নতুন সংস্করণ (ভেক্টর মুসহাফ)
                item {
                    EditionOptionCard(
                        numberBn = "২",
                        titleBn = "নতুন সংস্করণ (ভেক্টর মুসহাফ)",
                        subtitleBn = "ক্বারীয়ানা অর্থো-কুরআন — প্রতিটি শব্দের নিচে উচ্চারণ, বিশুদ্ধ বাংলা অর্থ ও ক্রিস্প ভেক্টর শার্পনেস।",
                        badgeText = "১০ পৃষ্ঠা ফ্রি প্রিভিউ",
                        badgeColor = Color(0xFF0284C7),
                        icon = Icons.Default.FormatColorText,
                        isActive = settings.readingMode == ReadingMode.PAGE,
                        isDark = isDark,
                        features = listOf("১–১০ পৃষ্ঠা সম্পূর্ণ ফ্রি", "শব্দে-শব্দে বাংলা অর্থ", "ভেক্টর শার্পনেস ও জুম"),
                        onSelect = { handleSelect(ReadingMode.PAGE) }
                    )
                }

                // সংস্করণ ৩: ডিজিটাল সংস্করণ (আয়াত ও তাফসীর)
                item {
                    EditionOptionCard(
                        numberBn = "৩",
                        titleBn = "ডিজিটাল সংস্করণ (আয়াত ও তাফসীর)",
                        subtitleBn = "আয়াতভিত্তিক কার্ড বিন্যাস — বড় আরামদায়ক আরবি ফন্ট, বিশুদ্ধ বাংলা উচ্চারণ, বঙ্গানুবাদ এবং প্রামাণ্য তাফসীর।",
                        badgeText = "স্থায়ী ডিজিটাল সংস্করণ",
                        badgeColor = Color(0xFFD97706),
                        icon = Icons.Default.Translate,
                        isActive = settings.readingMode == ReadingMode.AYAH,
                        isDark = isDark,
                        features = listOf("সকল আয়াত ও সূরা", "আয়াতভিত্তিক কার্ড লেআউট", "প্রামাণ্য তাফসীর নোট"),
                        onSelect = { handleSelect(ReadingMode.AYAH) }
                    )
                }

                // সংস্করণ ৪: হাফেজী ভেক্টর সংস্করণ (১৫ ও ১৪ লাইন)
                item {
                    EditionOptionCard(
                        numberBn = "৪",
                        titleBn = "হাফেজী সংস্করণ (১৫ ও ১৪ লাইন)",
                        subtitleBn = "ঐতিহ্যবাহী ১৫ ও ১৪ লাইন হাফেজী বিন্যাস — খাঁটি ভেক্টর শার্পনেস, শব্দার্থ, উচ্চারণ ও রঙিন তাজবীদ সংকেত সহ।",
                        badgeText = "১০ পৃষ্ঠা ফ্রি • নতুন সংযোজন",
                        badgeColor = Color(0xFF059669),
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        isActive = settings.readingMode == ReadingMode.HAFEZI,
                        isDark = isDark,
                        features = listOf("১–১০ পৃষ্ঠা সম্পূর্ণ ফ্রি", "১৫ ও ১৪ লাইন হাফেজী বিন্যাস", "আরবি, অর্থ ও উচ্চারণ ৩টি মোড", "রঙিন তাজবীদ"),
                        onSelect = { handleSelect(ReadingMode.HAFEZI) }
                    )
                }

                // ৪. অতিরিক্ত কুইক বাটন (সূচিপত্র ও ১ম পৃষ্ঠা)

                item {

                    Spacer(Modifier.height(4.dp))

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.spacedBy(10.dp)

                    ) {

                        OutlinedButton(

                            onClick = {

                                scope.launch { sheetState.hide() }.invokeOnCompletion {

                                    onDismiss()

                                    vm.openReader(1)

                                }

                            },

                            shape = RoundedCornerShape(12.dp),

                            modifier = Modifier.weight(1f)

                        ) {

                            Text(

                                text = "১ম পৃষ্ঠা (ফাতিহা)",

                                fontFamily = fonts.kalpurush,

                                fontSize = 12.sp,

                                fontWeight = FontWeight.SemiBold,

                                color = Color(0xFF0F6B56)

                            )

                        }



                        OutlinedButton(

                            onClick = {

                                scope.launch { sheetState.hide() }.invokeOnCompletion {

                                    onDismiss()

                                    vm.openIndex()

                                }

                            },

                            shape = RoundedCornerShape(12.dp),

                            modifier = Modifier.weight(1f)

                        ) {

                            Text(

                                text = "সূরা ও পারা সূচিপত্র",

                                fontFamily = fonts.kalpurush,

                                fontSize = 12.sp,

                                fontWeight = FontWeight.SemiBold,

                                color = Color(0xFF0F6B56)

                            )

                        }

                    }

                }

            }

        }

    }

}



@Composable

private fun EditionOptionCard(

    numberBn: String,

    titleBn: String,

    subtitleBn: String,

    badgeText: String,

    badgeColor: Color,

    icon: ImageVector,

    isActive: Boolean,

    isDark: Boolean,

    features: List<String>,

    onSelect: () -> Unit,

) {

    val fonts = LocalAppFonts.current



    val cardBg = if (isActive) {

        if (isDark) Color(0xFF132A1E) else Color(0xFFF0FDF4)

    } else {

        if (isDark) Color(0xFF131F19) else Color.White

    }



    val borderColor = if (isActive) Color(0xFF0F6B56) else if (isDark) Color(0xFF22352B) else Color(0xFFE2EBE5)



    Card(

        modifier = Modifier

            .fillMaxWidth()

            .clip(RoundedCornerShape(16.dp))

            .clickable(onClick = onSelect),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(containerColor = cardBg),

        border = androidx.compose.foundation.BorderStroke(if (isActive) 1.8.dp else 1.dp, borderColor),

        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 3.dp else 1.dp)

    ) {

        Column(

            modifier = Modifier

                .fillMaxWidth()

                .padding(14.dp)

        ) {

            // কার্ড হেডার

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(

                        modifier = Modifier

                            .size(34.dp)

                            .clip(RoundedCornerShape(10.dp))

                            .background(badgeColor.copy(alpha = 0.14f)),

                        contentAlignment = Alignment.Center

                    ) {

                        Icon(

                            imageVector = icon,

                            contentDescription = null,

                            tint = badgeColor,

                            modifier = Modifier.size(19.dp)

                        )

                    }



                    Spacer(Modifier.width(10.dp))



                    Box(

                        modifier = Modifier

                            .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))

                            .padding(horizontal = 8.dp, vertical = 2.5.dp)

                    ) {

                        Text(

                            text = badgeText,

                            fontFamily = fonts.kalpurush,

                            fontSize = 10.sp,

                            fontWeight = FontWeight.Bold,

                            color = badgeColor

                        )

                    }

                }



                if (isActive) {

                    Row(

                        verticalAlignment = Alignment.CenterVertically,

                        modifier = Modifier

                            .background(Color(0xFF0F6B56), RoundedCornerShape(20.dp))

                            .padding(horizontal = 9.dp, vertical = 3.dp)

                    ) {

                        Icon(

                            imageVector = Icons.Default.CheckCircle,

                            contentDescription = null,

                            tint = Color.White,

                            modifier = Modifier.size(13.dp)

                        )

                        Spacer(Modifier.width(4.dp))

                        Text(

                            text = "বর্তমান সক্রিয়",

                            fontFamily = fonts.kalpurush,

                            fontSize = 10.5.sp,

                            fontWeight = FontWeight.Bold,

                            color = Color.White

                        )

                    }

                }

            }



            Spacer(Modifier.height(10.dp))



            // টাইটেল ও সাবটাইটেল

            Text(

                text = titleBn,

                fontFamily = fonts.kalpurush,

                fontSize = 15.sp,

                fontWeight = FontWeight.Bold,

                color = if (isDark) Color.White else Color(0xFF0F1E16)

            )



            Spacer(Modifier.height(3.dp))



            Text(

                text = subtitleBn,

                fontFamily = fonts.kalpurush,

                fontSize = 11.5.sp,

                color = if (isDark) Color(0xFF9EABA3) else Color(0xFF546A5E),

                lineHeight = 16.5.sp

            )



            Spacer(Modifier.height(8.dp))



            // ফিচার বুলেটের রো

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(6.dp)

            ) {

                features.forEach { feat ->

                    Box(

                        modifier = Modifier

                            .background(

                                if (isDark) Color(0xFF1E3027) else Color(0xFFE8F2EC),

                                RoundedCornerShape(5.dp)

                            )

                            .padding(horizontal = 6.dp, vertical = 2.dp)

                    ) {

                        Text(

                            text = "• $feat",

                            fontFamily = fonts.kalpurush,

                            fontSize = 9.5.sp,

                            color = if (isDark) Color(0xFFB0C0B6) else Color(0xFF385547)

                        )

                    }

                }

            }



            Spacer(Modifier.height(10.dp))



            // বাটন

            Button(

                onClick = onSelect,

                shape = RoundedCornerShape(10.dp),

                colors = ButtonDefaults.buttonColors(

                    containerColor = if (isActive) Color(0xFF0F6B56) else if (isDark) Color(0xFF1D3327) else Color(0xFFE4EDE7),

                    contentColor = if (isActive) Color.White else if (isDark) Color(0xFFA5C5B3) else Color(0xFF0F6B56)

                ),

                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 5.dp),

                modifier = Modifier

                    .fillMaxWidth()

                    .height(36.dp)

            ) {

                Text(

                    text = if (isActive) "এই সংস্করণে পড়া শুরু করুন" else "এই সংস্করণ নির্বাচন করুন ও পড়ুন >",

                    fontFamily = fonts.kalpurush,

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Bold

                )

            }

        }

    }

}


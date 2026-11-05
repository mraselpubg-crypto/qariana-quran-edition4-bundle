package com.karianaquran.reader.ui.overview

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ক্বারীয়ানা কুরআনের ৩টি স্বতন্ত্র সংস্করণের বিস্তারিত তথ্য ও তুলনামূলক বৈশিষ্ট্য প্রদর্শনের বটমশিট
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditionInfoBottomSheet(
    initialMode: ReadingMode = ReadingMode.CLASSIC,
    onDismiss: () -> Unit,
    onSelectEdition: (ReadingMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTabIndex by remember {
        mutableIntStateOf(
            when (initialMode) {
                ReadingMode.CLASSIC -> 0
                ReadingMode.PAGE -> 1
                ReadingMode.AYAH -> 2
                ReadingMode.HAFEZI -> 3
            }
        )
    }

    val currentMode = when (selectedTabIndex) {
        0 -> ReadingMode.CLASSIC
        1 -> ReadingMode.PAGE
        else -> ReadingMode.AYAH
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFBF9F5),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // ১. হেডার
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F6B56),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD4AF37).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color(0xFFFFDF79),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ক্বারীয়ানা ৩ সংস্করণ পরিচিতি",
                                fontFamily = fonts.kalpurush,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "আপনার পছন্দের পাঠশৈলী নির্বাচন করুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ২. সংস্করণ ৩টির ট্যাব বার
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color(0xFFECE6DA),
                contentColor = Color(0xFF0F6B56),
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF0F6B56),
                            height = 3.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "জনপ্রিয় সংস্করণ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 0) Color(0xFF0F6B56) else Color(0xFF64748B)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "নতুন সংস্করণ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 1) Color(0xFF0F6B56) else Color(0xFF64748B)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = "ডিজিটাল সংস্করণ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == 2) Color(0xFF0F6B56) else Color(0xFF64748B)
                        )
                    }
                )
            }

            // ৩. নির্বাচিত ট্যাবের পূর্ণাঙ্গ বিবরণ ও বৈশিষ্ট্য স্ক্রোল
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                val (title, subtitle, description, features, targetAudience) = when (selectedTabIndex) {
                    0 -> TabInfo(
                        title = "জনপ্রিয় সংস্করণ (Popular Classical Tajweed Edition)",
                        subtitle = "শব্দে শব্দে অর্থ ও তাজবীদ সংস্করণ • ব্যাকরণগত বিশ্লেষণ ও প্রথাগত পঠনরীতি",
                        description = "শব্দে শব্দে অর্থ ও তাজবীদ সংস্করণ। প্রতিটি আরবি শব্দের মূল ব্যুৎপত্তি ও আন্তর্জাতিক ৭টি কালার কোডেড তাজবীদ নিয়মাবলি। তিলাওয়াত শেখার পাশাপাশি তাজবীদের গভীর জ্ঞান অর্জনে ইচ্ছুক পাঠকদের জন্য আদর্শ।",
                        features = listOf(
                            "প্রতিটি শব্দের পৃথক ব্যাকরণিক শব্দার্থ ও ব্যুৎপত্তি বিশ্লেষণ",
                            "তিলাওয়াতের বিশুদ্ধতা নিশ্চিত করতে তাজবীদের স্পষ্ট সংকেত",
                            "নতুন শিক্ষার্থী ও শিশুদের সহজে কুরআন শেখার সেরা সহায়ক",
                            "ক্লান্তিহীন পাঠের জন্য চোখের সুরক্ষায় ৬টি প্রিমিয়াম কালার থিম",
                            "প্রথাগত মাদরাসা ও হিফজখানার জনপ্রিয় বিন্যাস"
                        ),
                        targetAudience = "কুরআন শিক্ষার শিক্ষার্থী, শিক্ষক ও যাঁরা বিশুদ্ধ তাজবীদ ও ব্যাকরণসম্মত পঠনে পারদর্শী হতে চান।"
                    )
                    1 -> TabInfo(
                        title = "নতুন সংস্করণ (New Indesign Vector Mushaf)",
                        subtitle = "১৫-লাইন নূরানী ও হাফেজী প্রিন্ট রেপ্লিকা • প্রতি শব্দের নিচে অর্থ • ৭ রঙিন তাজবীদ",
                        description = "ক্বারীয়ানা ইনডিজাইন প্রিন্ট রেপ্লিকা। প্রতিটি আরবি শব্দের ঠিক নিচে নিচে বিশুদ্ধ বাংলা উচ্চারণ ও সহজ শব্দার্থ সন্নিবেশিত। ১৫-লাইন নূরানী ও হাফেজী পৃষ্ঠার অবিকল ডিজিটাল রূপ, যা পাঠককে আসল মুদ্রিত কুরআন পড়ার সম্পূর্ণ প্রশান্তি ও শুদ্ধতা দান করে।",
                        features = listOf(
                            "১৫-লাইন নূরানী ও হাফেজী পৃষ্ঠার অবিকল মুদ্রণ লেআউট",
                            "প্রতিটি আরবি শব্দের নিচে উচ্চারণ ও সহজ বাংলা শব্দার্থ",
                            "আন্তর্জাতিক ৭টি কালার কোডেড তাজবীদ বর্ণমালা ও ওয়াকফ সংকেত",
                            "ক্রপ মার্জিন ও হাই-রেজোলিউশন ভেক্টর জুম সুবিধা (১x থেকে ৪x)",
                            "মুসহাফ পেজ মসৃণ সুইপিং ও পিন্চ-টু-জুম কন্ট্রোল"
                        ),
                        targetAudience = "যাঁরা নূরানী বা হাফেজী ছাপা কুরআন পড়ায় অভ্যস্ত এবং শব্দে শব্দে বাংলা অর্থসহ আসল মুদ্রণের অনুভূতি চান।"
                    )
                    else -> TabInfo(
                        title = "ডিজিটাল সংস্করণ (Digital Edition)",
                        subtitle = "আয়াতভিত্তিক কার্ড লেআউট • বড় স্পষ্ট ফন্ট • বাংলা উচ্চারণ • বঙ্গানুবাদ ও সংক্ষিপ্ত তাফসীর",
                        description = "নিয়মিত কুরআন তিলাওয়াত, অনুধাবন ও তাফসীর পাঠের জন্য এটি সর্বাধুনিক ও আরামদায়ক সংস্করণ। প্রতিটি আয়াতের সুস্পষ্ট আরবি ফন্ট, নিচে বিশুদ্ধ বাংলা উচ্চারণ এবং নির্ভরযোগ্য প্রামাণ্য বঙ্গানুবাদ ও সংক্ষিপ্ত তাফসীর নোট সন্নিবেশিত।",
                        features = listOf(
                            "আয়াতভিত্তিক পৃথক কার্ড বিন্যাস ও আরামদায়ক বড় স্পষ্ট ফন্ট",
                            "প্রতিটি আয়াতের বিশুদ্ধ বাংলা উচ্চারণ ও নির্ভরযোগ্য প্রামাণ্য বঙ্গানুবাদ",
                            "আয়াত সংলগ্ন নির্ভরযোগ্য সংক্ষিপ্ত তাফসীর ও শানে নুযূল নোট",
                            "১-ট্যাপে সরাসরি আয়াত কপি, শেয়ার ও পার্সোনাল বুকমার্কিং সুবিধা",
                            "প্রতিটি শব্দের অডিও উচ্চারণ শোনার সুবিধা"
                        ),
                        targetAudience = "যাঁরা নিয়মিত তিলাওয়াতের পাশাপাশি প্রতিটি আয়াতের মর্মার্থ ও প্রামাণ্য তাফসীর সহজ ভাষায় বুঝতে চান।"
                    )
                }

                Text(
                    text = title,
                    fontFamily = fonts.kalpurush,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F6B56)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD4AF37)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = description,
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "প্রধান বৈশিষ্ট্যসমূহ:",
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(Modifier.height(6.dp))

                features.forEach { feat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F6B56).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF0F6B56),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = feat,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F6B56).copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F6B56).copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "কাদের জন্য উপযোগী:",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F6B56)
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = targetAudience,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = Color(0xFF475569),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ৪. অ্যাকশন বাটন: এই সংস্করণে সরাসরি পড়া শুরু করুন
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Button(
                    onClick = {
                        onSelectEdition(currentMode)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFFFFDF79),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "এই সংস্করণ নির্বাচন করে পড়ুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private data class TabInfo(
    val title: String,
    val subtitle: String,
    val description: String,
    val features: List<String>,
    val targetAudience: String,
)


package com.karianaquran.reader.ui.index

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.components.QuranMetadata
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.components.DarUlMujtabaBottomBar
import androidx.compose.material.icons.outlined.Info
import com.karianaquran.reader.ui.overview.EditionInfoBottomSheet
import com.karianaquran.reader.ui.theme.LocalAppFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahIndexScreen(
    vm: AppViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val userData by vm.userData.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"

    var selectedTabIndex by remember(initialTab) { mutableIntStateOf(initialTab) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var showAssetManagerSheet by remember { mutableStateOf(false) }

    val effectivePage = userData.lastPage.coerceIn(1, 611)
    val isPageBookmarked = userData.bookmarks.contains(effectivePage)

    val filteredSurahs = QuranMetadata.surahList.filter { s ->
        searchQuery.isEmpty() ||
            s.nameBn.contains(searchQuery, ignoreCase = true) ||
            s.nameAr.contains(searchQuery, ignoreCase = true) ||
            s.number.toString().contains(searchQuery)
    }

    val filteredJuz = QuranMetadata.juzList.filter { j ->
        searchQuery.isEmpty() ||
            j.nameBn.contains(searchQuery, ignoreCase = true) ||
            j.number.toString().contains(searchQuery)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) Color(0xFF0A120E) else Color(0xFFF8FAF9),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            DarUlMujtabaBottomBar(
                activeTab = 2, // Index / Quran tab
                isDark = isDark,
                isEnglish = isEnglish,
                onTabSelect = { tabIdx ->
                    when (tabIdx) {
                        0 -> vm.navigate(Screen.Home)
                        1 -> vm.openLibrary()
                        2 -> {
                            // ইতোমধ্যে কুরআন/সূচিপত্রে থাকলে ট্যাব টগল অথবা প্রথম ট্যাবে ফেরা
                            if (selectedTabIndex != 0) selectedTabIndex = 0
                        }
                        3 -> vm.openProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ১. হেডার (ট্যাব ০ হলে বইয়ের পরিচিতি, ট্যাব ১ বা ২ হলে সূচিপত্র ও সার্চ বার)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F6B56),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 6.dp)
                        .padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        IconButton(
                            onClick = { vm.navigate(Screen.Home) },
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

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) {
                                        if (selectedTabIndex == 0) "Qariana Ortho-Quran" else "Quran Index"
                                    } else {
                                        if (selectedTabIndex == 0) "ক্বারীয়ানা অর্থ-কুরআন" else "কুরআন সূচিপত্র"
                                    },
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                // সংস্করণের সুস্পষ্ট পরিচিতি ব্যাজ (ট্যাপ করে সংস্করণ পরিবর্তনও করা যাবে)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFD4AF37),
                                    modifier = Modifier.clickable {
                                        val nextMode = when (readerSettings.readingMode) {
                                            com.karianaquran.reader.data.ReadingMode.CLASSIC -> com.karianaquran.reader.data.ReadingMode.PAGE
                                            com.karianaquran.reader.data.ReadingMode.PAGE -> com.karianaquran.reader.data.ReadingMode.AYAH
                                            com.karianaquran.reader.data.ReadingMode.AYAH -> com.karianaquran.reader.data.ReadingMode.HAFEZI
                                            com.karianaquran.reader.data.ReadingMode.HAFEZI -> com.karianaquran.reader.data.ReadingMode.CLASSIC
                                        }
                                        vm.setReadingMode(nextMode)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = when (readerSettings.readingMode) {
                                                com.karianaquran.reader.data.ReadingMode.CLASSIC -> "জনপ্রিয় সংস্করণ ▾"
                                                com.karianaquran.reader.data.ReadingMode.PAGE -> "নতুন সংস্করণ ▾"
                                                com.karianaquran.reader.data.ReadingMode.AYAH -> "ডিজিটাল সংস্করণ ▾"
                                                com.karianaquran.reader.data.ReadingMode.HAFEZI -> "হাফেজী সংস্করণ ▾"
                                            },
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF063328)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = when (readerSettings.readingMode) {
                                    com.karianaquran.reader.data.ReadingMode.CLASSIC -> if (isEnglish) "Popular Edition (973 Pages Index)" else "জনপ্রিয় মুদ্রিত সংস্করণ (৯৭৩ পৃষ্ঠা সূচিপত্র)"
                                    com.karianaquran.reader.data.ReadingMode.PAGE -> if (isEnglish) "New Edition (611 Pages Index)" else "নতুন ইনডিজাইন সংস্করণ (৬১১ পৃষ্ঠা সূচিপত্র)"
                                    com.karianaquran.reader.data.ReadingMode.AYAH -> if (isEnglish) "Digital Edition (Ayah & Meaning Index)" else "ডিজিটাল সংস্করণ (আয়াত ও শব্দার্থ সূচিপত্র)"
                                    com.karianaquran.reader.data.ReadingMode.HAFEZI -> if (isEnglish) "Hafezi Edition (15/14 Lines Index)" else "হাফেজী সংস্করণ (১৫/১৪ লাইন সূচিপত্র)"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        // ডাউনলোড, ইনফো ও সার্চ আইকন সবসময় টপ বারে একসাথে থাকবে
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { vm.navigate(com.karianaquran.reader.ui.Screen.EditionDetail(readerSettings.readingMode)) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "সংস্করণ পরিচিতি",
                                    tint = Color(0xFFFDE68A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { showAssetManagerSheet = true },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "অফলাইন ডাউনলোড",
                                    tint = Color(0xFFFDE68A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { 
                                    isSearchVisible = !isSearchVisible
                                    if (isSearchVisible && selectedTabIndex == 0) {
                                        selectedTabIndex = 1 // সার্চ ওপেন করলে সূরা ট্যাবে নেওয়া
                                    }
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "অনুসন্ধান",
                                    tint = if (isSearchVisible) Color(0xFFFDE68A) else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    if (isSearchVisible) {
                        Spacer(Modifier.height(4.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .padding(bottom = 6.dp)
                                .height(48.dp),
                            placeholder = {
                                Text(
                                    if (isEnglish) "Search Surah or Para name/number..." else "সূরা বা পারার নাম অথবা নম্বর খুঁজুন...",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.65f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { 
                                    if (searchQuery.isNotEmpty()) searchQuery = "" else isSearchVisible = false 
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.18f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                                focusedBorderColor = Color(0xFFFDE68A),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFFFDE68A)
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // ২. ৩-ট্যাব রো (বইয়ের বিবরণ | সূরাসমূহ | পারাসমূহ)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = if (isDark) Color(0xFF13221A) else Color.White,
                contentColor = Color(0xFF0F6B56),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF0F6B56),
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = if (isEnglish) "Overview" else "ওভারভিউ",
                            fontFamily = fonts.kalpurush,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTabIndex == 0) Color(0xFF0F6B56) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = if (isEnglish) "Surahs (114)" else "সূরাসমূহ (১১৪)",
                            fontFamily = fonts.kalpurush,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTabIndex == 1) Color(0xFF0F6B56) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = if (isEnglish) "Paras (30)" else "পারাসমূহ (৩০)",
                            fontFamily = fonts.kalpurush,
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTabIndex == 2) Color(0xFF0F6B56) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        )
                    }
                )
            }

            // ৩. নির্বাচিত ট্যাবের কনটেন্ট
            when (selectedTabIndex) {
                0 -> {
                    // ট্যাব ০: বইয়ের ৩ডি কভার, সংস্করণ নির্বাচন, বিবরণ ও রিড বাটন
                    com.karianaquran.reader.ui.overview.QuranOverviewContent(
                        resumePage = effectivePage,
                        vm = vm,
                        onOpenSurahTab = { selectedTabIndex = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
                1 -> {
                    val isUnlocked = vm.isUnlocked
                    val freeLimit = vm.freePagesLimit()
                    var lockedPromptItem by remember { mutableStateOf<String?>(null) }

                    if (lockedPromptItem != null) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { lockedPromptItem = null },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            title = {
                                Text(
                                    text = "কুরআন সম্পূর্ণ আনলক করুন",
                                    fontFamily = fonts.kalpurush,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            text = {
                                Text(
                                    text = "${lockedPromptItem} পড়তে সম্পূর্ণ ৩০ পারা আনলক করুন। ক্বারীয়ানা কুরআনের প্রথম ${freeLimit.toBn()} পৃষ্ঠা ফ্রি পড়ার জন্য উন্মুক্ত রয়েছে।",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        lockedPromptItem = null
                                        vm.navigate(com.karianaquran.reader.ui.Screen.MembershipPackages)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFFFDF79),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("এখনই আনলক করুন", fontFamily = fonts.kalpurush, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            lockedPromptItem = null
                                            vm.navigate(com.karianaquran.reader.ui.Screen.Login)
                                        }
                                    ) {
                                        Text("লগইন", fontFamily = fonts.kalpurush)
                                    }
                                    TextButton(onClick = { lockedPromptItem = null }) {
                                        Text("বাতিল", fontFamily = fonts.kalpurush)
                                    }
                                }
                            }
                        )
                    }

                    // ট্যাব ১: সূরা তালিকা
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredSurahs) { surah ->
                            val targetPage = QuranMetadata.startPageForSurah(surah.number, readerSettings.readingMode)
                            val isLocked = !isUnlocked && targetPage > freeLimit
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isLocked) {
                                            lockedPromptItem = "সূরা ${surah.nameBn}"
                                        } else {
                                            vm.openReader(targetPage)
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) Color(0xFF13221A) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // গোল্ডেন সার্কেল সূরা নম্বর
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isLocked) Color(0xFFD4AF37).copy(alpha = 0.15f) else Color(0xFF0F6B56).copy(alpha = 0.12f))
                                            .border(1.dp, if (isLocked) Color(0xFFD4AF37).copy(alpha = 0.6f) else Color(0xFF0F6B56).copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isEnglish) surah.number.toString() else surah.number.toBn(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLocked) Color(0xFFB45309) else Color(0xFF0F6B56)
                                        )
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    // সূরার বাংলা নাম ও তথ্য
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = surah.name(isEnglish),
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color.White else Color(0xFF0F172A)
                                            )
                                            if (isLocked) {
                                                Spacer(Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "লকড",
                                                    tint = Color(0xFFD4AF37),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "${surah.totalAyahs.toBn()} আয়াত • পারা ${surah.juz.toBn()} • পৃষ্ঠা ${targetPage.toBn()}",
                                            fontSize = 11.5.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    }

                                    // আরবী নাম ও অ্যাকশন
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = surah.nameAr,
                                            fontFamily = fonts.arabic,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLocked) Color(0xFFB45309) else Color(0xFF0F6B56)
                                        )

                                        Spacer(Modifier.width(8.dp))

                                        Icon(
                                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = if (isLocked) Color(0xFFD4AF37) else (if (isDark) Color(0xFF64748B) else Color(0xFFCBD5E1)),
                                            modifier = Modifier.size(if (isLocked) 16.dp else 20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    val isUnlocked = vm.isUnlocked
                    val freeLimit = vm.freePagesLimit()
                    var lockedPromptItem by remember { mutableStateOf<String?>(null) }

                    if (lockedPromptItem != null) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { lockedPromptItem = null },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            title = {
                                Text(
                                    text = "কুরআন সম্পূর্ণ আনলক করুন",
                                    fontFamily = fonts.kalpurush,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            text = {
                                Text(
                                    text = "${lockedPromptItem} পড়তে সম্পূর্ণ ৩০ পারা আনলক করুন। ক্বারীয়ানা কুরআনের প্রথম ${freeLimit.toBn()} পৃষ্ঠা ফ্রি পড়ার জন্য উন্মুক্ত রয়েছে।",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        lockedPromptItem = null
                                        vm.navigate(com.karianaquran.reader.ui.Screen.MembershipPackages)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFFFDF79),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("এখনই আনলক করুন", fontFamily = fonts.kalpurush, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            lockedPromptItem = null
                                            vm.navigate(com.karianaquran.reader.ui.Screen.Login)
                                        }
                                    ) {
                                        Text("লগইন", fontFamily = fonts.kalpurush)
                                    }
                                    TextButton(onClick = { lockedPromptItem = null }) {
                                        Text("বাতিল", fontFamily = fonts.kalpurush)
                                    }
                                }
                            }
                        )
                    }

                    // ট্যাব ২: পারা তালিকা
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredJuz) { juz ->
                            val targetPage = QuranMetadata.juzStartPage(juz.number, readerSettings.readingMode)
                            val isLocked = !isUnlocked && targetPage > freeLimit
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isLocked) {
                                            lockedPromptItem = "পারা ${juz.number.toBn()} (${juz.nameBn})"
                                        } else {
                                            vm.openReader(targetPage)
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) Color(0xFF13221A) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isLocked) Color(0xFFD4AF37).copy(alpha = 0.15f) else Color(0xFFFEF3C7))
                                            .border(1.dp, if (isLocked) Color(0xFFD4AF37).copy(alpha = 0.6f) else Color(0xFFD97706), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = juz.number.toBn(),
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "পারা ${juz.number.toBn()}: ${juz.nameBn}",
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color.White else Color(0xFF0F172A)
                                            )
                                            if (isLocked) {
                                                Spacer(Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "লকড",
                                                    tint = Color(0xFFD4AF37),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "শুরু: পৃষ্ঠা ${targetPage.toBn()}",
                                            fontSize = 11.5.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = if (isLocked) Color(0xFFD4AF37) else (if (isDark) Color(0xFF64748B) else Color(0xFFCBD5E1)),
                                        modifier = Modifier.size(if (isLocked) 16.dp else 20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAssetManagerSheet) {
        com.karianaquran.reader.ui.components.OfflineAssetManagerSheet(
            vm = vm,
            isDark = isDark,
            isEnglish = isEnglish,
            onDismiss = { showAssetManagerSheet = false }
        )
    }

    // সংস্করণ পরিচিতি এখন Screen.EditionDetail ফুল-স্ক্রিনে মাইগ্রেট করা হয়েছে।
}

package com.karianaquran.reader.ui.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karianaquran.reader.data.model.Publication
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.components.RealBookCover
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.components.DarUlMujtabaBottomBar
import com.karianaquran.reader.ui.theme.LocalAppFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"
    val isUnlocked = vm.isUnlocked

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সকল") }
    var detailBook by remember { mutableStateOf<Publication?>(null) }

    val categories = listOf("সকল", "কুরআন ও তাফসীর", "কায়দা ও তাজবীদ", "আমল ও নামাজ")

    val filteredBooks = Publications.all.filter { book ->
        val matchesQuery = searchQuery.isEmpty() ||
            book.titleBn.contains(searchQuery, ignoreCase = true) ||
            book.subtitleBn.contains(searchQuery, ignoreCase = true) ||
            book.titleAr.contains(searchQuery, ignoreCase = true)

        val matchesCat = when (selectedCategory) {
            "কুরআন ও তাফসীর" -> book.id == "ortho_quran" || book.id == "ortho_sara" || book.id == "tafsir" || book.id == "hifz"
            "কায়দা ও তাজবীদ" -> book.id == "quran_shikkha" || book.id == "qaida" || book.id == "hifz"
            "আমল ও নামাজ" -> book.id == "namaz_shikkha" || book.id == "ampara" || book.id == "namaz" || book.id == "gojol"
            else -> true
        }
        matchesQuery && matchesCat
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) Color(0xFF0A120E) else Color(0xFFF8FAF9),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            DarUlMujtabaBottomBar(
                activeTab = 1, // Library tab
                isDark = isDark,
                isEnglish = isEnglish,
                onTabSelect = { tabIdx ->
                    when (tabIdx) {
                        0 -> vm.navigate(Screen.Home)
                        1 -> { /* বর্তমানে লাইব্রেরিতে */ }
                        2 -> vm.openIndex()
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
            // ১. টপ হেডার (সলিড ইসলামিক এমারেল্ড ও ব্যাক বাটন)
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
                            Text(
                                text = if (isEnglish) "Qariana Islamic Library" else "ক্বারীয়ানা ইসলামিক লাইব্রেরি",
                                fontFamily = fonts.kalpurush,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnglish) "All Printed & Digital Publications Collection" else "সকল মুদ্রিত ও ডিজিটাল প্রকাশনা সংকলন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // সার্চ বার
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
                                if (isEnglish) "Search books or publications..." else "বই বা প্রকাশনা খুঁজুন...",
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
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "মুছুন", tint = Color.White)
                                }
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
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

            // ২. ক্যাটাগরি ফিল্টার চিপস
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF0F6B56) else (if (isDark) Color(0xFF16251E) else Color.White),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F6B56) else (if (isDark) Color(0xFF264034) else Color(0xFFE2E8F0))
                        ),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else (if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // ৩. প্রকাশনা তালিকা
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredBooks.size.toBn()}টি প্রকাশনা উপলব্ধ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                        Text(
                            text = "ইনডিজাইন প্রেস কোয়ালিটি",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color(0xFF0F6B56),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                items(filteredBooks) { book ->
                    val bookUnlocked = vm.isBookUnlocked(book.id)
                    LibraryBookCard(
                        book = book,
                        isDark = isDark,
                        isUnlocked = bookUnlocked,
                        onOrder = {
                            vm.navigate(Screen.InAppBrowser(url = book.orderUrl, title = book.titleBn))
                        },
                        onRead = {
                            if (book.id == Publications.ORTHO_QURAN_ID || book.id == "ortho_sara") {
                                vm.openQuranOverview()
                            } else {
                                detailBook = book
                            }
                        },
                        onDetail = {
                            detailBook = book
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    // বিস্তারিত সেন্টার্ড ডায়ালগ (বটম শিট নয়)
    detailBook?.let { book ->
        Dialog(onDismissRequest = { detailBook = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF14241C) else Color.White,
                border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RealBookCover(
                        publication = book,
                        height = 140.dp
                    )

                    Spacer(Modifier.height(12.dp))

                    if (book.discountPercent.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE11D48),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = book.discountPercent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = book.title(isEnglish),
                        fontFamily = fonts.kalpurush,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    // পেপার ও পৃষ্ঠা স্পেক্স
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (book.paperType.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Description,
                                        contentDescription = null,
                                        tint = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = book.paperType,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        if (book.totalPages > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.MenuBook,
                                        contentDescription = null,
                                        tint = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${book.totalPages.toBn()} পৃষ্ঠা",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // মূল্য
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (book.originalPriceBn.isNotEmpty()) {
                            Text(
                                text = book.originalPriceBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        Text(
                            text = book.priceBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color(0xFFFDE68A) else Color(0xFF0F6B56)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = book.subtitle(isEnglish),
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B),
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                detailBook = null
                                vm.navigate(Screen.InAppBrowser(url = book.orderUrl, title = book.titleBn))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Icon(Icons.Rounded.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("অর্ডার করুন", fontFamily = fonts.kalpurush, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (book.id == Publications.ORTHO_QURAN_ID || book.id == "ortho_sara") {
                                OutlinedButton(
                                    onClick = {
                                        detailBook = null
                                        vm.openQuranOverview()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = Color(0xFF0F6B56), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("পড়ে দেখুন", fontFamily = fonts.kalpurush, fontSize = 12.sp, color = Color(0xFF0F6B56))
                                }
                            }

                            TextButton(
                                onClick = { detailBook = null },
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text("বন্ধ করুন", fontFamily = fonts.kalpurush, fontSize = 12.sp, color = if (isDark) Color.White else Color(0xFF334155))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryBookCard(
    book: Publication,
    isDark: Boolean,
    isUnlocked: Boolean,
    onOrder: () -> Unit,
    onRead: () -> Unit,
    onDetail: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val isQuran = book.id == Publications.ORTHO_QURAN_ID

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDetail),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF13221A) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // বুক কভার
            RealBookCover(
                publication = book,
                height = 145.dp,
                onClick = onDetail
            )

            Spacer(Modifier.width(12.dp))

            // বইয়ের তথ্যাবলী
            Column(modifier = Modifier.weight(1f)) {
                // ব্যাজ সারি (Hot Sale + প্রকাশনা স্ট্যাটাস)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (book.discountPercent.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE11D48)
                        ) {
                            Text(
                                text = book.discountPercent,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isQuran) Color(0xFF0F6B56).copy(alpha = 0.15f) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = if (isUnlocked) "ক্রয়কৃত ও আনলকড" else (if (isQuran) "১০ পৃষ্ঠা ফ্রি প্রিভিউ" else "স্বতন্ত্র প্রকাশনা"),
                            fontFamily = fonts.kalpurush,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) Color(0xFF0F6B56) else (if (isQuran) Color(0xFFD97706) else Color(0xFF475569)),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = book.titleBn,
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    lineHeight = 19.sp
                )

                // পেপার ও পৃষ্ঠা ট্যাগ
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (book.paperType.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isDark) Color(0xFF1E2D24) else Color(0xFFF1F5F9)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Article,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = book.paperType,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 10.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                    if (book.totalPages > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isDark) Color(0xFF1E2D24) else Color(0xFFF1F5F9)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MenuBook,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = "${book.totalPages.toBn()} পৃষ্ঠা",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 10.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // মূল্য সারি
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (book.originalPriceBn.isNotEmpty()) {
                        Text(
                            text = book.originalPriceBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Text(
                        text = book.priceBn,
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFF0F6B56)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // অ্যাকশন বাটনসমূহ (ওয়েবসাইটের হুবহু লেআউট)
                // ১. অর্ডার করুন প্রধান বাটন
                Button(
                    onClick = onOrder,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (book.comingSoon) "অগ্রিম অর্ডার করুন" else "প্রিন্ট কপি অর্ডার",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(4.dp))

                // ২. পড়ে দেখুন এবং বিস্তারিত বাটন
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clickable(onClick = onRead),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) Color(0xFF1E2D24) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF264034) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "পড়ে দেখুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clickable(onClick = onDetail),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD4AF37),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = Color(0xFF091712),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = "বিস্তারিত",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF091712)
                            )
                        }
                    }
                }
            }
        }
    }
}

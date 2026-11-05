package com.karianaquran.reader.ui.reader

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.data.model.WordMarkType
import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.data.model.WordRef
import com.karianaquran.reader.ui.components.QuranMetadata
import com.karianaquran.reader.ui.components.SecureBottomSheet
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahIndexSheet(
    currentPage: Int,
    bookmarks: Set<Int>,
    wordMarks: Map<String, String>,
    readingMode: ReadingMode = ReadingMode.CLASSIC,
    isUnlocked: Boolean = true,
    onSelectPage: (Int) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToPay: (() -> Unit)? = null,
    onNavigateToLogin: (() -> Unit)? = null,
) {
    val fonts = LocalAppFonts.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var lockedPromptItem by remember { mutableStateOf<String?>(null) }
    val freeLimit = Publications.freePagesForMode(readingMode)

    if (lockedPromptItem != null) {
        AlertDialog(
            onDismissRequest = { lockedPromptItem = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Brand.Gold,
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
                    text = "${lockedPromptItem} পড়তে সম্পূর্ণ ৩০ পারা আনলক করুন। ক্বারীয়ানা কুরআনের প্রথম ১০ পৃষ্ঠা ফ্রি পড়ার জন্য উন্মুক্ত রয়েছে।",
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
                        onDismiss()
                        onNavigateToPay?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFDF79), modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("এখনই আনলক করুন", fontFamily = fonts.kalpurush, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = {
                            lockedPromptItem = null
                            onDismiss()
                            onNavigateToLogin?.invoke()
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

    val tabs = listOf("সূরা", "পারা", "বুকমার্ক (${bookmarks.size.toBn()})")

    SecureBottomSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // হেডার
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "কুরআন সূচিপত্র",
                    fontFamily = fonts.kalpurush,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ট্যাব বার
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Brand.EmeraldDark
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // সার্চ ইনপুট (সূরা ও পারা ট্যাবের জন্য)
            if (selectedTab != 2) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedTab == 0) "সূরার নাম দিয়ে খুঁজুন..." else "পারার নাম দিয়ে খুঁজুন...",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = if (searchQuery.isNotBlank()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "মুছুন",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
            }

            // কনটেন্ট লিস্ট
            when (selectedTab) {
                0 -> { // সূরা তালিকা
                    val filtered = QuranMetadata.surahList.filter {
                        it.nameBn.contains(searchQuery, ignoreCase = true) ||
                        it.nameAr.contains(searchQuery, ignoreCase = true) ||
                        it.number.toString().contains(searchQuery)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filtered, key = { it.number }) { surah ->
                            val targetPage = QuranMetadata.startPageForSurah(surah.number, readingMode)
                            val isCurrent = currentPage in targetPage until (targetPage + 10)
                            val isLocked = !isUnlocked && targetPage > freeLimit
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Brand.EmeraldDark.copy(alpha = 0.08f) else Color.Transparent)
                                    .clickable {
                                        if (isLocked) {
                                            lockedPromptItem = "সূরা ${surah.nameBn}"
                                        } else {
                                            onSelectPage(targetPage)
                                            onDismiss()
                                        }
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isLocked) Brand.Gold.copy(alpha = 0.15f) else Brand.EmeraldDark.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = surah.number.toBn(),
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLocked) Brand.Gold else Brand.EmeraldDark
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = surah.nameBn,
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isLocked) {
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "লকড",
                                                tint = Brand.Gold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "আয়াত: ${surah.totalAyahs.toBn()} | পারা: ${surah.juz.toBn()}",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = surah.nameAr,
                                        fontFamily = fonts.qariana,
                                        fontSize = 18.sp,
                                        color = if (isLocked) Brand.Gold else Brand.EmeraldDark
                                    )
                                    Text(
                                        text = "পৃষ্ঠা ${targetPage.toBn()}",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))
                        }
                    }
                }
                1 -> { // পারা তালিকা
                    val filtered = QuranMetadata.juzList.filter {
                        it.nameBn.contains(searchQuery, ignoreCase = true) ||
                        it.number.toString().contains(searchQuery)
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filtered, key = { it.number }) { juz ->
                            val targetPage = QuranMetadata.juzStartPage(juz.number, readingMode)
                            val isLocked = !isUnlocked && targetPage > freeLimit
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (isLocked) {
                                            lockedPromptItem = "পারা ${juz.number.toBn()} (${juz.nameBn})"
                                        } else {
                                            onSelectPage(targetPage)
                                            onDismiss()
                                        }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isLocked) Brand.Gold.copy(alpha = 0.15f) else Brand.EmeraldDark.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = juz.number.toBn(),
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLocked) Brand.Gold else Brand.EmeraldDark
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "পারা ${juz.number.toBn()}: ${juz.nameBn}",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isLocked) {
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "লকড",
                                                tint = Brand.Gold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "পৃষ্ঠা ${targetPage.toBn()}",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (isLocked) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "লকড",
                                            tint = Brand.Gold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))
                        }
                    }
                }
                2 -> { // বুকমার্ক ও চিহ্নিত শব্দ
                    if (bookmarks.isEmpty() && wordMarks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো বুকমার্ক বা চিহ্নিত শব্দ নেই।\nপড়ার সময় পৃষ্ঠা বা শব্দের উপর ট্যাপ করে চিহ্নিত করতে পারেন।",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            if (bookmarks.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "সংরক্ষিত পৃষ্ঠাসমূহ",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Brand.EmeraldDark,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(bookmarks.sorted()) { page ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                onSelectPage(page)
                                                onDismiss()
                                            }
                                            .padding(vertical = 10.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = Brand.Gold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = "পৃষ্ঠা ${page.toBn()}",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.2f))
                                }
                            }

                            if (wordMarks.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(14.dp))
                                    Text(
                                        text = "চিহ্নিত শব্দসমূহ (${wordMarks.size.toBn()})",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Brand.EmeraldDark,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(wordMarks.entries.toList()) { entry ->
                                    val markRef = WordRef.parse(entry.key)
                                    val type = WordMarkType.from(entry.value)
                                    if (markRef != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    onSelectPage(markRef.page)
                                                    onDismiss()
                                                }
                                                .padding(vertical = 10.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = Brand.Gold, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = type?.label ?: "চিহ্নিত শব্দ",
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "পৃষ্ঠা ${markRef.page.toBn()}, সারি ${markRef.row.toBn()}",
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.2f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

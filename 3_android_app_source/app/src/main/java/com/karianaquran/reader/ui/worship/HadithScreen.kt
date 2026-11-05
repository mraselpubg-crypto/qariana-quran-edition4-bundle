package com.karianaquran.reader.ui.worship

import android.content.Intent
import androidx.compose.material.icons.filled.Share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class HadithItem(
    val id: String,
    val titleBn: String,
    val category: String,
    val narratorBn: String,
    val arabic: String,
    val translationBn: String,
    val bookRef: String,
    val grade: String = "সহীহ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সকল") }
    val favoriteIds = remember { mutableStateListOf<String>() }

    val hadiths = remember {
        listOf(
            HadithItem(
                id = "1",
                titleBn = "কাজের ফলাফল নিয়তের উপর নির্ভরশীল",
                category = "নিয়ত ও ঈমান",
                narratorBn = "উমর ইবনুল খাত্তাব (রা.) হতে বর্ণিত",
                arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
                translationBn = "নিশ্চয়ই সমস্ত কাজ নিয়তের উপর নির্ভরশীল। আর প্রত্যেক ব্যক্তি তাই পাবে যার সে নিয়ত করেছে।",
                bookRef = "সহীহ আল-বুখারী: ১, সহীহ মুসলিম: ১৯০৭"
            ),
            HadithItem(
                id = "2",
                titleBn = "উত্তম চরিত্র ও সুন্দর ব্যবহারের গুরুত্ব",
                category = "চরিত্র ও আচার",
                narratorBn = "আবদুল্লাহ ইবনু আমর (রা.) হতে বর্ণিত",
                arabic = "إِنَّ مِنْ خِيَارِكُمْ أَحْسَنَكُمْ أَخْلاَقًا",
                translationBn = "রাসূলুল্লাহ (সা.) বলেছেন: নিশ্চয়ই তোমাদের মধ্যে সর্বোত্তম ব্যক্তি সে, যার চরিত্র ও ব্যবহার সবচেয়ে সুন্দর।",
                bookRef = "সহীহ আল-বুখারী: ৩৫৫৯, সহীহ মুসলিম: ২৩২১"
            ),
            HadithItem(
                id = "3",
                titleBn = "কুরআন শিক্ষা ও প্রচারের মর্যাদা",
                category = "কুরআন ও ইলম",
                narratorBn = "উসমান ইবনু আফফান (রা.) হতে বর্ণিত",
                arabic = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
                translationBn = "নবী করীম (সা.) বলেছেন: তোমাদের মধ্যে সর্বশ্রেষ্ঠ ব্যক্তি সেই, যে নিজে কুরআন শিখে এবং অন্যকে তা শিক্ষা দেয়।",
                bookRef = "সহীহ আল-বুখারী: ৫০২৭"
            ),
            HadithItem(
                id = "4",
                titleBn = "মুসলিম ভ্রাতৃত্ব ও ভালোবাসার মূলনীতি",
                category = "নিয়ত ও ঈমান",
                narratorBn = "আনাস (রা.) হতে বর্ণিত",
                arabic = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
                translationBn = "তোমাদের কেউ প্রকৃত মুমিন হতে পারবে না, যতক্ষণ না সে তার ভাইয়ের জন্য তাই পছন্দ করে যা সে নিজের জন্য পছন্দ করে।",
                bookRef = "সহীহ আল-বুখারী: ১৩, সহীহ মুসলিম: ৪৫"
            ),
            HadithItem(
                id = "5",
                titleBn = "সদকার ফজিলত ও সহজ আমল",
                category = "দান ও সদকা",
                narratorBn = "আবু হুরায়রা (রা.) হতে বর্ণিত",
                arabic = "كُلُّ مَعْرُوفٍ صَدَقَةٌ، وَالتَّبَسُّمُ فِي وَجْهِ أَخِيكَ صَدَقَةٌ",
                translationBn = "প্রত্যেক ভালো কাজই সদকা। আর তোমার মুসলিম ভাইয়ের মুখের দিকে তাকিয়ে মুচকি হাসাও একটি সদকা।",
                bookRef = "সহীহ আল-বুখারী: ৬০২১, জামি আত-তিরমিযী: ১৯৫৬"
            ),
            HadithItem(
                id = "6",
                titleBn = "আল্লাহর নৈকট্য ও তাওবার শক্তি",
                category = "ক্ষমা ও তাওবা",
                narratorBn = "আনাস (রা.) হতে বর্ণিত",
                arabic = "كُلُّ بَنِي آدَمَ خَطَّاءٌ وَخَيْرُ الْخَطَّائِينَ التَّوَّابُونَ",
                translationBn = "আদম সন্তান সকলেই ভুলকারী ও পাপপ্রবণ, আর ভুলকারীদের মধ্যে সর্বাপেক্ষা উত্তম তারাই যারা তওবা করে ফিরে আসে।",
                bookRef = "সুনান আত-তিরমিযী: ২৪৯৯, ইবনু মাজাহ: ৪২৫১"
            )
        )
    }

    val categories = listOf("সকল", "পছন্দের হাদিস", "নিয়ত ও ঈমান", "চরিত্র ও আচার", "কুরআন ও ইলম", "দান ও সদকা", "ক্ষমা ও তাওবা")

    val filteredHadiths = hadiths.filter { h ->
        val matchesQuery = searchQuery.isEmpty() ||
            h.titleBn.contains(searchQuery, ignoreCase = true) ||
            h.translationBn.contains(searchQuery, ignoreCase = true) ||
            h.arabic.contains(searchQuery, ignoreCase = true)
        val matchesCat = when (selectedCategory) {
            "সকল" -> true
            "পছন্দের হাদিস" -> favoriteIds.contains(h.id)
            else -> h.category == selectedCategory
        }
        matchesQuery && matchesCat
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) Color(0xFF0A120E) else Color(0xFFF8FAF9),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ১. বিকাশ অ্যাপ স্ট্যান্ডার্ড টপ বার (#0F6B56)
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
                            onClick = { vm.back() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "পেছনে যান",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সহীহ হাদিস সম্ভার",
                                fontFamily = fonts.kalpurush,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "রাসূলুল্লাহ (সা.)-এর প্রামাণ্য বাণী ও নির্দেশনা",
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
                            .height(48.dp)
                            .padding(horizontal = 4.dp),
                        placeholder = {
                            Text(
                                "হাদিস বা বিষয় খুঁজুন...",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.8f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedContainerColor = Color.White.copy(alpha = 0.15f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                }
            }

            // ২. ক্যাটাগরি চিপস
            Spacer(Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) Color(0xFF0F6B56) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F6B56) else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // ৩. হাদিস তালিকা
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredHadiths) { hadith ->
                    val isFav = favoriteIds.contains(hadith.id)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // কার্ড টপবার
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF0F6B56).copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = hadith.grade,
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F6B56),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = hadith.titleBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val shareText = "${hadith.titleBn}\n\n${hadith.arabic}\n\nবর্ণনাকারী: ${hadith.narratorBn}\n\nবঙ্গানুবাদ: ${hadith.translationBn}\n\nসূত্র: ${hadith.bookRef}\n\n— ক্বারীয়ানা কুরআন অ্যাপ\nডাউনলোড: https://karianaquran.com/download/qariana_quran.apk"
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, hadith.titleBn)
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "হাদিস শেয়ার করুন"))
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Share, "শেয়ার", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }

                                    Spacer(Modifier.width(2.dp))

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            val clip = ClipData.newPlainText("Hadith", "${hadith.titleBn}\n${hadith.arabic}\n${hadith.translationBn}\n[${hadith.bookRef}]")
                                            clipboard?.setPrimaryClip(clip)
                                            Toast.makeText(context, "হাদিস কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, "কপি", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }

                                    Spacer(Modifier.width(4.dp))

                                    IconButton(
                                        onClick = {
                                            if (isFav) favoriteIds.remove(hadith.id) else favoriteIds.add(hadith.id)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isFav) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "বুকমার্ক",
                                            tint = if (isFav) Color(0xFFD4AF37) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = hadith.narratorBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(Modifier.height(10.dp))

                            // আরবি হাদিস
                            Text(
                                text = hadith.arabic,
                                fontFamily = fonts.arabic,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F6B56),
                                lineHeight = 32.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(10.dp))

                            // বাংলা অনুবাদ
                            Text(
                                text = hadith.translationBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 19.sp
                            )

                            Spacer(Modifier.height(10.dp))

                            // কিতাব রেফারেন্স
                            Text(
                                text = "গ্রন্থসূত্র: ${hadith.bookRef}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color(0xFFD4AF37),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

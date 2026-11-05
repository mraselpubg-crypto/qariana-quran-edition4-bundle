package com.karianaquran.reader.ui.worship

import android.content.Intent

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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

data class DuaItem(
    val id: String,
    val titleBn: String,
    val category: String,
    val arabic: String,
    val transliteration: String,
    val meaningBn: String,
    val reference: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaScreen(
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

    val allDuas = remember {
        listOf(
            DuaItem(
                id = "1",
                titleBn = "পিতা-মাতার জন্য দো'আ",
                category = "পিতা-মাতা",
                arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                transliteration = "রাব্বির হামহুমা কামা রাব্বাইয়ানী সাগীরা।",
                meaningBn = "হে আমার রব! তাদের উভয়ের প্রতি রহম করুন, যেমন তারা শৈশবে আমাকে লালন-পালন করেছেন।",
                reference = "সূরা বনী ইসরাঈল: ২৪"
            ),
            DuaItem(
                id = "2",
                titleBn = "দুনিয়া ও আখিরাতের কল্যাণ",
                category = "দৈনন্দিন দো'আ",
                arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                transliteration = "রাব্বানা আতিনা ফিদ-দুনয়া হাসানাতাওঁ ওয়া ফিল আখিরাতি হাসানাতাওঁ ওয়া কিনা আযাবান নার।",
                meaningBn = "হে আমাদের রব! আমাদের দুনিয়াতেও কল্যাণ দিন এবং আখিরাতেও কল্যাণ দিন এবং জাহান্নামের শাস্তি থেকে বাঁচান।",
                reference = "সূরা বাক্বারা: ২০১"
            ),
            DuaItem(
                id = "3",
                titleBn = "বিপদ ও দুশ্চিন্তা মুক্তির দো'আ (ইউনুস আ.)",
                category = "বিপদ ও মুক্তি",
                arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
                transliteration = "লা ইলাহা ইল্লা আনতা সুবহানাকা ইন্নি কুনতু মিনায যলিমীন।",
                meaningBn = "আপনি ব্যতীত কোনো সত্য উপাস্য নেই, আপনি মহাপবিত্র! নিশ্চয়ই আমি যালেমদের অন্তর্ভুক্ত ছিলাম।",
                reference = "সূরা আল-আম্বিয়া: ৮৭"
            ),
            DuaItem(
                id = "4",
                titleBn = "সাইয়্যিদুল ইস্তিগফার (শ্রেষ্ঠ ক্ষমা প্রার্থনা)",
                category = "ক্ষমা প্রার্থনা",
                arabic = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
                transliteration = "আল্লাহুম্মা আনতা রব্বী লা ইলাহা ইল্লা আনতা খালাক্বতানী ওয়া আনা আবদুকা, ওয়া আনা আলা আহদিকা ওয়া ওয়াদিকা মাসতাত্বা'তু, আ'ঊযু বিকা মিন শাররি মা সানা'তু, আবূউ লাকা বিনি'মাতিকা আলাইয়্যা, ওয়া আবূউ লাকা বিযাম্বী ফাগফির লী, ফাইন্নাহূ লা ইয়াগফিরুয যুনূবা ইল্লা আনতা।",
                meaningBn = "হে আল্লাহ! আপনিই আমার প্রতিপালক। আপনি ছাড়া কোনো ইলাহ নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা। আমি আমার সাধ্যমতো আপনার অঙ্গীকার ও প্রতিশ্রুতির উপর আছি। আমি আমার কৃতকর্মের অনিষ্ট থেকে আপনার আশ্রয় চাই। আমার উপর আপনার প্রদত্ত নিয়ামত স্বীকার করছি এবং আমার গুনাহ স্বীকার করছি। অতএব আপনি আমাকে ক্ষমা করুন। নিশ্চয় আপনি ছাড়া আর কেউ গুনাহ ক্ষমা করতে পারে না।",
                reference = "সহীহ আল-বুখারী: ৬৩০৬"
            ),
            DuaItem(
                id = "5",
                titleBn = "ঘুম থেকে ওঠার দো'আ",
                category = "ঘুম ও জাগরণ",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                transliteration = "আলহামদু লিল্লাহিল্লাযী আহ্ইয়ানা বা'দা মা আমাতানা ওয়া ইলাইহিন নুশূর।",
                meaningBn = "সকল প্রশংসা আল্লাহর জন্য, যিনি আমাদেরকে মৃত্যু (ঘুম) দেওয়ার পর পুনরায় জীবিত করলেন এবং তাঁর দিকেই সবার পুনরুত্থান।",
                reference = "সহীহ আল-বুখারী: ৬৩১২"
            ),
            DuaItem(
                id = "6",
                titleBn = "ইলম ও জ্ঞান বৃদ্ধির দো'আ",
                category = "দৈনন্দিন দো'আ",
                arabic = "رَّبِّ زِدْنِي عِلْمًا",
                transliteration = "রাব্বি যিদনী ইলমা।",
                meaningBn = "হে আমার পালনকর্তা! আমার জ্ঞান বৃদ্ধি করে দিন।",
                reference = "সূরা ত্বা-হা: ১১৪"
            ),
            DuaItem(
                id = "7",
                titleBn = "ঈমানের উপর অটল থাকার দো'আ",
                category = "ক্ষমা প্রার্থনা",
                arabic = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ",
                transliteration = "রাব্বানা লা তুযিগ কুলুবানা বা'দা ইয হাদাইতানা ওয়া হাব লানা মিল্লাদুনকা রাহমাহ, ইন্নাকা আনতাল ওয়াহ্হাব।",
                meaningBn = "হে আমাদের রব! সরল পথ দেখানোর পর আপনি আমাদের অন্তরকে সত্যলঙ্ঘনকারী করবেন না এবং আপনার নিকট থেকে আমাদের অনুগ্রহ দান করুন। নিশ্চয় আপনি মহাদাতা।",
                reference = "সূরা আল-ইমরান: ৮"
            )
        )
    }

    val categories = listOf("সকল", "পছন্দের দো'আ", "দৈনন্দিন দো'আ", "বিপদ ও মুক্তি", "ক্ষমা প্রার্থনা", "পিতা-মাতা", "ঘুম ও জাগরণ")

    val filteredDuas = allDuas.filter { d ->
        val matchesQuery = searchQuery.isEmpty() ||
            d.titleBn.contains(searchQuery, ignoreCase = true) ||
            d.meaningBn.contains(searchQuery, ignoreCase = true) ||
            d.arabic.contains(searchQuery, ignoreCase = true)
        val matchesCat = when (selectedCategory) {
            "সকল" -> true
            "পছন্দের দো'আ" -> favoriteIds.contains(d.id)
            else -> d.category == selectedCategory
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
                                text = "হিসনুল মুসলিম ও দো'আ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "কুরআন ও সুন্নাহ থেকে সংকলিত প্রামাণ্য দো'আ",
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
                                "দো'আ খুঁজুন...",
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

            // ৩. দো'আ তালিকা
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDuas) { dua ->
                    val isFav = favoriteIds.contains(dua.id)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // কার্ড হেডার
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F6B56).copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = dua.titleBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F6B56),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val shareText = "${dua.titleBn}\n\n${dua.arabic}\n\nউচ্চারণ: ${dua.transliteration}\n\nঅর্থ: ${dua.meaningBn}\n\nসূত্র: ${dua.reference}\n\n— ক্বারীয়ানা কুরআন অ্যাপ\nডাউনলোড: https://karianaquran.com/download/qariana_quran.apk"
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, dua.titleBn)
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "দো'আ শেয়ার করুন"))
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Share, "শেয়ার", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }

                                    Spacer(Modifier.width(2.dp))

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            val clip = ClipData.newPlainText("Dua", "${dua.titleBn}\n${dua.arabic}\n${dua.transliteration}\n${dua.meaningBn}\n[${dua.reference}]")
                                            clipboard?.setPrimaryClip(clip)
                                            Toast.makeText(context, "দো'আ কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, "কপি", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    }

                                    Spacer(Modifier.width(4.dp))

                                    IconButton(
                                        onClick = {
                                            if (isFav) favoriteIds.remove(dua.id) else favoriteIds.add(dua.id)
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

                            Spacer(Modifier.height(12.dp))

                            // আরবি টেক্সট
                            Text(
                                text = dua.arabic,
                                fontFamily = fonts.arabic,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F6B56),
                                lineHeight = 34.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(10.dp))

                            // বাংলা উচ্চারণ
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "উচ্চারণ: ${dua.transliteration}",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            // বাংলা অর্থ
                            Text(
                                text = "অর্থ: ${dua.meaningBn}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 19.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            // তথ্যসূত্র
                            Text(
                                text = "সূত্র: ${dua.reference}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.5.sp,
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

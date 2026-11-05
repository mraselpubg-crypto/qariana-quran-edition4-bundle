package com.karianaquran.reader.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.RealBookCover
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.LightBlackText
import com.karianaquran.reader.ui.home.MutedText
import com.karianaquran.reader.ui.home.PrimaryDarkEmerald
import com.karianaquran.reader.ui.theme.LocalAppFonts

// =============================================================================
// Legacy / currently-unused Home sheets, dialogs and showcase composables.
// Extracted verbatim from HomeScreen.kt (pure refactor, behavior unchanged).
// =============================================================================

// -------------------------------------------------------------------------------------------------
// ৪. ফ্ল্যাগশিপ কিতাব শোকেস (HomeFlagshipPublicationCard — রাজকীয় প্রিমিয়াম ডিজাইন ও দ্বিভাষিক রেসপনসিভনেস)
// -------------------------------------------------------------------------------------------------
@Composable
private fun HomeFlagshipPublicationCard(
    isDark: Boolean,
    isUnlocked: Boolean,
    onReadPreview: () -> Unit,
    onUnlockOrOpen: () -> Unit,
    onCardClick: () -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val publication = Publications.ortho

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFFD4AF37).copy(alpha = 0.50f) else Color(0xFFD4AF37).copy(alpha = 0.80f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = if (isDark) listOf(Color(0xFF04241B), Color(0xFF09392B), Color(0xFF064E3B))
                        else listOf(Color(0xFF064E3B), Color(0xFF0A5843), Color(0xFF0E6950))
                    )
                )
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // টপ রো: ৩ডি কভার (কমপ্যাক্ট ৭৬ডিপি) + টাইটেল ও ব্যাজ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // বাম পাশ: কমপ্যাক্ট ৭৬ডিপি কভার
                    RealBookCover(
                        publication = publication,
                        height = 76.dp,
                        showSpine = true,
                        onClick = onCardClick
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // ডান পাশ: টাইটেল, ব্যাজ ও বিবরণ
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // ব্যাজ ও প্রাইস রো (সিঙ্গেল লাইন)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = Color(0xFFD4AF37).copy(alpha = 0.22f),
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFFD4AF37).copy(alpha = 0.75f))
                            ) {
                                Text(
                                    text = if (isEnglish) "Complete 30 Paras" else "৩০ পারা পূর্ণাঙ্গ",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE68A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp)
                                )
                            }

                            Text(
                                text = if (isEnglish) publication.priceIntl else publication.priceBd,
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE68A)
                            )
                        }

                        // মূল নাম
                        Text(
                            text = if (isEnglish) "Qariana Ortho-Quran" else "ক্বারীয়ানা অর্থ-কুরআন শরীফ",
                            fontFamily = fonts.kalpurush,
                            fontSize = if (isEnglish) 14.sp else 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // সাবটাইটেল (কমপ্যাক্ট ১ লাইন)
                        Text(
                            text = if (isEnglish)
                                "Pronunciation, Word Meaning & Color Tajweed"
                            else
                                "উচ্চারণ, শব্দার্থ ও রঙিন তাজবীদসহ মুদ্রিত সংস্করণ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 10.5.sp,
                            color = Color(0xFFF1F5F9).copy(alpha = 0.9f),
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // বই পরিচিতি লিংক
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onCardClick() }
                                .padding(vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "Book Overview & Editions →" else "বইয়ের পরিচিতি ও ৩টি সংস্করণ →",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFDE68A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(8.dp))

                // বটম অ্যাকশন রো: কমপ্যাক্ট ৩৪ডিপি দুটি বাটন
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ১. ফ্রি প্রিভিউ বাটন
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onReadPreview() },
                        color = Color.White.copy(alpha = 0.16f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.40f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isEnglish) "Free Preview" else "ফ্রি প্রিভিউ (১–১০)",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }

                    // ২. আনলক বা ওপেন বাটন
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onUnlockOrOpen() },
                        color = if (isUnlocked) Color(0xFF10B981) else Color(0xFFD4AF37),
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.AutoMirrored.Filled.MenuBook else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) Color.White else Color(0xFF04241B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isUnlocked) {
                                    if (isEnglish) "Open Quran" else "কুরআন খুলুন"
                                } else {
                                    if (isEnglish) "Unlock Book" else "সম্পূর্ণ আনলক"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color.White else Color(0xFF04241B),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ৮. আরও ফিচার মডাল শীট (MoreFeaturesModalSheet)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreFeaturesModalSheet(
    isDark: Boolean,
    onDismiss: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    val fonts = LocalAppFonts.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF101B24) else Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "অন্যান্য ইসলামিক সুবিধাসমূহ",
                fontFamily = fonts.kalpurush,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PrimaryDarkEmerald
            )
            Spacer(modifier = Modifier.height(16.dp))

            val items = listOf(
                Triple("হাদিস শরীফ", R.drawable.ic_hadith, "hadith"),
                Triple("৬ কালিমা", R.drawable.kalima, "kalma"),
                Triple("নামাজ শিক্ষা", R.drawable.yaseen, "guide"),
                Triple("রমজান সময়সূচি", R.drawable.ic_ramadantimes2, "ramadan"),
                Triple("হিজরি ক্যালেন্ডার", R.drawable.ic_calendar, "calendar"),
                Triple("যাকাত ক্যালকুলেটর", R.drawable.zakat, "zakat"),
                Triple("তারিখ রূপান্তর", R.drawable.dateconverticon, "calendar"),
                Triple("মসজিদ নির্দেশক", R.drawable.mosqueicon, "qibla"),
                Triple("আমাদের সম্পর্কে", R.drawable.aboutus, "about")
            )

            // ৩x৩ গ্রিড
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { (title, iconRes, actionKey) ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clickable { onItemClick(actionKey) },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isDark) Color(0xFF182937) else Color(0xFFF8FAF9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF263D54) else Color(0xFFE2E8F0))
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = title,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = title,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ৯. আল্লাহর ৯৯ নাম মডাল শীট (NinetyNineNamesModalSheet)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NinetyNineNamesModalSheet(
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val names = remember { getSample99Names() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF101B24) else Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "আল্লাহর ৯৯টি গুণবাচক নাম (আসমাউল হুসনা)",
                    fontFamily = fonts.kalpurush,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else PrimaryDarkEmerald
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(names) { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF182937) else Color(0xFFF8FAF9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF263D54) else Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = if (isDark) Color(0xFF263D54) else Color(0xFFDCFCE7)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.index.toBn(),
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryDarkEmerald
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.nameBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.meaning,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.sp,
                                        color = MutedText
                                    )
                                }
                            }
                            Text(
                                text = item.nameAr,
                                fontFamily = fonts.arabic,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkEmerald
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ১০. ইসলামের ৫টি স্তম্ভ মডাল শীট (PillarsOfIslamModalSheet)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PillarsOfIslamModalSheet(
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val pillars = listOf(
        Pair("১. কালিমা (শাহাদাত)", "আল্লাহ ছাড়া কোনো ইলাহ নেই এবং মুহাম্মাদ (সা.) তাঁর বান্দা ও রাসূল—এ কথার সাক্ষ্য দেওয়া।"),
        Pair("২. সালাত (নামাজ)", "দৈনিক ৫ ওয়াক্ত নামাজ নির্ধারিত সময়ে যথাযথভাবে আদায় করা।"),
        Pair("৩. সিয়াম (রোজা)", "পবিত্র মাহে রমজানের ৩০ দিন সুবহে সাদিক থেকে সূর্যাস্ত পর্যন্ত রোজা রাখা।"),
        Pair("৪. যাকাত", "সাহেবে নিসাব মুসলিমদের বাৎসরিক উদ্বৃত্ত ধন-সম্পদের ২.৫% হকদারদের মাঝে বিতরণ করা।"),
        Pair("৫. হজ", "শারীরিক ও আর্থিকভাবে সক্ষম ব্যক্তিদের জীবনে অন্তত একবার পবিত্র কাবা শরীফ তাওয়াফ ও হজ সম্পন্ন করা।")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF101B24) else Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "ইসলামের ৫টি মৌলিক স্তম্ভ",
                fontFamily = fonts.kalpurush,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PrimaryDarkEmerald
            )
            Spacer(modifier = Modifier.height(14.dp))
            pillars.forEach { (title, desc) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) Color(0xFF182937) else Color(0xFFF8FAF9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF263D54) else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = title,
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkEmerald
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else LightBlackText
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ১১. কিবলা কম্পাস মডাল শীট (QiblaCompassModalSheet)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QiblaCompassModalSheet(
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF101B24) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "কিবলা কম্পাস (কাবার দিক)",
                fontFamily = fonts.kalpurush,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PrimaryDarkEmerald
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ঢাকা থেকে কিবলার কোণ: ২৪২° (পশ্চিম-দক্ষিণ-পশ্চিম)",
                fontFamily = fonts.kalpurush,
                fontSize = 12.sp,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(20.dp))

            // কম্পাস ডায়াল
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = if (isDark) Color(0xFF182937) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(3.dp, PrimaryDarkEmerald)
                ) {}

                Image(
                    painter = painterResource(id = R.drawable.compassicon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .rotate(242f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "মক্কা শরীফের দূরত্ব: প্রায় ৪,৮৫০ কিলোমিটার",
                fontFamily = fonts.kalpurush,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryDarkEmerald
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ১২. রমজান শিডিউল মডাল শীট (RamadanScheduleModalSheet)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RamadanScheduleModalSheet(
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF101B24) else Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "পবিত্র মাহে রমজান ও সেহরি-ইফতার সময়সূচি",
                fontFamily = fonts.kalpurush,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PrimaryDarkEmerald
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF182937) else Color(0xFFDCFCE7).copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryDarkEmerald.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "সেহরি শেষ সময়: ভোর ০৪:৪৫ মি.",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ইফতার শুরু সময়: সন্ধ্যা ০৬:১২ মি.",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "রমজানের নিয়ত ও ইফতারের দোয়া অ্যাপের 'দোয়া' মেনুতে সংরক্ষিত রয়েছে।",
                fontFamily = fonts.kalpurush,
                fontSize = 12.sp,
                color = MutedText
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ১৩. কুইক সেটিংস মডাল শীট (QuickSettingsModalSheet)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickSettingsModalSheet(
    vm: AppViewModel,
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (isDark) Color(0xFF101B24) else Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "অ্যাপ ও রিডার সেটিংস",
                fontFamily = fonts.kalpurush,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PrimaryDarkEmerald
            )
            Spacer(modifier = Modifier.height(14.dp))

            // থিম অপশনস
            Text(
                text = "থিম পছন্দ করুন:",
                fontFamily = fonts.kalpurush,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("light", "লাইট"),
                    Pair("dark", "ডার্ক"),
                    Pair("sepia", "সেপিয়া"),
                    Pair("emerald", "এমেরাল্ড")
                ).forEach { (id, label) ->
                    val isSelected = readerSettings.themeId == id
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { vm.updateSettings { it.copy(themeId = id) } },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryDarkEmerald else (if (isDark) Color(0xFF182937) else Color(0xFFF1F5F9)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryDarkEmerald else Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = label,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else (if (isDark) Color.White else LightBlackText),
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // রমজান কাউন্টডাউন ব্যানার সুইচ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { vm.toggleRamadanCountdown() }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "রমজান কাউন্টডাউন হোম ব্যানার",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else LightBlackText
                    )
                    Text(
                        text = "হোম স্ক্রিনে রমজান কাউন্টডাউন সিন কার্ড প্রদর্শন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
                Switch(
                    checked = readerSettings.showRamadanCountdown,
                    onCheckedChange = { vm.toggleRamadanCountdown() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryDarkEmerald
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // আযান সাউন্ড সতর্কতা সুইচ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { vm.toggleAzanSound() }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "নামাজের সময়ে আযান সতর্কতা",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else LightBlackText
                    )
                    Text(
                        text = "ওয়াক্ত শুরু হলে আযান অ্যালার্ট ও সাউন্ড প্লে",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
                Switch(
                    checked = readerSettings.azanSoundEnabled,
                    onCheckedChange = { vm.toggleAzanSound() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryDarkEmerald
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // অ্যাপ আপডেট চেক বাটন (ম্যানুয়াল রিমোট চেক)
            Surface(
                onClick = {
                    onDismiss()
                    vm.checkAppUpdate(manual = true)
                },
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryDarkEmerald.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = PrimaryDarkEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "অ্যাপের নতুন আপডেট চেক করুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else LightBlackText
                        )
                    }
                    Text(
                        text = "v${com.karianaquran.reader.BuildConfig.VERSION_NAME}",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryDarkEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private data class NameItem(val index: Int, val nameAr: String, val nameBn: String, val meaning: String)

private fun getSample99Names(): List<NameItem> = listOf(
    NameItem(1, "الرَّحْمَنُ", "আর-রহমান", "পরম দয়ালু"),
    NameItem(2, "الرَّحِيمُ", "আর-রাহীম", "অতিশয় মেহেরবান"),
    NameItem(3, "الْمَلِكُ", "আল-মালিক", "সর্বভৌম ক্ষমতার অধিকারী"),
    NameItem(4, "الْقُدُّوسُ", "আল-কুদ্দুস", "মহা পবিত্র"),
    NameItem(5, "السَّلَامُ", "আস-সালাম", "শান্তিদাতা"),
    NameItem(6, "الْمُؤْمِنُ", "আল-মু'মিন", "নিরাপত্তা ও ঈমানদানকারী"),
    NameItem(7, "الْمُهَيْمিনُ", "আল-মুহাইমিন", "রক্ষণাবেক্ষণকারী"),
    NameItem(8, "الْعَزِيزُ", "আল-আযীয", "পরাক্রমশালী"),
    NameItem(9, "الْجَبَّارُ", "আল-জাব্বার", "দুর্নিবার"),
    NameItem(10, "الْمُتَكَبِّرُ", "আল-মুতাকাব্বির", "মহিমান্বিত")
)

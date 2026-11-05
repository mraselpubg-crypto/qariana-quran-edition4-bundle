package com.karianaquran.reader.ui.pay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * MindBloom আন্তর্জাতিক ডিজাইনের আদলে ক্বারীয়ানা কুরআন মেম্বারশিপ প্যাকেজসমূহ
 * ৩টি সেগমেন্টেড ট্যাব: সিলভার (কাস্টমাইজড + লাইভ প্রাইস), গোল্ড (ফিক্সড) ও ডায়মন্ড (ভিআইপি)
 * সব প্যাকেজে অ্যাড রিমুভ (বিজ্ঞাপনমুক্ত) সুবিধা অন্তর্ভুক্ত।
 */
@Composable
fun MembershipPackagesScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"

    // নির্বাচিত ট্যাব: 0 -> সিলভার, 1 -> গোল্ড, 2 -> ডায়মন্ড
    var selectedTab by remember { mutableIntStateOf(1) } // ডিফল্ট গোল্ড (জনপ্রিয়)

    // সিলভার প্যাকেজের ইন্টারেক্টিভ চেকবক্স স্টেটসমূহ
    var silverSelectPopular by remember { mutableStateOf(true) } // জনপ্রিয় সংস্করণ (৳৫০০)
    var silverSelectModern by remember { mutableStateOf(false) }  // আধুনিক মুসহাফ সংস্করণ (+৳৫০০)
    var silverSelectVerses by remember { mutableStateOf(false) }  // আয়াত ও শব্দার্থ সংস্করণ (+৳৫০০)

    // সিলভার প্যাকেজের ডায়নামিক লাইভ প্রাইস ক্যালকুলেশন
    val silverPrice by remember {
        derivedStateOf {
            val count = (if (silverSelectPopular) 1 else 0) +
                    (if (silverSelectModern) 1 else 0) +
                    (if (silverSelectVerses) 1 else 0)
            maxOf(1, count) * 500
        }
    }

    val bgPage = if (isDark) Color(0xFF091410) else Color(0xFFFBFBFD)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgPage)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // =========================================================================
        // ১. স্লিম টপ বার: ব্যাক অ্যারো, টাইটেল ও অপশন মেনু
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(onClick = { vm.back() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = if (isEnglish) "Subscription Plans" else "মেম্বারশিপ প্যাকেজসমূহ",
                fontFamily = fonts.kalpurush,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Surface(
                shape = CircleShape,
                color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(onClick = { /* Option info */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // =========================================================================
            // ২. টাইটেল ও সাবটাইটেল (রেফারেন্স ডিজাইনের মতো)
            // =========================================================================
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (isEnglish) "Upgrade to Qariana+" else "ক্বারীয়ানা কুরআনে আপগ্রেড করুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isEnglish)
                        "Read all 30 Paras offline with authentic Tajweed rules, zero advertisements, and lifetime personal access."
                    else
                        "৩০ পারা অফলাইন তিলাওয়াত, বিশুদ্ধ রঙিন তাজবীদ, বিজ্ঞাপনমুক্ত অভিজ্ঞতা ও আজীবন অ্যাক্সেস।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(Modifier.height(20.dp))
            }

            // =========================================================================
            // ৩. সেগমেন্টেড কন্ট্রোল ট্যাব (সিলভার, গোল্ড, ডায়মন্ড)
            // =========================================================================
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isDark) Color(0xFF13221A) else Color(0xFFECEFF3),
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PackageTabItem(
                            title = if (isEnglish) "Silver" else "সিলভার",
                            subtitle = if (isEnglish) "Custom" else "কাস্টম",
                            isSelected = selectedTab == 0,
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        PackageTabItem(
                            title = if (isEnglish) "Gold" else "গোল্ড",
                            subtitle = if (isEnglish) "Popular" else "জনপ্রিয়",
                            isSelected = selectedTab == 1,
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                        PackageTabItem(
                            title = if (isEnglish) "Diamond" else "ডায়মন্ড",
                            subtitle = if (isEnglish) "VIP" else "ভিআইপি",
                            isSelected = selectedTab == 2,
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 2 }
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
            }

            // =========================================================================
            // ৪. মূল ফ্রস্টেড গ্লোয়িং বর্ডার সাবস্ক্রিপশন কার্ড
            // =========================================================================
            item {
                val cardBorderBrush = if (isDark) {
                    Brush.verticalGradient(listOf(Color(0xFF10B981).copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF93C5FD).copy(alpha = 0.7f), Color(0xFFA7F3D0).copy(alpha = 0.5f)))
                }

                val cardBg = if (isDark) Color(0xFF101D17) else Color.White

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = cardBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(24.dp), ambientColor = Color(0xFF0F6B56).copy(alpha = 0.15f))
                        .border(BorderStroke(1.5.dp, cardBorderBrush), RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        // কার্ড হেডার: বড় প্রাইস ও বিবরণ
                        when (selectedTab) {
                            0 -> {
                                // ---- সিলভার প্যাকেজ (কাস্টমাইজড + লাইভ প্রাইস) ----
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "৳ ${silverPrice.toBn()}",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = if (isEnglish) " /lifetime" else " /আজীবন",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 14.sp,
                                            color = textSecondary,
                                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0F6B56).copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Customizable" else "কাস্টমাইজড",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F6B56),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish)
                                        "Select any edition of Quran for lifetime. Dynamic price updates instantly."
                                    else
                                        "আপনার পছন্দ অনুযায়ী যেকোনো সংস্করণ বা ফিচার বেছে নিন। লাইভ প্রাইস স্বয়ংক্রিয়ভাবে আপডেট হবে:",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = textSecondary
                                )

                                Spacer(Modifier.height(14.dp))

                                // চেকবক্স সিলেকশন প্যানেল
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    EditionCheckboxItem(
                                        title = if (isEnglish) "Popular Edition (৳500)" else "জনপ্রিয় মুদ্রিত সংস্করণ (৳৫০০)",
                                        subtitle = if (isEnglish) "Printed layout with pronunciation & Tajweed" else "ক্লাসিক ইনডিজাইন প্রিন্ট লেআউট ও উচ্চারণ",
                                        checked = silverSelectPopular,
                                        onCheckedChange = {
                                            silverSelectPopular = it
                                            if (!silverSelectPopular && !silverSelectModern && !silverSelectVerses) {
                                                silverSelectPopular = true // কমপক্ষে ১টি সিলেক্টেড থাকবে
                                            }
                                        },
                                        isDark = isDark,
                                        fonts = fonts
                                    )
                                    EditionCheckboxItem(
                                        title = if (isEnglish) "Modern Mushaf Edition (+৳500)" else "আধুনিক ভেক্টর মুসহাফ সংস্করণ (+৳৫০০)",
                                        subtitle = if (isEnglish) "Vector sharpness with interactive word taps" else "ভেক্টর শার্পনেস ও ইন্টারেক্টিভ শব্দ ট্যাপ",
                                        checked = silverSelectModern,
                                        onCheckedChange = {
                                            silverSelectModern = it
                                            if (!silverSelectPopular && !silverSelectModern && !silverSelectVerses) {
                                                silverSelectModern = true
                                            }
                                        },
                                        isDark = isDark,
                                        fonts = fonts
                                    )
                                    EditionCheckboxItem(
                                        title = if (isEnglish) "Ayah & Tafsir Edition (+৳500)" else "আয়াত ও বিস্তারিত তাফসীর সংস্করণ (+৳৫০০)",
                                        subtitle = if (isEnglish) "Word-by-word meaning and full Tafsir" else "শব্দে শব্দে অর্থ ও পূর্ণাঙ্গ বঙ্গানুবাদ",
                                        checked = silverSelectVerses,
                                        onCheckedChange = {
                                            silverSelectVerses = it
                                            if (!silverSelectPopular && !silverSelectModern && !silverSelectVerses) {
                                                silverSelectVerses = true
                                            }
                                        },
                                        isDark = isDark,
                                        fonts = fonts
                                    )
                                    // বিজ্ঞাপনমুক্ত আজীবন লাইসেন্স (সব প্যাকেজে ডিফল্ট অন্তর্ভুক্ত)
                                    EditionCheckboxItem(
                                        title = if (isEnglish) "Ad-Free Experience (Included - ৳0)" else "বিজ্ঞাপনমুক্ত আজীবন অভিজ্ঞতা (অন্তর্ভুক্ত - ৳০)",
                                        subtitle = if (isEnglish) "100% ad-free lifetime reading" else "কোনো প্রকার বিজ্ঞাপন ছাড়া আজীবন পড়ার নিশ্চয়তা",
                                        checked = true,
                                        enabled = false,
                                        onCheckedChange = {},
                                        isDark = isDark,
                                        fonts = fonts
                                    )
                                }
                            }
                            1 -> {
                                // ---- গোল্ড প্যাকেজ (ফিক্সড ৳১,০০০) ----
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "৳ ১,০০০",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = if (isEnglish) " /lifetime" else " /আজীবন",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 14.sp,
                                            color = textSecondary,
                                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFD4AF37).copy(alpha = 0.20f)
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Most Popular" else "সর্বাধিক জনপ্রিয়",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish)
                                        "All-in-one package: All 3 Quran editions unlocked forever on 1 personal device, fully ad-free."
                                    else
                                        "সর্বাধিক জনপ্রিয় অল-ইন-ওয়ান প্যাকেজ — সকল ৩টি সংস্করণ (জনপ্রিয়, আধুনিক ও আয়াত) + ১ ডিভাইসে আজীবন লাইসেন্স + বিজ্ঞাপনমুক্ত।",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.5.sp,
                                    color = textSecondary
                                )
                            }
                            else -> {
                                // ---- ডায়মন্ড প্যাকেজ (ফিক্সড ৳১,৫০০) ----
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "৳ ১,৫০০",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = if (isEnglish) " /lifetime" else " /আজীবন",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 14.sp,
                                            color = textSecondary,
                                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = if (isEnglish) "VIP Diamond" else "আজীবন সেরা ভ্যালু",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish)
                                        "Ultimate VIP access: All 3 editions + upcoming publications (Qaida, Ampara, Namaz, Tajweed) + multi-device backup + VIP support."
                                    else
                                        "আল্টিমেট ভিআইপি লাইসেন্স — সকল ৩টি সংস্করণ + কায়েদা, আমপারা ও ভবিষ্যৎ প্রকাশনা + মাল্টি-ডিভাইস/ক্লাউড ব্যাকআপ + ভিআইপি সাপোর্ট + বিজ্ঞাপনমুক্ত।",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.5.sp,
                                    color = textSecondary
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Features ডিভাইডার (রেফারেন্স স্ক্রিনশটের মতো: ── Features ──)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0))
                            Text(
                                text = if (isEnglish) "  Features  " else "  অন্তর্ভুক্ত সুবিধাসমূহ  ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0))
                        }

                        Spacer(Modifier.height(16.dp))

                        // ফিচার চেকলিস্ট
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            FeatureRowItem(
                                icon = Icons.Default.Block,
                                text = if (isEnglish) "100% Ad-Free Lifetime Experience" else "বিজ্ঞাপনমুক্ত প্রিমিয়াম অভিজ্ঞতা (Ad-Free)",
                                highlight = true,
                                isDark = isDark,
                                fonts = fonts
                            )
                            FeatureRowItem(
                                icon = Icons.Default.DownloadDone,
                                text = if (isEnglish) "Complete 30 Paras Offline Vault Download" else "সম্পূর্ণ ৩০ পারা অফলাইন ডাউনলোড ভল্ট",
                                isDark = isDark,
                                fonts = fonts
                            )
                            FeatureRowItem(
                                icon = Icons.Default.Translate,
                                text = if (isEnglish) "Word-by-word Bangla Meanings & Tajweed" else "প্রতি শব্দের উচ্চারণ, শব্দার্থ ও রঙিন তাজবীদ",
                                isDark = isDark,
                                fonts = fonts
                            )
                            FeatureRowItem(
                                icon = Icons.Default.Security,
                                text = if (selectedTab == 2)
                                    (if (isEnglish) "Multi-Device License Recovery & Binding" else "মাল্টি-ডিভাইস ও ইনস্ট্যান্ট লাইসেন্স রিকভারি")
                                else
                                    (if (isEnglish) "1 Personal Device Hardware Security Binding" else "১টি ব্যক্তিগত ডিভাইসে সুরক্ষিত লাইসেন্স লক"),
                                isDark = isDark,
                                fonts = fonts
                            )
                            FeatureRowItem(
                                icon = Icons.Default.CloudDone,
                                text = if (isEnglish) "Cloud Bookmarks & Reading Progress Sync" else "ক্লাউড সিঙ্ক ব্যাকআপ ও পড়ার ইতিহাস",
                                isDark = isDark,
                                fonts = fonts
                            )
                            FeatureRowItem(
                                icon = Icons.Default.FlashOn,
                                text = if (isEnglish) "0-ms Fast Vector Sharpness & Page Zoom" else "০ মিলি-সেকেন্ড ফাস্ট ভেক্টর শার্পনেস ও জুম",
                                isDark = isDark,
                                fonts = fonts
                            )
                            if (selectedTab == 2) {
                                FeatureRowItem(
                                    icon = Icons.Default.Star,
                                    text = if (isEnglish) "Free Access to Upcoming Publications & VIP Care" else "ভবিষ্যতের সকল ইসলামিক বইতে ফ্রি অ্যাক্সেস ও ভিআইপি সেবা",
                                    highlight = true,
                                    isDark = isDark,
                                    fonts = fonts
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        // =========================================================================
        // ৫. বটম স্টিকি কল-টু-অ্যাকশন (CTA বাটন)
        // =========================================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = bgPage,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val pkgId = when (selectedTab) {
                            0 -> "basic"
                            1 -> "standard"
                            else -> "vip_diamond"
                        }
                        vm.selectPackageAndProceed(pkgId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF0F6B56) else Color(0xFF091712)
                    )
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> if (isEnglish) "Proceed to Payment (৳$silverPrice) >" else "পেমেন্ট এগিয়ে নিন (৳${silverPrice.toBn()}) ➔"
                            1 -> if (isEnglish) "Upgrade to Gold (৳1,000) >" else "গোল্ডে আপগ্রেড করুন (৳১,০০০) ➔"
                            else -> if (isEnglish) "Upgrade to Diamond VIP (৳1,500) >" else "ডায়মন্ড ভিআইপি নিন (৳১,৫০০) ➔"
                        },
                        fontFamily = fonts.kalpurush,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = if (isEnglish)
                        "🛡️ 100% Secure Checkout via SMS Sync Pay (bKash/Nagad/Rocket)"
                    else
                        "🛡️ শতভাগ নিরাপদ লেনদেন • বিকাশ / নগদ / রকেট (এসএমএস সিঙ্ক পে)",
                    fontFamily = fonts.kalpurush,
                    fontSize = 10.5.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * সেগমেন্টেড ট্যাব আইটেম
 */
@Composable
private fun PackageTabItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fonts = LocalAppFonts.current
    val bg = if (isSelected) {
        if (isDark) Color(0xFF0F6B56) else Color.White
    } else {
        Color.Transparent
    }
    val textColor = if (isSelected) {
        if (isDark) Color.White else Color(0xFF0F172A)
    } else {
        if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        shadowElevation = if (isSelected && !isDark) 2.dp else 0.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontFamily = fonts.kalpurush,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = subtitle,
                fontFamily = fonts.kalpurush,
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * সিলভার প্যাকেজের ইন্টারেক্টিভ চেকবক্স আইটেম
 */
@Composable
private fun EditionCheckboxItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean,
    fonts: com.karianaquran.reader.ui.theme.AppFonts,
    enabled: Boolean = true
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (checked) {
            if (isDark) Color(0xFF132D23) else Color(0xFFF0FDF4)
        } else {
            if (isDark) Color(0xFF16231E) else Color(0xFFF8FAFC)
        },
        border = BorderStroke(
            1.dp,
            if (checked) Color(0xFF10B981).copy(alpha = 0.6f) else (if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
                enabled = enabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF0F6B56),
                    checkmarkColor = Color.White
                )
            )
            Spacer(Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontFamily = fonts.kalpurush,
                    fontSize = 10.5.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    }
}

/**
 * ফিচার লিস্ট রো
 */
@Composable
private fun FeatureRowItem(
    icon: ImageVector,
    text: String,
    isDark: Boolean,
    fonts: com.karianaquran.reader.ui.theme.AppFonts,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (highlight) Color(0xFFD4AF37) else Color(0xFF10B981),
            modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            fontFamily = fonts.kalpurush,
            fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
        )
    }
}

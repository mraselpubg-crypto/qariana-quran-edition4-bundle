package com.karianaquran.reader.ui.overview

import android.content.Intent
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import com.karianaquran.reader.ui.overview.EditionInfoBottomSheet
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.filled.WorkspacePremium
import com.karianaquran.reader.ui.pay.PackageSelectionBottomSheet
import com.karianaquran.reader.ui.pay.QARIANA_PACKAGES
import com.karianaquran.reader.ui.Screen
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.karianaquran.reader.data.remote.OfflineDownloadState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * ক্বারীয়ানা কুরআন — ডেডিকেটেড বুক ডিটেইলস ও সংস্করণ নির্বাচন স্ক্রিন।
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranOverviewScreen(
    resumePage: Int,
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    com.karianaquran.reader.ui.index.SurahIndexScreen(vm = vm, initialTab = 0, modifier = modifier)
}

/**
 * কুরআনের বই পরিচিতি ও ৩টি সংস্করণ নির্বাচন ট্যাব কনটেন্ট
 */
@Composable
fun QuranOverviewContent(
    resumePage: Int,
    vm: AppViewModel,
    onOpenSurahTab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val userData by vm.userData.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"
    val isUnlocked = vm.isUnlocked

    val effectivePage = if (resumePage in 1..611) resumePage else (userData.lastPage.coerceIn(1, 611))
    val currentMode = readerSettings.readingMode

    val pageBgColor = if (isDark) Color(0xFF0C1410) else Color(0xFFFBF9F5)
    val backdropColor = if (isDark) Color(0xFF13221A) else Color(0xFFF2ECE1)
    val primaryText = if (isDark) Color.White else Color(0xFF1E293B)
    val secondaryText = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(pageBgColor),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 620.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 4.dp, bottom = 112.dp)
        ) {
        // ১. সফট কার্ভড ব্যাকড্রপ ও সেন্টারে ভাসমান প্রিমিয়াম ৩ডি বুক + সংস্করণ সিলেকশন স্লাইডার ট্যাব
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(backdropColor)
                    .padding(top = 14.dp, bottom = 14.dp, start = 12.dp, end = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ৩ডি অ্যানিমেটেড বুক (ওপেন / ক্লোজ ও সিলেক্টেড সংস্করণের ২-পৃষ্ঠা স্প্রেড)
                    OverviewBookCenterpiece(
                        mode = currentMode,
                        isUnlocked = isUnlocked,
                        isDark = isDark,
                        onDirectOpenReader = {
                            val target = if (currentMode == ReadingMode.CLASSIC) {
                                if (effectivePage <= 1) Publications.CLASSIC_FIRST_SURAH_PAGE else effectivePage
                            } else {
                                effectivePage
                            }
                            vm.openReader(target)
                        }
                    )

                    Spacer(Modifier.height(14.dp))

                    // সংস্করণের ক্যাপসুল স্লাইডার ট্যাব (বুক ওপেন ক্লোজিং কার্ডের ভেতরেই অন্তর্ভুক্ত)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        color = if (isDark) Color(0xFF16251E) else Color(0xFFE5DFD4),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF1E382A) else Color(0xFFDCD4C7)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ১. প্রথম সংস্করণ: জনপ্রিয় সংস্করণ (Popular Edition)
                            EditionPillTab(
                                title = if (isEnglish) "Popular Edition" else "জনপ্রিয় সংস্করণ",
                                isSelected = currentMode == ReadingMode.CLASSIC,
                                isDark = isDark,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    vm.setReadingMode(ReadingMode.CLASSIC)
                                }
                            )

                            // ২. দ্বিতীয় সংস্করণ: নতুন সংস্করণ (New Vector Edition)
                            EditionPillTab(
                                title = if (isEnglish) "New Edition" else "নতুন সংস্করণ",
                                isSelected = currentMode == ReadingMode.PAGE,
                                isDark = isDark,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    vm.setReadingMode(ReadingMode.PAGE)
                                }
                            )

                            // ৩. তৃতীয় সংস্করণ: ডিজিটাল সংস্করণ (Digital Edition)
                            EditionPillTab(
                                title = if (isEnglish) "Digital Edition" else "ডিজিটাল সংস্করণ",
                                isSelected = currentMode == ReadingMode.AYAH,
                                isDark = isDark,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    vm.setReadingMode(ReadingMode.AYAH)
                                }
                            )
                        }
                    }
                }
            }
        }

        // ২. বইয়ের নাম ও ইনফো (i) আইকন রো
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEnglish) "Qariana Ortho-Quran" else "ক্বারীয়ানা অর্থ-কুরআন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryText
                    )
                    Text(
                        text = if (isEnglish) "Qariana Publications • 3 Distinct Editions" else "ক্বারীয়ানা পাবলিকেশন্স • ৩টি পূর্ণাঙ্গ সংস্করণ",
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryText
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF16251E) else Color(0xFFECE6DA),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { vm.navigate(Screen.EditionDetail(currentMode)) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "সংস্করণ পরিচিতি",
                            tint = if (isDark) Color(0xFFFFDF79) else Color(0xFF0F6B56),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "সংস্করণ বিবরণ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFFDF79) else Color(0xFF0F6B56)
                        )
                    }
                }
            }
        }

        // ৩. মেম্বারশিপ প্যাকেজ শোকেস কার্ড (৩-টায়ার সংক্ষেপ)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        if (isUnlocked) vm.openProfile() else vm.navigate(Screen.MembershipPackages)
                    },
                shape = RoundedCornerShape(18.dp),
                color = if (isDark) Color(0xFF13221A) else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (isDark) Color(0xFF2E533C) else Color(0xFFD4AF37).copy(alpha = 0.6f)
                ),
                shadowElevation = if (isDark) 0.dp else 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD4AF37).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFFFDF79) else Color(0xFFB45309),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isUnlocked) "আপনার লাইসেন্স সক্রিয় আছে" else "ক্বারীয়ানা মেম্বারশিপ প্যাকেজসমূহ",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "আজীবন অ্যাক্সেস • বিশেষ মেম্বারশিপ অফার",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 10.5.sp,
                                    color = secondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.wrapContentSize(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isUnlocked) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = if (isUnlocked) "আনলকড" else "৩টি প্যাকেজ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(0xFF065F46) else Color(0xFFB45309),
                                maxLines = 1,
                                softWrap = false,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // প্যাকেজ ৩টির সামারি স্ট্রিপ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PackageSummaryChip(
                            name = "বেসিক",
                            price = "৳ ৫০০",
                            subtitle = "১ সংস্করণ",
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                        PackageSummaryChip(
                            name = "স্ট্যান্ডার্ড",
                            price = "৳ ১,০০০",
                            subtitle = "২ সংস্করণ",
                            isPopular = true,
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                        PackageSummaryChip(
                            name = "ভিআইপি",
                            price = "৳ ১,৫০০",
                            subtitle = "সব ৩ সংস্করণ",
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (isUnlocked) {
                                vm.openProfile()
                            } else {
                                vm.navigate(Screen.MembershipPackages)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.Verified else Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFFDF79),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isUnlocked) "লাইসেন্স ও প্রোফাইল তথ্য দেখুন ›" else "প্যাকেজ বিস্তারিত দেখুন ও কিনুন ›",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // ৪. পেইড ইউজারের জন্য অফলাইন ডাউনলোড কার্ড
        if (isUnlocked) {
            item {
                val offlineState by vm.offlineDownloadState.collectAsState()
                Spacer(Modifier.height(6.dp))
                OfflineDownloadBannerCard(
                    state = offlineState,
                    isDark = isDark,
                    onStartDownload = { vm.startOfflineDownload() },
                    onCancelDownload = { vm.cancelOfflineDownload() }
                )
            }
        }

        // ৫. বড় রিড বাটন ও সূচিপত্র সুইচ বাটন
        item {
            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val target = if (currentMode == ReadingMode.CLASSIC) {
                            if (effectivePage <= 1) Publications.CLASSIC_FIRST_SURAH_PAGE else effectivePage
                        } else {
                            effectivePage
                        }
                        vm.openReader(target)
                    },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = Color(0xFF0F6B56).copy(alpha = 0.4f), spotColor = Color(0xFF0F6B56).copy(alpha = 0.5f))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0F6B56), Color(0xFF14886C))
                            ),
                            RoundedCornerShape(28.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFFFFDF79),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (effectivePage > 1) {
                                if (isEnglish) "Resume Reading (Page ${effectivePage})" else "পড়া চালিয়ে যান (পৃষ্ঠা ${effectivePage.toBn()})"
                            } else {
                                if (isEnglish) "Start Reading (Surah Al-Fatihah)" else "পড়া শুরু করুন (সূরা আল-ফাতিহা)"
                            },
                            fontFamily = fonts.kalpurush,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = onOpenSurahTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F6B56),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isEnglish) "Choose another page from Surah & Para Index" else "সূরা ও পারার সূচিপত্র থেকে অন্য পৃষ্ঠা বেছে নিন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F6B56)
                    )
                }
            }
        }
    }
}

    // বটমশিটগুলো ফুল-স্ক্রিন স্ক্রিনে (Screen.EditionDetail / Screen.MembershipPackages) মাইগ্রেট করা হয়েছে।
}

@Composable
private fun PackageSummaryChip(
    name: String,
    price: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    isPopular: Boolean = false,
    isDark: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val borderColor = if (isPopular) Color(0xFFD4AF37) else (if (isDark) Color(0xFF2E533C) else Color(0xFFE2E8F0))
    val bgColor = if (isPopular) (if (isDark) Color(0xFF2A2818) else Color(0xFFFEF9C3)) else (if (isDark) Color(0xFF16251E) else Color(0xFFF8FAFC))

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(if (isPopular) 1.2.dp else 0.8.dp, borderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                fontFamily = fonts.kalpurush,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1E293B)
            )
            Text(
                text = price,
                fontFamily = fonts.kalpurush,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isPopular) Color(0xFFB45309) else Color(0xFF0F6B56)
            )
            Text(
                text = subtitle,
                fontFamily = fonts.kalpurush,
                fontSize = 9.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun OverviewBookCenterpiece(
    mode: ReadingMode,
    isUnlocked: Boolean,
    isDark: Boolean,
    onDirectOpenReader: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val isEnglish = com.karianaquran.reader.ui.l10n.L10n.current.isEnglish

    val cardBg = if (isDark) Color(0xFF13221A) else Color(0xFFFCFAF4)
    val cardBorder = if (isDark) Color(0xFF1E382A) else Color(0xFFD4AF37).copy(alpha = 0.75f)
    val primaryColor = if (isDark) Color(0xFF34D399) else Color(0xFF064E3B)
    val textPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 256.dp),
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorder),
        shadowElevation = if (isDark) 0.dp else 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ১. শীর্ষ হেডার রো: নির্বাচিত সংস্করণ ব্যাজ ও সরাসরি পড়ুন বাটন
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f, fill = false),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF1E2E26) else Color(0xFF064E3B).copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = when (mode) {
                                ReadingMode.CLASSIC -> if (isEnglish) "Popular Edition" else "জনপ্রিয় সংস্করণ"
                                ReadingMode.PAGE -> if (isEnglish) "New Edition" else "নতুন সংস্করণ"
                                ReadingMode.AYAH -> if (isEnglish) "Digital Edition" else "ডিজিটাল সংস্করণ"
                                ReadingMode.HAFEZI -> if (isEnglish) "Hafezi Edition" else "হাফেজী সংস্করণ"
                            },
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Surface(
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onDirectOpenReader() },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F6B56),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFFFFDF79),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isEnglish) "Read Now" else "সরাসরি পড়ুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // ২. সেন্টারে নির্বাচিত সংস্করণের রেসপনসিভ প্রিভিউ কার্ড (কোনো টেক্সট ভাঙবে না)
            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(tween(280)) togetherWith fadeOut(tween(180))
                },
                label = "edition_showcase_preview"
            ) { selectedEdition ->
                when (selectedEdition) {
                    ReadingMode.CLASSIC -> {
                        // জনপ্রিয় সংস্করণ (অফসেট মুদ্রিত): কভার থেকে ৩ডি ওপেন হয়ে হুবহু পিডিএফ ২-পাতার স্প্রেড
                        AnimatedBookSpread(isClassic = true, isDark = isDark)
                    }
                    ReadingMode.PAGE -> {
                        // নতুন সংস্করণ (আর্ট পেপার ইনডিজাইন): কভার থেকে ৩ডি ওপেন হয়ে হুবহু পিডিএফ ২-পাতার স্প্রেড
                        AnimatedBookSpread(isClassic = false, isDark = isDark)
                    }
                    ReadingMode.HAFEZI -> {
                        // হাফেজী সংস্করণ
                        AnimatedBookSpread(isClassic = false, isDark = isDark)
                    }
                    ReadingMode.AYAH -> {
                        // ডিজিটাল সংস্করণ (আয়াত ও তাফসীর ভিউ প্রিভিউ)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0xFF16251E) else Color.White)
                                .border(0.8.dp, if (isDark) Color(0xFF234030) else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সূরা আল-ফাতিহা • আয়াত ১",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    maxLines = 1
                                )
                                Text(
                                    text = "পৃষ্ঠা ১ • তাফসীরসহ",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 10.sp,
                                    color = textSecondary,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ",
                                fontFamily = fonts.arabic,
                                fontSize = 19.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "উচ্চারণ: বিসমিল্লাহির রাহমানির রাহিম",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "পরম করুণাময় অতি দয়ালু আল্লাহর নামে শুরু করছি।",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                MiniFeatureTag("শব্দে শব্দে অর্থ", fonts.kalpurush, isDark, modifier = Modifier.weight(1f))
                                MiniFeatureTag("পূর্ণ তাফসীর", fonts.kalpurush, isDark, modifier = Modifier.weight(1f))
                                MiniFeatureTag("ডিজিটাল বুকমার্ক", fonts.kalpurush, isDark, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // ৩. ফুটার ইনফো বার (সকল স্ক্রিন প্রস্থে এক লাইনে সংক্ষিপ্ত ও সংঘর্ষমুক্ত)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (mode) {
                        ReadingMode.CLASSIC -> "অফসেট মুদ্রিত সংস্করণ • ৩০ পারা কুরআন"
                        ReadingMode.PAGE -> "রঙিন তাজবীদ ও অর্থসহ • ৩০ পারা কুরআন"
                        ReadingMode.AYAH -> "আয়াতভিত্তিক উচ্চারণ, অর্থ ও তাফসীর"
                        ReadingMode.HAFEZI -> "১৫ ও ১৪ লাইন হাফেজী ভেক্টর মুসহাফ"
                    },
                    fontFamily = fonts.kalpurush,
                    fontSize = 10.5.sp,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (mode == ReadingMode.AYAH) "অডিও ও ওয়ার্ড ট্যাপ" else "স্মুথ সোয়াইপ ও জুম",
                    fontFamily = fonts.kalpurush,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
private fun MiniFeatureTag(
    text: String,
    font: androidx.compose.ui.text.font.FontFamily,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isDark) Color(0xFF1E382A) else Color(0xFFE2E8F0).copy(alpha = 0.65f),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 3.dp)
        ) {
            Text(
                text = text,
                fontFamily = font,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF334155),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EditionPillTab(
    title: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current

    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> Color(0xFF0F6B56)
            isSelected && !isDark -> Color.White
            else -> Color.Transparent
        },
        animationSpec = tween(220),
        label = "pill_bg"
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> Color.White
            isSelected && !isDark -> Color(0xFF0F6B56)
            else -> if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        },
        animationSpec = tween(220),
        label = "pill_text"
    )

    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        color = bgColor,
        shadowElevation = if (isSelected && !isDark) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 2.dp)) {
            Text(
                text = title,
                fontFamily = fonts.kalpurush,
                fontSize = 11.sp,
                letterSpacing = (-0.2).sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * ছোট মেটাডাটা চিপ
 */
@Composable
private fun MetaChip(
    text: String,
    isDark: Boolean,
    isHighlight: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
            isHighlight -> Color(0xFF0F6B56).copy(alpha = 0.15f)
            isDark -> Color(0xFF1E2E26)
            else -> Color(0xFFEFE9DF)
        }
    ) {
        Text(
            text = text,
            fontFamily = fonts.kalpurush,
            fontSize = 11.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isHighlight -> Color(0xFF0F6B56)
                isDark -> Color(0xFFCBD5E1)
                else -> Color(0xFF475569)
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/**
 * ৮-পয়েন্ট ইসলামিক স্টার পাথ জেনারেটর
 */
private fun overviewStarPath(center: Offset, outerR: Float, innerR: Float, points: Int): Path {
    val path = Path()
    val angle = PI / points
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerR else innerR
        val a = i * angle - PI / 2
        val x = center.x + (r * cos(a)).toFloat()
        val y = center.y + (r * sin(a)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Composable
fun OfflineDownloadBannerCard(
    state: OfflineDownloadState,
    isDark: Boolean,
    onStartDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val containerBg = if (isDark) Color(0xFF10281F) else Color(0xFFEBF6F1)
    val borderColor = if (isDark) Color(0xFF1E4D38) else Color(0xFFB7DFCD)
    val emeraldText = if (isDark) Color(0xFF34D399) else Color(0xFF065F46)
    val bodyText = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerBg,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (state) {
                is OfflineDownloadState.Idle -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F6B56).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (state.isReady) Icons.Default.Check else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (state.isReady) Color(0xFF10B981) else Color(0xFFD4AF37),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (state.isReady) "সম্পূর্ণ কুরআন অফলাইনে সক্রিয়" else "অফলাইন কুরআন ডাউনলোড",
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = emeraldText
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = if (state.isReady) {
                                    "সকল ৩০ পারা ও ৬১০ পৃষ্ঠার ডেটা ইন্টারনেট ছাড়াই সবসময় পড়ার জন্য এনক্রিপ্ট হয়ে প্রস্তুত আছে।"
                                } else {
                                    "ইন্টারনেট ছাড়া যেকোনো সময় নিরবচ্ছিন্নভাবে পড়তে সম্পূর্ণ কুরআন এক ক্লিকে অফলাইনে সংরক্ষণ করুন (${state.downloadedCount.toBn()}/${state.totalPages.toBn()} পৃষ্ঠা প্রস্তুত)।"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                color = bodyText,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onStartDownload,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isReady) Color(0xFF0F6B56).copy(alpha = 0.85f) else Color(0xFF0F6B56)
                        )
                    ) {
                        Text(
                            text = if (state.isReady) "পুনরায় সিঙ্ক / রিফ্রেশ করুন" else "অফলাইনে সম্পূর্ণ কুরআন ডাউনলোড করুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                is OfflineDownloadState.Downloading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF0F6B56),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "অফলাইন ডাউনলোড হচ্ছে... (${state.progressPercent.toBn()}%)",
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = emeraldText
                            )
                        }

                        TextButton(
                            onClick = onCancelDownload,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "বাতিল",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { state.progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF0F6B56),
                        trackColor = Color(0xFF0F6B56).copy(alpha = 0.2f)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = state.currentStage,
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        color = bodyText
                    )
                }

                is OfflineDownloadState.Completed -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "সম্পূর্ণ ৩০ পারা অফলাইনে সফলভাবে সংরক্ষিত হয়েছে!",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = emeraldText
                        )
                    }
                }

                is OfflineDownloadState.Error -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ডাউনলোডে সমস্যা দেখা দিয়েছে",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            color = bodyText
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onStartDownload,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                        ) {
                            Text(
                                text = "পুনরায় চেষ্টা করুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * জনপ্রিয় ও নতুন সংস্করণের রেসপন্সিভ এবং স্মুথ ৩ডি বুক-ওপেনিং অ্যানিমেশন।
 * - বাইরের কভার: সংস্করণের আসল মুদ্রিত কভার অক্ষুণ্ণ রাখা হয়েছে।
 * - ভেতরের পাতা: প্রতিটি সংস্করণের মূল পিডিএফ/মুসহাফ পৃষ্ঠার হুবহু ১:১ অনুপাতের হাই-রেজ্যুলেশন স্প্রেড
 *   (ডান পাতা: সূরা আল-ফাতিহা, বাম পাতা: সূরা আল-বাকারাহ শুরু)।
 * - রেসপন্সিভ প্রস্থ ও উচ্চতা, মাল্টি-লেয়ার পেপার এজ, ডাইনামিক শ্যাডো ও স্মুথ CubicBezier ৩ডি ফিজিক্স।
 */
@Composable
private fun AnimatedBookSpread(
    isClassic: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    var replayKey by remember(isClassic) { mutableStateOf(0) }
    val progress = remember(isClassic, replayKey) { androidx.compose.animation.core.Animatable(0f) }

    val smoothBookEasing = remember { CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f) }

    LaunchedEffect(isClassic, replayKey) {
        progress.snapTo(0f)
        delay(280)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1320, easing = smoothBookEasing)
        )
    }

    val p = progress.value
    val midArc = sin(p * PI).toFloat()

    // আসল পিডিএফ পৃষ্ঠার নিখুঁত অনুপাত (Popular: 720x1082 = 0.6654, New: 1000x1384 = 0.7225)
    val pageAspect = if (isClassic) 0.6654f else 0.7225f
    val coverRes = if (isClassic) {
        com.karianaquran.reader.R.drawable.qariana_ortho_sara_cover
    } else {
        com.karianaquran.reader.R.drawable.qariana_ortho_cover
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { replayKey++ },
        contentAlignment = Alignment.Center
    ) {
        val pageWidth = ((maxWidth - 14.dp) / 2f).coerceIn(108.dp, 184.dp)
        val pageHeight = pageWidth / pageAspect

        // বন্ধ অবস্থায় কভারটি একদম মাঝখানে থাকে; খোলার সাথে সাথে ২-পাতার স্প্রেড মসৃণভাবে কেন্দ্রে আসে
        val shiftPx = with(density) { (pageWidth / 2f).toPx() } * (1f - p)
        val liftYPx = -midArc * with(density) { 4.5.dp.toPx() }
        val spreadScale = 0.96f + (0.04f * p) + (0.018f * midArc)
        val spineAlpha = ((p - 0.38f) / 0.62f).coerceIn(0f, 1f)
        val stackAlpha = ((p - 0.25f) / 0.75f).coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pageHeight + 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // বইয়ের নিচে নরম অ্যাম্বিয়েন্ট গ্রাউন্ড শ্যাডো (কভার থেকে ফুল স্প্রেডে প্রসারিত হয়)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 1.dp)
                    .graphicsLayer {
                        translationX = shiftPx * 0.35f
                        alpha = if (isDark) 0.45f else 0.22f
                    }
                    .size(
                        width = pageWidth * (1.02f + 0.94f * p),
                        height = 10.dp
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
                        )
                    )
            )

            // মূল ৩ডি বই কন্টেইনার
            Box(
                modifier = Modifier.graphicsLayer {
                    translationX = shiftPx
                    translationY = liftYPx
                    scaleX = spreadScale
                    scaleY = spreadScale
                },
                contentAlignment = Alignment.Center
            ) {
                // ৬১২ পৃষ্ঠার মাল্টি-লেয়ার পেপার স্ট্যাক এজ (খোলা অবস্থায় দুই পাশে বাস্তবসম্মত বইয়ের পুরুত্ব)
                if (p > 0.22f) {
                    Row(
                        modifier = Modifier
                            .graphicsLayer { alpha = stackAlpha }
                            .offset(y = 2.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(pageWidth + 3.dp, pageHeight)
                                .offset(x = (-3).dp)
                                .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))
                                .background(if (isDark) Color(0xFFCBD5E1) else Color(0xFFDDD4C3))
                                .border(
                                    0.6.dp,
                                    if (isDark) Color(0xFF64748B) else Color(0xFFC4B8A3),
                                    RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(pageWidth + 3.dp, pageHeight)
                                .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))
                                .background(if (isDark) Color(0xFFCBD5E1) else Color(0xFFDDD4C3))
                                .border(
                                    0.6.dp,
                                    if (isDark) Color(0xFF64748B) else Color(0xFFC4B8A3),
                                    RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp)
                                )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // বাম অর্ধেক: নিচে বাম পাতা (পৃষ্ঠা ২ - সূরা আল-বাকারাহ), ওপরে ঘূর্ণায়মান কভার -> ডান পাতা (পৃষ্ঠা ১ - সূরা আল-ফাতিহা)
                    Box(modifier = Modifier.size(pageWidth, pageHeight)) {
                        if (p > 0.02f) {
                            BookSpreadPage(
                                isClassic = isClassic,
                                isLeft = true,
                                isDark = isDark,
                                shadowOverlayAlpha = ((1f - p) * 0.34f).coerceIn(0f, 0.34f),
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // ৩ডি ঘূর্ণায়মান কভার ও ডান পাতা (স্পাইন পিভট = ডান কিনারা 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0.5f)
                                    rotationY = 180f * p
                                    cameraDistance = 18f * density.density
                                }
                        ) {
                            if (p < 0.5f) {
                                // সামনের দিক: সংস্করণের আসল মুদ্রিত কভার
                                val coverShape = RoundedCornerShape(
                                    topStart = 6.dp,
                                    bottomStart = 6.dp,
                                    topEnd = 2.dp,
                                    bottomEnd = 2.dp
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .shadow(if (isDark) 4.dp else 8.dp, coverShape)
                                        .clip(coverShape)
                                        .border(0.6.dp, Color(0xFFD4AF37).copy(alpha = 0.45f), coverShape)
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = coverRes),
                                        contentDescription = if (isClassic) "জনপ্রিয় সংস্করণের কভার" else "নতুন সংস্করণের কভার",
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // ডান কিনারায় হার্ডকভার স্পাইন গ্রুপ ও গোল্ডেন বর্ডার ডিটেইল
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .fillMaxHeight()
                                            .width(10.dp)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        Color.White.copy(alpha = 0.12f),
                                                        Color.Black.copy(alpha = 0.38f)
                                                    )
                                                )
                                            )
                                    )
                                    // ঘোরার সময় ডাইনামিক ৩ডি শেডিং
                                    if (midArc > 0.01f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = midArc * 0.18f))
                                        )
                                    }
                                }
                            } else {
                                // পেছনের দিক: খোলার পর ডান পাতা (পৃষ্ঠা ১ - সূরা আল-ফাতিহা, হুবহু আসল পিডিএফ)
                                BookSpreadPage(
                                    isClassic = isClassic,
                                    isLeft = false,
                                    isDark = isDark,
                                    shadowOverlayAlpha = (midArc * 0.16f).coerceIn(0f, 0.18f),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { rotationY = 180f }
                                )
                            }
                        }
                    }

                    // ডান অর্ধেক: খোলার পর ডান পাতাটি এখানে এসে স্থির হয়
                    Spacer(Modifier.size(pageWidth, pageHeight))
                }

                // মাঝখানের বাস্তবসম্মত বইয়ের বাঁধাই / স্পাইন ক্রিজ শ্যাডো
                if (spineAlpha > 0.01f) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer { alpha = spineAlpha }
                            .size(width = 16.dp, height = pageHeight)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.10f),
                                        Color.Black.copy(alpha = 0.26f),
                                        Color.White.copy(alpha = 0.10f),
                                        Color.Black.copy(alpha = 0.14f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * স্প্রেডের একটি পাতা — হুবহু ১:১ আসল পিডিএফ/মুসহাফ পৃষ্ঠা রেন্ডারিং (কোনো ক্রপিং বা স্ট্রেচিং ছাড়া)।
 * RTL মুসহাফ নিয়ম অনুযায়ী ডান পাতা: সূরা আল-ফাতিহা (পৃষ্ঠা ১), বাম পাতা: সূরা আল-বাকারাহ শুরু (পৃষ্ঠা ২)।
 */
@Composable
private fun BookSpreadPage(
    isClassic: Boolean,
    isLeft: Boolean,
    isDark: Boolean,
    shadowOverlayAlpha: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val imageRes = when {
        isClassic && !isLeft -> com.karianaquran.reader.R.drawable.qariana_classic_spread_p1
        isClassic && isLeft  -> com.karianaquran.reader.R.drawable.qariana_classic_spread_p2
        !isClassic && !isLeft -> com.karianaquran.reader.R.drawable.qariana_new_spread_p1
        else -> com.karianaquran.reader.R.drawable.qariana_new_spread_p2
    }
    val outerShape = if (isLeft) {
        RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 1.dp, bottomEnd = 1.dp)
    } else {
        RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp, topStart = 1.dp, bottomStart = 1.dp)
    }

    Box(
        modifier = modifier
            .shadow(if (isDark) 2.dp else 4.dp, outerShape)
            .clip(outerShape)
            .background(Color.White)
            .border(0.5.dp, Color(0xFFD6CFC2), outerShape)
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = imageRes),
            contentDescription = if (!isLeft) "সূরা আল-ফাতিহা (পৃষ্ঠা ১)" else "সূরা আল-বাকারাহ (পৃষ্ঠা ২)",
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(1.5.dp)
        )

        // পাতার ভেতরের কিনারায় সূক্ষ্ম কার্ভ পেপার শেডিং যাতে বইয়ের ভাঁজ জীবন্ত মনে হয়
        Box(
            modifier = Modifier
                .align(if (isLeft) Alignment.CenterEnd else Alignment.CenterStart)
                .fillMaxHeight()
                .width(8.dp)
                .background(
                    Brush.horizontalGradient(
                        if (isLeft) {
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.09f))
                        } else {
                            listOf(Color.Black.copy(alpha = 0.09f), Color.Transparent)
                        }
                    )
                )
        )

        if (shadowOverlayAlpha > 0.005f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = shadowOverlayAlpha))
            )
        }
    }
}

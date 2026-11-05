package com.karianaquran.reader.ui.overview

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ক্বারীয়ানা ৩ সংস্করণ পরিচিতি — সম্পূর্ণ ফুল-স্ক্রিন স্ক্রিন।
 * বটমশিটের বদলে একটি ডেডিকেটেড স্ক্রিনে ৩টি সংস্করণের তুলনা ও "এই সংস্করণে পড়ুন" অ্যাকশন।
 */
@Composable
fun EditionDetailScreen(
    initialMode: ReadingMode,
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"

    var selectedMode by remember { mutableStateOf(initialMode) }

    val pageBg = if (isDark) Color(0xFF0B131E) else Color(0xFFF8FAF9)
    val cardBg = if (isDark) Color(0xFF13221A) else Color.White
    val textPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(pageBg)
    ) {
        // ---- টপ বার ----
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F6B56),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 6.dp)
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.back() }, modifier = Modifier.size(48.dp)) {
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
                        text = if (isEnglish) "Qariana 3 Editions" else "ক্বারীয়ানা ৩ সংস্করণ পরিচিতি",
                        fontFamily = fonts.kalpurush,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isEnglish) "Compare all 3 editions & read" else "৩টি সংস্করণের তুলনা ও সরাসরি পাঠ",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // ---- ৩ সংস্করণ ট্যাব ----
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            shape = RoundedCornerShape(28.dp),
            color = if (isDark) Color(0xFF16251E) else Color(0xFFE5DFD4)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EditionDetailTab(
                    title = if (isEnglish) "Popular" else "জনপ্রিয়",
                    isSelected = selectedMode == ReadingMode.CLASSIC,
                    isDark = isDark,
                    onClick = { selectedMode = ReadingMode.CLASSIC },
                    modifier = Modifier.weight(1f)
                )
                EditionDetailTab(
                    title = if (isEnglish) "New Vector" else "নতুন",
                    isSelected = selectedMode == ReadingMode.PAGE,
                    isDark = isDark,
                    onClick = { selectedMode = ReadingMode.PAGE },
                    modifier = Modifier.weight(1f)
                )
                EditionDetailTab(
                    title = if (isEnglish) "Digital" else "ডিজিটাল",
                    isSelected = selectedMode == ReadingMode.AYAH,
                    isDark = isDark,
                    onClick = { selectedMode = ReadingMode.AYAH },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ---- বিস্তারিত কনটেন্ট ----
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                EditionHeroCard(mode = selectedMode, isDark = isDark, isEnglish = isEnglish, cardBg = cardBg, textPrimary = textPrimary, textSecondary = textSecondary)
            }

            item {
                EditionFeatureList(mode = selectedMode, isDark = isDark, isEnglish = isEnglish, cardBg = cardBg, textPrimary = textPrimary, textSecondary = textSecondary)
            }

            item {
                ComparisonMatrix(selectedMode = selectedMode, isDark = isDark, isEnglish = isEnglish, cardBg = cardBg, textPrimary = textPrimary, textSecondary = textSecondary)
            }

            item { Spacer(Modifier.height(8.dp)) }
        }

        // ---- বটম অ্যাকশন ----
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isDark) Color(0xFF0E1A14) else Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = {
                    vm.setReadingMode(selectedMode)
                    vm.openReader(null)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFFFFDF79),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isEnglish) "Read in this Edition" else "এই সংস্করণে পড়ুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun EditionDetailTab(
    title: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val bg by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> Color(0xFF0F6B56)
            isSelected && !isDark -> Color.White
            else -> Color.Transparent
        }, animationSpec = tween(200), label = "ed_detail_bg"
    )
    val fg by animateColorAsState(
        targetValue = when {
            isSelected && isDark -> Color.White
            isSelected && !isDark -> Color(0xFF0F6B56)
            else -> if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        }, animationSpec = tween(200), label = "ed_detail_fg"
    )
    Surface(
        modifier = modifier.height(40.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = bg,
        shadowElevation = if (isSelected && !isDark) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text = title,
                fontFamily = fonts.kalpurush,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = fg,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EditionHeroCard(
    mode: ReadingMode,
    isDark: Boolean,
    isEnglish: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
) {
    val fonts = LocalAppFonts.current
    val (title, subtitle, badge, accent) = when (mode) {
        ReadingMode.AYAH -> EditionMeta(
            if (isEnglish) "Digital Edition (Ayah & Tafsir)" else "ডিজিটাল সংস্করণ (আয়াত ও তাফসীর)",
            if (isEnglish) "Interactive ayah-by-ayah view with word-by-word meaning and full tafsir." else "আয়াতভিত্তিক ইন্টারেক্টিভ ভিউ — শব্দে শব্দে অর্থ ও পূর্ণ তাফসীরসহ।",
            if (isEnglish) "Digital" else "ডিজিটাল",
            Color(0xFF0EA5E9)
        )
        ReadingMode.PAGE -> EditionMeta(
            if (isEnglish) "New Vector Mushaf (610 pages)" else "নতুন ভেক্টর মুসহাফ (৬১০ পৃষ্ঠা)",
            if (isEnglish) "InDesign vector pages with tajweed colors, RTL swipe and pinch-zoom." else "ইনডিজাইন ভেক্টর পৃষ্ঠা — রঙিন তাজবীদ, RTL সোয়াইপ ও পিন্চ-জুম।",
            if (isEnglish) "New" else "নতুন",
            Color(0xFF10B981)
        )
        ReadingMode.CLASSIC -> EditionMeta(
            if (isEnglish) "Popular Print Edition (963 pages)" else "জনপ্রিয় মুদ্রিত সংস্করণ (৯৬৩ পৃষ্ঠা)",
            if (isEnglish) "Original printed layout with faithful typography and page structure." else "মূল মুদ্রিত লেআউট — হুবহু টাইপোগ্রাফি ও পৃষ্ঠা বিন্যাস।",
            if (isEnglish) "Popular" else "জনপ্রিয়",
            Color(0xFFD4AF37)
        )
        ReadingMode.HAFEZI -> EditionMeta(
            if (isEnglish) "Hafezi Vector Edition (15 & 14 Lines)" else "হাফেজী ভেক্টর সংস্করণ (১৫ ও ১৪ লাইন)",
            if (isEnglish) "Traditional 15 and 14 lines Hafezi layout with vector sharpness and tajweed." else "ঐতিহ্যবাহী ১৫ ও ১৪ লাইন হাফেজী বিন্যাস — ভেক্টর শার্পনেস ও রঙিন তাজবীদ।",
            if (isEnglish) "Hafezi" else "হাফেজী",
            Color(0xFF059669)
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, accent.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (mode) {
                            ReadingMode.AYAH -> Icons.Default.Stars
                            ReadingMode.PAGE -> Icons.Default.AutoStories
                            ReadingMode.CLASSIC -> Icons.Default.Description
                            ReadingMode.HAFEZI -> Icons.Default.AutoStories
                        },
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = fonts.kalpurush,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = badge,
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = subtitle,
                fontFamily = fonts.kalpurush,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = textSecondary
            )
        }
    }
}

private data class EditionMeta(
    val title: String,
    val subtitle: String,
    val badge: String,
    val accent: Color,
)

private data class ComparisonRow(
    val label: String,
    val popular: String,
    val newEdition: String,
    val digital: String,
)

@Composable
private fun EditionFeatureList(
    mode: ReadingMode,
    isDark: Boolean,
    isEnglish: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
) {
    val fonts = LocalAppFonts.current
    val features = when (mode) {
        ReadingMode.AYAH -> listOf(
            if (isEnglish) "Word-by-word Arabic + pronunciation + Bangla meaning" else "প্রতিটি আরবি শব্দের উচ্চারণ ও বাংলা অর্থ",
            if (isEnglish) "Full Bengali translation for every ayah" else "প্রতিটি আয়াতের পূর্ণ বঙ্গানুবাদ",
            if (isEnglish) "Expandable tafsir notes & digital bookmarks" else "প্রসারিত তাফসীর নোট ও ডিজিটাল বুকমার্ক"
        )
        ReadingMode.PAGE -> listOf(
            if (isEnglish) "Vector-sharp InDesign page rendering" else "ভেক্টর-শার্প ইনডিজাইন পৃষ্ঠা রেন্ডারিং",
            if (isEnglish) "12 tajweed color rules with legend" else "১২টি তাজবীদ রঙের নিয়ম ও লেজেন্ড",
            if (isEnglish) "Smooth RTL swipe + pinch zoom (1x–4x)" else "স্মুথ RTL সোয়াইপ + পিন্চ জুম (১x–৪x)"
        )
        ReadingMode.CLASSIC -> listOf(
            if (isEnglish) "Faithful printed book layout" else "মূল মুদ্রিত বইয়ের হুবহু লেআউট",
            if (isEnglish) "973 pages faithful reproduction" else "৯৭৩ পৃষ্ঠার অবিকল মুদ্রিত রূপ",
            if (isEnglish) "Fast local PDF page rendering" else "দ্রুত লোকাল পিডিএফ পৃষ্ঠা রেন্ডারিং"
        )
        ReadingMode.HAFEZI -> listOf(
            if (isEnglish) "15 and 14 line Hafezi layout" else "১৫ ও ১৪ লাইন হাফেজী লেআউট",
            if (isEnglish) "Arabic, meaning and pronunciation modes" else "আরবি, অর্থ ও উচ্চারণ ৩টি মোড",
            if (isEnglish) "Vector sharpness with tajweed colors" else "ভেক্টর শার্পনেস ও রঙিন তাজবীদ"
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E382A) else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (isEnglish) "Key Features" else "মূল বৈশিষ্ট্যসমূহ",
                fontFamily = fonts.kalpurush,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            features.forEach { f ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp).padding(top = 1.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = f,
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonMatrix(
    selectedMode: ReadingMode,
    isDark: Boolean,
    isEnglish: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
) {
    val fonts = LocalAppFonts.current
    val rows = listOf(
        ComparisonRow(if (isEnglish) "Pages" else "পৃষ্ঠা", "৯৭৩", "৬১০", "৬১০"),
        ComparisonRow(if (isEnglish) "Tajweed Colors" else "তাজবীদ রঙ", "yes", "yes", "no"),
        ComparisonRow(if (isEnglish) "Word Meaning" else "শব্দার্থ", "yes", "yes", "yes"),
        ComparisonRow(if (isEnglish) "Offline" else "অফলাইন", "yes", "yes", "yes"),
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E382A) else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isEnglish) "Comparison Matrix" else "তুলনা ম্যাট্রিক্স",
                fontFamily = fonts.kalpurush,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(Modifier.height(10.dp))

            // হেডার
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isEnglish) "Feature" else "বৈশিষ্ট্য",
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSecondary,
                    modifier = Modifier.weight(1.6f)
                )
                listOf(
                    if (isEnglish) "Popular" else "জনপ্রিয়",
                    if (isEnglish) "New" else "নতুন",
                    if (isEnglish) "Digital" else "ডিজিটাল"
                ).forEachIndexed { idx, label ->
                    val active = (idx == 0 && selectedMode == ReadingMode.CLASSIC) ||
                        (idx == 1 && selectedMode == ReadingMode.PAGE) ||
                        (idx == 2 && selectedMode == ReadingMode.AYAH)
                    Text(
                        text = label,
                        fontFamily = fonts.kalpurush,
                        fontSize = 10.5.sp,
                        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (active) Color(0xFF0F6B56) else textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            rows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = row.label,
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1.6f)
                    )
                    listOf(row.popular, row.newEdition, row.digital).forEach { v ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when (v) {
                                "yes" -> Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "সমর্থিত",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(17.dp)
                                )
                                "no" -> Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "অনুপলব্ধ",
                                    tint = textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(17.dp)
                                )
                                else -> Text(
                                    text = v,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

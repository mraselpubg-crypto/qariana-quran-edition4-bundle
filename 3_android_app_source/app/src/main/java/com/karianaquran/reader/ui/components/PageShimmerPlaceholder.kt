package com.karianaquran.reader.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * কুরআনের পৃষ্ঠা লোডিংয়ের সময় প্রিমিয়াম ইসলামিক শিমার স্কেলিটন।
 * এর ফলে কোনো অবস্থাতেই সাদা বা ফাঁকা পাতা দৃশ্যমান হবে না।
 */
@Composable
fun PageShimmerPlaceholder(
    pageNum: Int,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current
    val infiniteTransition = rememberInfiniteTransition(label = "page_shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_pos"
    )

    val bgColor = if (isDark) Color(0xFF0A130E) else Color(0xFFFCFAF4)
    val baseBarColor = if (isDark) Color(0xFF16251E) else Color(0xFFEFE8DB)
    val highlightColor = if (isDark) Color(0xFF263D31) else Color(0xFFFFF9ED)
    val goldFrame = Color(0xFFD4AF37).copy(alpha = if (isDark) 0.35f else 0.50f)

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(baseBarColor, highlightColor, baseBarColor),
        start = Offset(shimmerTranslate, shimmerTranslate),
        end = Offset(shimmerTranslate + 350f, shimmerTranslate + 350f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(8.dp)
            .border(1.2.dp, goldFrame, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ১. শীর্ষ ব্যানার শিমার (সূরা হেডারের ফ্রেম)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerBrush)
                    .border(0.8.dp, goldFrame, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontFamily = fonts.arabic,
                    fontSize = 13.sp,
                    color = goldFrame.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(10.dp))

            // ২. ১৫টি লাইন বিশিষ্ট কুরআনের পৃষ্ঠার সমতুল্য স্কেলিটন লাইনসমূহ
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(13) { idx ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(if (idx % 2 == 0) 0.95f else 0.88f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush)
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(goldFrame.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ৩. ফুটার শিমার
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
                Text(
                    text = "পৃষ্ঠা ${pageNum.toBn()}",
                    fontFamily = fonts.kalpurush,
                    fontSize = 10.sp,
                    color = goldFrame.copy(alpha = 0.7f)
                )
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush)
                )
            }
        }
    }
}

/**
 * জনপ্রিয় সংস্করণ (ক্লাসিক ৯৭৩ পৃষ্ঠা) এর জন্য ৯-রো অথেন্টিক শিমার স্কেলিটন।
 * এতে রয়েছে ক্লাসিক ইনডিজাইন প্রিন্ট পেজের মতো শীর্ষ হেডার, ৯টি আরবি সবুজ স্ট্রিপ ও বাংলা অর্থ লাইন, এবং ফুটার।
 */
@Composable
fun ClassicPageShimmer(
    pageNum: Int,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current
    val infiniteTransition = rememberInfiniteTransition(label = "classic_page_shimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "classic_shimmer_pos"
    )

    val pageBg = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF)
    val cardStripBg = if (isDark) Color(0xFF1E293B) else Color(0xFFD0E8C4).copy(alpha = 0.55f)
    val cardStripHighlight = if (isDark) Color(0xFF334155) else Color(0xFFE2F2DA)
    val bnBaseColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val bnHighlightColor = if (isDark) Color(0xFF334155) else Color(0xFFFFFFFF)
    val goldAccent = Color(0xFFD4AF37).copy(alpha = if (isDark) 0.40f else 0.60f)
    val frameBorder = if (isDark) Color(0xFFD4AF37).copy(alpha = 0.35f) else Color(0xFF1E293B).copy(alpha = 0.35f)

    val arabicStripBrush = Brush.linearGradient(
        colors = listOf(cardStripBg, cardStripHighlight, cardStripBg),
        start = Offset(shimmerTranslate, shimmerTranslate),
        end = Offset(shimmerTranslate + 400f, shimmerTranslate + 400f)
    )

    val meaningBrush = Brush.linearGradient(
        colors = listOf(bnBaseColor, bnHighlightColor, bnBaseColor),
        start = Offset(shimmerTranslate, shimmerTranslate),
        end = Offset(shimmerTranslate + 350f, shimmerTranslate + 350f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(pageBg)
            .padding(6.dp)
            .border(1.5.dp, frameBorder, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ১. ক্লাসিক শীর্ষ হেডার ব্যানার
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(arabicStripBrush)
                    .border(0.8.dp, frameBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(45.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(meaningBrush)
                )
                androidx.compose.material3.Text(
                    text = "ক্বারীয়ানা পদ্ধতিতে কুরআন মাজীদ",
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.sp,
                    color = goldAccent,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .width(55.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(meaningBrush)
                )
            }

            Spacer(Modifier.height(6.dp))

            // ২. ক্লাসিক ৯টি আয়াতের রো (প্রতিটি রো = আরবি সবুজ স্ট্রিপ + বাংলা অর্থ লাইন)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(9) { idx ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.5.dp, frameBorder.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                    ) {
                        // আরবি হরফের সবুজ স্ট্রিপ
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .background(arabicStripBrush)
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(goldAccent.copy(alpha = 0.5f))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(cardStripHighlight)
                            )
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(goldAccent.copy(alpha = 0.5f))
                            )
                        }

                        // বাংলা অর্থের স্ট্রিপ
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .background(pageBg)
                                .padding(horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(0.92f)
                                    .height(9.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(meaningBrush)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ৩. ক্লাসিক ফুটার (পৃষ্ঠা নম্বর)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(meaningBrush)
                )
                androidx.compose.material3.Text(
                    text = "পৃষ্ঠা ${pageNum.toBn()}",
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.sp,
                    color = goldAccent,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(meaningBrush)
                )
            }
        }
    }
}

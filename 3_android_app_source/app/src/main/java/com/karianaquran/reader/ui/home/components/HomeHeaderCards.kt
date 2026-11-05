package com.karianaquran.reader.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.AccentMint
import com.karianaquran.reader.ui.home.PrimaryDarkEmerald
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * হোম স্ক্রিনের হেডার কম্পোনেন্ট:
 * - HomeTopBar (মেনু / তারিখ / সার্চ / ক্রাউন / সেটিংস)
 * - HomeRamadanCountdownCard + CountdownBox
 * HomeScreen.kt থেকে ভাগ করা হয়েছে — behavior অপরিবর্তিত।
 */

// -------------------------------------------------------------------------------------------------
// ১. টপ অ্যাপ বার (HomeTopBar)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun HomeTopBar(
    isDark: Boolean,
    isUnlocked: Boolean,
    gregorianDate: String,
    hijriDate: String,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCrownClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val fonts = LocalAppFonts.current

    val infiniteTransition = rememberInfiniteTransition(label = "CrownPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = if (isDark) Color(0xFF131F2A) else Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onMenuClick),
                shape = RoundedCornerShape(10.dp),
                color = if (isDark) Color(0xFF1A2B3C) else Color(0xFFF1F5F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "মেনু",
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(if (isDark) AccentMint else PrimaryDarkEmerald)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gregorianDate,
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else PrimaryDarkEmerald,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = hijriDate,
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(onClick = onSearchClick),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF1A2B3C) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF283D52) else Color(0xFFE2E8F0))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "সার্চ",
                            tint = if (isDark) AccentMint else PrimaryDarkEmerald,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .clickable(onClick = onCrownClick),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isUnlocked) Color(0xFFFEF3C7) else (if (isDark) Color(0xFF22303C) else Color(0xFFF1F5F9)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (isUnlocked) Color(0xFFF59E0B).copy(alpha = glowAlpha) else Color(0xFF94A3B8).copy(alpha = glowAlpha)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.crown),
                            contentDescription = if (isUnlocked) "প্রো মেম্বার" else "আপগ্রেড করুন",
                            modifier = Modifier.size(20.dp),
                            colorFilter = if (isUnlocked) null else ColorFilter.tint(if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B))
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(onClick = onSettingsClick),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF1A2B3C) else Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF283D52) else Color(0xFFE2E8F0))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_settings_black_24dp),
                            contentDescription = "সেটিংস",
                            modifier = Modifier.size(19.dp),
                            colorFilter = ColorFilter.tint(if (isDark) AccentMint else PrimaryDarkEmerald)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ৩. রমজান কাউন্টডাউন সিন কার্ড (HomeRamadanCountdownCard)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun HomeRamadanCountdownCard(
    isDark: Boolean,
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    onViewAllClick: () -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF1E3A2E) else Color(0xFF9CCBB0)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.bg_home_ramadan_countdown_scene),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (isDark) Color(0xEB0A1C16) else Color(0xE6F5FAF7)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = PrimaryDarkEmerald
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Ramadan Countdown" else "রমজান কাউন্টডাউন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PrimaryDarkEmerald
                        )
                    }

                    Surface(
                        modifier = Modifier.clickable(onClick = onViewAllClick),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF143026) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF275544) else Color(0xFFC7E2D3)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "All Timings" else "সকল সময়",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) AccentMint else PrimaryDarkEmerald
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (isDark) AccentMint else PrimaryDarkEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountdownBox(value = if (isEnglish) days.toString() else days.toBn(), label = if (isEnglish) "Days" else "দিন", isDark = isDark, modifier = Modifier.weight(1f))
                    CountdownBox(value = if (isEnglish) hours.toString() else hours.toBn(), label = if (isEnglish) "Hours" else "ঘণ্টা", isDark = isDark, modifier = Modifier.weight(1f))
                    CountdownBox(value = if (isEnglish) minutes.toString() else minutes.toBn(), label = if (isEnglish) "Minutes" else "মিনিট", isDark = isDark, modifier = Modifier.weight(1f))
                    CountdownBox(value = if (isEnglish) seconds.toString() else seconds.toBn(), label = if (isEnglish) "Seconds" else "সেকেন্ড", isDark = isDark, modifier = Modifier.weight(1f), isLive = true)
                }
            }
        }
    }
}

@Composable
private fun CountdownBox(
    value: String,
    label: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    isLive: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF142720) else Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF234438) else Color(0xFFD6EAE0)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = fonts.kalpurush,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLive) Color(0xFFE11D48) else (if (isDark) Color.White else PrimaryDarkEmerald)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = fonts.kalpurush,
                fontSize = 11.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280)
            )
        }
    }
}

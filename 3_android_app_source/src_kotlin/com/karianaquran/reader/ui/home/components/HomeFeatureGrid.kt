package com.karianaquran.reader.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.home.AccentMint
import com.karianaquran.reader.ui.home.LightBlackText
import com.karianaquran.reader.ui.home.PrimaryDarkEmerald
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * হোম স্ক্রিনের ৯-ফিচার গ্রিড (আল-কুরআন ব্যানার + ৮টি ইসলামিক ফিচার বাটন)।
 * HomeScreen.kt (৩৪০০+ লাইন) থেকে ভাগ করা হয়েছে — behavior অপরিবর্তিত।
 */
@Composable
internal fun HomeFeatureGrid(
    isDark: Boolean,
    onQuranClick: () -> Unit,
    onPrayerClick: () -> Unit,
    onNamesClick: () -> Unit,
    onTasbihClick: () -> Unit,
    onQiblaClick: () -> Unit,
    onDuasClick: () -> Unit,
    onPillarsClick: () -> Unit,
    onRamadanClick: () -> Unit,
    onMoreClick: () -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF131F2A) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF1E2E3D) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // লাইন ১: আল-কুরআন ওয়াইড ব্যানার বাটন
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onQuranClick),
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF152A21) else Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (isDark) Color(0xFF24503E) else Color(0xFFBBF7D0)
                ),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(width = 36.dp, height = 48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qariana_ortho_cover),
                            contentDescription = "কুরআন শরীফ",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) "Qariana Ortho-Quran (30 Paras)" else "ক্বারীয়ানা অর্থ-কুরআন (৩০ পারা)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PrimaryDarkEmerald,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (isEnglish) "Tajweed & Meaning" else "রঙিন তাজবীদ ও শব্দার্থ",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                )
                            }
                            Text(
                                text = if (isEnglish) "Indesign Edition" else "ইনডিজাইন প্রেস সংস্করণ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F6B56),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "Read" else "পড়ুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFFFDE68A),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // লাইন ২: ৪টি বাটন (নামাজের সময়, কিবলা, তাসবীহ, ৯৯ নাম)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeatureGridItem(
                    title = if (isEnglish) "Prayer Times" else "নামাজের সময়",
                    iconRes = R.drawable.prayericon,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFFEF3C7),
                    badgeBgDark = Color(0xFF382F16),
                    badgeBorderLight = Color(0xFFFDE68A),
                    badgeBorderDark = Color(0xFFD97706),
                    onClick = onPrayerClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureGridItem(
                    title = if (isEnglish) "Qibla" else "কিবলা",
                    iconRes = R.drawable.compassicon,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFF3E8FF),
                    badgeBgDark = Color(0xFF2F1845),
                    badgeBorderLight = Color(0xFFE9D5FF),
                    badgeBorderDark = Color(0xFF9333EA),
                    onClick = onQiblaClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureGridItem(
                    title = if (isEnglish) "Tasbih" else "তাসবীহ",
                    iconRes = R.drawable.tasbeeicon,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFE0F2FE),
                    badgeBgDark = Color(0xFF152F3F),
                    badgeBorderLight = Color(0xFFBAE6FD),
                    badgeBorderDark = Color(0xFF0284C7),
                    onClick = onTasbihClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureGridItem(
                    title = if (isEnglish) "99 Names" else "৯৯ নাম",
                    iconRes = R.drawable.allahicon,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFFFE4E6),
                    badgeBgDark = Color(0xFF381926),
                    badgeBorderLight = Color(0xFFFECDD3),
                    badgeBorderDark = Color(0xFFE11D48),
                    onClick = onNamesClick,
                    modifier = Modifier.weight(1f)
                )
            }

            // লাইন ৩: ৪টি বাটন (দোয়া, স্তম্ভ, রমজান, আরও)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeatureGridItem(
                    title = if (isEnglish) "Duas" else "দোয়া",
                    iconRes = R.drawable.duaaicon,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFDCFCE7),
                    badgeBgDark = Color(0xFF143522),
                    badgeBorderLight = Color(0xFFBBF7D0),
                    badgeBorderDark = Color(0xFF16A34A),
                    onClick = onDuasClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureGridItem(
                    title = if (isEnglish) "Pillars" else "স্তম্ভ",
                    iconRes = R.drawable.pillarsicon,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFE0E7FF),
                    badgeBgDark = Color(0xFF192342),
                    badgeBorderLight = Color(0xFFC7D2FE),
                    badgeBorderDark = Color(0xFF4F46E5),
                    onClick = onPillarsClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureGridItem(
                    title = if (isEnglish) "Ramadan" else "রমজান",
                    iconRes = R.drawable.ic_ramadantimes2,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFFFFBEB),
                    badgeBgDark = Color(0xFF332A15),
                    badgeBorderLight = Color(0xFFFDE68A),
                    badgeBorderDark = Color(0xFFD97706),
                    onClick = onRamadanClick,
                    modifier = Modifier.weight(1f)
                )
                FeatureGridItem(
                    title = if (isEnglish) "More" else "আরও",
                    iconVector = Icons.Default.MoreHoriz,
                    isDark = isDark,
                    badgeBgLight = Color(0xFFF1F5F9),
                    badgeBgDark = Color(0xFF1E2D3D),
                    badgeBorderLight = Color(0xFFE2E8F0),
                    badgeBorderDark = Color(0xFF475569),
                    iconTint = if (isDark) AccentMint else PrimaryDarkEmerald,
                    onClick = onMoreClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FeatureGridItem(
    title: String,
    iconRes: Int? = null,
    iconVector: ImageVector? = null,
    isDark: Boolean,
    badgeBgLight: Color,
    badgeBgDark: Color,
    badgeBorderLight: Color,
    badgeBorderDark: Color,
    iconTint: Color? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 76.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF162534) else Color(0xFFF9FBFA),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF22364A) else Color(0xFFE5EDE8)
        ),
        shadowElevation = if (isDark) 0.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) badgeBgDark else badgeBgLight,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) badgeBorderDark else badgeBorderLight
                ),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (iconRes != null) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = title,
                            modifier = Modifier.size(22.dp),
                            colorFilter = if (iconTint != null) ColorFilter.tint(iconTint) else null
                        )
                    } else if (iconVector != null) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = title,
                            tint = iconTint ?: (if (isDark) AccentMint else PrimaryDarkEmerald),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = title,
                fontFamily = fonts.kalpurush,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color(0xFFF1F5F9) else LightBlackText,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

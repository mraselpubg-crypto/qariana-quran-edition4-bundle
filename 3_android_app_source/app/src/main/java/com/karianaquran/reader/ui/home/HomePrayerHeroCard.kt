package com.karianaquran.reader.ui.home

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.prayer.NextPrayerInfo
import com.karianaquran.reader.data.prayer.PrayerTime
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

/**
 * পাঁচ ওয়াক্ত ও সকালের আর্চ পয়েন্টার ডেটা মডেল (আর্চে দেখানোর জন্য)
 */
data class ArcPrayerPoint(
    val key: String,
    val nameBn: String,
    val nameEn: String,
    val nameAr: String,
    val hour: Int,
    val minute: Int,
    val timeFormattedBn: String,
    val shortTimeBn: String,
    val icon: ImageVector,
    val angleDeg: Float,
    val progressWeight: Float,
)

/**
 * সময়ের আবহাওয়ানুযায়ী ডাইনামিক থিম কালার প্যালেট (লাইট মোডে ফ্রেশ এমেরাল্ড-মিন্ট এবং ডার্ক মোডে মিডনাইট এমেরাল্ড)
 */
private data class AtmospherePalette(
    val bgGradient: List<Color>,
    val cardBorder: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val arcTrackColor: Color,
    val arcProgressColor1: Color,
    val arcProgressColor2: Color,
    val activeGlowColor: Color,
    val mosqueSilhouetteColor: Color,
    val badgeBg: Color,
    val badgeBorder: Color,
    val badgeTextColor: Color,
    val buttonBg: Color,
    val buttonBorder: Color,
    val buttonTextColor: Color,
    val isNight: Boolean,
)

/**
 * ক্বারীয়ানা কুরআন - প্রধান হিরো প্রেয়ার কার্ড (HomePrayerHeroCard)
 *
 * বৈশিষ্ট্য:
 * - আন্তর্জাতিক ইসলামিক মানের ফ্রেশ মিন্ট-এমারেল্ড গ্রেডিয়েন্ট ব্যাকগ্রাউন্ড
 * - ব্যালান্সড হেডার: বামে জেলা নির্বাচন পিল ও ডানে আযান অডিও বাটন
 * - সেমি-সার্কুলার মহাজাগতিক আর্চ ডোম (ফজর, সকাল, যোহর, আসর, মাগরিব, এশা)
 * - আর্চের অভ্যন্তরে সম্পূর্ণ সুরক্ষিতভাবে ওয়াক্তের নাম ("তাহাজ্জুদ"), সময়সীমা, কাউন্টডাউন ও নামাজ বাটন
 * - নিচে স্লিম ইন্টিগ্রেটেড মার্জিত পরবর্তী ওয়াক্ত স্ট্রিপ
 */
@Composable
fun HomePrayerHeroCard(
    isDark: Boolean,
    nextPrayerInfo: NextPrayerInfo,
    locationName: String = "ঢাকা, বাংলাদেশ",
    isAzanPlaying: Boolean,
    onLocationClick: () -> Unit,
    onToggleAzan: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val currentPrayerName = nextPrayerInfo.currentPrayerNameBn
    val schedule = nextPrayerInfo.schedule

    // ১. ছয়টি মাইলস্টোন পয়েন্ট নির্ধারণ (ফজর, সকাল/সূর্যোদয়, যোহর, আসর, মাগরিব, এশা)
    val prayerPoints = remember(schedule) {
        val fajr = schedule.getOrNull(0) ?: PrayerTime("ফজর", "Fajr", "الفجر", 5, 0, "ভোর ৫:০০", "৪ রাকাত")
        val sunrise = schedule.getOrNull(1) ?: PrayerTime("সূর্যোদয়", "Sunrise", "الشروق", 6, 15, "সকাল ৬:১৫", "নিষিদ্ধ সময়")
        val dhuhr = schedule.getOrNull(2) ?: PrayerTime("যোহর", "Dhuhr", "الظهر", 12, 0, "দুপুর ১২:০০", "১২ রাকাত")
        val asr = schedule.getOrNull(3) ?: PrayerTime("আসর", "Asr", "العصر", 15, 45, "বিকাল ৩:৪৫", "৪ রাকাত")
        val maghrib = schedule.getOrNull(4) ?: PrayerTime("মাগরিব", "Maghrib", "المغرب", 17, 30, "সন্ধ্যা ৫:৩০", "৭ রাকাত")
        val isha = schedule.getOrNull(5) ?: PrayerTime("এশা", "Isha", "العشاء", 18, 50, "রাত ৬:৫০", "১৭ রাকাত")

        fun format12h(h: Int, m: Int): String {
            val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
            val mStr = String.format("%02d", m)
            val period = if (h < 12) "AM" else "PM"
            return "$displayH:$mStr $period".toBn()
        }

        listOf(
            ArcPrayerPoint("fajr", "ফজর", "Fajr", "الفجر", fajr.hour, fajr.minute, fajr.timeFormattedBn, format12h(fajr.hour, fajr.minute), Icons.Default.WbTwilight, 180f, 0.00f),
            ArcPrayerPoint("sunrise", "সকাল", "Sunrise", "الشروق", sunrise.hour, sunrise.minute, sunrise.timeFormattedBn, format12h(sunrise.hour, sunrise.minute), Icons.Default.WbSunny, 216f, 0.20f),
            ArcPrayerPoint("dhuhr", "যোহর", "Dhuhr", "الظهر", dhuhr.hour, dhuhr.minute, dhuhr.timeFormattedBn, format12h(dhuhr.hour, dhuhr.minute), Icons.Default.WbSunny, 252f, 0.40f),
            ArcPrayerPoint("asr", "আসর", "Asr", "العصر", asr.hour, asr.minute, asr.timeFormattedBn, format12h(asr.hour, asr.minute), Icons.Default.WbSunny, 288f, 0.60f),
            ArcPrayerPoint("maghrib", "মাগরিব", "Maghrib", "المغرب", maghrib.hour, maghrib.minute, maghrib.timeFormattedBn, format12h(maghrib.hour, maghrib.minute), Icons.Default.WbTwilight, 324f, 0.80f),
            ArcPrayerPoint("isha", "এশা", "Isha", "العشاء", isha.hour, isha.minute, isha.timeFormattedBn, format12h(isha.hour, isha.minute), Icons.Default.NightsStay, 360f, 1.00f)
        )
    }

    // ২. সার্বিক দিনের আর্চ প্রগ্রেস (০.০ থেকে ১.০) হিসাব
    val arcProgress by remember(nextPrayerInfo, prayerPoints) {
        derivedStateOf {
            val cal = Calendar.getInstance()
            val currentSec = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)

            val fSec = prayerPoints[0].hour * 3600 + prayerPoints[0].minute * 60
            val sSec = prayerPoints[1].hour * 3600 + prayerPoints[1].minute * 60
            val dSec = prayerPoints[2].hour * 3600 + prayerPoints[2].minute * 60
            val aSec = prayerPoints[3].hour * 3600 + prayerPoints[3].minute * 60
            val mSec = prayerPoints[4].hour * 3600 + prayerPoints[4].minute * 60
            val iSec = prayerPoints[5].hour * 3600 + prayerPoints[5].minute * 60

            when {
                currentSec < fSec -> 0.02f
                currentSec in fSec until sSec -> {
                    val span = (sSec - fSec).coerceAtLeast(1)
                    val frac = (currentSec - fSec).toFloat() / span.toFloat()
                    0.00f + frac * 0.20f
                }
                currentSec in sSec until dSec -> {
                    val span = (dSec - sSec).coerceAtLeast(1)
                    val frac = (currentSec - sSec).toFloat() / span.toFloat()
                    0.20f + frac * 0.20f
                }
                currentSec in dSec until aSec -> {
                    val span = (aSec - dSec).coerceAtLeast(1)
                    val frac = (currentSec - dSec).toFloat() / span.toFloat()
                    0.40f + frac * 0.20f
                }
                currentSec in aSec until mSec -> {
                    val span = (mSec - aSec).coerceAtLeast(1)
                    val frac = (currentSec - aSec).toFloat() / span.toFloat()
                    0.60f + frac * 0.20f
                }
                currentSec in mSec until iSec -> {
                    val span = (iSec - mSec).coerceAtLeast(1)
                    val frac = (currentSec - mSec).toFloat() / span.toFloat()
                    0.80f + frac * 0.20f
                }
                else -> {
                    val span = (86400 - iSec + fSec).coerceAtLeast(1)
                    val elapsed = currentSec - iSec
                    val frac = (elapsed.toFloat() / span.toFloat()).coerceIn(0f, 1f)
                    0.95f + frac * 0.05f
                }
            }.coerceIn(0.01f, 1.0f)
        }
    }

    fun format12hEn(h: Int, m: Int): String {
        val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
        val mStr = String.format("%02d", m)
        val period = if (h < 12) "AM" else "PM"
        return "$displayH:$mStr $period"
    }

    val currentWaqtRangeEn by remember(nextPrayerInfo, prayerPoints) {
        derivedStateOf {
            val sunrise = schedule.getOrNull(1)
            val sunriseStr = sunrise?.let { format12hEn(it.hour, it.minute) } ?: "6:15 AM"
            when (currentPrayerName) {
                "ফজর" -> "${format12hEn(prayerPoints[0].hour, prayerPoints[0].minute)} - $sunriseStr"
                "যোহর" -> "${format12hEn(prayerPoints[2].hour, prayerPoints[2].minute)} - ${format12hEn(prayerPoints[3].hour, prayerPoints[3].minute)}"
                "আসর" -> "${format12hEn(prayerPoints[3].hour, prayerPoints[3].minute)} - ${format12hEn(prayerPoints[4].hour, prayerPoints[4].minute)}"
                "মাগরিব" -> "${format12hEn(prayerPoints[4].hour, prayerPoints[4].minute)} - ${format12hEn(prayerPoints[5].hour, prayerPoints[5].minute)}"
                "এশা" -> "${format12hEn(prayerPoints[5].hour, prayerPoints[5].minute)} - ${format12hEn(prayerPoints[0].hour, prayerPoints[0].minute)}"
                else -> "Until ${nextPrayerInfo.nextPrayerNameEn}"
            }
        }
    }

    // ৩. বর্তমান ওয়াক্তের সময়সীমা ফরম্যাটিং
    val currentWaqtRangeBn by remember(nextPrayerInfo, prayerPoints) {
        derivedStateOf {
            val sunrise = schedule.getOrNull(1)
            when (currentPrayerName) {
                "ফজর" -> "${prayerPoints[0].shortTimeBn} - ${sunrise?.let { String.format("%02d:%02d AM", if (it.hour > 12) it.hour - 12 else it.hour, it.minute).toBn() } ?: "৬:১৫ AM".toBn()}"
                "ইশরাক / চাশত" -> "${sunrise?.timeFormattedBn ?: "সূর্যোদয়"} - ${prayerPoints[2].shortTimeBn}"
                "যোহর" -> "${prayerPoints[2].shortTimeBn} - ${prayerPoints[3].shortTimeBn}"
                "আসর" -> "${prayerPoints[3].shortTimeBn} - ${prayerPoints[4].shortTimeBn}"
                "মাগরিব" -> "${prayerPoints[4].shortTimeBn} - ${prayerPoints[5].shortTimeBn}"
                "এশা" -> "${prayerPoints[5].shortTimeBn} - ${prayerPoints[0].shortTimeBn}"
                "তাহাজ্জুদ / সাহরি", "তাহাজ্জুদ" -> "রাত ১২:০০ - ${prayerPoints[0].shortTimeBn}"
                else -> "${nextPrayerInfo.nextPrayerNameBn} এর পূর্ব পর্যন্ত"
            }
        }
    }

    // ৪. লাইভ কাউন্টডাউন টেক্সট
    val liveCountdownFormatted by remember(nextPrayerInfo) {
        derivedStateOf {
            val h = nextPrayerInfo.remainingHours
            val m = nextPrayerInfo.remainingMinutes
            val s = nextPrayerInfo.remainingSeconds
            if (h > 0) {
                "${h}ঘ ${m}মি ${s}সে".toBn()
            } else {
                "${m}মি ${s}সে".toBn()
            }
        }
    }

    // ৫. সময়ের সাথে আবহাওয়ানুযায়ী বায়ুমণ্ডলীয় থিম নির্ধারণ
    val currentCalHour = remember(nextPrayerInfo) { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val isNightTime = currentCalHour < 5 || currentCalHour >= 18
    val screenBg = if (isDark) Color(0xFF0B131E) else Color(0xFFF6FFF9)

    val targetPalette = remember(isDark, isNightTime) {
        if (!isDark) {
            // লাইট থিম: সফট ইসলামিক মিন্ট-এমারেল্ড গ্রেডিয়েন্ট (নিচের দিকে ব্যাকগ্রাউন্ডের সাথে সম্পূর্ণ ফেইড)
            AtmospherePalette(
                bgGradient = listOf(
                    Color(0xFFE8F6ED), // শীর্ষভাগ: স্নিগ্ধ মিন্ট গ্রিন
                    Color(0xFFDCF4E7), // মধ্যভাগ: নরম প্যাস্টেল সেজ
                    Color(0xFFE5F7EC), // আর্চ লোয়ার এরিয়া
                    Color(0xFFEFF9F3), // পরবর্তী ওয়াক্ত স্ট্রিপ এরিয়া
                    Color(0xFFF6FFF9)  // শেষপ্রান্ত: পেজ ব্যাকগ্রাউন্ডে ১০০% মসৃণ ফেইড
                ),
                cardBorder = Color(0xFFA7F3D0).copy(alpha = 0.80f),
                primaryTextColor = Color(0xFF064E3B),   // ডিপ রয়েল ফরেস্ট গ্রিন
                secondaryTextColor = Color(0xFF0F766E), // মিড গ্রিন
                arcTrackColor = Color(0xFF059669).copy(alpha = 0.28f),
                arcProgressColor1 = Color(0xFF34D399),
                arcProgressColor2 = Color(0xFF059669),
                activeGlowColor = Color(0xFF10B981),
                mosqueSilhouetteColor = Color(0xFF065F46).copy(alpha = 0.08f),
                badgeBg = Color(0xFF064E3B).copy(alpha = 0.09f),
                badgeBorder = Color(0xFF059669).copy(alpha = 0.35f),
                badgeTextColor = Color(0xFF064E3B),
                buttonBg = Color(0xFF059669),
                buttonBorder = Color(0xFF34D399),
                buttonTextColor = Color.White,
                isNight = isNightTime
            )
        } else {
            // ডার্ক থিম: ডিপ মিডনাইট অবসিডিয়ান এমেরাল্ড (নিচের দিকে ডার্ক ব্যাকগ্রাউন্ডে ফেইড)
            AtmospherePalette(
                bgGradient = listOf(
                    Color(0xFF051712),
                    Color(0xFF092920),
                    Color(0xFF0D382C),
                    Color(0xFF0A261E),
                    Color(0xFF0B131E)  // ডার্ক স্ক্রিন ব্যাকগ্রাউন্ডে ফেইড
                ),
                cardBorder = Color(0xFF34D399).copy(alpha = 0.45f),
                primaryTextColor = Color.White,
                secondaryTextColor = Color(0xFFA7F3D0),
                arcTrackColor = Color.White.copy(alpha = 0.22f),
                arcProgressColor1 = Color(0xFF6EE7B7),
                arcProgressColor2 = Color(0xFF10B981),
                activeGlowColor = Color(0xFF34D399),
                mosqueSilhouetteColor = Color.White.copy(alpha = 0.12f),
                badgeBg = Color(0x3D064E3B),
                badgeBorder = Color(0x8034D399),
                badgeTextColor = Color(0xFFA7F3D0),
                buttonBg = Color(0xFF10B981),
                buttonBorder = Color(0xFF6EE7B7),
                buttonTextColor = Color.White,
                isNight = true
            )
        }
    }

    val animatedBg0 by animateColorAsState(targetPalette.bgGradient[0], tween(600, easing = LinearEasing), label = "bg0")
    val animatedBg1 by animateColorAsState(targetPalette.bgGradient[1], tween(600, easing = LinearEasing), label = "bg1")
    val animatedBg2 by animateColorAsState(targetPalette.bgGradient[2], tween(600, easing = LinearEasing), label = "bg2")
    val animatedBg3 by animateColorAsState(targetPalette.bgGradient[3], tween(600, easing = LinearEasing), label = "bg3")
    val animatedBg4 by animateColorAsState(targetPalette.bgGradient[4], tween(600, easing = LinearEasing), label = "bg4")
    val animatedBorder by animateColorAsState(targetPalette.cardBorder, tween(600), label = "border")
    val animatedActiveGlow by animateColorAsState(targetPalette.activeGlowColor, tween(600), label = "activeGlow")

    // লাইভ অ্যানিমেশন (পালস রিং)
    val infiniteTransition = rememberInfiniteTransition(label = "heroUnifiedGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(animatedBg0, animatedBg1, animatedBg2, animatedBg3, animatedBg4)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        animatedBorder,
                        animatedBorder.copy(alpha = 0.35f),
                        Color.Transparent,
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
            )
    ) {
        // ১. ব্যাকগ্রাউন্ড অ্যানিমেশন: হালকা মেঘ ও মসজিদের ক্লাসিক সিলুয়েট
        Canvas(modifier = Modifier.matchParentSize()) {
            drawMosqueSilhouette(targetPalette.mosqueSilhouetteColor)
        }

        // ২. মূল কন্টেন্ট লেআউট (হেডার + ডোম আর্চ + মার্জিত নেক্সট প্রেয়ার স্ট্রিপ)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
        ) {
                // =========================================================================
                // ক. হিরো কার্ড হেডার: লোকেশন পিল (বামে) এবং আযান বাটন (ডানে)
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // বামে: বর্তমান লোকেশন পিল (ক্লিকেবল - ৬৪ জেলা বা জিপিএস সিলেক্টর ওপেন করবে)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFF059669).copy(alpha = 0.30f)
                        ),
                        shadowElevation = 1.dp,
                        modifier = Modifier.clickable(onClick = onLocationClick)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "লোকেশন",
                                tint = targetPalette.buttonBg,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locationName,
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = targetPalette.primaryTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "জেলা পরিবর্তন",
                                tint = targetPalette.secondaryTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // ডানে: আযান অডিও বাটন
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isAzanPlaying) Color(0xFFEF4444) else if (nextPrayerInfo.isAzanActive) Color(0xFF10B981) else if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAzanPlaying) Color(0xFFFCA5A5) else Color(0xFF059669).copy(alpha = 0.30f)
                        ),
                        shadowElevation = 1.dp,
                        modifier = Modifier.clickable { onToggleAzan() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = if (isAzanPlaying) Icons.Default.Close else if (nextPrayerInfo.isAzanActive) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "আযান",
                                tint = if (isAzanPlaying || nextPrayerInfo.isAzanActive) Color.White else targetPalette.buttonBg,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEnglish) {
                                    if (isAzanPlaying) "Stop" else if (nextPrayerInfo.isAzanActive) "Azan Active" else "Listen Azan"
                                } else {
                                    if (isAzanPlaying) "থামান" else if (nextPrayerInfo.isAzanActive) "আযান সক্রিয়" else "আযান শুনুন"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAzanPlaying || nextPrayerInfo.isAzanActive) Color.White else targetPalette.primaryTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // =========================================================================
                // খ. মহাজাগতিক সেমি-সার্কুলার ডোম আর্চ (সকালসহ ৬টি ওয়াক্তের ডটেড পয়েন্টার)
                // =========================================================================
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(158.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // ক্যানভাসে আর্চ, ডটেড গাইডলাইন, ৬টি মাইলস্টোন নোড ও লাইভ চলমান পয়েন্টার
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCelestialDomeArc(
                            prayerPoints = prayerPoints,
                            currentPrayerName = currentPrayerName,
                            arcProgress = arcProgress,
                            trackColor = targetPalette.arcTrackColor,
                            progressColors = listOf(targetPalette.arcProgressColor1, targetPalette.arcProgressColor2),
                            activeGlowColor = animatedActiveGlow,
                            primaryTextColor = targetPalette.primaryTextColor,
                            pulseScale = pulseScale,
                            pulseAlpha = pulseAlpha,
                            isDark = isDark,
                            isEnglish = isEnglish
                        )
                    }

                    // আর্চের অভ্যন্তরে ওয়াক্তের সময়সীমা, কাউন্টডাউন ও চলমান ওয়াক্ত (নিচে)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .widthIn(max = 180.dp)
                            .padding(top = 32.dp)
                    ) {
                        // ১. ওয়াক্তের সময়সীমা (উপরে)
                        Text(
                            text = if (isEnglish) currentWaqtRangeEn else currentWaqtRangeBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = targetPalette.secondaryTextColor,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        // ২. কাউন্টডাউন ব্যাজ (মাঝে)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = targetPalette.badgeBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, targetPalette.badgeBorder)
                        ) {
                            Text(
                                text = if (isEnglish) {
                                val h = nextPrayerInfo.remainingHours
                                val m = nextPrayerInfo.remainingMinutes
                                val s = nextPrayerInfo.remainingSeconds
                                "${nextPrayerInfo.nextPrayerNameEn} in: ${if (h > 0) "${h}h " else ""}${m}m ${s}s"
                            } else {
                                "${nextPrayerInfo.nextPrayerNameBn} বাকি: $liveCountdownFormatted"
                            },
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = targetPalette.badgeTextColor,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        // ৩. চলমান ওয়াক্ত (নিচে - পূর্বে যেখানে 'নামাজ শুরু করুন' ছিল)
                        val displayPrayerTitle = remember(currentPrayerName, isEnglish) {
                            if (isEnglish) {
                                nextPrayerInfo.currentPrayerNameEn
                            } else {
                                when {
                                    currentPrayerName.contains("তাহাজ্জুদ") -> "তাহাজ্জুদ"
                                    currentPrayerName.contains("সাহরি") -> "সাহরি"
                                    else -> currentPrayerName
                                }
                            }
                        }

                        Text(
                            text = displayPrayerTitle,
                            fontFamily = fonts.kalpurush,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = targetPalette.primaryTextColor,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // =========================================================================
                // গ. নিচের মার্জিত ইন্টিগ্রেটেড পরবর্তী ওয়াক্ত ও "সব ওয়াক্ত" নেভিগেশন স্ট্রিপ
                // =========================================================================
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFF064E3B).copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFF059669).copy(alpha = 0.20f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCardClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF6EE7B7) else Color(0xFF059669),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = if (isEnglish) {
                                    "Next Waqt: ${nextPrayerInfo.nextPrayerNameEn} (${nextPrayerInfo.nextPrayerTimeEn})"
                                } else {
                                    "পরবর্তী ওয়াক্ত: ${nextPrayerInfo.nextPrayerNameBn} (${nextPrayerInfo.nextPrayerTimeBn})"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = targetPalette.primaryTextColor
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isEnglish) "All Waqt" else "সব ওয়াক্ত",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = targetPalette.secondaryTextColor
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = targetPalette.secondaryTextColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }

/**
 * সেমি-সার্কুলার ডোম আর্চ এবং সকালসহ ৬টি প্রেয়ার মাইলস্টোন ড্রয়িং লজিক
 */
private fun DrawScope.drawCelestialDomeArc(
    prayerPoints: List<ArcPrayerPoint>,
    currentPrayerName: String,
    arcProgress: Float,
    trackColor: Color,
    progressColors: List<Color>,
    activeGlowColor: Color,
    primaryTextColor: Color,
    pulseScale: Float,
    pulseAlpha: Float,
    isDark: Boolean,
    isEnglish: Boolean = false
) {
    val hPadding = 26.dp.toPx()
    val strokeWidth = 3.dp.toPx()
    val radius = (size.width - 2 * hPadding) / 2
    val centerX = size.width / 2
    val centerY = size.height - 4.dp.toPx()

    val arcLeft = centerX - radius
    val arcTop = centerY - radius
    val arcDiameter = radius * 2

    // ১. ব্যাকগ্রাউন্ড ডটেড গাইড ট্র্যাক (ডটেড রাউন্ড দাগ - ১৮০° থেকে ৩৬০°)
    drawArc(
        color = trackColor.copy(alpha = 0.15f),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(arcLeft, arcTop),
        size = Size(arcDiameter, arcDiameter),
        style = Stroke(width = strokeWidth * 0.7f)
    )
    drawArc(
        color = trackColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(arcLeft, arcTop),
        size = Size(arcDiameter, arcDiameter),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f)
        )
    )

    // ২. প্রগ্রেস ট্র্যাক (বর্তমান সময় পর্যন্ত রঙিন ট্র্যাক)
    val sweepAngleProgress = (arcProgress * 180f).coerceIn(3f, 180f)
    drawArc(
        brush = Brush.horizontalGradient(
            colors = progressColors,
            startX = arcLeft,
            endX = centerX + radius
        ),
        startAngle = 180f,
        sweepAngle = sweepAngleProgress,
        useCenter = false,
        topLeft = Offset(arcLeft, arcTop),
        size = Size(arcDiameter, arcDiameter),
        style = Stroke(width = strokeWidth + 1.2.dp.toPx(), cap = StrokeCap.Round)
    )

    // ৩. আর্চের উপর ৬টি মাইলস্টোনের নোড ও ডটেড মার্কস
    val textPaint = Paint().apply {
        color = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.rgb(6, 78, 59)
        textSize = 9.dp.toPx()
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    prayerPoints.forEach { pt ->
        val rad = Math.toRadians(pt.angleDeg.toDouble())
        val nodeX = (centerX + radius * cos(rad)).toFloat()
        val nodeY = (centerY + radius * sin(rad)).toFloat()

        val isCurrent = currentPrayerName.contains(pt.nameBn)
        val isPassed = arcProgress >= pt.progressWeight

        // ওয়াক্তের মাইলস্টোন নোড
        if (isCurrent) {
            // অ্যাক্টিভ নোডের চারপাশের পালসিং অরা
            drawCircle(
                color = activeGlowColor.copy(alpha = pulseAlpha),
                radius = 11.dp.toPx() * pulseScale,
                center = Offset(nodeX, nodeY)
            )
            drawCircle(
                color = if (isDark) Color.White else Color(0xFF047857),
                radius = 5.dp.toPx(),
                center = Offset(nodeX, nodeY)
            )
            drawCircle(
                color = activeGlowColor,
                radius = 3.dp.toPx(),
                center = Offset(nodeX, nodeY)
            )
        } else {
            // নন-অ্যাক্টিভ নোড
            drawCircle(
                color = if (isPassed) activeGlowColor else (if (isDark) Color.White.copy(alpha = 0.55f) else Color(0xFF059669).copy(alpha = 0.45f)),
                radius = 4.dp.toPx(),
                center = Offset(nodeX, nodeY)
            )
            drawCircle(
                color = if (isDark) Color.Black.copy(alpha = 0.40f) else Color.White,
                radius = 1.8.dp.toPx(),
                center = Offset(nodeX, nodeY)
            )
        }

        // নোডের লেবেল পজিশন (ফজর ও এশাকে মার্জিনের ভিতরে সুন্দরভাবে রাখা)
        val labelAngleDeg = when (pt.key) {
            "fajr" -> 192f
            "isha" -> 348f
            else -> pt.angleDeg
        }
        val labelRad = Math.toRadians(labelAngleDeg.toDouble())
        val labelRadius = radius + 11.dp.toPx()
        val labelX = (centerX + labelRadius * cos(labelRad)).toFloat()
        val labelY = (centerY + labelRadius * sin(labelRad)).toFloat() + 3.dp.toPx()

        drawIntoCanvas { canvas ->
            textPaint.color = if (isCurrent) {
                if (isDark) android.graphics.Color.WHITE else android.graphics.Color.rgb(6, 78, 59)
            } else if (isPassed) {
                if (isDark) android.graphics.Color.argb(220, 240, 240, 240) else android.graphics.Color.rgb(15, 118, 110)
            } else {
                if (isDark) android.graphics.Color.argb(160, 200, 200, 200) else android.graphics.Color.rgb(100, 116, 139)
            }
            canvas.nativeCanvas.drawText(if (isEnglish) pt.nameEn else pt.nameBn, labelX, labelY, textPaint)
        }
    }

    // ৪. লাইভ সান/মুন ইন্ডিকেটর (বর্তমান অবস্থানের চলমান গ্লোয়িং অরবিটার)
    val pointerAngleDeg = 180f + arcProgress * 180f
    val pointerRad = Math.toRadians(pointerAngleDeg.toDouble())
    val pointerX = (centerX + radius * cos(pointerRad)).toFloat()
    val pointerY = (centerY + radius * sin(pointerRad)).toFloat()

    // চলমান পয়েন্টারের বহিঃস্থ উজ্জ্বল আলোকচ্ছটা
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(activeGlowColor.copy(alpha = 0.75f), activeGlowColor.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(pointerX, pointerY),
            radius = 16.dp.toPx() * pulseScale
        ),
        radius = 16.dp.toPx() * pulseScale,
        center = Offset(pointerX, pointerY)
    )

    // কোর পয়েন্টার
    drawCircle(
        color = if (isDark) Color(0xFFF1F5F9) else Color(0xFFFFB703),
        radius = 5.dp.toPx(),
        center = Offset(pointerX, pointerY)
    )
}

/**
 * কার্ডের ব্যাকগ্রাউন্ডে মসজিদের সূক্ষ্ম সিলুয়েট আর্ট
 */
private fun DrawScope.drawMosqueSilhouette(color: Color) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h - 35.dp.toPx())

        // বাম মিনার
        lineTo(w * 0.10f, h - 35.dp.toPx())
        lineTo(w * 0.10f, h - 75.dp.toPx())
        lineTo(w * 0.12f, h - 82.dp.toPx())
        lineTo(w * 0.14f, h - 75.dp.toPx())
        lineTo(w * 0.14f, h - 35.dp.toPx())

        // মধ্য গম্বুজ
        lineTo(w * 0.35f, h - 35.dp.toPx())
        cubicTo(
            w * 0.40f, h - 70.dp.toPx(),
            w * 0.60f, h - 70.dp.toPx(),
            w * 0.65f, h - 35.dp.toPx()
        )

        // ডান মিনার
        lineTo(w * 0.86f, h - 35.dp.toPx())
        lineTo(w * 0.86f, h - 75.dp.toPx())
        lineTo(w * 0.88f, h - 82.dp.toPx())
        lineTo(w * 0.90f, h - 75.dp.toPx())
        lineTo(w * 0.90f, h - 35.dp.toPx())

        lineTo(w, h - 35.dp.toPx())
        lineTo(w, h)
        close()
    }

    val fadeBrush = Brush.verticalGradient(
        colors = listOf(
            color,
            color.copy(alpha = color.alpha * 0.5f),
            Color.Transparent
        ),
        startY = h - 85.dp.toPx(),
        endY = h
    )
    drawPath(path = path, brush = fadeBrush)
}
